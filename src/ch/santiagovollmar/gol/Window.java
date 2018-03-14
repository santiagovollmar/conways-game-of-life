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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
    gd = new GameDisplay(this.frame, 200, 100, 5, new Color(0, 102, 255));
    frame.add(new MenuBar(), BorderLayout.NORTH);
    frame.add(gd, BorderLayout.CENTER);
    frame.add(new ToolBar(), BorderLayout.SOUTH);
    frame.pack();
    
    for (int x = 0; x < 100; x++) {
      for (int y = 0; y < 100; y++) {
        if (true || Math.random() > 0.5) {
          GridManager.fill(new Point(x + 45020, y + 45020), false);
        }
      }
    }
    
    GlobalKeyListener.apply(frame.getContentPane());
    
    new Thread(this::run_normal).start();
    
    SwingUtilities.invokeLater(gd::grabFocus);
  }
  
  private void run_normal() {
    for (;;) {
      try {
        long start = System.nanoTime();
        LogicManager.renderNext();
        SwingUtilities.invokeLater(() -> {
          gd.repaint();
        });
        long sleepTime = 50_000_000 - (System.nanoTime() - start);
        if (sleepTime > 0) {
          Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
        }
      } catch (Exception e) {}
    }
  }
  
  private void test_performance() {
    long[] times = new long[100];
    long r_start = System.currentTimeMillis();
    
    for (int i = 0; i < times.length; i++) {
      try {
        long start = System.nanoTime();
        LogicManager.renderNext();
        SwingUtilities.invokeLater(() -> {
          gd.repaint();
        });
        long sleepTime = 50_000_000 - (System.nanoTime() - start);
        if (sleepTime > 0) {
          Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
        }
        
        times[i] = System.currentTimeMillis();
      } catch (Exception e) {}
    }
    
    long[] diff = new long[times.length - 1];
    for (int i = 0; i < times.length - 1; i++) {
      diff[i] = times[i + 1] - times[i];
    }
    
    long totalError = 0;
    for (long difference : diff) {
      totalError += Math.abs(50 - difference);
    }
    
    long meanError = totalError / diff.length;
    
    System.out.println("time elapsed: " + (System.currentTimeMillis() - r_start) + "ms");
    System.out.println("\ttotal error: " + totalError);
    System.out.println("\tmean error: " + meanError);
    
    System.exit(0);
  }
}
