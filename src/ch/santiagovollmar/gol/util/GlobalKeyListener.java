package ch.santiagovollmar.gol.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class GlobalKeyListener implements KeyListener {
    private static final HashMap<String, GlobalKeyListener> KEY_LISTENERS = new HashMap<>();

    static {
        KEY_LISTENERS.put("dump", new GlobalKeyListener());
    }

    /**
     * Reapplies a specific listener space to a component if it's content was changed.
     * @param listenerSpace
     * @param parent
     */
    public static void reapply(String listenerSpace, Component parent) {
        if (parent instanceof JComponent) {
            for (Component component : ((JComponent) parent).getComponents()) {
                reapply(listenerSpace, component);
            }
        }

        // apply KeyListener from KeyListenerSpace to component
        GlobalKeyListener listener = KEY_LISTENERS.get(listenerSpace);
        if (!Arrays.asList(parent.getKeyListeners()).contains(listener)) {
            listener.children.add(parent);
            SwingUtilities.invokeLater(() -> parent.addKeyListener(listener));
        }
    }


    /**
     * Applies a specific listener space to a Swing component and all of it's children.
     * If new children are added to the component, the listener space won't be applied to them.
     * Should the content of the Component have changed on may use {@link #reapply(String, Component)}.
     * @param listenerSpace
     * @param parent
     */
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

    /**
     * Attaches a new Listener to a specific listener space. This means, that all events in the listener space which match the provided keys will trigger this event.
     * @param listenerSpace
     * @param type
     * @param action
     * @param codes
     */
    public static void attach(String listenerSpace, KeyListenerType type, Consumer<KeyEvent> action, int... codes) {
        KEY_LISTENERS.get(listenerSpace).eventHandlers.get(type)
                .add(new ListenerPair(action, codes));
    }

    /**
     * Creates a new listener space for later use.
     * Listener spaces can be seen as seperate global key listeners.
     * @param listenerSpace
     */
    public static void createListenerSpace(String listenerSpace) {
        KEY_LISTENERS.put(listenerSpace, new GlobalKeyListener());
    }

    /**
     * Removes everything that is associated with a specific key space.
     * @param listenerSpace
     */
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

    /**
     * Specifies the kind of key event
     */
    public enum KeyListenerType {
        PRESSED, RELEASED, TYPED;
    }

    private HashMap<KeyListenerType, LinkedList<ListenerPair>> eventHandlers;
    private ArrayList<Component> children = new ArrayList<>();

    private GlobalKeyListener() {
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

        for (ListenerPair listener : listeners) {
            for (int code : listener
                    .getCodes()) {
                if (code == e.getKeyCode()) {
                    listener
                            .getConsumer()
                            .accept(e);
                    break;
                }
            }
        }
    }

}
