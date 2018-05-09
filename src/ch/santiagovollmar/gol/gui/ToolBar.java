package ch.santiagovollmar.gol.gui;

import ch.santiagovollmar.gol.logic.GridManager;
import ch.santiagovollmar.gol.logic.LogicManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class ToolBar extends JToolBar {
    private static ToolBar currentInstance;

    public static ToolBar getCurrentInstance() {
        return currentInstance;
    }

    public void setPaused(boolean paused) {
        pauseButton.setIcon(paused ? playIcon : pauseIcon);
        SwingUtilities.invokeLater(pauseButton::repaint);
    }

    // pauseImage = SVGReader.readImage(new
    // File("C:\\Users\\vollmar\\Downloads\\ionicons-2.0.1\\ionicons-2.0.1\\src\\pause"),
    // new Dimension(23, 23));
    // playImage = SVGReader.readImage(new
    // File("C:\\Users\\vollmar\\Downloads\\ionicons-2.0.1\\ionicons-2.0.1\\src\\play"),
    // new Dimension(23, 23));

    private JButton pauseButton;
    private JDialog colorChooserDialog;
    private JDialog ruleChangerDialog;
    private JColorChooser colorChooser;
    private boolean previousPauseState;

    private ImageIcon pauseIcon = new ImageIcon(getClass().getResource("pictures/pause.png"));
    private ImageIcon playIcon = new ImageIcon(getClass().getResource("pictures/play.png"));

    public ToolBar() {
        currentInstance = this;
        setBackground(Color.WHITE);
        addPauseButton();
        addColorChooser();
        addResetButton();
        addRuleChanger();
        addSpeedSlider();
    }

    private void addSpeedSlider() {
        JSlider slider = new JSlider();
        add(slider);

        slider.addChangeListener(e -> {
            LogicManager.setPercentageSleepTime(100 - slider.getValue());
        });
    }

    private void addResetButton() {
        JButton resetButton = new JButton(new ImageIcon(getClass().getResource("pictures/refresh.png")));
        resetButton.setBackground(Color.WHITE);
        resetButton.addActionListener(e -> {
            boolean pausedState = LogicManager.isPaused();
            LogicManager.setPaused(true);
            int choice = JOptionPane.showConfirmDialog(Window.getCurrentInstance()
                            .getFrame(),
                    "<html>Clearing the current scene disposes all the information associated with it.<br />Do you want to continue?</html>",
                    "Confirm Action", JOptionPane.YES_NO_OPTION);
            if (choice == 0) {
                GridManager.update();
                GridManager.clearScene();
            }
            LogicManager.setPaused(pausedState);
        });
        add(resetButton);
    }

    private void addRuleChanger() {
        // grid class
        class GridInput extends JPanel {
            private JCheckBox[] checkBoxes;

            public GridInput(String caption, int min, int max) {
                // set layout
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

                // add title
                JPanel titleWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
                titleWrapper.add(new JLabel(caption));
                add(titleWrapper);

                // add boxes
                JPanel wrapper = new JPanel();
                wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
                checkBoxes = new JCheckBox[max - min + 1];
                for (int i = min; i <= max; i++) {
                    checkBoxes[i] = new JCheckBox(Integer.toString(i), false);
                    wrapper.add(checkBoxes[i]);
                }
                add(wrapper);
            }

            public ArrayList<Integer> getValues() {
                ArrayList<Integer> values = new ArrayList<>();

                for (int i = 0; i < checkBoxes.length; i++) {
                    if (checkBoxes[i].isSelected()) {
                        values.add(i);
                    }
                }

                return values;
            }
        }

        // create dialog
        this.ruleChangerDialog = new JDialog(Window.getCurrentInstance().getFrame(), "Change the rules of the simulation", true);
        this.ruleChangerDialog.setBounds(300, 300, 700, 400);
        this.ruleChangerDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        SpringLayout layout = new SpringLayout();
        this.ruleChangerDialog.setLayout(layout);

        /*
         * Add components
         */

        // add title
        JLabel title = new JLabel("Change the rules");
        title.setFont(title.getFont().deriveFont(18f));
        ruleChangerDialog.add(title);

        // grid wrapper
        JPanel gridWrapper = new JPanel();
        gridWrapper.setLayout(new BoxLayout(gridWrapper, BoxLayout.Y_AXIS));
        ruleChangerDialog.add(gridWrapper);

        // survive grid input
        GridInput surviveGridInput = new GridInput("Survival:", 0, 8);
        gridWrapper.add(surviveGridInput);

        // birth grid input
        GridInput birthGridInput = new GridInput("Birth:", 0, 8);
        gridWrapper.add(birthGridInput);

        // 'apply' button
        JButton applyButton = new JButton("apply");
        ruleChangerDialog.add(applyButton);

        // 'exit' button
        JButton exitButton = new JButton("exit");
        ruleChangerDialog.add(exitButton);

        // 'open' button
        JButton ruleChangerOpener = new JButton(new ImageIcon(getClass().getResource("pictures/settings.png")));
        ruleChangerOpener.setBackground(Color.WHITE);
        ruleChangerOpener.setToolTipText("Change the rules of the simulation");
        add(ruleChangerOpener);

        /*
         * add functionality
         */
        // 'open' button
        ruleChangerOpener.addActionListener(e -> {
            previousPauseState = LogicManager.isPaused();
            LogicManager.setPaused(true);
            ruleChangerDialog.setVisible(true);
        });

        // 'apply' button
        applyButton.addActionListener(e -> {
            ArrayList<Integer> surviveGrid = surviveGridInput.getValues();
            ArrayList<Integer> birthGrid = birthGridInput.getValues();

            String surviveRule = surviveGrid.stream().map(i -> Integer.toString(i)).collect(Collectors.joining());
            String birthRule = birthGrid.stream().map(i -> Integer.toString(i)).collect(Collectors.joining());

            LogicManager.setRule(surviveGrid, birthGrid);
            JOptionPane.showMessageDialog(ruleChangerDialog, String.format("The new rules, %sS/%sB, have been applied!",
                    surviveRule, birthRule), "Rules changed", JOptionPane.INFORMATION_MESSAGE);
        });

        // 'exit' button
        exitButton.addActionListener(e -> LogicManager.setPaused(previousPauseState));

        ruleChangerDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitButton.doClick();
            }
        });

        /*
         * add layout constraints
         */
        Component panel = ruleChangerDialog.getContentPane();

        // title
        layout.putConstraint(SpringLayout.NORTH, title, 10, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, title, 10, SpringLayout.WEST, panel);

        // gridwrapper
        layout.putConstraint(SpringLayout.NORTH, gridWrapper, 5, SpringLayout.SOUTH, title);
        layout.putConstraint(SpringLayout.WEST, gridWrapper, 10, SpringLayout.WEST, panel);
        //layout.putConstraint(SpringLayout.EAST, gridWrapper, -10, SpringLayout.EAST, panel);

        // 'exit' button
        layout.putConstraint(SpringLayout.EAST, exitButton, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, exitButton, -10, SpringLayout.SOUTH, panel);

        // 'apply' button
        layout.putConstraint(SpringLayout.EAST, applyButton, -5, SpringLayout.WEST, exitButton);
        layout.putConstraint(SpringLayout.SOUTH, applyButton, 0, SpringLayout.SOUTH, exitButton);
    }

    private void addColorChooser() { // TODO add ability to close window with 'X' in edge
        this.colorChooserDialog = new JDialog(Window.getCurrentInstance()
                .getFrame(), "Choose new color", true);
        colorChooserDialog.setBounds(300, 300, 700, 400);
        colorChooserDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        SpringLayout layout = new SpringLayout();
        colorChooserDialog.getContentPane()
                .setLayout(layout);

        this.colorChooser = new JColorChooser(GameDisplay.getFillColor());
        layout.putConstraint(SpringLayout.NORTH, colorChooser, 0, SpringLayout.NORTH,
                colorChooserDialog.getContentPane());
        layout.putConstraint(SpringLayout.EAST, colorChooser, 0, SpringLayout.EAST,
                colorChooserDialog.getContentPane());
        layout.putConstraint(SpringLayout.WEST, colorChooser, 0, SpringLayout.WEST,
                colorChooserDialog.getContentPane());
        this.colorChooserDialog.add(this.colorChooser);
        JButton okButton = new JButton("apply");
        okButton.addActionListener(e -> {
            GameDisplay.setFillColor(colorChooser.getColor());
            GameDisplay.repaintAll();
        });
        layout.putConstraint(SpringLayout.SOUTH, okButton, -10, SpringLayout.SOUTH,
                colorChooserDialog.getContentPane());
        layout.putConstraint(SpringLayout.EAST, okButton, -10, SpringLayout.EAST, colorChooserDialog.getContentPane());
        layout.putConstraint(SpringLayout.SOUTH, colorChooser, -10, SpringLayout.NORTH, okButton);
        colorChooserDialog.add(okButton);

        JButton cancelButton = new JButton("exit");
        cancelButton.addActionListener(e -> {
            colorChooserDialog.setVisible(false);
            LogicManager.setPaused(previousPauseState);
        });
        layout.putConstraint(SpringLayout.SOUTH, cancelButton, -10, SpringLayout.SOUTH,
                colorChooserDialog.getContentPane());
        layout.putConstraint(SpringLayout.EAST, cancelButton, -10, SpringLayout.WEST, okButton);
        colorChooserDialog.add(cancelButton);

        JButton colorChooserOpener = new JButton(new ImageIcon(getClass().getResource("pictures/palette.PNG")));
        colorChooserOpener.setBackground(Color.WHITE);
        colorChooserOpener.setToolTipText("Change the coloring of the game");
        colorChooserOpener.addActionListener(e -> {
            previousPauseState = LogicManager.isPaused();
            LogicManager.setPaused(true);
            colorChooserDialog.setVisible(true);
        });
        add(colorChooserOpener);
    }

    private void addPauseButton() {
        pauseButton = new JButton(pauseIcon);
        pauseButton.setBackground(Color.WHITE);
        add(pauseButton);
        pauseButton.addActionListener((e) -> {
            boolean paused = LogicManager.isPaused();

            if (!paused) {
                pauseButton.setIcon(playIcon);
            } else {
                pauseButton.setIcon(pauseIcon);
            }
            SwingUtilities.invokeLater(pauseButton::repaint);
            LogicManager.setPaused(!paused);
        });
    }
}
