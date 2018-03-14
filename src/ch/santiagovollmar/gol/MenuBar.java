package ch.santiagovollmar.gol;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SpringLayout;

public class MenuBar extends JMenuBar {
  private static MenuBar currentInstance;
  
  public static MenuBar getCurrentInstance() {
    return currentInstance;
  }
  
  //pauseImage = SVGReader.readImage(new File("C:\\Users\\vollmar\\Downloads\\ionicons-2.0.1\\ionicons-2.0.1\\src\\pause"), new Dimension(23, 23));
  //playImage = SVGReader.readImage(new File("C:\\Users\\vollmar\\Downloads\\ionicons-2.0.1\\ionicons-2.0.1\\src\\play"), new Dimension(23, 23));
  
  public MenuBar() {
    currentInstance = this;
    addFileMenu();
  }
  
  private void addFileMenu() {
    // Add menu entries
    JMenu menu = new JMenu("File");
    add(menu);
    JMenuItem save = new JMenuItem("Export");
    menu.add(save);
    JMenuItem impord = new JMenuItem("Import");
    menu.add(impord);
  }
}
