package ch.santiagovollmar.gol.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.LinkedList;

import javax.security.auth.x500.X500Principal;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.bcel.generic.IF_ACMPEQ;

import ch.santiagovollmar.gol.logic.GridManager;
import ch.santiagovollmar.gol.logic.LogicManager;
import ch.santiagovollmar.gol.logic.Point;
import ch.santiagovollmar.gol.util.GlobalKeyListener;
import ch.santiagovollmar.gol.util.GlobalKeyListener.KeyListenerType;

@SuppressWarnings("serial")
public class GameDisplay extends JPanel {
  /*
   * static
   */
  public interface FetchOperation {
    public Collection<Point> fetch(int x1, int y1, int x2, int y2);
  }
  
  private static LinkedList<Point> dataPlaceholder = new LinkedList<>();
  private static FetchOperation fetchOperation = (x1, y1, x2, y2) -> {
    return dataPlaceholder;
  };
  
  public static void setFetchOperation(FetchOperation o) {
    fetchOperation = o;
  }
  
  /*
   * fields
   */
  private Color fillColor = Color.BLUE;
  private Color lineColor = Color.WHITE;
  private Color paneColor = Color.GRAY;
  private Color selectionColor = Color.WHITE;
  
  private final Point viewport;
  
  private volatile int vsize;
  private volatile int hsize;
  private volatile int scaling;
  
  private volatile boolean ctrlIsPressed;
  private volatile boolean shiftIsPressed;
  private volatile boolean selectionCreation;
  
  @SuppressWarnings("unused")
  private final JFrame parent;
  
  private final Point dragStart = new Point(-1, -1);
  
  private final Point selectionStart = new Point(-1, -1);
  private final Point selectionEnd = new Point(-1, -1);
  
  private Point copyBufferStart;
  private HashSet<Point> copyBuffer;
  
  int i;
  
  /*
   * getters and setters
   */
  public Color getFillColor() {
    synchronized (fillColor) {
      return fillColor;
    }
  }
  
  public void setFillColor(Color fillColor) {
    synchronized (fillColor) {
      this.fillColor = fillColor;
    }
    
    int lineColorAvg = ((fillColor.getGreen() + fillColor.getRed() + fillColor.getBlue()) / 3) + 30;
    lineColorAvg = lineColorAvg > 255 ? 255 : lineColorAvg;
    int paneColorAvg = 255 - (lineColorAvg / 2);
    
    lineColor = new Color(lineColorAvg, lineColorAvg, lineColorAvg);
    paneColor = new Color(paneColorAvg, paneColorAvg, paneColorAvg);
    selectionColor = new Color(paneColorAvg + 20, paneColorAvg + 20, 255);
    selectionColor = Color.WHITE;
    System.out.printf("%d %d %d\n", selectionColor.getRed(), selectionColor.getGreen(), selectionColor.getBlue());
  }
  
  public Point getViewport() {
    return new Point(viewport.x, viewport.y);
  }
  
  public void setViewport(Point edge) {
    viewport.x = edge.x;
    viewport.y = edge.y;
  }
  
  public int getScaling() {
    return scaling;
  }
  
  public void setScaling(int scaling) {
    this.scaling = scaling;
  }
  
