package ch.santiagovollmar.gol.util;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class GlobalKeyListener implements KeyListener {
    private static final HashMap<String, GlobalKeyListener> KEY_LISTENERS = new HashMap<>();
    static {
        KEY_LISTENERS.put("dump", new GlobalKeyListener());
    }

    public static void apply(String listenerSpace, Component parent) {
        if (parent instanceof JComponent) {
            for (Component component : ((JComponent) parent).getComponents()) {
                apply(listenerSpace, component);
            }
        }

        // apply KeyListener from KeyListenerSpace to component
        SwingUtilities.invokeLater(() -> parent.addKeyListener(KEY_LISTENERS.get(listenerSpace)));
    }

    public static void attach(String listenerSpace, KeyListenerType type, Consumer<KeyEvent> action, int... codes) {
        KEY_LISTENERS.get(listenerSpace).eventHandlers.get(type).add(new ListenerPair(action, codes));
    }

    public static void createListenerSpace(String listenerSpace) {
        KEY_LISTENERS.put(listenerSpace, new GlobalKeyListener());
    }

    public enum KeyListenerType {
        PRESSED,
        RELEASED,
        TYPED;
    }

    private HashMap<KeyListenerType, LinkedList<ListenerPair>> eventHandlers;

    public GlobalKeyListener() {
        this.eventHandlers = new HashMap<>();

        for (KeyListenerType type : KeyListenerType.values()) {
            eventHandlers.put(type, new LinkedList<>());
        }
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
        LinkedList<ListenerPair> listeners = eventHandlers.get(type);

        for (int i = 0; i < listeners.size(); i++) {
            for (int code : listeners.get(i).getCodes()) {
                if (code == e.getKeyCode()) {
                    listeners.get(i).getConsumer().accept(e);
                }
            }
        }
    }

}
