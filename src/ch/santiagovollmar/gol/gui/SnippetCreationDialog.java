package ch.santiagovollmar.gol.gui;

import ch.santiagovollmar.gol.logic.FunctionalityMatrix;
import ch.santiagovollmar.gol.logic.Point;
import ch.santiagovollmar.gol.logic.Snippet;
import ch.santiagovollmar.gol.util.GlobalKeyListener;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.Window;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

public class SnippetCreationDialog extends JDialog {
    private Snippet snippet;
    private Consumer<Snippet> callback;
    private String listenerSpace = "SnippetCreationDialog:" + Math.random();
    private Rectangle bounds;
    private GameDisplay gd;

    public SnippetCreationDialog(AbstractCollection<Point> buffer, Point offset, Consumer<Snippet> callback) {
        super(ch.santiagovollmar.gol.gui.Window.getCurrentInstance().getFrame());

        // fill scene
        HashSet<Point> scene = new HashSet<>(buffer.size(), 2f);
        for (Point p : buffer) {
            scene.add(new Point(p.x - offset.x, p.y - offset.y));
        }

        // set callback
        this.callback = callback;

        // create snippet preliminary
        snippet = new Snippet(scene);

        // create new Listenerspace
        GlobalKeyListener.createListenerSpace(listenerSpace);

        // setup gui
        bounds = new Rectangle(100, 100, 1000, 800);
        setBounds(bounds);
        initializeGui();
        setVisible(true);

        // apply key listener
        GlobalKeyListener.apply(listenerSpace, this.getContentPane());
    }

    private void initializeGui() {
        /*
         * set layout
         */
        SpringLayout l = new SpringLayout();
        setLayout(l);

        /*
         * add components
         */
        // title
        JLabel title = new JLabel("Add Snippet");
        title.setFont(title.getFont().deriveFont(18f));
        add(title);

        // preview
        gd = new GameDisplay(listenerSpace, new FunctionalityMatrix(
                FunctionalityMatrix.Functionality.DRAG,
                FunctionalityMatrix.Functionality.ZOOM,
                FunctionalityMatrix.Functionality.DYNAMIC_CONTENT
        ), 100, 100, 5, GameDisplay.getFillColor());
        gd.setFetchoperation(new GameDisplay.FetchOperation() {
            @Override
            public Collection<Point> fetch(int x1, int y1, int x2, int y2) {
                return snippet.getScene();
            }
        });
        gd.setViewport(new Point(0, 0));
        add(gd);
        //TODO maybe enable drawing
        //TODO center preview

        // name input
        JLabel lblName = new JLabel("Name:");
        JTextField fldName = new JTextField();
        add(lblName);
        add(fldName);

        // description input
        JLabel lblDesc = new JLabel("Description:");
        JTextArea fldDesc = new JTextArea();
        JScrollPane descWrapper = new JScrollPane(fldDesc);
        descWrapper.setBorder(fldName.getBorder());
        add(lblDesc);
        add(descWrapper);

        // 'Cancel'-Button
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> finalize(true, null, null));
        add(btnCancel);

        // 'OK'-Button
        JButton btnConfirm = new JButton("Add");
        btnConfirm.addActionListener(e -> finalize(false, fldName.getText(), fldDesc.getText()));
        add(btnConfirm);

        /*
         * set constraints
         */

        // title
        l.putConstraint(SpringLayout.NORTH, title, 10, SpringLayout.NORTH, this.getContentPane());
        l.putConstraint(SpringLayout.WEST, title, 10, SpringLayout.WEST, this.getContentPane());

        // preview
        l.putConstraint(SpringLayout.NORTH, gd, 5, SpringLayout.SOUTH, title);
        l.putConstraint(SpringLayout.WEST, gd, 10, SpringLayout.WEST, this.getContentPane());
        l.putConstraint(SpringLayout.EAST, gd, -10, SpringLayout.EAST, this.getContentPane());
        l.putConstraint(SpringLayout.SOUTH, gd, (int) (bounds.height * 0.4), SpringLayout.NORTH, this.getContentPane());

        // name label
        l.putConstraint(SpringLayout.NORTH, lblName, 10, SpringLayout.SOUTH, gd);
        l.putConstraint(SpringLayout.WEST, lblName, 10, SpringLayout.WEST, this.getContentPane());

        // description label
        l.putConstraint(SpringLayout.NORTH, lblDesc, 10, SpringLayout.SOUTH, lblName);
        l.putConstraint(SpringLayout.WEST, lblDesc, 10, SpringLayout.WEST, this.getContentPane());

        // name input
        l.putConstraint(SpringLayout.NORTH, fldName, 0, SpringLayout.NORTH, lblName);
        l.putConstraint(SpringLayout.WEST, fldName, 15, SpringLayout.EAST, lblDesc);
        l.putConstraint(SpringLayout.EAST, fldName, -10, SpringLayout.EAST, this.getContentPane());

        // desc input
        l.putConstraint(SpringLayout.NORTH, descWrapper, 0, SpringLayout.NORTH, lblDesc);
        l.putConstraint(SpringLayout.WEST, descWrapper, 15, SpringLayout.EAST, lblDesc);
        l.putConstraint(SpringLayout.EAST, descWrapper, -10, SpringLayout.EAST, this.getContentPane());
        l.putConstraint(SpringLayout.SOUTH, descWrapper, -5, SpringLayout.NORTH, btnConfirm);

        // buttons
        l.putConstraint(SpringLayout.SOUTH, btnConfirm, -10, SpringLayout.SOUTH, this.getContentPane());
        l.putConstraint(SpringLayout.EAST, btnConfirm, -10, SpringLayout.EAST, this.getContentPane());

        l.putConstraint(SpringLayout.SOUTH, btnCancel, 0, SpringLayout.SOUTH, btnConfirm);
        l.putConstraint(SpringLayout.EAST, btnCancel, -5, SpringLayout.WEST, btnConfirm);
    }

    private void finalize(boolean cancelled, String name, String description) {
        if (cancelled) { // user has cancelled the event
            callback.accept(null); // return null as an indicator
            destroy();
            return;
        }

        // check inputs
        name = name.trim();
        description = description.trim().replaceAll("\\s+", " ");

        if (name.isEmpty()) { // abort if no name was given
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Please provide a name", "Error", JOptionPane.ERROR_MESSAGE));
            return;
        }

        // update snippet and return it
        snippet.setName(name);
        snippet.setDescription(description);
        callback.accept(snippet);
        destroy();
    }

    private void destroy() {
        setVisible(false);
        GlobalKeyListener.freeListenerSpace(listenerSpace);
        gd.killThread();
        dispose();
    }
}
