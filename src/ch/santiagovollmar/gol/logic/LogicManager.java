package ch.santiagovollmar.gol.logic;

import ch.santiagovollmar.gol.gui.ToolBar;

import java.util.*;

/**
 * Handles the game logic
 */
public class LogicManager {
    private static boolean isPaused = false;

    private static volatile boolean updating;

    /**
     * Returns whether or not the game is currently updating
     *
     * @return {@code true} if the game is updating and {@code false} if no update is currently taking place
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

    private static long genPerSec = 0;

    public static void startGPSCounter() {
        Timer t = new Timer(true);
        t.schedule(new TimerTask() {
            private long prev = generation;

            @Override
            public void run() {
                genPerSec = generation - prev;
                prev = generation;
            }
        }, 0, 1000);
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
            generation++;
            updating = false;
        }
    }

    private static volatile long generation = 0;

    public static void resetGeneration() {
        generation = 0;
    }

    public static void resetGeneration(int generation) {
        LogicManager.generation = generation;
    }

    public static long getGeneration() {
        return generation;
    }

    /**
     * Changes the ruleset of the game.
     *
     * @param surviveGrid a list of
     * @param birthGrid
     * @throws IllegalArgumentException
     * @see <a href='https://de.wikipedia.org/wiki/Conways_Spiel_des_Lebens#Abweichende_Regeln'>Wikipedia</a>
     */
    public static void setRule(Iterable<Integer> surviveGrid, Iterable<Integer> birthGrid) throws IllegalArgumentException {
        Arrays.fill(LogicManager.surviveGrid, false);
        Arrays.fill(LogicManager.birthGrid, false);

        StringBuilder builder = new StringBuilder();

        for (int i : surviveGrid) {
            try {
                LogicManager.surviveGrid[i] = true;
                builder.append(i);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Values must be in {[1; 8]}");
            }
        }

        builder.append("S/");

        for (int i : birthGrid) {
            try {
                LogicManager.birthGrid[i] = true;
                builder.append(i);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Values must be in {[1; 8]}");
            }
        }

        builder.append('B');
        rules = builder.toString();
    }

    private static boolean[] surviveGrid = new boolean[8];
    private static boolean[] birthGrid = new boolean[8];
    private static String rules;

    public static String getRules() {
        return rules;
    }

    static {
        setRule(Arrays.asList(2, 3), Arrays.asList(3));
        //setRule(Arrays.asList(1, 3, 5, 7), Arrays.asList( 1, 3, 5, 7));
    }

    /**
     * Clears the cache of already checked neighbors. This method should be invoked after having executed the computation for each {@link Point}
     */
    public static void clearCheckedCache() {
        checkedNeighbors.clear();
    }

    private static Set<Point> checkedNeighbors = Collections.synchronizedSet(new HashSet<>());

    /**
     * Computes the state of a point in the next generation and checks if any neighbors of the given point will be born in the next generation.
     * Changes will be forwarded to the {@link GridManager}.
     *
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
        if (!surviveGrid[neighborAmount]) { // cell dies
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

            if (birthGrid[subNeighborAmount]) { // cell gets born
                GridManager.fill(neighbor, true);
            }
        }
    }

    /**
     * Pauses or unpauses the game based on the given argument.
     * This method directly affects {@link LogicManager#renderNext()}
     * and changes the appearance of the GUI by invoking {@link ToolBar#setPaused(boolean)} on the current {@link ToolBar} instance.
     *
     * @param paused The desired state of the game
     */
    public static synchronized void setPaused(boolean paused) {
        isPaused = paused;
        ToolBar.getCurrentInstance()
                .setPaused(paused);
    }

    /**
     * Returns the current game state
     *
     * @return A boolean stating whether or not the game is paused
     */
    public static synchronized boolean isPaused() {
        return isPaused;
    }

    public static long getGenPerSec() {
        return genPerSec;
    }
}
