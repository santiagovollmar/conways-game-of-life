package ch.santiagovollmar.gol.gui;

import ch.santiagovollmar.gol.logic.FunctionalityMatrix;
import ch.santiagovollmar.gol.logic.Point;
import ch.santiagovollmar.gol.logic.Snippet;
import ch.santiagovollmar.gol.util.PropertyManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SnippetPreview extends JButton {
    private final Snippet snippet;
    private final GameDisplay gd;

    private final Point min;
    private final Point max;

    private int snippetHeight;
    private int snippetWidth;
    private int scaling;

    private boolean showDescription;

    public SnippetPreview(Snippet snippet) {
        this.snippet = snippet;
        GameDisplay.addRepaintList(this);

        // determine snippet height and width
        min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        max = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

        for (Point point : snippet.getScene()) {
            max.x = point.x > max.x ? point.x : max.x;
            max.y = point.y > max.y ? point.y : max.y;

            min.x = point.x < min.x ? point.x : min.x;
            min.y = point.y < min.y ? point.y : min.y;
        }

        snippetHeight = max.y - min.y + 3;
        snippetWidth = max.x - min.x + 3;

        String[] rgbValues = PropertyManager.get("display.color").split("\\s*\\,\\s*");
        gd = new GameDisplay("dump",
                new FunctionalityMatrix(false),
                snippetWidth,
                snippetHeight,
                Integer.valueOf(PropertyManager.get("display.scaling")),
                new Color(Integer.valueOf(rgbValues[0]), Integer.valueOf(rgbValues[1]), Integer.valueOf(rgbValues[2])));
        gd.setFetchoperation((int x1, int y1, int x2, int y2) -> {
            return snippet.getScene();
        });

        setLayout(new BorderLayout(0, 0));
        //add(gd, BorderLayout.CENTER);
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                showDescription = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                showDescription = false;
                repaint();
            }
        });
    }

    public int getSnippetHeight() {
        return snippetHeight;
    }

    public int getSnippetWidth() {
        return snippetWidth;
    }

    public int getScaling() {
        return scaling;
    }

    public int projectHeight(int width) {
        return snippetHeight * (width / snippetWidth);
    }

    public int projectWidth(int height) {
        return snippetWidth * (height / snippetHeight);
    }

    public int projectScalingByWidth(int width) {
        return width / snippetWidth;
    }

    public int projectScalingByHeight(int height) {
        return height / snippetHeight;
    }

    /*
     * GUI stuff
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);

        scaling = width / snippetWidth;

        gd.setScaling(scaling);
        gd.setViewport(new Point(min.x - 1, min.y - 1));

        /*SwingUtilities.invokeLater(() -> {
            Dimension size = new Dimension(width, snippetHeight * scaling);
            setMinimumSize(size);
            setMaximumSize(size);
            setPreferredSize(size);
        });*/

        SwingUtilities.invokeLater(this::repaint);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        gd.setBounds(getBounds());
        gd.paintComponent(graphics);

        Color background = new Color(0, 0, 0, 120);
        Color foreground = Color.LIGHT_GRAY;

        // paint name
        int yOffset = paintName(graphics, graphics.getFont().deriveFont(16f), background, foreground);

        // paint description
        if (showDescription) {
            paintDescription(graphics, yOffset, graphics.getFont().deriveFont(10f), background, foreground);
        }
    }

    private int paintName(Graphics graphics, Font font, Color background, Color foreground) {
        // draw background
        int height = graphics.getFontMetrics(font).getHeight();
        graphics.setColor(background);
        graphics.fillRect(0, 0, getWidth(), height + 4);

        // draw text
        graphics.setColor(foreground);
        graphics.setFont(font);
        graphics.drawString(snippet.getName(), 2, height - 2);

        return height + 4;
    }

    private void paintDescription(Graphics graphics, int offset, Font font, Color background, Color foreground) {
        // draw background
        graphics.setColor(background);
        graphics.fillRect(0, offset, getWidth(), getHeight() - offset);

        // draw text with word wrap
        graphics.setFont(font);
        graphics.setColor(foreground);

        String[] words = snippet.getDescription().split("\\s+");
        int currentLine = 1;
        int lineHeight = graphics.getFontMetrics(font).getHeight();
        int width = getWidth();
        int spaceWidth = graphics.getFontMetrics(font).charWidth(' ');
        int linePos = 2;

        // loop through words and wrap them
        for (String word : words) {
            int wordWith = graphics.getFontMetrics(font).stringWidth(word);
            if (linePos + wordWith + spaceWidth > width) { // word would exceed current line
                currentLine++;
                linePos = 2;
            }

            graphics.drawString(word, linePos, currentLine * lineHeight + offset);
            linePos += wordWith + spaceWidth;
        }
    }
}
