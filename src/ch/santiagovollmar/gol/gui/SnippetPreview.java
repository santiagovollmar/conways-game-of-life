package ch.santiagovollmar.gol.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.xpath.operations.Variable;

import ch.santiagovollmar.gol.logic.FunctionalityMatrix;
import ch.santiagovollmar.gol.logic.FunctionalityMatrix.Functionality;
import ch.santiagovollmar.gol.util.PropertyManager;
import javafx.scene.layout.Border;
import ch.santiagovollmar.gol.logic.Point;
import ch.santiagovollmar.gol.logic.Snippet;

public class SnippetPreview extends JPanel {
  private final Snippet snippet;
  private final GameDisplay gd;
  
  private final Point min;
  private final Point max;
  
  private int snippetHeight;
  private int snippetWidth;
  private int scaling;
  
  public SnippetPreview(Snippet snippet) {
    this.snippet = snippet;
    
    // determine snippet height and width
    min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
    max = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
    
    for (Point point : snippet.getScene()) {
      max.x = point.x > max.x ? point.x : max.x;
      max.y = point.y > max.y ? point.y : max.y;
      
      min.x = point.x < min.x ? point.x : min.x;
      min.y = point.y < min.y ? point.y : min.y;
    }
    
    snippetHeight = max.y - min.y + 3;
    snippetWidth = max.x - min.x + 3;
    String[] rgbValues = PropertyManager.get("display.color").split("\\s*\\,\\s*");
    gd = new GameDisplay(Window.getCurrentInstance().getFrame(),
        new FunctionalityMatrix(Functionality.DRAG, Functionality.ZOOM, Functionality.SELECTION, Functionality.ARROW_ACTIONS), snippetWidth, 0,
        Integer.valueOf(PropertyManager.get("display.scaling")),
        new Color(Integer.valueOf(rgbValues[0]), Integer.valueOf(rgbValues[1]), Integer.valueOf(rgbValues[2])));
    gd.setFetchoperation((int x1, int y1, int x2, int y2) -> {
      return snippet.getScene();
    });
    
    setLayout(new BorderLayout(0, 0));
    add(gd, BorderLayout.CENTER);
  }
  
  /*
   * GUI stuff
   */
  @Override
  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);
    
    scaling = width / snippetWidth;
    
    gd.setScaling(scaling);
    gd.setViewport(new Point(min.x - 1, min.y - 1));
    
    SwingUtilities.invokeLater(() -> {
      Dimension size = new Dimension(width, snippetHeight * scaling);
      setMinimumSize(size);
      setMaximumSize(size);
      setPreferredSize(size);
      System.out.println(size);
    });
  }
  
  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    
    // paint border
    try {
      getBorder().paintBorder(this, graphics, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
    } catch (NullPointerException e) {}
    
    // paint snippet
    // gd.paintComponent(graphics);
  }
}
