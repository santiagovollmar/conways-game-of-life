package ch.santiagovollmar.gol.gui;

import ch.santiagovollmar.gol.logic.FunctionalityMatrix;
import ch.santiagovollmar.gol.logic.GridManager;
import ch.santiagovollmar.gol.logic.Point;
import ch.santiagovollmar.gol.logic.Snippet;
import ch.santiagovollmar.gol.util.GlobalKeyListener;
import ch.santiagovollmar.gol.util.PropertyManager;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;

public class Window {

    private static Window currentInstance;

    public static Window getCurrentInstance() {
        return currentInstance;
    }

    private JFrame frame;

    private GameDisplay gd;

    private SnippetPreviewBar spb;

    public GameDisplay getGameDisplay() {
        return gd;
    }
    public JFrame getFrame() {
        return frame;
    }
    public SnippetPreviewBar getSnippetPreviewBar() {
        return spb;
    }

    /**
     * Launch the application.
     */
    public static void open() {
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        Window window = new Window();
                        window.frame.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Create the application.
     */
    public Window() {
        currentInstance = this;
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        // set LAF
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Could not find system LAF.\n\tChanging to cross-plattform LAF");
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
                System.err.println("Could not find cross-plattform LAF.\n\texiting...");
            }
        }

        frame = new JFrame();
        frame.setBounds(100, 100, 500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GlobalKeyListener.createListenerSpace("main");
        String[] rgbValues = PropertyManager.get("display.color")
                .split("\\s*\\,\\s*");
        gd = new GameDisplay("main", new FunctionalityMatrix(true),
                Integer.valueOf(PropertyManager.get("display.width")),
                Integer.valueOf(PropertyManager.get("display.height")),
                Integer.valueOf(PropertyManager.get("display.scaling")),
                new Color(Integer.valueOf(rgbValues[0]), Integer.valueOf(rgbValues[1]), Integer.valueOf(rgbValues[2])));

        frame.add(new MenuBar(), BorderLayout.NORTH);
        frame.add(gd, BorderLayout.CENTER);
        frame.add(new ToolBar(), BorderLayout.SOUTH);
        spb = new SnippetPreviewBar();
        frame.add(spb, BorderLayout.EAST);
        frame.pack();

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                if (Math.random() > 0.5) {
                    //GridManager.fill(new Point(x + 45020, y + 45020), false);
                }
            }
        }
    }
}
