package com.isp.ui.components;

import com.isp.dao.PaymentTransactionDAO;
import com.isp.model.PaymentTransaction;
import com.isp.model.Plan;
import com.isp.ui.utils.UIConstants;
import com.isp.ui.utils.UIHelper;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * Payment Gateway Dialog - Supports Card and UPI payments
 * Functional payment processing with mock gateway integration
 */
public class PaymentGatewayDialog extends JDialog {
    private final Plan selectedPlan;
    private final int customerId;
    private final PaymentTransactionDAO paymentDAO;

    // Payment method selection
    private JRadioButton cardPaymentRadio;
    private JRadioButton upiPaymentRadio;
    private ButtonGroup paymentMethodGroup;

    // Card payment fields
    private ModernTextField cardNumberField;
    private ModernTextField cardExpiryField;
    private ModernTextField cardCVVField;
    private ModernTextField cardHolderNameField;

    // UPI payment fields
    private ModernTextField upiIdField;
    private JButton generateQRButton;
    private JLabel qrCodeLabel;

    // Common fields
    private ModernButton proceedButton;
    private ModernButton cancelButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    // Payment result
    private boolean paymentSuccessful = false;
    private String transactionId = null;

    public PaymentGatewayDialog(Frame parent, Plan plan, int customerId) {
        super(parent, "Payment Gateway - " + plan.getPlanName(), true);
        this.selectedPlan = plan;
        this.customerId = customerId;
        this.paymentDAO = new PaymentTransactionDAO();

        initializeUI();
        setupEventHandlers();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createPaymentPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        // Initialize with card payment selected
        cardPaymentRadio.setSelected(true);
        updatePaymentFields();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                UIHelper.enableAntiAliasing(g2);
                GradientPaint gradient = new GradientPaint(0, 0, UIConstants.PRIMARY_COLOR,
                    getWidth(), 0, UIConstants.ACCENT_BLUE);
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setPreferredSize(new Dimension(500, 80));
        headerPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 20, 5, 20);

        JLabel titleLabel = new JLabel("💳 Secure Payment");
        titleLabel.setFont(UIConstants.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel planLabel = new JLabel(selectedPlan.getPlanName() + " - ₹" + selectedPlan.getPriceInr());
        planLabel.setFont(UIConstants.FONT_SUBTITLE);
        planLabel.setForeground(Color.WHITE);
        headerPanel.add(planLabel, gbc);

        return headerPanel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 0);

        // Payment method selection
        JPanel methodPanel = createPaymentMethodPanel();
        panel.add(methodPanel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);

        // Card payment panel
        JPanel cardPanel = createCardPaymentPanel();
        cardPanel.setVisible(true);
        panel.add(cardPanel, gbc);

        // UPI payment panel
        JPanel upiPanel = createUPIPaymentPanel();
        upiPanel.setVisible(false);
        gbc.gridy++;
        panel.add(upiPanel, gbc);

