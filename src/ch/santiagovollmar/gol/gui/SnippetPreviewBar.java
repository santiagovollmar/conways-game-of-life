package ch.santiagovollmar.gol.gui;

import ch.santiagovollmar.gol.logic.Snippet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A toolbar displaying previews to given
 * {@link ch.santiagovollmar.gol.logic.Snippet Snippets}
 */
public class SnippetPreviewBar extends JPanel {
    private JPanel snippetPreviewWrapper;

    public SnippetPreviewBar() {
        //super("Snippets", ToolBar.VERTICAL);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Snippets:");
        title.setFont(title.getFont().deriveFont(18f));
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapper.add(title);
        add(wrapper, BorderLayout.NORTH);

        snippetPreviewWrapper = new JPanel();
        snippetPreviewWrapper.setLayout(new BoxLayout(snippetPreviewWrapper, BoxLayout.PAGE_AXIS));
        JScrollPane scrollWrapper = new JScrollPane(snippetPreviewWrapper);
        scrollWrapper.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollWrapper.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollWrapper.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollWrapper, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            setPreferredSize(new Dimension(130 + scrollWrapper.getVerticalScrollBar().getWidth(), 0));
            snippetPreviewWrapper.setPreferredSize(new Dimension(130, 0));
        });
    }

    /**
     * Sets the content of this SnippetPreviewBar's to the contents of the given Iterable
     *
     * @param content The content to insert
     */
    public void setContent(Iterable<Snippet> content) {
        snippetPreviewWrapper.removeAll();

        for (Snippet snippet : content) {
            SnippetPreview preview = new SnippetPreview(snippet);
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            wrapper.add(preview);
            snippetPreviewWrapper.add(wrapper);

            SwingUtilities.invokeLater(() -> {
                System.out.println("projected: " + preview.projectHeight(snippetPreviewWrapper.getWidth()));
                System.out.println("board width: " + getWidth());
                Dimension size = new Dimension(getWidth(), preview.projectHeight(snippetPreviewWrapper.getWidth()));
                System.out.println("old: " + preview.getSize());
                System.out.println("new: " + size);

                preview.setPreferredSize(size);
                preview.setMaximumSize(size);
                preview.setMinimumSize(size);
                preview.setSize(size);
                preview.revalidate();
                preview.repaint();
                SwingUtilities.invokeLater(() -> {
                    System.out.println("actual size: " + preview.getSize());
                    SwingUtilities.invokeLater(() -> {
                        System.out.println("actual board size: " + getSize());
                    });
                });
            });
        }

        SwingUtilities.invokeLater(snippetPreviewWrapper::revalidate);
        SwingUtilities.invokeLater(snippetPreviewWrapper::repaint);
    }
}