  /*
   * Constructors
   */
  public GameDisplay(JFrame parent, int hsize, int vsize, int scaling, Color fillColor) {
    super();
    this.vsize = vsize;
    this.hsize = hsize;
    this.scaling = scaling;
    setFillColor(fillColor);
    this.parent = parent;
    this.viewport = new Point(45000, 45000);
    
    // set sizes
    setPreferredSize(new Dimension(hsize * scaling, vsize * scaling));
    setMinimumSize(new Dimension(hsize * scaling, vsize * scaling));
    
    // focus
    setFocusable(true);
    grabFocus();
    requestFocus();
    
    // functionality
    setupDraw();
    setupDrag();
    setupZoom();
    setupSelection();
    setupDeleteAction();
    setupCopyPasteAction();
    
    // add global key listeners
    GlobalKeyListener.attach(KeyListenerType.PRESSED, e -> {
      LogicManager.setPaused(!LogicManager.isPaused());
    }, KeyEvent.VK_SPACE);
    
    GlobalKeyListener.attach(KeyListenerType.PRESSED, e -> {
      ctrlIsPressed = true;
    }, KeyEvent.VK_CONTROL);
    
    GlobalKeyListener.attach(KeyListenerType.RELEASED, e -> {
      ctrlIsPressed = false;
    }, KeyEvent.VK_CONTROL);
    
    GlobalKeyListener.attach(KeyListenerType.PRESSED, e -> {
      shiftIsPressed = true;
    }, KeyEvent.VK_SHIFT);
    
    GlobalKeyListener.attach(KeyListenerType.RELEASED, e -> {
      shiftIsPressed = false;
    }, KeyEvent.VK_SHIFT);
    
    GlobalKeyListener.attach(KeyListenerType.PRESSED, e -> {
      Point direction = new Point(0, 0);
      switch (e.getKeyCode()) {
        case KeyEvent.VK_UP:
          direction.y--;
          break;
        
        case KeyEvent.VK_DOWN:
          direction.y++;
          break;
        
        case KeyEvent.VK_LEFT:
          direction.x--;
          break;
        
        case KeyEvent.VK_RIGHT:
          direction.x++;
          break;
      }
      
      if (ctrlIsPressed) {
        viewport.x += direction.x;
        viewport.y += direction.y;
      } else if (shiftIsPressed) {
        ensureSelection();
        selectionEnd.x += direction.x;
        selectionEnd.y += direction.y;
      } else {
        ensureSelection();
        selectionStart.x += direction.x;
        selectionStart.y += direction.y;
        
        selectionEnd.x += direction.x;
        selectionEnd.y += direction.y;
      }
    }, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
    
    GlobalKeyListener.attach(KeyListenerType.PRESSED, e -> {
      if (!LogicManager.isPaused() || selectionStart.x == -1 || selectionEnd.x == -1) {
        return;
      }
      
      Point min = getSelectionMin();
      Point max = getSelectionMax();
      
      for (int x = min.x; x < max.x; x++) {
        for (int y = min.y; y < max.y; y++) {
          GridManager.fill(new Point(x, y), false);
        }
      }
    }, KeyEvent.VK_F);
    
    GlobalKeyListener.attach(KeyListenerType.PRESSED, e -> clearSelection(), KeyEvent.VK_ESCAPE);
  }
  
  public GameDisplay(JFrame parent, int hsize, int vsize, Color fillColor) {
    this(parent, hsize, vsize, 10, fillColor);
  }
  
  public GameDisplay(JFrame parent, int hsize, int vsize, int scaling) {
    this(parent, hsize, vsize, scaling, Color.MAGENTA);
  }
  
  public GameDisplay(JFrame parent, int scaling) {
    this(parent, 100, 100, scaling);
  }
  
  public GameDisplay(JFrame parent, Color fillColor) {
    this(parent, 100, 100, fillColor);
  }
  
  public GameDisplay(JFrame parent) {
    this(parent, 10);
  }
  
  /*
   * Functionality
   */
  private void setupDeleteAction() {
    GlobalKeyListener.attach(KeyListenerType.PRESSED, e -> {
      if (LogicManager.isPaused()) { // check if game is paused
        if (selectionStart.x != -1 && selectionEnd.x != -1) { // user has an active selection
          Point min = getSelectionMin();
          Point max = getSelectionMax();
          
          // put points to be cleared into a list
          ArrayList<Point> points = new ArrayList<>((max.x - min.x) * (max.y - min.y));
          for (int x = min.x; x < max.x; x++) {
            for (int y = min.y; y < max.y; y++) {
              points.add(new Point(x, y));
            }
          }
          
          // clear all points
          GridManager.clear(points, false);
        }
      }
    }, KeyEvent.VK_DELETE, KeyEvent.VK_C);
  }
  
  private void setupCopyPasteAction() {
    // copy
    GlobalKeyListener.attach(KeyListenerType.PRESSED, e -> {
      if (LogicManager.isPaused() && ctrlIsPressed) { // check if ctrl is pressed and game is paused
        if (selectionStart.x != -1 && selectionEnd.x != -1) { // check if there is an active selection
          Point min = getSelectionMin();
          Point max = getSelectionMax();
          
          copyBufferStart = min; // save start of copy selection
          
          // save data in copied area
          copyBuffer = new HashSet<>();
          for (int x = min.x; x < max.x; x++) {
            for (int y = min.y; y < max.y; y++) {
              Point point = new Point(x, y);
              
              if (GridManager.isAlive(point)) {
                copyBuffer.add(point);
              }
            }
          }
        }
      }
    }, KeyEvent.VK_C);
    
    // paste
    GlobalKeyListener.attach(KeyListenerType.PRESSED, e -> {
      if (LogicManager.isPaused() && ctrlIsPressed) { // check if game is paused and ctrl is pressed
        if (copyBufferStart != null && selectionStart.x != -1 && selectionEnd.x != -1) { // check if clipboard contains
                                                                                         // data and there is a
                                                                                         // selection
          // get selection
          Point min = getSelectionMin();
          Point max = getSelectionMax();
          
          // calculate offset
          Point offset = new Point(-1, -1);
          offset.x = copyBufferStart.x - min.x;
          offset.y = copyBufferStart.y - min.y;
          
          // overwrite selected area
          for (int x = min.x; x < max.x; x++) {
            for (int y = min.y; y < max.y; y++) {
              Point point = new Point(x + offset.x, y + offset.y);
              
              if (copyBuffer.contains(point)) {
                point.x = x;
                point.y = y;
                GridManager.fill(point, false);
              } else {
                point.x = x;
                point.y = y;
                GridManager.clear(point, false);
              }
            }
          }
        }
      }
    }, KeyEvent.VK_V);
  }
  
  private void setupSelection() {
    addMouseMotionListener(new MouseMotionListener() {
      
      @Override
      public void mouseMoved(MouseEvent arg0) {
      }
      
      @Override
      public void mouseDragged(MouseEvent e) {
        if (shiftIsPressed) {
          selectionEnd.x = (e.getX() / scaling) + viewport.x;
          selectionEnd.y = (e.getY() / scaling) + viewport.y;
          selectionCreation = true;
        } else {
          selectionCreation = false;
        }
      }
    });
    
    addMouseListener(new MouseListener() {
      
      @Override
      public void mouseReleased(MouseEvent e) {
        if (shiftIsPressed) {
          selectionEnd.x = (e.getX() / scaling) + viewport.x;
          selectionEnd.y = (e.getY() / scaling) + viewport.y;
        }
        
        selectionCreation = false;
      }
      
      @Override
      public void mousePressed(MouseEvent e) {
        if (shiftIsPressed) {
          clearSelection();
          selectionStart.x = (e.getX() / scaling) + viewport.x;
          selectionStart.y = (e.getY() / scaling) + viewport.y;
          selectionCreation = true;
        } else {
          selectionCreation = false;
        }
      }
      
      @Override
      public void mouseExited(MouseEvent arg0) {
      }
      
      @Override
      public void mouseEntered(MouseEvent arg0) {
      }
      
      @Override
      public void mouseClicked(MouseEvent arg0) {
      }
    });
  }
  
  private void setupDraw() {
    addMouseListener(new MouseListener() {
      
      @Override
      public void mouseReleased(MouseEvent arg0) {
      }
      
      @Override
      public void mousePressed(MouseEvent arg0) {
      }
      
      @Override
      public void mouseExited(MouseEvent arg0) {
      }
      
      @Override
      public void mouseEntered(MouseEvent arg0) {
      }
      
      @Override
      public void mouseClicked(MouseEvent e) {
        System.out.println("e { x: " + e.getX() + ", y: " + e.getY() + " }");
        System.out.println("selection_start: " + selectionStart);
        System.out.println("selection_end: " + selectionEnd);
        grabFocus();
        if (selectionStart.x == -1 && selectionEnd.x == -1) {
          if (LogicManager.isPaused() && !ctrlIsPressed && !selectionCreation) {
            if (e.getButton() == 1) {
              fillCell(e);
            } else if (e.getButton() == 3) {
              clearCell(e);
            }
          }
        } else {
          clearSelection();
        }
      }
    });
    
    addMouseMotionListener(new MouseMotionListener() {
      
      @Override
      public void mouseMoved(MouseEvent e) {
      }
      
      @Override
      public void mouseDragged(MouseEvent e) {
        if (!ctrlIsPressed && LogicManager.isPaused() && !selectionCreation) { // draw
          clearSelection();
          
          if (SwingUtilities.isLeftMouseButton(e)) {
            fillCell(e);
          } else if (SwingUtilities.isRightMouseButton(e)) {
            clearCell(e);
          }
        }
      }
    });
  }
  
  private void setupDrag() {
    GameDisplay parentDisplay = this;
    
    addMouseListener(new MouseListener() {
      
      @Override
      public void mouseReleased(MouseEvent e) {
        dragStart.x = -1;
        dragStart.y = -1;
      }
      
      @Override
      public void mousePressed(MouseEvent e) {
        if (ctrlIsPressed) {
          dragStart.x = e.getX();
          dragStart.y = e.getY();
        }
      }
      
      @Override
      public void mouseExited(MouseEvent arg0) {
      }
      
      @Override
      public void mouseEntered(MouseEvent arg0) {
        grabFocus();
      }
      
      @Override
      public void mouseClicked(MouseEvent arg0) {
      }
    });
    
    addMouseMotionListener(new MouseMotionListener() {
      
      @Override
      public void mouseMoved(MouseEvent e) {
      }
      
      @Override
      public void mouseDragged(MouseEvent e) {
        if (ctrlIsPressed) { // drag viewport
          if (dragStart.x != -1) {
            int x = dragStart.x;
            int y = dragStart.y;
            
            int mouse_x = e.getX();
            int mouse_y = e.getY();
            
            if ((((double) Math.abs(x - mouse_x)) / ((double) scaling)) > 0) {
              int difference_x;
              synchronized (dragStart) {
                dragStart.x = mouse_x;
                difference_x = (x - dragStart.x);
              }
              
              synchronized (viewport) {
                viewport.x += (int) Math.round(((double) (difference_x)) / ((double) parentDisplay.scaling));// parentDisplay.scaling;
              }
            }
            
            if ((((double) Math.abs(y - mouse_y)) / ((double) scaling)) > 0) {
              int difference_y;
              synchronized (dragStart) {
                dragStart.y = mouse_y;
                difference_y = (y - dragStart.y);
              }
              
              synchronized (viewport) {
                viewport.y += (int) Math.round(((double) (difference_y)) / ((double) parentDisplay.scaling));// parentDisplay.scaling;
              }
            }
          }
        }
      }
    });
  }
  
  private void setupZoom() {
    GameDisplay parentDisplay = this;
    
    addMouseWheelListener(e -> {
      if (ctrlIsPressed) {
        if (e.getWheelRotation() > 0) {
          if (parentDisplay.scaling > 4) {
            parentDisplay.scaling--;
          }
        } else if (e.getWheelRotation() < 0) {
          if (scaling < 100) {
            parentDisplay.scaling++;
          }
        }
        
        SwingUtilities.invokeLater(parentDisplay::revalidate);
        SwingUtilities.invokeLater(parentDisplay::repaint);
      }
    });
  }
  
  /*
   * User editing
   */
  public void fillCell(MouseEvent e) {
    synchronized (viewport) {
      GridManager.fill(new Point((e.getX() / scaling) + viewport.x, (e.getY() / scaling) + viewport.y), false);
    }
  }
  
  public void clearCell(MouseEvent e) {
    synchronized (viewport) {
      GridManager.clear(new Point((e.getX() / scaling) + viewport.x, (e.getY() / scaling) + viewport.y), false);
    }
  }
  
  public void clearSelection() {
    selectionStart.x = -1;
    selectionStart.y = -1;
    
    selectionEnd.x = -1;
    selectionEnd.y = -1;
  }
  
  public Point getSelectionMin() {
    int x = selectionStart.x < selectionEnd.x ? selectionStart.x : selectionEnd.x;
    int y = selectionStart.y < selectionEnd.y ? selectionStart.y : selectionEnd.y;
    
    return new Point(x, y);
  }
  
  public Point getSelectionMax() {
    int x = selectionStart.x > selectionEnd.x ? selectionStart.x : selectionEnd.x;
    int y = selectionStart.y > selectionEnd.y ? selectionStart.y : selectionEnd.y;
    
    return new Point(x, y);
  }
  
  private void ensureSelection() {
    if (selectionStart.x == -1 || selectionEnd.x == -1) { // no active selection
      // spawn new selection in center
      selectionStart.x = viewport.x + hsize / 2;
      selectionStart.y = viewport.y + vsize / 2;
      selectionEnd.x = selectionStart.x + 1;
      selectionEnd.y = selectionStart.y + 1;
    }
  }
  
  /*
   * Drawing methods
   */
  @Override
  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);
    
