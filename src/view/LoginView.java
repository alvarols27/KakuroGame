package view;

import javax.swing.*;
import interfaces.LoginUI;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JFrame implements LoginUI {
    private JTextField usernameField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private JButton loginButton = new JButton("Login");
    private JButton registerButton = new JButton("Register");
    private JButton guestButton = new JButton("Continue as Guest");

    public LoginView() {
        setTitle("Kakuro Master - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185)); 
        JLabel titleLabel = new JLabel("KAKURO MASTER");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        //form
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
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        formPanel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 9));
        formPanel.add(passwordField, gbc);

        //buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(236, 240, 241));
        styleButton(loginButton, new Color(46, 204, 113));
        styleButton(registerButton, new Color(52, 152, 219));
        styleButton(guestButton, new Color(149, 165, 166));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(guestButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Guest info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(236, 240, 241));
        JLabel infoLabel = new JLabel("Guest mode: Your progress won't be saved");
        infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        infoLabel.setForeground(Color.GRAY);
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);

        setSize(450, 350);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
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
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    @Override
    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public void close() {
        this.dispose();
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    @Override
    public void setLoginAction(ActionListener listener) { 
        loginButton.addActionListener(listener); 
    }
    
    @Override
    public void setRegisterAction(ActionListener listener) { 
        registerButton.addActionListener(listener); 
    }
    
    @Override
    public void setGuestAction(ActionListener listener) { 
        guestButton.addActionListener(listener); 
    }
}
