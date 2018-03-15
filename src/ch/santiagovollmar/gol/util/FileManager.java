package ch.santiagovollmar.gol.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.SwingUtilities;

import org.apache.bcel.generic.NEW;

import ch.santiagovollmar.gol.gui.Window;
import ch.santiagovollmar.gol.logic.GridManager;
import ch.santiagovollmar.gol.logic.LogicManager;
import ch.santiagovollmar.gol.logic.Point;

public class FileManager {
  public static void put(File file) throws IOException {
    // ensure game is paused
    while (LogicManager.isUpdating()) {} // wait until current update has finished
    
    LogicManager.setPaused(true);
    
    // open output stream
    ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
    
    // write viewport data
    out.writeObject(Window.getCurrentInstance().getGameDisplay().getViewport());
    out.writeObject(new Integer(Window.getCurrentInstance().getGameDisplay().getScaling()));
    
    // write map data
    Point[] data = GridManager.dumpScene();
    out.writeObject(data);
    out.flush();
    out.close();
  }
  
  public static void load(File file) throws IOException {
    // ensure game is paused
    while (LogicManager.isUpdating()) {} // wait until current update has finished
    
    LogicManager.setPaused(true);
    
    // clear current scene
    GridManager.clearScene();
    
    // open input stream
    ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
    
    try {
      // load viewport data
      Point viewport = (Point) in.readObject();
      int scaling = (Integer) in.readObject();
      Window.getCurrentInstance().getGameDisplay().setViewport(viewport);
      Window.getCurrentInstance().getGameDisplay().setScaling(scaling);
      SwingUtilities.invokeLater(() -> {
        Window.getCurrentInstance().getGameDisplay().revalidate();
        Window.getCurrentInstance().getGameDisplay().repaint();
      });
      
      // load point data
      Point[] data;
      data = (Point[]) in.readObject();
      GridManager.loadScene(data);
    } catch (ClassNotFoundException e) {
      in.close();
      throw new IOException("File contains illegal data");
    }
    in.close();
  }
}
