package view;

import javax.swing.*;

import interfaces.RegisterUI;

import java.awt.*;
import java.awt.event.ActionListener;

public class RegisterView extends JFrame implements RegisterUI {
    private JTextField usernameField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private JPasswordField confirmPasswordField = new JPasswordField(20);
    private JButton registerButton = new JButton("Create Account");
    private JButton backButton = new JButton("Back to Login");

    public RegisterView() {
        setTitle("Kakuro Master - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        JLabel titleLabel = new JLabel("CREATE ACCOUNT");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(236, 240, 241));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridx = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField.setFont(new Font("SansSerif", Font.BOLD, 10));
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 9));
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0; 
        gbc.gridy = 2;
        JLabel confirmPasswordLabel = new JLabel("Confirm password:");
        confirmPasswordLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        formPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        confirmPasswordField.setFont(new Font("SansSerif", Font.BOLD, 9));
        formPanel.add(confirmPasswordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(236, 240, 241));
        styleButton(registerButton, new Color(46, 204, 113));
        styleButton(backButton, new Color(149, 165, 166));
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setSize(400, 300);
        setVisible(true);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    @Override
    public void close() {
        this.dispose();
    }

    @Override
    public String getUsername() { 
        return usernameField.getText(); 
    }
    
    @Override
    public String getPassword() { 
        return new String(passwordField.getPassword()); 
    }
    
    @Override
    public String getConfirmPassword() { 
        return new String(confirmPasswordField.getPassword()); 
    }
    
    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    @Override
    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void setRegisterAction(ActionListener listener) { 
        registerButton.addActionListener(listener); 
    }
    
    @Override
    public void setBackAction(ActionListener listener) { 
        backButton.addActionListener(listener); 
    }
}
