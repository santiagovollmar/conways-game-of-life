package ch.santiagovollmar.gol.gui;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import ch.santiagovollmar.gol.logic.GridManager;
import ch.santiagovollmar.gol.logic.LogicManager;

public class ToolBar extends JToolBar {
  private static ToolBar currentInstance;
  
  public static ToolBar getCurrentInstance() {
    return currentInstance;
  }
  
  public void setPaused(boolean paused) {
    pauseButton.setIcon(paused ? playIcon : pauseIcon);
    SwingUtilities.invokeLater(pauseButton::repaint);
  }
  
  // pauseImage = SVGReader.readImage(new
  // File("C:\\Users\\vollmar\\Downloads\\ionicons-2.0.1\\ionicons-2.0.1\\src\\pause"),
  // new Dimension(23, 23));
  // playImage = SVGReader.readImage(new
  // File("C:\\Users\\vollmar\\Downloads\\ionicons-2.0.1\\ionicons-2.0.1\\src\\play"),
  // new Dimension(23, 23));
  
  private JButton pauseButton;
  private JDialog colorChooserDialog;
  private JColorChooser colorChooser;
  private boolean previousPauseState;
  
  private ImageIcon pauseIcon = new ImageIcon(getClass().getResource("pictures/pause.png"));
  private ImageIcon playIcon = new ImageIcon(getClass().getResource("pictures/play.png"));
  
  public ToolBar() {
    currentInstance = this;
    setBackground(Color.WHITE);
    addPauseButton();
    addColorChooser();
    addResetButton();
  }
  
  private void addResetButton() {
    JButton resetButton = new JButton(new ImageIcon(getClass().getResource("pictures/refresh.png")));
    resetButton.setBackground(Color.WHITE);
    resetButton.addActionListener(e -> {
      boolean pausedState = LogicManager.isPaused();
      LogicManager.setPaused(true);
      int choice = JOptionPane.showConfirmDialog(Window.getCurrentInstance().getFrame(),
          "<html>Clearing the current scene disposes all the information associated with it.<br />Do you want to continue?</html>",
          "Confirm Action", JOptionPane.YES_NO_OPTION);
      if (choice == 0) {
        GridManager.update();
        GridManager.clearScene();
      }
      LogicManager.setPaused(pausedState);
    });
    add(resetButton);
  }
  
  private void addColorChooser() {
    this.colorChooserDialog = new JDialog(Window.getCurrentInstance().getFrame(), "Choose new color", true);
    colorChooserDialog.setBounds(300, 300, 700, 400);
    colorChooserDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    SpringLayout layout = new SpringLayout();
    colorChooserDialog.getContentPane().setLayout(layout);
    
    this.colorChooser = new JColorChooser(Window.getCurrentInstance().getGameDisplay().getFillColor());
    layout.putConstraint(SpringLayout.NORTH, colorChooser, 0, SpringLayout.NORTH, colorChooserDialog.getContentPane());
    layout.putConstraint(SpringLayout.EAST, colorChooser, 0, SpringLayout.EAST, colorChooserDialog.getContentPane());
    layout.putConstraint(SpringLayout.WEST, colorChooser, 0, SpringLayout.WEST, colorChooserDialog.getContentPane());
    this.colorChooserDialog.add(this.colorChooser);
    
    JButton okButton = new JButton("apply");
    okButton.addActionListener(e -> {
      Window.getCurrentInstance().getGameDisplay().setFillColor(colorChooser.getColor());
    });
    layout.putConstraint(SpringLayout.SOUTH, okButton, -10, SpringLayout.SOUTH, colorChooserDialog.getContentPane());
    layout.putConstraint(SpringLayout.EAST, okButton, -10, SpringLayout.EAST, colorChooserDialog.getContentPane());
    layout.putConstraint(SpringLayout.SOUTH, colorChooser, -10, SpringLayout.NORTH, okButton);
    colorChooserDialog.add(okButton);
    
    JButton cancelButton = new JButton("exit");
    cancelButton.addActionListener(e -> {
      colorChooserDialog.setVisible(false);
      LogicManager.setPaused(previousPauseState);
    });
    layout.putConstraint(SpringLayout.SOUTH, cancelButton, -10, SpringLayout.SOUTH,
        colorChooserDialog.getContentPane());
    layout.putConstraint(SpringLayout.EAST, cancelButton, -10, SpringLayout.WEST, okButton);
    colorChooserDialog.add(cancelButton);
    
    JButton colorChooserOpener = new JButton(new ImageIcon(getClass().getResource("pictures/palette.PNG")));
    colorChooserOpener.setBackground(Color.WHITE);
    colorChooserOpener.setToolTipText("Change the color of the living cells");
    colorChooserOpener.addActionListener(e -> {
      previousPauseState = LogicManager.isPaused();
      LogicManager.setPaused(true);
      colorChooserDialog.setVisible(true);
    });
    add(colorChooserOpener);
  }
  
  private void addPauseButton() {
    pauseButton = new JButton(pauseIcon);
    pauseButton.setBackground(Color.WHITE);
    add(pauseButton);
    pauseButton.addActionListener((e) -> {
      boolean paused = LogicManager.isPaused();
      
      if (!paused) {
        pauseButton.setIcon(playIcon);
      } else {
        pauseButton.setIcon(pauseIcon);
      }
      SwingUtilities.invokeLater(pauseButton::repaint);
      LogicManager.setPaused(!paused);
    });
  }
}
