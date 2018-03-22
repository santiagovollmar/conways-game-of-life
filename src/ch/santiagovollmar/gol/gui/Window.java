package ch.santiagovollmar.gol.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ch.santiagovollmar.gol.logic.GridManager;
import ch.santiagovollmar.gol.logic.Point;
import ch.santiagovollmar.gol.util.PropertyManager;

public class Window {
  
  private static Window currentInstance;
  
  public static Window getCurrentInstance() {
    return currentInstance;
  }
  
  private JFrame frame;
  
  public JFrame getFrame() {
    return frame;
  }
  
  private GameDisplay gd;
  
  public GameDisplay getGameDisplay() {
    return gd;
  }
  
  /**
   * Launch the application.
   */
  public static void open() {
    try {
      EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          try {
            Window window = new Window();
            window.frame.setVisible(true);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
    } catch (InvocationTargetException | InterruptedException e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }
  
  /**
   * Create the application.
   */
  public Window() {
    currentInstance = this;
    initialize();
  }
  
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    // set LAF
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
        | UnsupportedLookAndFeelException e) {
      System.err.println("Could not find system LAF.\n\tChanging to cross-plattform LAF");
      try {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
          | UnsupportedLookAndFeelException e1) {
        System.err.println("Could not find cross-plattform LAF.\n\texiting...");
      }
    }
    
    frame = new JFrame();
    frame.setBounds(100, 100, 500, 500);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    String[] rgbValues = PropertyManager.get("display.color").split("\\s*\\,\\s*");
    gd = new GameDisplay(this.frame, 
        Integer.valueOf(PropertyManager.get("display.width")),
        Integer.valueOf(PropertyManager.get("display.height")),
        Integer.valueOf(PropertyManager.get("display.scaling")), 
        new Color(Integer.valueOf(rgbValues[0]), 
            Integer.valueOf(rgbValues[1]), 
            Integer.valueOf(rgbValues[2])));
    
    frame.add(new MenuBar(), BorderLayout.NORTH);
    frame.add(gd, BorderLayout.CENTER);
    frame.add(new ToolBar(), BorderLayout.SOUTH);
    frame.pack();
    
    for (int x = 0; x < 100; x++) {
      for (int y = 0; y < 100; y++) {
        if (Math.random() > 0.5) {
          GridManager.fill(new Point(x + 45020, y + 45020), false);
        }
      }
    }
  }
}
