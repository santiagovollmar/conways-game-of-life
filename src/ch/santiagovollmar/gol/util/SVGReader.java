package ch.santiagovollmar.gol.util;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.apache.batik.swing.JSVGCanvas;

public class SVGReader {
  public static JSVGCanvas readImage(File file, JComponent size) {
    JSVGCanvas canvas = new JSVGCanvas();
    canvas.setURI(file.toURI().toString());
    SwingUtilities.invokeLater(() -> {
      canvas.setPreferredSize(size.getPreferredSize());
      canvas.setSize(size.getSize());
      canvas.setMinimumSize(size.getSize());
      canvas.setMaximumSize(size.getSize());
      canvas.setMySize(size.getSize());
    });
    return canvas;
  }
  
  public static JSVGCanvas readImage(File file, Dimension size) {
    JSVGCanvas canvas = new JSVGCanvas();
    canvas.setURI(file.toURI().toString());
    SwingUtilities.invokeLater(() -> {
      canvas.setPreferredSize(size);
      canvas.setSize(size);
      canvas.setMinimumSize(size);
      canvas.setMaximumSize(size);
      canvas.setMySize(size);
    });
    return canvas;
  }
  
  @Deprecated
  public static Image readImage(File file, int width, int height) {
    System.setProperty("java.awt.headless", "false");
    JSVGCanvas canvas = new JSVGCanvas();
    
    Dimension size = new Dimension(width, height);
    canvas.setPreferredSize(size);
    canvas.setSize(size);
    canvas.setMinimumSize(size);
    canvas.setMaximumSize(size);
    canvas.setMySize(size);
    
    canvas.setURI(file.toURI().toString());
    return canvas.createImage(width, height);
  }
}
