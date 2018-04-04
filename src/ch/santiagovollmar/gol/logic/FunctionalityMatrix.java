package ch.santiagovollmar.gol.logic;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Specifies which functionalities of a GameDisplay should be enabled and allows for conditional execution based on this information
 */
public class FunctionalityMatrix {
    private HashSet<Functionality> functionalities = new HashSet<>();
    private boolean allEnabled;

    /**
     * Checks if a specific functionality is enabled
     * @param functionality The functionality to check
     * @return A boolean specifying whether the functionality is enabled or not
     */
    public boolean isEnabled(Functionality functionality) {
        return functionalities.contains(functionality) | allEnabled;
    }

    /**
     * Executes the given task if the specified functionality is enabled
     * @param functionality The functionality which should be enabled
     * @param task The task to execute
     */
    public void execute(Functionality functionality, Runnable task) {
        if (isEnabled(functionality)) {
            task.run();
        }
    }

    /**
     * Possible functionalities for a GameDisplay
     */
    public enum Functionality {
        DRAW, DRAG, ZOOM, SELECTION, DELETE_ACTION, COPY_PASTE_ACTION, ARROW_ACTIONS
    }

    /**
     * Creates a new matrix which has the given functionalities set to enabled
     * @param functionalities The functionalities to enable
     */
    public FunctionalityMatrix(Functionality... functionalities) {
        this.functionalities.addAll(Arrays.asList(functionalities));
        allEnabled = false;
    }

    /**
     * Creates a new matrix which has all functionalities either enabled or disabled
     * @param enableAll Specifies whether to enable all functionalities or not
     */
    public FunctionalityMatrix(boolean enableAll) {
        allEnabled = enableAll;
    }
}
