package ch.santiagovollmar.gol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;

public class FileManager {
  public static void put(File file) throws IOException {
    // ensure game is paused
    LogicManager.setPaused(true);
    
    // open output streams
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
    out.writeObject(GridManager.);
  }
  
  public static HashSet<Point> load(File file) {
    return null;
  }
}