    setPreferredSize(new Dimension(width, height));
    setMinimumSize(new Dimension(width, height));
    this.vsize = (int) Math.ceil(((double) height) / scaling);
    this.hsize = (int) Math.ceil(((double) width) / scaling);
  }
  
  protected final void drawLines(Graphics graphics, Color color, int stroke) {
    // set color and save current color for later
    Color prevColor = graphics.getColor();
    graphics.setColor(color);
    
    // draw vertical lines
    for (int i = 0; i < hsize + 1; i++) {
      graphics.fillRect(i * scaling, 0, stroke, vsize * scaling);
    }
    
    // draw horizontal lines
    for (int i = 0; i < vsize + 1; i++) {
      graphics.fillRect(0, i * scaling, hsize * scaling, stroke);
    }
    
    // set color back to old one
    graphics.setColor(prevColor);
  }
  
  protected final void drawSquares(Graphics graphics) {
    Color prevColor = graphics.getColor();
    synchronized (fillColor) {
      graphics.setColor(fillColor);
    }
    
    try {
      Collection<Point> points = fetchOperation.fetch(viewport.x, viewport.y, viewport.x + hsize - 1,
          viewport.y + vsize - 1);
      
      synchronized (viewport) {
        points.forEach((e) -> {
          graphics.fillRect((e.x - viewport.x) * scaling, (e.y - viewport.y) * scaling, scaling, scaling);
        });
      }
      
      graphics.setColor(prevColor);
    } catch (ConcurrentModificationException e) {
      e.printStackTrace(System.err);
    }
  }
  
  protected final void drawSelection(Graphics graphics) {
    if (selectionEnd.x == -1 || selectionStart.x == -1) {
      return;
    }
    
    int r, g, b;
    r = 205 - 60;
    g = 232 - 60;
    b = 255 - 60 + 40;
    
    graphics.setColor(new Color(r, g, b, 100));
    Point selectionMin = getSelectionMin();
    selectionMin.x -= viewport.x;
    selectionMin.y -= viewport.y;
    
    int selectionHeight = Math.abs(selectionStart.y - selectionEnd.y);
    int selectionWidth = Math.abs(selectionStart.x - selectionEnd.x);
    graphics.fillRect(selectionMin.x * scaling, selectionMin.y * scaling, selectionWidth * scaling,
        selectionHeight * scaling);
    
    ((Graphics2D) graphics).setStroke(new BasicStroke(2f));
    graphics.setColor(new Color(r, g, b, 255));
    graphics.drawRect(selectionMin.x * scaling, selectionMin.y * scaling, selectionWidth * scaling,
        selectionHeight * scaling);
  }
  
  @Override
  protected void paintComponent(Graphics graphics) {
    graphics.setColor(paneColor);
    graphics.fillRect(0, 0, hsize * scaling, vsize * scaling);
    drawSquares(graphics);
    drawLines(graphics, lineColor, 1);
    drawSelection(graphics);
  }
}
