package ch.santiagovollmar.gol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Window {
  
  private JFrame frame;
  
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Window window = new Window();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  
  /**
   * Create the application.
   */
  public Window() {
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
    
    GameDisplay gd = new GameDisplay(this.frame, 200, 100, 5, Color.BLUE);
    
    frame.add(new MenuBar(), BorderLayout.NORTH);
    frame.add(gd, BorderLayout.CENTER);
    frame.pack();
    
    ArrayList<Point> points = new ArrayList<Point>();
    points.add(new Point(0 + 5 + 45000, 2 + 5 + 45000));
    points.add(new Point(1 + 5 + 45000, 3 + 5 + 45000));
    points.add(new Point(2 + 5 + 45000, 3 + 5 + 45000));
    points.add(new Point(2 + 5 + 45000, 2 + 5 + 45000));
    points.add(new Point(2 + 5 + 45000, 1 + 5 + 45000));
    points.add(new Point(0 + 15 + 45000, 2 + 5 + 45000));
    points.add(new Point(1 + 15 + 45000, 3 + 5 + 45000));
    points.add(new Point(2 + 15 + 45000, 3 + 5 + 45000));
    points.add(new Point(2 + 15 + 45000, 2 + 5 + 45000));
    points.add(new Point(2 + 15 + 45000, 1 + 5 + 45000));
    points.add(new Point(0 + 2 + 45000, 2 + 10 + 45000));
    points.add(new Point(1 + 2 + 45000, 3 + 10 + 45000));
    points.add(new Point(2 + 2 + 45000, 3 + 10 + 45000));
    points.add(new Point(2 + 2 + 45000, 2 + 10 + 45000));
    points.add(new Point(2 + 2 + 45000, 1 + 10 + 45000));
    points.add(new Point(0 + 30 + 45000, 1 + 10 + 45000));
    points.add(new Point(0 + 30 + 45000, 4 + 10 + 45000));
    points.add(new Point(1 + 30 + 45000, 0 + 10 + 45000));
    points.add(new Point(1 + 30 + 45000, 2 + 10 + 45000));
    points.add(new Point(1 + 30 + 45000, 3 + 10 + 45000));
    points.add(new Point(1 + 30 + 45000, 5 + 10 + 45000));
    points.add(new Point(2 + 30 + 45000, 1 + 10 + 45000));
    points.add(new Point(2 + 30 + 45000, 4 + 10 + 45000));
    points.add(new Point(3 + 30 + 45000, 1 + 10 + 45000));
    points.add(new Point(3 + 30 + 45000, 4 + 10 + 45000));
    points.add(new Point(4 + 30 + 45000, 0 + 10 + 45000));
    points.add(new Point(4 + 30 + 45000, 2 + 10 + 45000));
    points.add(new Point(4 + 30 + 45000, 3 + 10 + 45000));
    points.add(new Point(4 + 30 + 45000, 5 + 10 + 45000));
    points.add(new Point(5 + 30 + 45000, 1 + 10 + 45000));
    points.add(new Point(5 + 30 + 45000, 4 + 10 + 45000));
    
    // GridManager.fill(points, false);
    
    for (int x = 0; x < 100; x++) {
      for (int y = 0; y < 100; y++) {
        GridManager.fill(new Point(x + 45020, y + 45020), false);
      }
    }
    
    GlobalKeyListener.apply(frame.getContentPane());
    
    new Thread(() -> {
      long[] times = new long[100];
      long r_start = System.currentTimeMillis();
      
      for (int i = 0; i < times.length; i++) {
        try {
          long start = System.nanoTime();
          LogicManager.renderNext();
          gd.repaint();
          long sleepTime = 75_000_000 - (System.nanoTime() - start);
          if (sleepTime > 0) {
            Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
          }
          
          times[i] = System.currentTimeMillis();
        } catch (Exception e) {
        }
      }
      
      long[] diff = new long[times.length - 1];
      for (int i = 0; i < times.length - 1; i++) {
        diff[i] = times[i + 1] - times[i];
      }
      
      long totalError = 0;
      for (long difference : diff) {
        totalError += Math.abs(75 - difference);
      }
      
      long meanError = totalError / diff.length;
      
      System.out.println("time elapsed: " + (System.currentTimeMillis() - r_start) + "ms");
      System.out.println("\ttotal error: " + totalError);
      System.out.println("\tmean error: " + meanError);
      
      System.exit(0);
    }).start();
  }
  
}
