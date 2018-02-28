package ch.santiagovollmar.gol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class GridManager {
  private static Set<Point> map = Collections.synchronizedSet(new HashSet<Point>(450, 1f));
  private static LinkedList<Point> fillStash = new LinkedList<Point>();
  private static LinkedList<Point> clearStash = new LinkedList<Point>();
  private static ExecutorService executor = Executors.newFixedThreadPool(8);
  
  /*
   * Set the fetch operation of GameDisplay
   */
  private static Collection<Point> fetchOperation(int x1, int y1, int x2, int y2) {
    // fill all points into a list which are within given bounds
    LinkedList<Point> list = new LinkedList<Point>();
    for (Point e : map) {
      if (e.x >= x1 && e.y >= y1) {
        if (e.x <= x2 && e.y <= y2) {
          list.add(e);
        }
      }
    }
    return list;
  }
  
  static {
    GameDisplay.setFetchOperation(GridManager::fetchOperation);
  }
  
  public static void consume() {
    ArrayList<Callable<Object>> tasks = new ArrayList<Callable<Object>>(map.size());
    
    Iterator<Point> iterator = map.iterator();
    while (iterator.hasNext()) {
      Point point = iterator.next();
      tasks.add(() -> {
        LogicManager.compute(point);
        return null;
      });
    }
    
    try {
      executor.invokeAll(tasks);
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
  public static ArrayList<Point> getNeighborCoordinates(Point p) {
    ArrayList<Point> neighbors = new ArrayList<Point>(8);
    int[][] coordinates =
    {
        {
            p.x + 1, p.y + 1
        },
        {
            p.x + 1, p.y - 1
        },
        {
            p.x + 1, p.y
        },
        {
            p.x - 1, p.y + 1
        },
        {
            p.x - 1, p.y - 1
        },
        {
            p.x - 1, p.y
        },
        {
            p.x, p.y + 1
        },
        {
            p.x, p.y - 1
        }
    };
    
    for (int[] coordinate : coordinates) {
      try {
        Point n = new Point(coordinate[0], coordinate[1]);
        neighbors.add(n);
      } catch (IllegalArgumentException e) {}
    }
    
    return neighbors;
  }
}