        // Status and progress
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 10, 0);
        statusLabel = new JLabel("Ready to process payment", SwingConstants.CENTER);
        statusLabel.setFont(UIConstants.FONT_BODY);
        statusLabel.setForeground(UIConstants.TEXT_SECONDARY);
        panel.add(statusLabel, gbc);

        gbc.gridy++;
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        progressBar.setString("Processing...");
        panel.add(progressBar, gbc);

        return panel;
    }

    private JPanel createPaymentMethodPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
            "Select Payment Method", 0, 0, UIConstants.FONT_SUBTITLE, UIConstants.PRIMARY_COLOR
        ));

        paymentMethodGroup = new ButtonGroup();

        cardPaymentRadio = new JRadioButton("💳 Credit/Debit Card");
        cardPaymentRadio.setFont(UIConstants.FONT_BODY);
        cardPaymentRadio.setBackground(Color.WHITE);
        cardPaymentRadio.addActionListener(e -> updatePaymentFields());

        upiPaymentRadio = new JRadioButton("📱 UPI Payment");
        upiPaymentRadio.setFont(UIConstants.FONT_BODY);
        upiPaymentRadio.setBackground(Color.WHITE);
        upiPaymentRadio.addActionListener(e -> updatePaymentFields());

        paymentMethodGroup.add(cardPaymentRadio);
        paymentMethodGroup.add(upiPaymentRadio);

        panel.add(cardPaymentRadio);
        panel.add(upiPaymentRadio);

        return panel;
    }

    private JPanel createCardPaymentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
            "Card Details", 0, 0, UIConstants.FONT_BODY, UIConstants.TEXT_SECONDARY
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Card holder name
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel nameLabel = new JLabel("Card Holder Name");
        nameLabel.setFont(UIConstants.FONT_BODY);
        panel.add(nameLabel, gbc);

        gbc.gridy++;
        cardHolderNameField = new ModernTextField(20);
        cardHolderNameField.setPlaceholder("Enter card holder name");
        panel.add(cardHolderNameField, gbc);

        // Card number
        gbc.gridy++;
        gbc.gridwidth = 2;
        JLabel numberLabel = new JLabel("Card Number");
        numberLabel.setFont(UIConstants.FONT_BODY);
        panel.add(numberLabel, gbc);

        gbc.gridy++;
        cardNumberField = new ModernTextField(20);
        cardNumberField.setPlaceholder("1234 5678 9012 3456");
        applyCardNumberFilter(cardNumberField);
        panel.add(cardNumberField, gbc);

        // Expiry and CVV
        gbc.gridy++;
        gbc.gridwidth = 1;
        JLabel expiryLabel = new JLabel("Expiry Date");
        expiryLabel.setFont(UIConstants.FONT_BODY);
        panel.add(expiryLabel, gbc);

        gbc.gridx = 1;
        JLabel cvvLabel = new JLabel("CVV");
        cvvLabel.setFont(UIConstants.FONT_BODY);
        panel.add(cvvLabel, gbc);

        gbc.gridx = 0; gbc.gridy++;
        cardExpiryField = new ModernTextField(10);
        cardExpiryField.setPlaceholder("MM/YY");
        applyExpiryFilter(cardExpiryField);
        panel.add(cardExpiryField, gbc);

        gbc.gridx = 1;
        cardCVVField = new ModernTextField(5);
        cardCVVField.setPlaceholder("123");
        applyCVVFilter(cardCVVField);
        panel.add(cardCVVField, gbc);

        return panel;
    }

    private JPanel createUPIPaymentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
            "UPI Payment", 0, 0, UIConstants.FONT_BODY, UIConstants.TEXT_SECONDARY
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // UPI ID field
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel upiLabel = new JLabel("UPI ID");
        upiLabel.setFont(UIConstants.FONT_BODY);
        panel.add(upiLabel, gbc);

        gbc.gridy++;
        upiIdField = new ModernTextField(20);
        upiIdField.setPlaceholder("9876543210@upi or rahul@hdfc");
        panel.add(upiIdField, gbc);

        // QR Code section
        gbc.gridy++;
        generateQRButton = new ModernButton("📱 Generate QR Code");
        generateQRButton.addActionListener(e -> generateQRCode());
        panel.add(generateQRButton, gbc);

        gbc.gridy++;
        qrCodeLabel = new JLabel("QR Code will appear here", SwingConstants.CENTER);
        qrCodeLabel.setFont(UIConstants.FONT_BODY);
        qrCodeLabel.setForeground(UIConstants.TEXT_SECONDARY);
        qrCodeLabel.setPreferredSize(new Dimension(200, 150));
        qrCodeLabel.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 2));
        qrCodeLabel.setOpaque(true);
        qrCodeLabel.setBackground(Color.WHITE);
        panel.add(qrCodeLabel, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        cancelButton = new ModernButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        proceedButton = new ModernButton("Pay ₹" + selectedPlan.getPriceInr());
        proceedButton.setBackground(UIConstants.SUCCESS_COLOR);
        proceedButton.addActionListener(e -> processPayment());

        panel.add(cancelButton);
        panel.add(proceedButton);

        return panel;
    }

    private void setupEventHandlers() {
        // Add enter key support for payment processing
        getRootPane().setDefaultButton(proceedButton);
    }

    private void updatePaymentFields() {
        boolean isCardPayment = cardPaymentRadio.isSelected();

        // Update panel visibility
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] subComponents = ((JPanel) comp).getComponents();
                for (Component subComp : subComponents) {
                    if (subComp instanceof JPanel) {
                        String title = null;
                        if (subComp instanceof JPanel) {
                            Border border = ((JPanel) subComp).getBorder();
                            if (border instanceof javax.swing.border.TitledBorder) {
                                title = ((javax.swing.border.TitledBorder) border).getTitle();
                            }
                        }

                        if ("Card Details".equals(title)) {
                            subComp.setVisible(isCardPayment);
                        } else if ("UPI Payment".equals(title)) {
                            subComp.setVisible(!isCardPayment);
                        }
                    }
                }
            }
        }

        revalidate();
        repaint();
    }

    private void generateQRCode() {
        String upiId = upiIdField.getText().trim();
        if (upiId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your UPI ID first!",
                "UPI ID Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mock QR code generation
        qrCodeLabel.setText("<html><center>📱<br>Scan to Pay<br>₹" + selectedPlan.getPriceInr() +
            "<br><br>UPI ID: " + upiId + "<br><br>[QR CODE IMAGE]</center></html>");
        qrCodeLabel.setForeground(UIConstants.PRIMARY_COLOR);

        JOptionPane.showMessageDialog(this,
            "QR Code generated! In a real app, this would show a scannable QR code.\n\n" +
            "Scan with your UPI app (Google Pay, PhonePe, Paytm, etc.) to complete payment.\n\n" +
            "Common UPI ID formats: 9876543210@upi, rahul@hdfc, etc.",
            "QR Code Generated", JOptionPane.INFORMATION_MESSAGE);
    }

    private void processPayment() {
        if (!validatePaymentDetails()) {
            return;
        }

        // Disable buttons and show progress
        proceedButton.setEnabled(false);
        cancelButton.setEnabled(false);
        progressBar.setVisible(true);
        statusLabel.setText("Processing payment...");
        statusLabel.setForeground(UIConstants.PRIMARY_COLOR);

        // Simulate payment processing
        CompletableFuture.runAsync(() -> {
            try {
                // Simulate gateway processing time
                Thread.sleep(2000);

                // Mock payment success (90% success rate)
                boolean success = Math.random() > 0.1;

                SwingUtilities.invokeLater(() -> {
                    if (success) {
                        completePayment();
                    } else {
                        handlePaymentFailure("Payment gateway error - please try again");
                    }
                });

            } catch (InterruptedException e) {
                SwingUtilities.invokeLater(() -> {
                    handlePaymentFailure("Payment interrupted");
                });
            }
        });
    }

    private boolean validatePaymentDetails() {
        if (cardPaymentRadio.isSelected()) {
            // Validate card details
            String cardHolder = cardHolderNameField.getText().trim();
            String cardNumber = cardNumberField.getText().replaceAll("\\s", "");
            String expiry = cardExpiryField.getText().trim();
            String cvv = cardCVVField.getText().trim();

            if (cardHolder.isEmpty()) {
                showValidationError("Please enter card holder name");
                return false;
            }

            if (!cardNumber.matches("\\d{16}")) {
                showValidationError("Please enter a valid 16-digit card number");
                return false;
            }

            if (!expiry.matches("\\d{2}/\\d{2}")) {
                showValidationError("Please enter expiry date in MM/YY format");
                return false;
            }

            if (!cvv.matches("\\d{3}")) {
                showValidationError("Please enter a valid 3-digit CVV");
                return false;
            }

        } else if (upiPaymentRadio.isSelected()) {
            // Validate UPI details
            String upiId = upiIdField.getText().trim();

            if (upiId.isEmpty()) {
                showValidationError("Please enter your UPI ID");
                return false;
            }

            if (!upiId.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+$")) {
                showValidationError("Please enter a valid UPI ID (e.g., 9876543210@upi or rahul@hdfc)");
                return false;
            }
        }

        return true;
    }

    private void completePayment() {
        try {
            // Create payment transaction
            PaymentTransaction transaction = new PaymentTransaction();
            transaction.setCustomerId(customerId);
            transaction.setAmount(selectedPlan.getPriceInr());
            transaction.setPaymentType("recharge");
            transaction.setPaymentMethod(cardPaymentRadio.isSelected() ? "debit_card" : "upi");
            transaction.setGatewayName("MockGateway");
            transaction.setGatewayTransactionId("MOCK" + System.currentTimeMillis());
            transaction.setStatus("success");
            transaction.setCompletedAt(LocalDateTime.now());

            if (cardPaymentRadio.isSelected()) {
                String cardNumber = cardNumberField.getText().replaceAll("\\s", "");
                transaction.setCardLast4Digits(cardNumber.substring(cardNumber.length() - 4));
            } else {
                transaction.setUpiId(upiIdField.getText().trim());
            }

            // Save transaction
            int createdTransactionId = paymentDAO.createTransaction(transaction);

            if (createdTransactionId > 0) {
                this.transactionId = "TXN" + createdTransactionId;
                paymentSuccessful = true;

                progressBar.setVisible(false);
                statusLabel.setText("✅ Payment successful!");
                statusLabel.setForeground(UIConstants.SUCCESS_COLOR);

                JOptionPane.showMessageDialog(this,
                    "🎉 Payment Successful!\n\n" +
                    "Plan: " + selectedPlan.getPlanName() + "\n" +
                    "Amount: ₹" + selectedPlan.getPriceInr() + "\n" +
                    "Transaction ID: " + this.transactionId + "\n\n" +
                    "Your plan has been activated!",
                    "Payment Success", JOptionPane.INFORMATION_MESSAGE);

                dispose();
            } else {
                handlePaymentFailure("Failed to save transaction");
            }

        } catch (Exception e) {
            handlePaymentFailure("Payment processing error: " + e.getMessage());
        }
    }

    private void handlePaymentFailure(String reason) {
        progressBar.setVisible(false);
        statusLabel.setText("❌ Payment failed");
        statusLabel.setForeground(UIConstants.DANGER_COLOR);

        proceedButton.setEnabled(true);
        cancelButton.setEnabled(true);

        JOptionPane.showMessageDialog(this,
            "Payment Failed\n\nReason: " + reason + "\n\nPlease try again or contact support.",
            "Payment Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    // Input filters
    private void applyCardNumberFilter(ModernTextField field) {
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '\b') {
                    evt.consume();
                }

                // Auto-format with spaces
                SwingUtilities.invokeLater(() -> {
                    String text = field.getText().replaceAll("\\s", "");
                    if (text.length() > 16) {
                        text = text.substring(0, 16);
                    }
                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < text.length(); i++) {
                        if (i > 0 && i % 4 == 0) {
                            formatted.append(" ");
                        }
                        formatted.append(text.charAt(i));
                    }
                    field.setText(formatted.toString());
                });
            }
        });
    }

    private void applyExpiryFilter(ModernTextField field) {
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '/' && c != '\b') {
                    evt.consume();
                }

                SwingUtilities.invokeLater(() -> {
                    String text = field.getText().replaceAll("[^0-9]", "");
                    if (text.length() >= 2) {
                        text = text.substring(0, 2) + "/" + text.substring(2, Math.min(text.length(), 4));
                    }
                    field.setText(text);
                });
            }
        });
    }

    private void applyCVVFilter(ModernTextField field) {
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '\b') {
                    evt.consume();
                }

                String text = field.getText();
                if (text.length() >= 3) {
                    field.setText(text.substring(0, 3));
                }
            }
        });
    }

    // Getters for result
    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }

    public String getTransactionId() {
        return transactionId;
    }
}