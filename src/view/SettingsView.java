package view;

import interfaces.SettingsUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

//MARK: STILL HAS TO BE DONE
public class SettingsView extends JFrame implements SettingsUI {
    private JComboBox<String> difficultyCombo;
    private JButton saveButton;
    private JButton backButton;
    
    public SettingsView() {
        setTitle("Settings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        //def grid difficulty
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Default Difficulty:"), gbc);
        gbc.gridx = 1;
        difficultyCombo = new JComboBox<>(new String[]{"Easy", "Hard"});
        mainPanel.add(difficultyCombo, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        //buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        saveButton = new JButton("Save");
        backButton = new JButton("Back");
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setSize(400, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    @Override public void close() { dispose(); }
    @Override public void showError(String message) { JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE); }
    @Override public void showSuccess(String message) { JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE); }
    @Override public String getSelectedDifficulty() { return (String) difficultyCombo.getSelectedItem(); }
    @Override public void setDifficultyOptions(String[] difficulties) { difficultyCombo.setModel(new DefaultComboBoxModel<>(difficulties)); }
    @Override public void setBackAction(ActionListener listener) { backButton.addActionListener(listener); }
    @Override public void setSaveAction(ActionListener listener) { saveButton.addActionListener(listener); }
}