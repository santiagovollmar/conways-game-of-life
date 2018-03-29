package ch.santiagovollmar.gol;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ch.santiagovollmar.gol.gui.SnippetPreview;
import ch.santiagovollmar.gol.gui.Window;
import ch.santiagovollmar.gol.logic.LogicManager;
import ch.santiagovollmar.gol.logic.Point;
import ch.santiagovollmar.gol.logic.Snippet;
import ch.santiagovollmar.gol.util.GlobalKeyListener;
import ch.santiagovollmar.gol.util.PropertyManager;

public class Main {
  public static void main(String[] arguments) {
    PropertyManager.readProperties();
    Window.open();
    
    JFrame frame = new JFrame("test");
    frame.setBounds(new Rectangle(10, 10, 100, 100));
    frame.setVisible(true);
    HashSet<Point> scene = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      for (int j = i % 2; j < 10; j += 2) {
        scene.add(new Point(j, i));
      }
    }
    
    frame.getContentPane().add(new SnippetPreview(new Snippet(scene, "ss", "sss")), BorderLayout.CENTER);
    
    new Thread(Main::run_normal).start();
    GlobalKeyListener.createListenerSpace("main");
    GlobalKeyListener.apply("main", Window.getCurrentInstance().getFrame().getContentPane());
    
    GlobalKeyListener.createListenerSpace(frame.toString());
    GlobalKeyListener.apply(frame.toString(), frame);
    
    SwingUtilities.invokeLater(Window.getCurrentInstance().getGameDisplay()::grabFocus);
  }
  
  @SuppressWarnings("unused")
  private static void run_normal() {
    for (;;) {
      try {
        long start = System.nanoTime();
        LogicManager.renderNext();
        long sleepTime = LogicManager.sleepTime - (System.nanoTime() - start);
        if (sleepTime > 0) {
          Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
        }
      } catch (Exception e) {}
    }
  }
  
  @SuppressWarnings("unused")
  private static void test_performance() {
    long[] times = new long[100];
    long r_start = System.currentTimeMillis();
    
    for (int i = 0; i < times.length; i++) {
      try {
        long start = System.nanoTime();
        LogicManager.renderNext();
        SwingUtilities.invokeLater(Window.getCurrentInstance().getGameDisplay()::repaint);
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
