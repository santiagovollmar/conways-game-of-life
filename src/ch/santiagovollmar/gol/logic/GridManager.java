package ch.santiagovollmar.gol.logic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.santiagovollmar.gol.gui.GameDisplay;

public class GridManager {
  public static Set<Point> map = Collections.synchronizedSet(new HashSet<Point>(10000, 1f));
  private static ArrayDeque<Point> fillStash = new ArrayDeque<Point>();
  private static ArrayDeque<Point> clearStash = new ArrayDeque<Point>();
  private static ExecutorService executor = Executors.newFixedThreadPool(16);
  
  public static void clearScene() {
    update(); // clear buffers
    map.clear();
  }
  
  /**
   * 
   * @return
   */
  public static Point[] dumpScene() {
    // ensure game is paused
    while (LogicManager.isUpdating()) {} // wait for current update cycle to complete
    
    LogicManager.setPaused(true);
    
    // return data contained in map
    return map.toArray(new Point[0]);
  }
  
  /**
   * 
   * @param data
   */
  public static void loadScene(Point[] data) {
    // ensure game is paused
    while (LogicManager.isUpdating()) {} // wait for current update cycle to complete
    
    LogicManager.setPaused(true);
    
    // clear buffers
    update();
    LogicManager.clearCheckedCache();
    
    // clear map
    map.clear();
    
    // insert new data
    map.addAll(Arrays.asList(data));
  }
  
  /*
   * Set the fetch operation of GameDisplay
   */
  private static synchronized Collection<Point> fetchOperation(int x1, int y1, int x2, int y2) {
    // fill all points into a list which are within given bounds
    LinkedList<Point> list = new LinkedList<Point>();
    synchronized (map) {
      for (Point e : map) {
        if (e.x >= x1 && e.y >= y1) {
          if (e.x <= x2 && e.y <= y2) {
            list.add(e);
          }
        }
      }
    }
    return list;
  }
  
  static {
    GameDisplay.setDefaultFetchOperation(GridManager::fetchOperation);
  }
  
  /**
   * Invokes calculation of each point
   */
  public static void consume() {
    ArrayList<Callable<Object>> tasks = new ArrayList<Callable<Object>>(map.size());
    
    Iterator<Point> iterator = map.iterator();
    while (iterator.hasNext()) {
      Point point = iterator.next();
      tasks.add(point);
    }
    
    try {
      executor.invokeAll(tasks);
      LogicManager.clearCheckedCache();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Applies the stashed changes to the grid map and clears the stash
   */
  public static void update() {
    synchronized (fillStash) {
      fill(fillStash, false);
      fillStash.clear();
    }
    
    synchronized (clearStash) {
      clear(clearStash, false);
      clearStash.clear();
    }
  }
  
  /**
   * Changes the state of a cell to 'alive'. If 'stashed' is true, the changes are
   * held back until 'update' is called
   * 
   * @param point
   * @param stashed
   */
  public static void fill(Point point, boolean stashed) {
    if (stashed) {
      synchronized (fillStash) {
        if (!fillStash.contains(point)) {
          fillStash.add(point);
        }
      }
    } else {
      map.add(point);
    }
  }
  
  /**
   * Changes the state of multiple cells to 'alive'. If 'stashed' is true, the
   * changes are held back until 'update' is called
   * 
   * @param points
   * @param stashed
   */
  public static void fill(Collection<Point> points, boolean stashed) {
    if (stashed) {
      synchronized (fillStash) {
        for (Point point : points) {
          if (!fillStash.contains(point)) {
            fillStash.add(point);
          }
        }
      }
    } else {
      map.addAll(points);
    }
  }
  
  /**
   * Changes the state of a cell to 'dead'. If 'stashed' is true, the changes are
   * held back until 'update' is called
   * 
   * @param point
   * @param stashed
   */
  public static void clear(Point point, boolean stashed) {
    if (stashed) {
      synchronized (clearStash) {
        if (!clearStash.contains(point)) {
          clearStash.add(point);
        }
      }
    } else {
      map.remove(point);
    }
  }
  
  /**
   * Changes the state of multiple cells to 'dead'. If 'stashed' is true, the
   * changes are held back until 'update' is called
   * 
   * @param points
   * @param stashed
   */
  public static void clear(Collection<Point> points, boolean stashed) {
    if (stashed) {
      synchronized (clearStash) {
        for (Point point : points) {
          if (!clearStash.contains(point)) {
            clearStash.add(point);
          }
        }
      }
    } else {
      map.removeAll(points);
    }
  }
  
  /**
   * Returns true, if the given cell is alive
   * 
   * @param point
   * @return
   */
  public static boolean isAlive(Point point) {
    return map.contains(point);
  }
  
  /**
   * Returns all valid and directly adjacent coordinates of a cell
   * 
   * @param p
   * @return
   */
  public static Point[] getNeighborCoordinates(Point p) {
    return new Point[] {
        new Point(p.x+1, p.y+1),
        new Point(p.x+1, p.y-1),
        new Point(p.x+1, p.y),
        new Point(p.x-1, p.y+1),
        new Point(p.x-1, p.y-1),
        new Point(p.x-1, p.y),
        new Point(p.x, p.y+1),
        new Point(p.x, p.y-1)
    };
  }
}
