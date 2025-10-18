package com.isp.ui.views;
import com.isp.model.User;
import com.isp.service.AuthService;
import com.isp.ui.components.ModernButton;
import com.isp.ui.components.ModernTextField;
import com.isp.ui.utils.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
public class LoginWindow extends JFrame {
    private JTabbedPane tabbedPane;
    // Login Tab
    private ModernTextField loginPhoneField;
    private ModernTextField loginOTPField;
    private ModernButton sendLoginOTPButton;
    private ModernButton verifyLoginButton;
    private JLabel loginStatusLabel;
    // Signup Tab
    private ModernTextField signupPhoneField;
    private ModernTextField signupNameField;
    private ModernTextField signupEmailField;
    private ModernTextField signupOTPField;
    private ModernButton sendSignupOTPButton;
    private ModernButton registerButton;
    private JLabel signupStatusLabel;
    private final AuthService authService;
    public LoginWindow() {
        authService = new AuthService();
        initializeUI();
    }
    private void initializeUI() {
        setTitle("ISP Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 750);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                com.isp.ui.utils.UIHelper.enableAntiAliasing(g2);
                GradientPaint gradient = new GradientPaint(0, 0, UIConstants.PRIMARY_BACKGROUND,
                    getWidth(), getHeight(), new Color(20, 30, 50));
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.FONT_BUTTON);
        tabbedPane.setBackground(UIConstants.SECONDARY_BACKGROUND);
        tabbedPane.setForeground(UIConstants.TEXT_PRIMARY);
        tabbedPane.addTab("Login", createLoginPanel());
        tabbedPane.addTab("Sign Up", createSignupPanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                com.isp.ui.utils.UIHelper.enableAntiAliasing(g2);
                GradientPaint gradient = new GradientPaint(0, 0, UIConstants.ACCENT_PURPLE,
                    getWidth(), 0, UIConstants.ACCENT_BLUE);
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(600, 120));
        headerPanel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("ISP Management");
        titleLabel.setFont(UIConstants.FONT_TITLE);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Secure Portal Access");
        subtitleLabel.setFont(UIConstants.FONT_LABEL);
        subtitleLabel.setForeground(UIConstants.TEXT_SECONDARY);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);

