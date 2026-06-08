package ui;

import model.Task;
import storage.StorageManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TodoFrame extends JFrame {
    private JPanel tasksPanel;
    private JScrollPane scrollPane;
    private JLabel statusLabel;
    private List<Task> tasks;
    private StorageManager storage;
    
    public TodoFrame() {
        setTitle("My TODO App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        storage = new StorageManager();
        tasks = storage.loadTasks();
        
        initializeUI();
        refreshTasksDisplay();
    }
    
    private void initializeUI() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout(10, 5));
        JLabel titleLabel = new JLabel("TODO List Manager", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addButton = new JButton("+ Add New Task");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.addActionListener(e -> addNewTask());
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshTasksDisplay());
        
        JButton sortByDateButton = new JButton("Sort by Date");
        sortByDateButton.addActionListener(e -> sortTasksByDate());
        
        JButton sortByPriorityButton = new JButton("Sort by Priority");
        sortByPriorityButton.addActionListener(e -> sortTasksByPriority());
        
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(sortByDateButton);
        buttonPanel.add(sortByPriorityButton);
        
        headerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Tasks panel
        tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        
        scrollPane = new JScrollPane(tasksPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel();
        statusPanel.add(statusLabel);
        
        // Assemble main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private void addNewTask() {
        AddTaskDialog dialog = new AddTaskDialog(this, null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            tasks.add(dialog.getTask());
            storage.saveTasks(tasks);
            refreshTasksDisplay();
            showStatusMessage("Task added successfully!");
        }
    }
    
    private void refreshTasksDisplay() {
        tasksPanel.removeAll();
        
        if (tasks.isEmpty()) {
            JLabel emptyLabel = new JLabel("No tasks yet! Click 'Add New Task' to get started.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            tasksPanel.add(emptyLabel);
        } else {
            for (Task task : tasks) {
                TaskPanel taskPanel = new TaskPanel(task, () -> {
                    // This will be called when task is updated/deleted
                    storage.saveTasks(tasks);
                    refreshTasksDisplay();
                    showStatusMessage("Task updated!");
                });
                tasksPanel.add(taskPanel);
                tasksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        updateStatusBar();
        tasksPanel.revalidate();
        tasksPanel.repaint();
    }
    
    private void updateStatusBar() {
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(Task::isCompleted).count();
        long pendingTasks = totalTasks - completedTasks;
        long overdueTasks = tasks.stream()
            .filter(t -> !t.isCompleted() && t.getDueDate().isBefore(java.time.LocalDate.now()))
            .count();
        
        statusLabel.setText(String.format(
            "Total: %d | Completed: %d | Pending: %d | Overdue: %d",
            totalTasks, completedTasks, pendingTasks, overdueTasks
        ));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    }
    
    private void sortTasksByDate() {
        tasks.sort(Comparator.comparing(Task::getDueDate));
        refreshTasksDisplay();
        showStatusMessage("Tasks sorted by due date");
    }
    
    private void sortTasksByPriority() {
        tasks.sort((t1, t2) -> t2.getPriority().compareTo(t1.getPriority()));
        refreshTasksDisplay();
        showStatusMessage("Tasks sorted by priority (High to Low)");
    }
    
    private void showStatusMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}