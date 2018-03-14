package ch.santiagovollmar.gol;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
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
  private JDialog colorChooserDialog;
  private JColorChooser colorChooser;
  
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
    
    // color chooser
    this.colorChooserDialog = new JDialog(Window.getCurrentInstance().getFrame(), "Choose new color", true);
    colorChooserDialog.setBounds(300, 300, 700, 400);
    this.colorChooser = new JColorChooser(Color.BLUE);
    this.colorChooserDialog.add(this.colorChooser);
    
    JButton colorChooserOpener = new JButton("Change color");
    colorChooserOpener.addActionListener(e -> {
      LogicManager.setPaused(true);
      colorChooserDialog.setVisible(true);
    });
    add(colorChooserOpener);
  }
}
