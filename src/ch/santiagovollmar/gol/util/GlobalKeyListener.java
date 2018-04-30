package ch.santiagovollmar.gol.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Consumer;

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
        GlobalKeyListener listener = KEY_LISTENERS.get(listenerSpace);
        listener.children.add(parent);
        SwingUtilities.invokeLater(() -> parent.addKeyListener(listener));
    }

    public static void attach(String listenerSpace, KeyListenerType type, Consumer<KeyEvent> action, int... codes) {
        KEY_LISTENERS.get(listenerSpace).eventHandlers.get(type)
                .add(new ListenerPair(action, codes));
    }

    public static void createListenerSpace(String listenerSpace) {
        KEY_LISTENERS.put(listenerSpace, new GlobalKeyListener());
    }

    public static void freeListenerSpace(String listenerSpace) {
        // get listener
        GlobalKeyListener listener = KEY_LISTENERS.get(listenerSpace);
        if (listener == null) {
            return;
        }

        // remove listener from children
        for (Component child : listener.children) {
            SwingUtilities.invokeLater(() -> child.removeKeyListener(listener));
        }

        // remove listener from listenerspaces
        KEY_LISTENERS.remove(listenerSpace);
    }

    public enum KeyListenerType {
        PRESSED, RELEASED, TYPED;
    }

    private HashMap<KeyListenerType, LinkedList<ListenerPair>> eventHandlers;
    private ArrayList<Component> children = new ArrayList<>();

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
            for (int code : listeners.get(i)
                    .getCodes()) {
                if (code == e.getKeyCode()) {
                    listeners.get(i)
                            .getConsumer()
                            .accept(e);
                }
            }
        }
    }

}
