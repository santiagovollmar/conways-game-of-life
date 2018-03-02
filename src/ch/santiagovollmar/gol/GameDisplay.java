package ch.santiagovollmar.gol;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.time.Year;
import java.util.Collection;
import java.util.LinkedList;

import javax.net.ssl.ExtendedSSLSession;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ch.santiagovollmar.gol.GlobalKeyListener.KeyListenerType;
import jdk.nashorn.internal.objects.Global;

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
  
  private Point viewport;
  
  private int vsize;
  private int hsize;
  private int scaling;
  
  private boolean ctrlIsPressed;
  private JFrame parent;
  
  private Point dragStart = new Point(-1, -1);
  
  /*
   * Constructors
   */
  public GameDisplay(JFrame parent, int hsize, int vsize, int scaling, Color fillColor) {
    super();
    this.vsize = vsize;
    this.hsize = hsize;
    this.scaling = scaling;
    this.fillColor = fillColor;
    this.parent = parent;
    this.viewport = new Point(45000, 45000);
    
    // set sizes
    setPreferredSize(new Dimension(hsize * scaling, vsize * scaling));
    setMinimumSize(new Dimension(hsize * scaling, vsize * scaling));
    
    // focus
    setFocusable(true);
    grabFocus();
    requestFocus();
    
    // add mouse listeners
    addMouseMotionListener(new MouseMotionListener() {
      
      @Override
      public void mouseMoved(MouseEvent e) {}
      
      @Override
      public void mouseDragged(MouseEvent e) {
        if (ctrlIsPressed) { // drag viewport
          if (dragStart.x != -1) {
            int x = dragStart.x;
            int y = dragStart.y;
            
            dragStart.x = e.getX();
            dragStart.y = e.getY();
            
            viewport.x += x - dragStart.x;
            viewport.y += y - dragStart.y;
            
            System.out.println("dragPos: " + dragStart);
          }
        } else { // draw
          if (LogicManager.isPaused()) {
            if (SwingUtilities.isLeftMouseButton(e)) {
              fillCell(e);
            } else if (SwingUtilities.isRightMouseButton(e)) {
              clearCell(e);
            }
          }
        }
      }
    });
    
    addMouseListener(new MouseListener() {
      @Override
      public void mouseReleased(MouseEvent e) {
        System.out.println("[reset drag]");
        dragStart.x = -1;
        dragStart.y = -1;
      }
      
      @Override
      public void mousePressed(MouseEvent e) {
        if (ctrlIsPressed) {
          System.out.println("[set drag]");
          
          dragStart.x = e.getX();
          dragStart.y = e.getY();
        }
      }
      
      @Override
      public void mouseExited(MouseEvent e) {}
      
      @Override
      public void mouseEntered(MouseEvent e) {
        grabFocus();
      }
      
      @Override
      public void mouseClicked(MouseEvent e) {
        System.out.println(e.getButton());
        grabFocus();
        if (LogicManager.isPaused() && !ctrlIsPressed) {
          if (e.getButton() == 1) {
            fillCell(e);
          } else if (e.getButton() == 3) {
            clearCell(e);
          }
        }
      }
    });
    
    GameDisplay parentDisplay = this;
    addMouseWheelListener(e -> {
      if (ctrlIsPressed) {
        if (e.getWheelRotation() > 0) {
          if (parentDisplay.scaling > 4) {
            parentDisplay.scaling--;
            parentDisplay.repaint();
          }
        } else if (e.getWheelRotation() < 0) {
          if (scaling < 100) {
            parentDisplay.scaling++;
            parentDisplay.repaint();
          }
        }
      }
    });
    
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
      switch (e.getKeyCode()) {
        case KeyEvent.VK_UP:
          viewport.y--;
          break;
          
        case KeyEvent.VK_DOWN:
          viewport.y++;
          break;
          
        case KeyEvent.VK_LEFT:
          viewport.x--;
          break;
          
        case KeyEvent.VK_RIGHT:
          viewport.x++;
          break;
      }
    }, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
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
   * User editing
   */
  public void fillCell(MouseEvent e) {
    GridManager.fill(new Point((e.getX() / scaling) + viewport.x, (e.getY() / scaling) + viewport.y), false);
  }
  
  public void clearCell(MouseEvent e) {
    GridManager.clear(new Point((e.getX() / scaling) + viewport.x, (e.getY() / scaling) + viewport.y), false);
  }
  
  /*
   * Drawing methods
   */
  int a;
  int b;
  
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
    graphics.setColor(fillColor);
    
    Collection<Point> points = fetchOperation.fetch(viewport.x, viewport.y, viewport.x + hsize - 1,
        viewport.y + vsize - 1);
    // System.out.printf("fetched: %d, %d to %d, %d\n", viewportOrigin.x,
    // viewportOrigin.y, viewportOrigin.x + hsize - 1, viewportOrigin.y + vsize -
    // 1);
    synchronized (points) {
      points.forEach((e) -> {
        graphics.fillRect((e.x - viewport.x) * scaling, (e.y - viewport.y) * scaling, scaling, scaling);
      });
    }
    
    graphics.setColor(prevColor);
  }
  
  @Override
  protected void paintComponent(Graphics graphics) {
    graphics.setColor(Color.LIGHT_GRAY);
    graphics.fillRect(0, 0, hsize * scaling, vsize * scaling);
    drawSquares(graphics);
    drawLines(graphics, Color.WHITE, 1);
  }
}
