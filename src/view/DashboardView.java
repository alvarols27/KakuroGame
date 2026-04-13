package view;

import interfaces.DashboardUI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class DashboardView extends JFrame implements DashboardUI {
    private JTabbedPane tabbedPane;
    private JPanel statsPanel;
    private JPanel bestScoresPanel;
    private JTable gameHistoryTable;
    private JTable savedGamesTable;
    private DefaultTableModel historyTableModel;
    private DefaultTableModel savedGamesTableModel;
    
    private JLabel totalGamesLabel;
    private JLabel avgTimeLabel;
    private JLabel totalHintsLabel;
    private JLabel favoriteDiffLabel;
    private JLabel lastPlayedLabel;
    
    private JLabel easyBestLabel;
    private JLabel easyDateLabel;
    private JLabel mediumBestLabel;
    private JLabel mediumDateLabel;
    private JLabel hardBestLabel;
    private JLabel hardDateLabel;
    
    private JButton logouButton;
    private JButton newGameButton;
    private JButton loadGameButton;
    private JButton deleteGameButton;
    private JButton settingsButton;

    private int selectedSavedGameId = -1;
    
    public DashboardView() {
        setTitle("Kakuro Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        //header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        //tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Your Stats", createStatsTab());
        tabbedPane.addTab("Best Scores", createBestScoresTab());
        tabbedPane.addTab("Game History", loadGameHistoryTab());
        tabbedPane.addTab("Load Saved Game", loadSavedGamesTab());
        add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        
        //footer
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
        
        setSize(700, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 73, 94));
        JLabel titleLabel = new JLabel("KAKURO DASHBOARD");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        return headerPanel;
    }
    
    private JPanel createStatsTab() {
        statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        //title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("YOUR STATISTICS");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 21));
        titleLabel.setForeground(new Color(52, 73, 94));
        statsPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        addStatRow(statsPanel, gbc, 1, "Total Games Played:", totalGamesLabel = new JLabel("0"));
        gbc.gridy = 2;
        addStatRow(statsPanel, gbc, 2, "Average Time:", avgTimeLabel = new JLabel("0 sec"));
        gbc.gridy = 3;
        addStatRow(statsPanel, gbc, 3, "Total Hints Used:", totalHintsLabel = new JLabel("0"));
        gbc.gridy = 4;
        addStatRow(statsPanel, gbc, 4, "Favorite Difficulty:", favoriteDiffLabel = new JLabel("None"));
        gbc.gridy = 5;
        addStatRow(statsPanel, gbc, 5, "Last Played:", lastPlayedLabel = new JLabel("Never"));
        
        return statsPanel;
    }
    
    private void addStatRow(JPanel panel, GridBagConstraints gbc, int row, String label, JLabel valueLabel) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(labelComp, gbc);
        
        gbc.gridx = 1;
        valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        panel.add(valueLabel, gbc);
    }
    
    private JPanel createBestScoresTab() {
        bestScoresPanel = new JPanel(new GridBagLayout());
        bestScoresPanel.setBackground(Color.WHITE);
        bestScoresPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        //title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        JLabel titleLabel = new JLabel("PERSONAL BEST SCORES");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 21));
        titleLabel.setForeground(new Color(52, 73, 94));
        bestScoresPanel.add(titleLabel, gbc);
        
        //headers
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel diffHeader = new JLabel("Difficulty");
        diffHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
        bestScoresPanel.add(diffHeader, gbc);
        
        gbc.gridx = 1;
        JLabel timeHeader = new JLabel("Best Time");
        timeHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
        bestScoresPanel.add(timeHeader, gbc);
        
        gbc.gridx = 2;
        JLabel dateHeader = new JLabel("Date Achieved");
        dateHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
        bestScoresPanel.add(dateHeader, gbc);
        
        //easy row
        gbc.gridy = 2;
        gbc.gridx = 0;
        bestScoresPanel.add(new JLabel("Easy") {{
            setFont(new Font("SansSerif", Font.BOLD, 14));
        }}, gbc);

        gbc.gridx = 1;
        easyBestLabel = new JLabel("--");
        easyBestLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        bestScoresPanel.add(easyBestLabel, gbc);

        gbc.gridx = 2;
        easyDateLabel = new JLabel("--");
        easyDateLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        bestScoresPanel.add(easyDateLabel, gbc);
        
        //medium row
        gbc.gridy = 3;
        gbc.gridx = 0;
        bestScoresPanel.add(new JLabel("Medium") {{
            setFont(new Font("SansSerif", Font.BOLD, 14));
        }}, gbc);

        gbc.gridx = 1;
        mediumBestLabel = new JLabel("--");
        mediumBestLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        bestScoresPanel.add(mediumBestLabel, gbc);

        gbc.gridx = 2;
        mediumDateLabel = new JLabel("--");
        mediumDateLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        bestScoresPanel.add(mediumDateLabel, gbc);
        
        //hard row
        gbc.gridy = 4;
        gbc.gridx = 0;
        bestScoresPanel.add(new JLabel("Hard") {{
            setFont(new Font("SansSerif", Font.BOLD, 14));
        }}, gbc);

        gbc.gridx = 1;
        hardBestLabel = new JLabel("--");
        hardBestLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        bestScoresPanel.add(hardBestLabel, gbc);
        
        gbc.gridx = 2;
        hardDateLabel = new JLabel("--");
        hardDateLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        bestScoresPanel.add(hardDateLabel, gbc);

        return bestScoresPanel;
    }
    
    private JScrollPane loadGameHistoryTab() {
        String[] columns = {"Date", "Difficulty", "Time (sec)", "Hints Used"};
        historyTableModel = new DefaultTableModel(columns, 0);
        gameHistoryTable = new JTable(historyTableModel);
        gameHistoryTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gameHistoryTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        
        JScrollPane scrollPane = new JScrollPane(gameHistoryTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return scrollPane;
    }
    
    private JScrollPane loadSavedGamesTab() {
        String[] columns = {"ID", "Date", "Difficulty", "Time Elapsed (sec)"};
        savedGamesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        savedGamesTable = new JTable(savedGamesTableModel);
        savedGamesTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        savedGamesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        
        //selection listener
        savedGamesTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = savedGamesTable.getSelectedRow();
            if (selectedRow >= 0) {
                selectedSavedGameId = (Integer) savedGamesTableModel.getValueAt(selectedRow, 0);
            }
        });
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(savedGamesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loadGameButton = new JButton("Load Selected Game");
        deleteGameButton = new JButton("Delete Selected Game");
        styleButton(loadGameButton, new Color(46, 204, 113));
        styleButton(deleteGameButton, new Color(231, 76, 60));
        buttonPanel.add(loadGameButton);
        buttonPanel.add(deleteGameButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        JScrollPane wrapper = new JScrollPane(panel);
        wrapper.setBorder(null);
        return wrapper;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout());
        
        logouButton = new JButton("Logout");
        newGameButton = new JButton("New Game");
        settingsButton = new JButton("Settings");
        
        styleButton(logouButton, new Color(255, 0, 0));
        styleButton(newGameButton, new Color(241, 196, 15));
        styleButton(settingsButton, new Color(155, 89, 182));

        footerPanel.add(newGameButton);
        footerPanel.add(logouButton);
        footerPanel.add(settingsButton);
        
        return footerPanel;
    }
    
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
    }
    
    //dashboardUI Implementation
    @Override
    public void close() {
        dispose();
    }
    
    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    @Override
    public void updateStatsDisplay(String totalGames, String avgTime, String totalHints,
                                    String favoriteDiff, String lastPlayed) {
        totalGamesLabel.setText(totalGames);
        avgTimeLabel.setText(avgTime);
        totalHintsLabel.setText(totalHints);
        favoriteDiffLabel.setText(favoriteDiff);
        lastPlayedLabel.setText(lastPlayed);
    }
    
    @Override
    public void updateBestScoresDisplay(String easyBest, String easyDate,
                                         String mediumBest, String mediumDate,
                                         String hardBest, String hardDate) {
        easyBestLabel.setText(easyBest);
        easyDateLabel.setText(easyDate);
        mediumBestLabel.setText(mediumBest);
        mediumDateLabel.setText(mediumDate);
        hardBestLabel.setText(hardBest);
        hardDateLabel.setText(hardDate);
    }
    
    @Override
    public void updateGameHistoryDisplay(String[][] historyData) {
        historyTableModel.setRowCount(0);
        for (String[] row : historyData) {
            historyTableModel.addRow(row);
        }
    }
    
    @Override
    public void updateSavedGamesDisplay(Object[][] savedGamesData) {
        savedGamesTableModel.setRowCount(0);
        for (Object[] row : savedGamesData) {
            savedGamesTableModel.addRow(row);
        }
    }
    
    @Override
    public void setBackAction(ActionListener listener) {
        logouButton.addActionListener(listener);
    }
    
    @Override
    public void setNewGameAction(ActionListener listener) {
        newGameButton.addActionListener(listener);
    }
    
    @Override
    public void setLoadSavedAction(ActionListener listener) {
        loadGameButton.addActionListener(listener);
    }
    
    @Override
    public void setDeleteSavedAction(ActionListener listener) {
        deleteGameButton.addActionListener(listener);
    }
    
    @Override
    public int getSelectedSavedGameId() {
        return selectedSavedGameId;
    }

    @Override
    public void setSettingsAction(ActionListener listener) {
        settingsButton.addActionListener(listener);
    }
}