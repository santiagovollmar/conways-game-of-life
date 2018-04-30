package ch.santiagovollmar.gol.gui;

import ch.santiagovollmar.gol.logic.Snippet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A toolbar displaying previews to given
 * {@link ch.santiagovollmar.gol.logic.Snippet Snippets}
 */
public class SnippetPreviewBar extends JPanel {
    private JPanel snippetPreviewWrapper;
    private JScrollPane scrollPane;

    public SnippetPreviewBar() {
        GameDisplay.addRepaintList(this);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Snippets:");
        title.setFont(title.getFont()
                .deriveFont(16f));
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapper.add(title);
        add(wrapper, BorderLayout.NORTH);

        snippetPreviewWrapper = new JPanel();
        snippetPreviewWrapper.setLayout(new BoxLayout(snippetPreviewWrapper, BoxLayout.PAGE_AXIS));
        JScrollPane scrollWrapper = new JScrollPane(snippetPreviewWrapper);
        scrollWrapper.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(scrollWrapper, BorderLayout.CENTER);

        scrollPane = scrollWrapper;

        snippetPreviewWrapper.setPreferredSize(new Dimension(130, 0));
    }

    /**
     * Sets the content of this SnippetPreviewBar's to the contents of the given Iterable
     *
     * @param content The content to insert
     */
    public void setContent(Iterable<Snippet> content, double ratio) {
        SwingUtilities.invokeLater(() -> {
            snippetPreviewWrapper.removeAll();
            SpringLayout layout = new SpringLayout();
            snippetPreviewWrapper.setLayout(layout);

            JComponent prev = null;
            int totalHeight = 0;

            for (Snippet snippet : content) {
                SnippetPreview preview = new SnippetPreview(snippet);
                snippetPreviewWrapper.add(preview);

                if (prev == null) {
                    layout.putConstraint(SpringLayout.NORTH, preview, 5, SpringLayout.NORTH, snippetPreviewWrapper);
                } else {
                    layout.putConstraint(SpringLayout.NORTH, preview, 5, SpringLayout.SOUTH, prev);
                }

                layout.putConstraint(SpringLayout.WEST, preview, 5, SpringLayout.WEST, snippetPreviewWrapper);
                layout.putConstraint(SpringLayout.EAST, preview, -5, SpringLayout.EAST, snippetPreviewWrapper);
                layout.putConstraint(SpringLayout.SOUTH, preview,
                        (int) (snippetPreviewWrapper.getPreferredSize().width * ratio), SpringLayout.NORTH, preview);

                prev = preview;
                totalHeight += ((int) (snippetPreviewWrapper.getPreferredSize().width * ratio)) + 5; //TODO ask each snippet for it's own preferred height
            }

            snippetPreviewWrapper.setPreferredSize(
                    new Dimension(snippetPreviewWrapper.getPreferredSize().width, totalHeight));
            int finalTotalHeight = totalHeight;
            SwingUtilities.invokeLater(() -> scrollPane.setPreferredSize(new Dimension(
                    snippetPreviewWrapper.getPreferredSize().width + scrollPane.getVerticalScrollBar()
                            .getWidth(), finalTotalHeight)));
            SwingUtilities.invokeLater(this::revalidate);

            snippetPreviewWrapper.revalidate();
            snippetPreviewWrapper.repaint();
        });
    }
}
