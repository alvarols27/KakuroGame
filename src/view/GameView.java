package view;

import interfaces.GameUI;
import model.Grid;
import model.Cell;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.BiConsumer;

public class GameView extends JFrame implements GameUI {
    private JTextField[][] textFields;
    private JPanel gridPanel;
    private JLabel timerLabel;
    private JLabel hintsLabel;
    private JButton hintButton;
    private JButton undoButton;
    private JButton redoButton;
    private JButton noteModeButton;
    private JButton settingsButton;
    private JButton dashboardButton;
    private JButton saveGameButton;

    //color constants
    private static final Color CLUE_NORMAL = new Color(208, 215, 221);
    private static final Color CLUE_CORRECT = new Color(46, 204, 113);
    private static final Color CLUE_ERROR = new Color(231, 76, 60);
    private static final Color WHITE_CELL = new Color(255, 255, 255);
    private static final Color WHITE_CELL_ERROR = new Color(249, 234, 172);
    private static final Color BORDER_COLOR = new Color(208, 215, 221);

    public GameView(Grid grid) {
        setTitle("Kakuro Game - Playing");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        int size = grid.getSize();
        textFields = new JTextField[size][size];

        //title and timer
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        //grid
        gridPanel = createGridPanel(grid, size);
        add(gridPanel, BorderLayout.CENTER);
        
        //bottom (buttons panel)
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(700, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(41, 128, 185));
        topPanel.setPreferredSize(new Dimension(600,50));
        
        timerLabel = new JLabel("Time: 00:00", SwingConstants.CENTER);
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        topPanel.add(timerLabel, BorderLayout.CENTER);
        
        hintsLabel = new JLabel("Hints: --", SwingConstants.RIGHT);
        hintsLabel.setForeground(Color.WHITE);
        hintsLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        hintsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        topPanel.add(hintsLabel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createGridPanel(Grid grid, int size) {
        JPanel panel = new JPanel(new GridLayout(size, size));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(240, 240, 240));
        
        int cellSize = Math.min(90, 600 / size);
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Cell cell = grid.getCell(i, j);
                if (cell.isReadOnly()) {
                    JPanel cluePanel = createClueCell(cell, cellSize);
                    panel.add(cluePanel);
                    textFields[i][j] = null;
                } else {
                    JTextField tf = createWhiteCell(cell, cellSize, i, j);
                    textFields[i][j] = tf;
                    panel.add(tf);
                }
            }
        }
        
