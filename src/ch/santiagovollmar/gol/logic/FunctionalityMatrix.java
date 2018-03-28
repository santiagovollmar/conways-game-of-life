package ch.santiagovollmar.gol.logic;

import java.util.Arrays;
import java.util.HashSet;

public class FunctionalityMatrix {
  private HashSet<Functionality> functionalities = new HashSet<>();
  private boolean allEnabled;
  
  public boolean isEnabled(Functionality functionality) {
    return functionalities.contains(functionality) | allEnabled;
  }
  
  public void execute(Functionality functionality, Runnable task) {
    if (isEnabled(functionality)) {
      task.run();
    }
  }
  
  public enum Functionality {
    DRAW,
    DRAG,
    ZOOM,
    SELECTION,
    DELETE_ACTION,
    COPY_PASTE_ACTION,
    ARROW_ACTIONS
  }
  
  public FunctionalityMatrix(Functionality ...functionalities) {
    this.functionalities.addAll(Arrays.asList(functionalities));
    allEnabled = false;
  }
  
  public FunctionalityMatrix(boolean enableAll) {
    allEnabled = enableAll;
  }
}
