package ch.santiagovollmar.gol.gui;

import java.awt.Graphics;

import javax.swing.JPanel;

import ch.santiagovollmar.gol.logic.Snippet;

public class SnippetPreview extends JPanel {
  private final Snippet snippet;

  public SnippetPreview(Snippet snippet) {
    this.snippet = snippet;
  }
  
  /*
   * GUI stuff
   */
  @Override
  protected void paintComponent(Graphics graphics) {
    // paint border
    getBorder().paintBorder(this, graphics, getBounds().x, getBounds().y, getBounds().width, getBounds().height);
  }
}
