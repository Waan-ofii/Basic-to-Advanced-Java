package ui;

import model.Task;
import utils.DateUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddTaskDialog extends JDialog {
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField dueDateField;
    private JComboBox<Task.Priority> priorityCombo;
    private boolean confirmed = false;
    private Task existingTask;
    
    public AddTaskDialog(Frame parent, Task taskToEdit) {
        super(parent, taskToEdit == null ? "Add New Task" : "Edit Task", true);
        this.existingTask = taskToEdit;
        initializeUI();
        
        if (existingTask != null) {
            loadTaskData();
        }
        
        setLocationRelativeTo(parent);
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Title:*"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        titleField = new JTextField(20);
        formPanel.add(titleField, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane, gbc);
        
        // Due Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Due Date (YYYY-MM-DD):*"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dueDateField = new JTextField(20);
        dueDateField.setText(LocalDate.now().toString());
        formPanel.add(dueDateField, gbc);
        
        // Priority
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Priority:*"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        priorityCombo = new JComboBox<>(Task.Priority.values());
        formPanel.add(priorityCombo, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveTask());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setMinimumSize(new Dimension(500, 400));
    }
    
    private void loadTaskData() {
        titleField.setText(existingTask.getTitle());
        descriptionArea.setText(existingTask.getDescription());
        dueDateField.setText(DateUtils.formatDate(existingTask.getDueDate()));
        priorityCombo.setSelectedItem(existingTask.getPriority());
    }
    
    private void saveTask() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String dueDateStr = dueDateField.getText().trim();
        if (dueDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Due date is required!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        LocalDate dueDate;
        try {
            dueDate = DateUtils.parseDate(dueDateStr);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid date format! Please use YYYY-MM-DD format.", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Task.Priority priority = (Task.Priority) priorityCombo.getSelectedItem();
        String description = descriptionArea.getText().trim();
        
        if (existingTask == null) {
            existingTask = new Task(title, description, dueDate, priority);
        } else {
            existingTask.setTitle(title);
            existingTask.setDescription(description);
            existingTask.setDueDate(dueDate);
            existingTask.setPriority(priority);
        }
        
        confirmed = true;
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Task getTask() {
        return existingTask;
    }
}