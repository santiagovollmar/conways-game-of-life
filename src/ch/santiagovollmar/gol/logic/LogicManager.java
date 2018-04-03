package ch.santiagovollmar.gol.logic;

import ch.santiagovollmar.gol.gui.ToolBar;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LogicManager {
    private static boolean isPaused = false;

    private static volatile boolean updating;

    public static boolean isUpdating() {
        return updating;
    }

    public static volatile long sleepTime;

    static {setPercentageSleepTime(50);}

    public static void setPercentageSleepTime(int percentage) {
        // ensure bounds
        percentage = percentage > 100 ? 100 : percentage;
        percentage = percentage < 0 ? 0 : percentage;

        sleepTime = ((long) (1_000_000_000 * (percentage / 100d)));
        if (sleepTime < 1_000_000) {
            sleepTime = 1_000_000;
        }
    }

    public static synchronized void renderNext() {
        if (!isPaused) {
            updating = true;
            GridManager.consume();
            GridManager.update();
            updating = false;
        }
    }

    private static Set<Point> checkedNeighbors = Collections.synchronizedSet(new HashSet<Point>());

    public static void clearCheckedCache() {
        checkedNeighbors.clear();
    }


    public static void compute(Point point) {
        // get amount of neighbors
        int neighborAmount = 0;
        Point[] neighbors = GridManager.getNeighborCoordinates(point);
        for (Point neighbor : neighbors) {
            neighborAmount += GridManager.isAlive(neighbor) ? 1 : 0;
        }

        // check if cell survives
        if (neighborAmount < 2 || neighborAmount > 3) { // cell dies
            GridManager.clear(point, true);
        }

        // check if any near, dead cells get born
        for (Point neighbor : neighbors) {
            if (checkedNeighbors.contains(neighbor)) {
                continue;
            }
            checkedNeighbors.add(neighbor);

            // get amount of neighbors
            int subNeighborAmount = 0;
            Point[] subNeighbors = GridManager.getNeighborCoordinates(neighbor);
            for (Point subNeighbor : subNeighbors) {
                subNeighborAmount += GridManager.isAlive(subNeighbor) ? 1 : 0;
            }

            if (subNeighborAmount == 3) { // cell gets born
                GridManager.fill(neighbor, true);
            }
        }
    }

    public static synchronized void setPaused(boolean paused) {
        isPaused = paused;
        ToolBar.getCurrentInstance()
                .setPaused(paused);
    }

    public static synchronized boolean isPaused() {
        return isPaused;
    }
}
