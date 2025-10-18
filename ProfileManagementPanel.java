package com.isp.ui.views.customer;

import com.isp.dao.CustomerDAO;
import com.isp.dao.PlanDAO;
import com.isp.dao.UserDAO;
import com.isp.model.Customer;
import com.isp.model.Plan;
// REMOVED: import com.isp.model.User - not needed after removing password change
// REMOVED: import com.isp.ui.components.ModernPasswordField - not needed
import com.isp.ui.components.ModernButton;
import com.isp.ui.components.ModernTextField;
import com.isp.ui.utils.UIConstants;
// REMOVED: import com.isp.util.PasswordUtil - not needed

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ProfileManagementPanel extends JPanel {
    private final CustomerDAO customerDAO;
    // userDAO kept for potential future use
    @SuppressWarnings("unused")
    private final UserDAO userDAO;
    private final PlanDAO planDAO;
    private final int userId;
    
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JLabel planNameLabel;
    private JLabel planSpeedLabel;
    private JLabel planPriceLabel;
    private JLabel connectionStatusLabel;
    private JLabel registrationDateLabel;

    public ProfileManagementPanel(int userId) {
        this.userId = userId;
        this.customerDAO = new CustomerDAO();
        this.userDAO = new UserDAO();
        this.planDAO = new PlanDAO();
        
        initializeUI();
        loadCustomerData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        centerPanel.add(createPersonalInfoPanel());
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createPlanInfoPanel());
        centerPanel.add(Box.createVerticalStrut(20));
        // REMOVED: createSecurityPanel() - Security section removed from profile

        return centerPanel;
    }

    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR), 
                "Personal Information", TitledBorder.LEFT, TitledBorder.TOP, 
                UIConstants.SUBTITLE_FONT, UIConstants.TEXT_PRIMARY),
            new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        firstNameField = new ModernTextField();
        lastNameField = new ModernTextField();
        emailField = new ModernTextField();
        phoneField = new ModernTextField();
        
        addressArea = new JTextArea(3, 20);
        addressArea.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setFont(UIConstants.NORMAL_FONT);

        registrationDateLabel = new JLabel();
        registrationDateLabel.setFont(UIConstants.NORMAL_FONT);
        
        connectionStatusLabel = new JLabel();
        connectionStatusLabel.setFont(UIConstants.SUBTITLE_FONT);

        addFormField(panel, gbc, "First Name:", firstNameField);
        addFormField(panel, gbc, "Last Name:", lastNameField);
        addFormField(panel, gbc, "Email:", emailField);
        addFormField(panel, gbc, "Phone:", phoneField);
        addFormField(panel, gbc, "Address:", new JScrollPane(addressArea));
        addFormField(panel, gbc, "Registration Date:", registrationDateLabel);
        addFormField(panel, gbc, "Connection Status:", connectionStatusLabel);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        ModernButton saveButton = new ModernButton("Save Changes");
        saveButton.addActionListener(e -> savePersonalInfo());

        ModernButton cancelButton = new ModernButton("Cancel");
        cancelButton.addActionListener(e -> loadCustomerData());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createPlanInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR), 
                "Current Plan", TitledBorder.LEFT, TitledBorder.TOP, 
                UIConstants.SUBTITLE_FONT, UIConstants.TEXT_PRIMARY),
            new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        planNameLabel = new JLabel();
        planNameLabel.setFont(UIConstants.SUBTITLE_FONT);
        
        planSpeedLabel = new JLabel();
        planSpeedLabel.setFont(UIConstants.NORMAL_FONT);
        
        planPriceLabel = new JLabel();
        planPriceLabel.setFont(UIConstants.NORMAL_FONT);

        addFormField(panel, gbc, "Plan Name:", planNameLabel);
        addFormField(panel, gbc, "Speed:", planSpeedLabel);
        addFormField(panel, gbc, "Monthly Price:", planPriceLabel);

        return panel;
    }

    // REMOVED: createSecurityPanel() method - Security section removed from profile

    private void loadCustomerData() {
        Customer customer = customerDAO.getCustomerByUserId(userId);
        
        if (customer != null) {
            firstNameField.setText(customer.getFirstName());
            lastNameField.setText(customer.getLastName());
            emailField.setText(customer.getEmail());
            phoneField.setText(customer.getPhone());
            addressArea.setText(customer.getAddress());
            registrationDateLabel.setText(customer.getRegistrationDate().toString());
            
            // Set connection status with color
            connectionStatusLabel.setText(customer.getConnectionStatus().toUpperCase());
            if ("active".equalsIgnoreCase(customer.getConnectionStatus())) {
                connectionStatusLabel.setForeground(UIConstants.SUCCESS_COLOR);
            } else {
                connectionStatusLabel.setForeground(UIConstants.DANGER_COLOR);
            }

            // Load plan info
            if (customer.getPlanId() != null) {
                Plan plan = planDAO.getPlanById(customer.getPlanId());
                if (plan != null) {
                    planNameLabel.setText(plan.getPlanName());
                    planSpeedLabel.setText(plan.getDataDescription());
                    planPriceLabel.setText(plan.getFormattedPrice() + "/month");
                }
            } else {
                planNameLabel.setText("No plan assigned");
                planSpeedLabel.setText("-");
                planPriceLabel.setText("-");
            }
        }
    }

    private void savePersonalInfo() {
        Customer customer = customerDAO.getCustomerByUserId(userId);
        
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Customer not found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate inputs
        if (firstNameField.getText().trim().isEmpty() || 
            lastNameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update customer
        customer.setFirstName(firstNameField.getText().trim());
        customer.setLastName(lastNameField.getText().trim());
        customer.setEmail(emailField.getText().trim());
        customer.setPhone(phoneField.getText().trim());
        customer.setAddress(addressArea.getText().trim());

        if (customerDAO.updateCustomer(customer)) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // REMOVED: showChangePasswordDialog() method - Security section removed from profile

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.NORMAL_FONT);
        
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        
        gbc.gridy++;
    }
}