        headerPanel.add(textPanel);
        return headerPanel;
    }
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                com.isp.ui.utils.UIHelper.enableAntiAliasing(g2);
                g2.setColor(UIConstants.SECONDARY_BACKGROUND);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        JLabel titleLabel = new JLabel("Login with Phone & OTP", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.FONT_SUBTITLE);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        panel.add(titleLabel, gbc);

        panel.add(Box.createVerticalStrut(20), gbc);

        JLabel phoneLabel = new JLabel("Phone Number (10 digits)");
        phoneLabel.setFont(UIConstants.FONT_BODY);
        phoneLabel.setForeground(UIConstants.TEXT_SECONDARY);
        panel.add(phoneLabel, gbc);

        loginPhoneField = new ModernTextField(20);
        loginPhoneField.setPreferredSize(new Dimension(350, 40));
        applyNumericFilter(loginPhoneField, 10);
        panel.add(loginPhoneField, gbc);

        gbc.insets = new Insets(12, 0, 12, 0);
        sendLoginOTPButton = new ModernButton("Send OTP");
        sendLoginOTPButton.setPreferredSize(new Dimension(350, 45));
        sendLoginOTPButton.addActionListener(e -> handleLoginSendOTP());
        panel.add(sendLoginOTPButton, gbc);

        gbc.insets = new Insets(8, 0, 8, 0);
        JLabel otpLabel = new JLabel("Enter 6-Digit OTP");
        otpLabel.setFont(UIConstants.FONT_BODY);
        otpLabel.setForeground(UIConstants.TEXT_SECONDARY);
        panel.add(otpLabel, gbc);

        loginOTPField = new ModernTextField(20);
        loginOTPField.setPreferredSize(new Dimension(350, 40));
        loginOTPField.setEnabled(false);
        applyNumericFilter(loginOTPField, 6);
        panel.add(loginOTPField, gbc);

        gbc.insets = new Insets(12, 0, 12, 0);
        verifyLoginButton = new ModernButton("Verify & Login");
        verifyLoginButton.setPreferredSize(new Dimension(350, 45));
        verifyLoginButton.setEnabled(false);
        verifyLoginButton.addActionListener(e -> handleLoginVerify());
        panel.add(verifyLoginButton, gbc);
        loginStatusLabel = new JLabel("", SwingConstants.CENTER);
        loginStatusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        gbc.insets = new Insets(15, 0, 10, 0);
        panel.add(loginStatusLabel, gbc);
        loginPhoneField.addActionListener(e -> handleLoginSendOTP());
        loginOTPField.addActionListener(e -> handleLoginVerify());
        return panel;
    }

    private JPanel createSignupPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                com.isp.ui.utils.UIHelper.enableAntiAliasing(g2);
                g2.setColor(UIConstants.SECONDARY_BACKGROUND);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.FONT_SUBTITLE);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        panel.add(titleLabel, gbc);

        panel.add(Box.createVerticalStrut(15), gbc);

        JLabel nameLabel = new JLabel("Full Name");
        panel.add(nameLabel, gbc);

        signupNameField = new ModernTextField(20);
        signupNameField.setPreferredSize(new Dimension(350, 40));
        panel.add(signupNameField, gbc);

        JLabel emailLabel = new JLabel("Email Address");
        panel.add(emailLabel, gbc);

        signupEmailField = new ModernTextField(20);
        signupEmailField.setPreferredSize(new Dimension(350, 40));
        panel.add(signupEmailField, gbc);

        JLabel phoneLabel = new JLabel("Phone Number (10 digits)");
        panel.add(phoneLabel, gbc);

        signupPhoneField = new ModernTextField(20);
        signupPhoneField.setPreferredSize(new Dimension(350, 40));
        applyNumericFilter(signupPhoneField, 10); // Limit to 10 digits
        panel.add(signupPhoneField, gbc);

        gbc.insets = new Insets(12, 0, 12, 0);
        sendSignupOTPButton = new ModernButton("Send Verification OTP");
        sendSignupOTPButton.setPreferredSize(new Dimension(350, 45));
        sendSignupOTPButton.addActionListener(e -> handleSignupSendOTP());
        panel.add(sendSignupOTPButton, gbc);

        gbc.insets = new Insets(8, 0, 8, 0);
        JLabel otpLabel = new JLabel("Enter 6-Digit OTP");
        panel.add(otpLabel, gbc);

        signupOTPField = new ModernTextField(20);
        signupOTPField.setPreferredSize(new Dimension(350, 40));
        signupOTPField.setEnabled(false);
        applyNumericFilter(signupOTPField, 6); // Limit to 6 digits
        panel.add(signupOTPField, gbc);

        gbc.insets = new Insets(12, 0, 12, 0);
        registerButton = new ModernButton("Create Account");
        registerButton.setPreferredSize(new Dimension(350, 45));
        registerButton.setEnabled(false);
        registerButton.addActionListener(e -> handleSignupRegister());
        panel.add(registerButton, gbc);

        signupStatusLabel = new JLabel("", SwingConstants.CENTER);
        signupStatusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        gbc.insets = new Insets(15, 0, 10, 0);
        panel.add(signupStatusLabel, gbc);

        return panel;
    }

    private void handleLoginSendOTP() {
        String phone = loginPhoneField.getText().trim();
        
        if (!validatePhone(phone)) {
            showError("Please enter a valid 10-digit phone number!");
            return;
        }
        
        sendLoginOTPButton.setEnabled(false);
        sendLoginOTPButton.setText("Sending...");
        loginStatusLabel.setText("Sending OTP...");
        loginStatusLabel.setForeground(Color.BLUE);
        
        new SwingWorker<String, Void>() {
            protected String doInBackground() {
                return authService.sendOTP(phone);
            }

            protected void done() {
                try {
                    String otp = get();
                    if (otp != null) {
                        loginOTPField.setEnabled(true);
                        verifyLoginButton.setEnabled(true);
                        loginPhoneField.setEnabled(false);
                        sendLoginOTPButton.setText("OTP Sent!");
                        sendLoginOTPButton.setBackground(new Color(16, 185, 129));
                        loginStatusLabel.setText("OTP sent! Check terminal for OTP code.");
                        loginStatusLabel.setForeground(new Color(16, 185, 129));
                        loginOTPField.requestFocus();
                        
                        // OTP is printed in terminal/database only - no popup dialog
                    } else {
                        showError("Phone not registered! Please sign up first.");
                        resetLoginSendButton();
                    }
                } catch (Exception e) {
                    showError("Failed to send OTP");
                    resetLoginSendButton();
                }
            }
        }.execute();
    }

    private void handleLoginVerify() {
        String phone = loginPhoneField.getText().trim();
        String otp = loginOTPField.getText().trim();
        
        if (!validateOTP(otp)) {
            showError("Please enter a valid 6-digit OTP!");
            return;
        }
        
        verifyLoginButton.setEnabled(false);
        verifyLoginButton.setText("Verifying...");
        
        new SwingWorker<User, Void>() {
            protected User doInBackground() {
                return authService.loginWithOTP(phone, otp);
            }
            
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        loginStatusLabel.setText("Login successful!");
                        loginStatusLabel.setForeground(new Color(16, 185, 129));
                        Thread.sleep(500);
                        openDashboard(user);
                        dispose();
                    } else {
                        showError("Invalid or expired OTP!");
                        resetLoginVerifyButton();
                        loginOTPField.setText("");
                    }
                } catch (Exception e) {
                    showError("Login failed");
                    resetLoginVerifyButton();
                }
            }
        }.execute();
    }

    private void handleSignupSendOTP() {
        String name = signupNameField.getText().trim();
        String email = signupEmailField.getText().trim();
        String phone = signupPhoneField.getText().trim();
        
        if (name.isEmpty()) {
            showError("Please enter your full name!");
            return;
        }
        
        if (!validateEmail(email)) {
            showError("Please enter a valid email!");
            return;
        }
        
        if (!validatePhone(phone)) {
            showError("Please enter a valid 10-digit phone!");
            return;
        }
        
        sendSignupOTPButton.setEnabled(false);
        sendSignupOTPButton.setText("Sending...");
        
        new SwingWorker<String, Void>() {
            protected String doInBackground() {
                return authService.generateSignupOTP(phone);
            }

            protected void done() {
                try {
                    String otp = get();
                    if (otp != null) {
                        signupOTPField.setEnabled(true);
                        registerButton.setEnabled(true);
                        signupPhoneField.setEnabled(false);
                        signupNameField.setEnabled(false);
                        signupEmailField.setEnabled(false);
                        sendSignupOTPButton.setText("OTP Sent!");
                        sendSignupOTPButton.setBackground(new Color(16, 185, 129));
                        signupStatusLabel.setText("OTP sent! Check terminal for OTP code.");
                        signupStatusLabel.setForeground(new Color(16, 185, 129));
                        signupOTPField.requestFocus();

                        // OTP is printed in terminal/database only - no popup dialog
                    } else {
                        showError("Failed to send OTP");
                        resetSignupSendButton();
                    }
                } catch (Exception e) {
                    showError("Error sending OTP");
                    resetSignupSendButton();
                }
            }
        }.execute();
    }

    private void handleSignupRegister() {
        String name = signupNameField.getText().trim();
        String email = signupEmailField.getText().trim();
        String phone = signupPhoneField.getText().trim();
        String otp = signupOTPField.getText().trim();
        
        if (!validateOTP(otp)) {
            showError("Please enter a valid 6-digit OTP!");
            return;
        }
        
        registerButton.setEnabled(false);
        registerButton.setText("Creating Account...");
        
        new SwingWorker<User, Void>() {
            protected User doInBackground() {
                return authService.signupWithOTP(name, email, phone, otp);
            }
            
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        signupStatusLabel.setText("Account created!");
                        signupStatusLabel.setForeground(new Color(16, 185, 129));
                        JOptionPane.showMessageDialog(LoginWindow.this,
                            "Welcome " + name + "!\nAccount created successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        Thread.sleep(500);
                        openDashboard(user);
                        dispose();
                    } else {
                        showError("Invalid OTP or phone already registered!");
                        resetSignupRegisterButton();
                    }
                } catch (Exception e) {
                    showError("Registration failed");
                    resetSignupRegisterButton();
                }
            }
        }.execute();
    }

    private boolean validatePhone(String phone) {
        phone = phone.replaceAll("[\\s\\-()]", "");
        return phone.matches("^\\d{10}$");
    }

    private boolean validateOTP(String otp) {
        return otp.matches("^\\d{6}$");
    }

    private boolean validateEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * Document filter to limit input to digits only with max length
     */
    private static class NumericDocumentFilter extends DocumentFilter {
        private final int maxLength;
        
        public NumericDocumentFilter(int maxLength) {
            this.maxLength = maxLength;
        }
        
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                throws BadLocationException {
            if (string == null) return;
            
            // Only allow digits
            if (string.matches("\\d+")) {
                // Check if adding this string would exceed max length
                if ((fb.getDocument().getLength() + string.length()) <= maxLength) {
                    super.insertString(fb, offset, string, attr);
                }
            }
        }
        
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                throws BadLocationException {
            if (text == null) {
                super.replace(fb, offset, length, text, attrs);
                return;
            }
            
            // Only allow digits
            if (text.matches("\\d+")) {
                // Check if replacing would exceed max length
                int newLength = fb.getDocument().getLength() - length + text.length();
                if (newLength <= maxLength) {
                    super.replace(fb, offset, length, text, attrs);
                }
            } else if (text.isEmpty()) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
    
    /**
     * Apply numeric filter to a text field
     */
    private void applyNumericFilter(ModernTextField field, int maxLength) {
        AbstractDocument doc = (AbstractDocument) field.getDocument();
        doc.setDocumentFilter(new NumericDocumentFilter(maxLength));
    }

    private void resetLoginSendButton() {
        sendLoginOTPButton.setEnabled(true);
        sendLoginOTPButton.setText("Send OTP");
        sendLoginOTPButton.setBackground(UIConstants.PRIMARY_COLOR);
        loginStatusLabel.setText("");
    }

    private void resetLoginVerifyButton() {
        verifyLoginButton.setEnabled(true);
        verifyLoginButton.setText("Verify & Login");
        loginStatusLabel.setText("");
    }

    private void resetSignupSendButton() {
        sendSignupOTPButton.setEnabled(true);
        sendSignupOTPButton.setText("Send Verification OTP");
        sendSignupOTPButton.setBackground(UIConstants.PRIMARY_COLOR);
        signupStatusLabel.setText("");
    }

    private void resetSignupRegisterButton() {
        registerButton.setEnabled(true);
        registerButton.setText("Create Account");
        signupStatusLabel.setText("");
    }

    private void openDashboard(User user) {
        try {
            String role = user.getRole().toLowerCase();
            
            if ("admin".equals(role)) {
                AdminDashboard dashboard = new AdminDashboard();
                dashboard.setVisible(true);
            } else if ("customer".equals(role)) {
                CustomerDashboard dashboard = new CustomerDashboard();
                dashboard.setVisible(true);
            } else {
                showError("Unknown role: " + role);
            }
            
            // Close login window after dashboard opens
            this.dispose();
        } catch (Exception e) {
            System.err.println("❌ Error opening dashboard: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to open dashboard: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true));
    }
}
