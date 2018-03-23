package ch.santiagovollmar.gol.util;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class GlobalKeyListener implements KeyListener {
  public static final KeyListener KEY_LISTENER = new GlobalKeyListener(); 
  
  public static void apply(Component parent) {
    if (parent instanceof JComponent) {
      for (Component component : ((JComponent) parent).getComponents()) {
        apply(component);
      }
    }
    
    SwingUtilities.invokeLater(() -> parent.addKeyListener(KEY_LISTENER));
  }
  
  public enum KeyListenerType {
    PRESSED(new LinkedList<Consumer<KeyEvent>>(), new LinkedList<int[]>()),
    RELEASED(new LinkedList<Consumer<KeyEvent>>(), new LinkedList<int[]>()),
    TYPED(new LinkedList<Consumer<KeyEvent>>(), new LinkedList<int[]>());
    
    protected final LinkedList<Consumer<KeyEvent>> listeners;
    protected final LinkedList<int[]> codes;
    
    private KeyListenerType(LinkedList<Consumer<KeyEvent>> list, LinkedList<int[]> codes) {
      listeners = list;
      this.codes = codes;
    }
  }
  
  public static void attach(KeyListenerType type, Consumer<KeyEvent> action, int ...codes) {
    type.listeners.add(action);
    type.codes.add(codes);
  }
  
  @Override
  public void keyPressed(KeyEvent e) {
    evaluateType(KeyListenerType.PRESSED, e);
  }

  @Override
  public void keyReleased(KeyEvent e) {
    evaluateType(KeyListenerType.RELEASED, e);
  }

  @Override
  public void keyTyped(KeyEvent e) {
    evaluateType(KeyListenerType.TYPED, e);
  }
  
  private void evaluateType(KeyListenerType type, KeyEvent e) {
    for (int i = 0; i < type.listeners.size(); i++) {
      for (int code : type.codes.get(i)) {
        if (code == e.getKeyCode()) {
          type.listeners.get(i).accept(e);
        }
      }
    }
  }
  
}