        return panel;
    }

    private JPanel createClueCell(Cell cell, int size) {
        int acrossHint = cell.getRowSumHint(); //always top right
        int downHint = cell.getColSumHint();// always bottom left

        JPanel panel = new JPanel() {
            @Override //since JPanel's parent is JComponent
            // paintComponent used to draw customizable shapes
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                
                //get stored colours for each half
                Color topRightColor = getTopRightColor();
                Color bottomLeftColor = getBottomLeftColor();

                //top right (rows ClueCells)
                int[] xPointsTR = {0, w, w};
                int[] yPointsTR = {0, 0, h};
                g2.setColor(topRightColor);
                g2.fillPolygon(xPointsTR, yPointsTR, 3);
                
                // bottom left (columns ClueCells)
                int[] xPointsBL = {0, 0, w};
                int[] yPointsBL = {0, h, h};
                g2.setColor(bottomLeftColor);
                g2.fillPolygon(xPointsBL, yPointsBL, 3);

                // Draw diagonal line
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(0, 0, w, h);

                // // Draw border
                // g2.setColor(new Color(100, 100, 100));
                // g2.drawRect(0, 0, w - 1, h - 1);

                //hint numbers
                Font font = new Font("Monospaced", Font.BOLD, Math.max(10, size / 3));
                g2.setFont(font);
                g2.setColor(new Color(60, 60, 60));
                FontMetrics fm = g2.getFontMetrics();

                // COLUMN hint (bottom-left)
                if (downHint > 0) {
                    String text = String.valueOf(downHint);
                    int x = 5;
                    int y = h - 5;
                    g2.drawString(text, x, y);
                }

                // ROW hint (top-right)
                if (acrossHint > 0) {
                    String text = String.valueOf(acrossHint);
                    int textWidth = fm.stringWidth(text);
                    int x = w - textWidth - 5;
                    int y = fm.getAscent() + 5;
                    g2.drawString(text, x, y);
                }

                g2.dispose();
            }
            
            private Color getTopRightColor() {
                Boolean isCorrect = (Boolean) getClientProperty("topRightCorrect");
                Boolean isError = (Boolean) getClientProperty("topRightError");
                
                if (isCorrect != null && isCorrect) {
                    return CLUE_CORRECT;
                } else if (isError != null && isError) {
                    return CLUE_ERROR;
                }
                return CLUE_NORMAL;
            }
            
            private Color getBottomLeftColor() {
                Boolean isCorrect = (Boolean) getClientProperty("bottomLeftCorrect");
                Boolean isError = (Boolean) getClientProperty("bottomLeftError");
                
                if (isCorrect != null && isCorrect) {
                    return CLUE_CORRECT;
                } else if (isError != null && isError) {
                    return CLUE_ERROR;
                }
                return CLUE_NORMAL;
            }
        };

        panel.setPreferredSize(new Dimension(size, size));
        panel.setOpaque(true);
        
        // Initialize properties (default to normal)
        panel.putClientProperty("topRightCorrect", false);
        panel.putClientProperty("topRightError", false);
        panel.putClientProperty("bottomLeftCorrect", false);
        panel.putClientProperty("bottomLeftError", false);
        
        return panel;
    }

    private JTextField createWhiteCell(Cell cell, int size, final int row, final int col) {
        JTextField tf = new JTextField();
        if (cell.getValue() > 0) {
            tf.setText(String.valueOf(cell.getValue()));
        }

        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setFont(new Font("SansSerif", Font.BOLD, Math.max(16, size / 3)));
        tf.setBackground(WHITE_CELL);
        tf.setForeground(new Color(44, 62, 80));
        tf.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        tf.setPreferredSize(new Dimension(size, size));

        tf.addKeyListener(new KeyAdapter() {
            //VALIDATION for white cells
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                
                //backspace, delete, enter, tab (control characters)
                if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE || 
                    c == KeyEvent.VK_ENTER || c == KeyEvent.VK_TAB) {
                    return;
                }
                
                if (c < '1' || c > '9') {
                    e.consume();  //blocks the character
                    return;
                }
                
                //single digit only
                if (tf.getText().length() >= 1) {
                    e.consume();
                }
            }
        });
        
        //focus listener
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                tf.setBackground(new Color(255, 255, 200));
            }
            public void focusLost(FocusEvent evt) {
                if (!tf.getBackground().equals(WHITE_CELL_ERROR)) {
                    tf.setBackground(WHITE_CELL);
                }
            }
        });
        
        return tf;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout());
        // validateButton = new JButton("Check");
        hintButton = new JButton("Hint");
        undoButton = new JButton("Undo");
        redoButton = new JButton("Redo");
        noteModeButton = new JButton("Note Mode");
        settingsButton = new JButton("Settings");
        dashboardButton = new JButton("Dashboard");
        saveGameButton = new JButton("Save");

        // styleButton(validateButton, new Color(46, 204, 113));
        styleButton(hintButton, new Color(241, 196, 15));
        styleButton(undoButton, new Color(155, 89, 182));
        styleButton(redoButton, new Color(52, 73, 94));
        styleButton(noteModeButton, new Color(230, 126, 34));
        styleButton(settingsButton, new Color(52, 152, 219));
        styleButton(dashboardButton, new Color(46, 204, 113));
        styleButton(saveGameButton, new Color(52, 152, 219));

        // bottomPanel.add(validateButton);
        bottomPanel.add(hintButton);
        bottomPanel.add(undoButton);
        bottomPanel.add(redoButton);
        bottomPanel.add(noteModeButton);
        bottomPanel.add(settingsButton);
        bottomPanel.add(dashboardButton);
        bottomPanel.add(saveGameButton);
        add(bottomPanel, BorderLayout.SOUTH);

        return bottomPanel;
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
    }

    // Set ROW hint (top-right triangle) color
    public void setClueCellRowCorrect(int row, int col, boolean isCorrect) {
        JPanel cluePanel = getClueCellPanel(row, col);
        if (cluePanel != null) {
            cluePanel.putClientProperty("topRightCorrect", isCorrect);
            cluePanel.putClientProperty("topRightError", !isCorrect);
            cluePanel.repaint();
        }
    }

    public void setClueCellRowError(int row, int col, boolean isError) {
        JPanel cluePanel = getClueCellPanel(row, col);
        if (cluePanel != null) {
            cluePanel.putClientProperty("topRightError", isError);
            cluePanel.putClientProperty("topRightCorrect", !isError);
            cluePanel.repaint();
        }
    }

    // Set COLUMN hint (bottom-left triangle) color
    public void setClueCellColCorrect(int row, int col, boolean isCorrect) {
        JPanel cluePanel = getClueCellPanel(row, col);
        if (cluePanel != null) {
            cluePanel.putClientProperty("bottomLeftCorrect", isCorrect);
            cluePanel.putClientProperty("bottomLeftError", !isCorrect);
            cluePanel.repaint();
        }
    }

    public void setClueCellColError(int row, int col, boolean isError) {
        JPanel cluePanel = getClueCellPanel(row, col);
        if (cluePanel != null) {
            cluePanel.putClientProperty("bottomLeftError", isError);
            cluePanel.putClientProperty("bottomLeftCorrect", !isError);
            cluePanel.repaint();
        }
    }

    //reset colour for both halves
    public void resetClueCellColors(int row, int col) {
        JPanel cluePanel = getClueCellPanel(row, col);
        if (cluePanel != null) {
            cluePanel.putClientProperty("topRightCorrect", false);
            cluePanel.putClientProperty("topRightError", false);
            cluePanel.putClientProperty("bottomLeftCorrect", false);
            cluePanel.putClientProperty("bottomLeftError", false);
            cluePanel.repaint();
        }
    }

    //GameUI implementation
    @Override
    public void close(){
        dispose();
    }

    @Override
    public void updateTimer(String time){
        timerLabel.setText(time);
    }

    @Override
        public void showVictoryMessage(String message, int seconds, int hintsUsed) {
        JOptionPane.showMessageDialog(this, message, "Victory!", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void updateCell(int row, int col, int value) {
        if (textFields[row][col] != null){
            textFields[row][col].setText(value > 0 ? String.valueOf(value) : "");
        }
    }

    @Override
    public void highlightError(int row, int col) {
        if (textFields[row][col] != null) {
            textFields[row][col].setBackground(WHITE_CELL_ERROR);
        } else {
            //clue cell turns RED when error
            Component comp = gridPanel.getComponent(row * gridPanel.getComponentCount() / 
                (int)Math.sqrt(gridPanel.getComponentCount()) + col);
            if (comp instanceof JPanel) {
                comp.setBackground(CLUE_ERROR);
            }
        }
    }

    @Override
    public void clearHighlight(int row, int col) {
        if (textFields[row][col] != null) {
            textFields[row][col].setBackground(WHITE_CELL);
        } else {
            //clue cell highlight goes back to normal
            Component comp = gridPanel.getComponent(row * gridPanel.getComponentCount() / 
                (int)Math.sqrt(gridPanel.getComponentCount()) + col);
            if (comp instanceof JPanel) {
                comp.setBackground(CLUE_NORMAL);
            }
        }
    }

    //clue cell is set to green (correct sum reached)
    @Override
    public void setClueCellCorrect(int row, int col, boolean isCorrect) {
        JPanel cluePanel = getClueCellPanel(row, col);

        if (cluePanel != null) {
            if (isCorrect) {
                cluePanel.setBackground(CLUE_CORRECT);
            } else {
                cluePanel.setBackground(CLUE_NORMAL);
            }
            cluePanel.repaint();
        }
        gridPanel.repaint();
    }

    //clue cell set to red
    @Override
    public void setClueCellError(int row, int col, boolean isError) {
        JPanel cluePanel = getClueCellPanel(row, col);
        if (cluePanel != null) {
            if (isError) {
                cluePanel.setBackground(CLUE_ERROR);
                            System.out.println("Setting clue cell at (" + row + "," + col + ") to RED");
            } else {
                cluePanel.setBackground(CLUE_NORMAL);
            }
            cluePanel.repaint();
        }
    }

    @Override
    public void setSubmitEnabled(boolean enabled) {
        saveGameButton.setEnabled(enabled);
    }

    @Override
    public void setNoteModeActive(boolean active) {
        if (active) {
            noteModeButton.setBackground(new Color(46, 204, 113));
        } else {
            noteModeButton.setBackground(new Color(230, 126, 34));
        }
    }

    @Override
    public void setHintsRemaining(int remaining) {
        hintsLabel.setText("Hints: " + remaining);
    }

    @Override
    public void openSettings() {
    }

    @Override
    public void openDashboard() {
    }

    @Override
    public void setHintAction(ActionListener listener) {
        hintButton.addActionListener(listener);
    }

    @Override
    public void setUndoAction(ActionListener listener) {
        undoButton.addActionListener(listener);
    }

    @Override
    public void setRedoAction(ActionListener listener) {
        redoButton.addActionListener(listener);
    }

    @Override
    public void setNoteModeAction(ActionListener listener) {
        noteModeButton.addActionListener(listener);
    }

    @Override
    public void setSettingsAction(ActionListener listener) {
        settingsButton.addActionListener(listener);
    }

    @Override
    public void setDashboardAction(ActionListener listener) {
        dashboardButton.addActionListener(listener);
    }

    @Override
    public void setSaveGameAction(ActionListener listener) {
        saveGameButton.addActionListener(listener);
    }

    @Override
    public void enterValue(BiConsumer<Integer, Integer> listener) {
        // Set up document listeners for real-time validation
        for (int i = 0; i < textFields.length; i++) {
            for (int j = 0; j < textFields[i].length; j++) {
                if (textFields[i][j] != null) {
                    final int row = i;
                    final int col = j;
                    textFields[i][j].getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                        public void insertUpdate(javax.swing.event.DocumentEvent e) {
                            listener.accept(row, col);
                        }
                        public void removeUpdate(javax.swing.event.DocumentEvent e) {
                            listener.accept(row, col);
                        }
                        public void changedUpdate(javax.swing.event.DocumentEvent e) {
                            listener.accept(row, col);
                        }
                    });
                }
            }
        }
    }

    public JPanel getClueCellPanel(int row, int col) {
        int gridSize = (int) Math.sqrt(gridPanel.getComponentCount());
        int index = row * gridSize + col;
        
        if (index >= 0 && index < gridPanel.getComponentCount()) {
            Component comp = gridPanel.getComponent(index);
            if (comp instanceof JPanel) {
                return (JPanel) comp;
            }
        }
        return null;
    }
    
    public JTextField[][] getTextFields() { return textFields; }
}