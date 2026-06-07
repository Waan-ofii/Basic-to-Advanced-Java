package ui;

import model.Task;
import utils.DateUtils;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class TaskPanel extends JPanel {
    private Task task;
    private JCheckBox checkBox;
    private JLabel titleLabel;
    private JLabel dueDateLabel;
    private JLabel priorityLabel;
    private JTextArea descriptionArea;
    private Runnable onTaskUpdated;
    
    public TaskPanel(Task task, Runnable onTaskUpdated) {
        this.task = task;
        this.onTaskUpdated = onTaskUpdated;
        initializeUI();
        updateTaskDisplay();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setBackground(Color.WHITE);
        
        // Top panel with checkbox and title
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBackground(Color.WHITE);
        
        checkBox = new JCheckBox();
        checkBox.addActionListener(e -> {
            task.setCompleted(checkBox.isSelected());
            updateTaskDisplay();
            if (onTaskUpdated != null) {
                onTaskUpdated.run();
            }
        });
        
        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(checkBox, BorderLayout.WEST);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Priority label
        priorityLabel = new JLabel();
        priorityLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(priorityLabel);
        
        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        // Due date label
        dueDateLabel = new JLabel();
        dueDateLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        
        // Description area
        descriptionArea = new JTextArea(2, 30);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(Color.WHITE);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Button panel for edit/delete
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> editTask());
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteTask());
        
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        // Assemble the panel
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(dueDateLabel, BorderLayout.NORTH);
        centerPanel.add(descriptionArea, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void updateTaskDisplay() {
        titleLabel.setText(task.getTitle());
        
        if (task.isCompleted()) {
            titleLabel.setForeground(Color.GRAY);
            titleLabel.setText("<html><strike>" + task.getTitle() + "</strike></html>");
        } else {
            titleLabel.setForeground(Color.BLACK);
            titleLabel.setText(task.getTitle());
        }
        
        // Set due date text with color coding
        String dueText = "Due: " + DateUtils.formatDate(task.getDueDate());
        if (DateUtils.isOverdue(task.getDueDate()) && !task.isCompleted()) {
            dueDateLabel.setForeground(Color.RED);
            dueText += " (OVERDUE!)";
        } else if (DateUtils.isToday(task.getDueDate()) && !task.isCompleted()) {
            dueDateLabel.setForeground(Color.ORANGE);
            dueText += " (Today)";
        } else {
            dueDateLabel.setForeground(Color.GRAY);
        }
        dueDateLabel.setText(dueText);
        
        // Set priority with color coding
        String priorityText = "Priority: " + task.getPriority().getDisplayName();
        priorityLabel.setText(priorityText);
        
        switch (task.getPriority()) {
            case HIGH:
                priorityLabel.setForeground(Color.RED);
                break;
            case MEDIUM:
                priorityLabel.setForeground(Color.ORANGE);
                break;
            case LOW:
                priorityLabel.setForeground(Color.GREEN);
                break;
        }
        
        // Set description
        descriptionArea.setText(task.getDescription());
        
        checkBox.setSelected(task.isCompleted());
    }
    
    private void editTask() {
        AddTaskDialog dialog = new AddTaskDialog(SwingUtilities.getWindowAncestor(this), task);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            updateTaskDisplay();
            if (onTaskUpdated != null) {
                onTaskUpdated.run();
            }
        }
    }
    
    private void deleteTask() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this task?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION && onTaskUpdated != null) {
            onTaskUpdated.run(); // This will trigger deletion in the parent
        }
    }
    
    public Task getTask() {
        return task;
    }
}