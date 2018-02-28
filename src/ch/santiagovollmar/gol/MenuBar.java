package ch.santiagovollmar.gol;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBar extends JMenuBar {
  private boolean runningState = false;
  
  private static MenuBar currentInstance;
  public static MenuBar getCurrentInstance() {
    return currentInstance;
  }
  
  public void setPaused(boolean paused) {
    pauseButton.setText(paused ? "play": "draw");
    pauseButton.repaint();
  }
  
  private JButton pauseButton;
  
  public MenuBar() {
    currentInstance = this;
    
    /*JMenu menu = new JMenu("play");
    add(menu);*/
    pauseButton = new JButton("draw");
    add(pauseButton);
    pauseButton.addActionListener((e) -> {
      boolean paused = LogicManager.isPaused();
      
      if (!paused) {
        pauseButton.setText("play");
      } else {
        pauseButton.setText("draw");
      }
      
      LogicManager.setPaused(!paused);
    });
  }
}
