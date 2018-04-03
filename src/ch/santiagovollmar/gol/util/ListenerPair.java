package ch.santiagovollmar.gol.util;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class ListenerPair {
    private final Consumer<KeyEvent> consumer;
    private final int[] codes;

    public ListenerPair(Consumer<KeyEvent> consumer, int[] codes) {
        super();
        this.consumer = consumer;
        this.codes = codes;
    }

    public Consumer<KeyEvent> getConsumer() {
        return consumer;
    }

    public int[] getCodes() {
        return codes;
    }
}