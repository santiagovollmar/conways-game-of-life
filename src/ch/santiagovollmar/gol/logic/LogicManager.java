package ch.santiagovollmar.gol.logic;

import ch.santiagovollmar.gol.gui.ToolBar;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles the game logic
 */
public class LogicManager {
    private static boolean isPaused = false;

    private static volatile boolean updating;

    /**
     * Returns whether or not the game is currently updating
     *
     * @return
     */
    public static boolean isUpdating() {
        return updating;
    }

    public static volatile long sleepTime;

    static {
        setPercentageSleepTime(50);
    }

    /**
     * Sets the time to sleep between two generations. The sleep time is calculated as follows:
     * <code> 1000ms * (percentage / 100d)</code>
     *
     * @param percentage A number between 0 and 100
     */
    public static void setPercentageSleepTime(int percentage) {
        // ensure bounds
        percentage = percentage > 100 ? 100 : percentage;
        percentage = percentage < 0 ? 0 : percentage;

        sleepTime = ((long) (1_000_000_000 * (percentage / 100d)));
        if (sleepTime < 1_000_000) {
            sleepTime = 1_000_000;
        }
    }

    /**
     * Invokes {@link GridManager#consume()} and {@link GridManager#update()} to move the game to the next generation.
     * If the game state is paused, the above mentioned will be skipped
     */
    public static synchronized void renderNext() {
        if (!isPaused) {
            updating = true;
            GridManager.consume();
            GridManager.update();
            updating = false;
        }
    }

    private static Set<Point> checkedNeighbors = Collections.synchronizedSet(new HashSet<Point>());

    /**
     * Clears the cache of already checked neighbors. This method should be invoked after having executed the computation for each {@link Point}
     */
    public static void clearCheckedCache() {
        checkedNeighbors.clear();
    }

    /**
     * Computes the state of a point in the next generation and checks if any neighbors of the given point will be born in the next generation.
     * Changes will be forwarded to the {@link GridManager}.
     * @param point The point for which to compute the above mentioned things
     */
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

    /**
     * Pauses or unpauses the game based on the given argument.
     * This method directly affects {@link LogicManager#renderNext()}
     * and changes the appearance of the GUI by invoking {@link ToolBar#setPaused(boolean)} on the current {@link ToolBar} instance.
     * @param paused
     */
    public static synchronized void setPaused(boolean paused) {
        isPaused = paused;
        ToolBar.getCurrentInstance()
                .setPaused(paused);
    }

    /**
     * Returns the current game state
     * @return A boolean stating whether or not the game is paused
     */
    public static synchronized boolean isPaused() {
        return isPaused;
    }
}
