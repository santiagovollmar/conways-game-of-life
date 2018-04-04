package ch.santiagovollmar.gol;

import ch.santiagovollmar.gol.gui.Window;
import ch.santiagovollmar.gol.logic.LogicManager;
import ch.santiagovollmar.gol.util.GlobalKeyListener;
import ch.santiagovollmar.gol.util.PropertyManager;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Executor;

public class Main {
    public static void main(String[] arguments) {
        long start = System.currentTimeMillis();
        PropertyManager.readProperties();
        Window.open();

        new Thread(Main::run_normal).start();

        GlobalKeyListener.apply("main", Window.getCurrentInstance()
                .getFrame()
                .getContentPane());

        SwingUtilities.invokeLater(Window.getCurrentInstance()
                .getGameDisplay()::grabFocus);

        Logger logger = LogManager.getLogger("main");
        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
        logger.fatal("fatal");
    }

    @SuppressWarnings("unused")
    private static void run_normal() {
        for (; ; ) {
            try {
                long start = System.nanoTime();
                LogicManager.renderNext();
                long sleepTime = LogicManager.sleepTime - (System.nanoTime() - start);
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
                }
            } catch (Exception e) {
            }
        }
    }

    @SuppressWarnings("unused")
    private static void test_performance() {
        long[] times = new long[100];
        long r_start = System.currentTimeMillis();

        for (int i = 0; i < times.length; i++) {
            try {
                long start = System.nanoTime();
                LogicManager.renderNext();
                SwingUtilities.invokeLater(Window.getCurrentInstance()
                        .getGameDisplay()::repaint);
                long sleepTime = 50_000_000 - (System.nanoTime() - start);
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
                }

                times[i] = System.currentTimeMillis();
            } catch (Exception e) {
            }
        }

        long[] diff = new long[times.length - 1];
        for (int i = 0; i < times.length - 1; i++) {
            diff[i] = times[i + 1] - times[i];
        }

        long totalError = 0;
        for (long difference : diff) {
            totalError += Math.abs(50 - difference);
        }

        long meanError = totalError / diff.length;

        System.out.println("time elapsed: " + (System.currentTimeMillis() - r_start) + "ms");
        System.out.println("\ttotal error: " + totalError);
        System.out.println("\tmean error: " + meanError);

        System.exit(0);
    }
}
