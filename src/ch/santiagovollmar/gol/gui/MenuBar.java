package ch.santiagovollmar.gol.gui;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import ch.santiagovollmar.gol.logic.LogicManager;
import ch.santiagovollmar.gol.util.FileManager;
import ch.santiagovollmar.gol.util.PropertyManager;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {
  private static MenuBar currentInstance;
  
  public static MenuBar getCurrentInstance() {
    return currentInstance;
  }
  
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
      boolean pausedState = LogicManager.isPaused();
      LogicManager.setPaused(true);
      File file = getFile(true);
      
      if (file != null) {
        new Thread(() -> {
          try {
            FileManager.put(file);
            SwingUtilities.invokeLater(() -> {
              JOptionPane.showMessageDialog(Window.getCurrentInstance().getFrame(),
                  "Current scene has been saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
              LogicManager.setPaused(pausedState);
            });
          } catch (IOException e1) {
            SwingUtilities.invokeLater(() -> {
              JOptionPane.showMessageDialog(Window.getCurrentInstance().getFrame(),
                  "Could not save current Scene: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            });
            e1.printStackTrace();
          }
        }).start();
      }
    });
    menu.add(save);
    JMenuItem load = new JMenuItem("Import");
    load.addActionListener(e -> {
      LogicManager.setPaused(true);
      File file = getFile(false);
      
      if (file != null) {
        new Thread(() -> {
          try {
            FileManager.load(file);
            SwingUtilities.invokeLater(() -> {
              JOptionPane.showMessageDialog(Window.getCurrentInstance().getFrame(),
                  "New scene has been loaded successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            });
          } catch (IOException e1) {
            SwingUtilities.invokeLater(() -> {
              JOptionPane.showMessageDialog(Window.getCurrentInstance().getFrame(),
                  "Could not load new Scene: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            });
            e1.printStackTrace();
          }
        }).start();
      }
    });
    menu.add(load);
  }
  
  private File getFile(boolean ensureExtension) {
    JFileChooser fileChooser = new JFileChooser(PropertyManager.get("path"));
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setMultiSelectionEnabled(false);
    FileFilter filter = new FileNameExtensionFilter("Conways game of life scene [.cgls, .cgols]", "cgls", "cgols");
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
        
        if (ensureExtension) {
          String[] fileNameParts = file.getName().split("\\.");
          if (!"cgls|||cgols".contains(fileNameParts[fileNameParts.length - 1])) {
            file = new File(file.getAbsolutePath() + ".cgls");
          }
        }
        
        try {
          PropertyManager.set("path", fileChooser.getCurrentDirectory().getAbsolutePath());
          PropertyManager.store();
        } catch (IOException e) {}
        return file;
      } catch (IOException e) {
        JOptionPane.showMessageDialog(Window.getCurrentInstance().getGameDisplay(),
            "An unexpected error occured. Please try again.", "Invalid File", JOptionPane.ERROR_MESSAGE);
        continue;
      }
    }
  }
}
