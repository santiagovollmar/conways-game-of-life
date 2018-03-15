package ch.santiagovollmar.gol.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MenuBar extends JMenuBar {
  private static MenuBar currentInstance;
  
  public static MenuBar getCurrentInstance() {
    return currentInstance;
  }
  
  // pauseImage = SVGReader.readImage(new
  // File("C:\\Users\\vollmar\\Downloads\\ionicons-2.0.1\\ionicons-2.0.1\\src\\pause"),
  // new Dimension(23, 23));
  // playImage = SVGReader.readImage(new
  // File("C:\\Users\\vollmar\\Downloads\\ionicons-2.0.1\\ionicons-2.0.1\\src\\play"),
  // new Dimension(23, 23));
  
  public MenuBar() {
    currentInstance = this;
    addFileMenu();
  }
  
  private void addFileMenu() {
    // Add menu entries
    JMenu menu = new JMenu("File");
    add(menu);
    JMenuItem save = new JMenuItem("Export");
    save.addActionListener(e -> {
      File file = getFile();
    });
    menu.add(save);
    JMenuItem load = new JMenuItem("Import");
    menu.add(load);
  }
  
  private File getFile() {
    JFileChooser fileChooser = new JFileChooser("/");
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setMultiSelectionEnabled(false);
    FileFilter filter = new FileNameExtensionFilter("Conways game of life scene", "cgls", "cgols");
    fileChooser.setFileFilter(filter);
    fileChooser.addChoosableFileFilter(filter);
    fileChooser.showDialog(Window.getCurrentInstance().getFrame(), "Select");
    
    while (true) {
      File file = null;
      while ((file = fileChooser.getSelectedFile()) == null) {
        int choice = JOptionPane.showConfirmDialog(Window.getCurrentInstance().getFrame(),
            "Do you want to cancel the current operation?", "Abort action", JOptionPane.YES_NO_OPTION);
        if (choice == 0) {
          break;
        }
        fileChooser.showDialog(Window.getCurrentInstance().getFrame(), "Select");
      }
      
      if (file == null) {
        return file;
      }
      
      try {
        file = file.getCanonicalFile();
        return file;
      } catch (IOException e) {
        JOptionPane.showMessageDialog(Window.getCurrentInstance().getGameDisplay(),
            "An unexpected error occured. Please try again.", "Invalid File", JOptionPane.ERROR_MESSAGE);
        continue;
      }
    }
  }
}
