package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class Main {
    private static JLabel levelLabel;
    private static JLabel pointsLabel;
    private static JProgressBar progressBar;
    private static Pet mascota;

    private static JList<Task> createTaskList(DefaultListModel<Task> model, TaskList taskManager, String taskType) {
        JList<Task> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Task task = (Task) value;
                setText((task.isComplete() ? "✓ " : "◻ ") + task.toString());
                if (task.isComplete()) {
                    setForeground(new Color(100, 100, 100));
                    setBackground(new Color(240, 240, 240));
                } else {
                    setForeground(Color.BLACK);
                    setBackground(Color.WHITE);
                }
                return c;
            }
        });

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = list.locationToIndex(e.getPoint());
                    if (index != -1) {
                        Task task = model.getElementAt(index);
                        if (!task.isComplete()) {
                            taskManager.completeTask(task);
                            mascota.evolve();
                            updatePetDisplay();
                            list.repaint();
                        }
                    }
                }
            }
        });
        return list;
    }

    private static JPanel createTaskTab(DefaultListModel<Task> model, TaskList taskManager, String taskType,
            int xpValue) {
        JPanel panel = new JPanel(new BorderLayout());

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel infoLabel = new JLabel(
                String.format("<html><div style='text-align: center; color: #555;'>" +
                        "<b>%s Tasks</b><br>Double-click to complete (+%d XP)<br>" +
                        "Completed tasks appear grayed out</div></html>",
                        taskType.substring(0, 1).toUpperCase() + taskType.substring(1), xpValue));
        infoPanel.add(infoLabel);

        // Task list
        JList<Task> taskList = createTaskList(model, taskManager, taskType);
        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBorder(new EmptyBorder(5, 5, 15, 5));

        JButton addButton = new JButton("Add New Task");
        styleButton(addButton, new Color(200, 230, 255)); // Light blue background
        addButton.addActionListener(e -> {
            String taskName = JOptionPane.showInputDialog(panel, "Enter new task name:");
            if (taskName != null && !taskName.trim().isEmpty()) {
                Task newTask = new Task(taskName);
                taskManager.addTask(newTask);
                model.addElement(newTask);
            }
        });

        JButton clearButton = new JButton("Clear Completed");
        styleButton(clearButton, new Color(255, 200, 200)); // Light red background
        clearButton.addActionListener(e -> {
            int completedCount = 0;
            for (int i = model.size() - 1; i >= 0; i--) {
                if (model.getElementAt(i).isComplete()) {
                    taskManager.removeTask(model.getElementAt(i));
                    model.remove(i);
                    completedCount++;
                }
            }
            if (completedCount > 0) {
                JOptionPane.showMessageDialog(panel,
                        "Cleared " + completedCount + " completed " + taskType + " tasks",
                        "Tasks Cleared", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private static JPanel createPetPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 245, 245));

        // Pet status panel
        JPanel statusPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        statusPanel.setBorder(new EmptyBorder(10, 10, 20, 10));
        statusPanel.setBackground(new Color(245, 245, 245));

        levelLabel = new JLabel("Level: " + mascota.getLevel(), SwingConstants.CENTER);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 24));
        levelLabel.setForeground(new Color(70, 70, 70));

        pointsLabel = new JLabel("XP: " + mascota.getPoints() + "/" + mascota.getCondition(), SwingConstants.CENTER);
        pointsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        pointsLabel.setForeground(new Color(90, 90, 90));

        progressBar = new JProgressBar(0, mascota.getCondition());
        progressBar.setValue(mascota.getPoints() % mascota.getCondition());
        progressBar.setStringPainted(true);
        progressBar.setString("Level Progress");
        progressBar.setFont(new Font("Arial", Font.PLAIN, 12));
        progressBar.setForeground(new Color(70, 130, 180));
        progressBar.setBackground(Color.WHITE);

        statusPanel.add(levelLabel);
        statusPanel.add(pointsLabel);
        statusPanel.add(progressBar);

        // Pet image placeholder
        JLabel petImage = new JLabel(new ImageIcon(), SwingConstants.CENTER);
        petImage.setPreferredSize(new Dimension(200, 200));
        petImage.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        petImage.setOpaque(true);
        petImage.setBackground(Color.WHITE);

        // Interaction buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        buttonPanel.setBorder(new EmptyBorder(20, 30, 10, 30));

        JButton feedButton = new JButton("Feed (+5 XP)");
        styleButton(feedButton, new Color(200, 255, 200)); // Light green background
        feedButton.addActionListener(e -> {
            mascota.setPoints(5);
            mascota.evolve();
            updatePetDisplay();
        });

        JButton playButton = new JButton("Play (+10 XP)");
        styleButton(playButton, new Color(200, 200, 255)); // Light blue background
        playButton.addActionListener(e -> {
            mascota.setPoints(10);
            mascota.evolve();
            updatePetDisplay();
        });

        buttonPanel.add(feedButton);
        buttonPanel.add(playButton);

        panel.add(statusPanel, BorderLayout.NORTH);
        panel.add(petImage, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private static void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK); // Black text for all buttons
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
    }

    private static void updatePetDisplay() {
        levelLabel.setText("Level: " + mascota.getLevel());
        pointsLabel.setText("XP: " + mascota.getPoints() + "/" + mascota.getCondition());
        progressBar.setMaximum(mascota.getCondition());
        progressBar.setValue(mascota.getPoints() % mascota.getCondition());
    }

    public static void main(String[] args) {
        // Load data
        mascota = Pet.loadPet();
        weeklyTasks semanal = weeklyTasks.loadTask(mascota);
        dailyTasks diaria = dailyTasks.loadTask(mascota);

        // Create main frame
        JFrame frame = new JFrame("Virtual Pet & Task Manager");
        frame.setSize(900, 600);
        frame.setMinimumSize(new Dimension(700, 500));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        // Daily Tasks Tab
        DefaultListModel<Task> dailyModel = new DefaultListModel<>();
        diaria.getTasks().forEach(dailyModel::addElement);
        tabbedPane.addTab("Daily Tasks", createTaskTab(dailyModel, diaria, "daily", 10));

        // Weekly Tasks Tab
        DefaultListModel<Task> weeklyModel = new DefaultListModel<>();
        semanal.getTasks().forEach(weeklyModel::addElement);
        tabbedPane.addTab("Weekly Tasks", createTaskTab(weeklyModel, semanal, "weekly", 30));

        // Pet Tab
        tabbedPane.addTab("My Pet", createPetPanel());

        // Window closing handler
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(frame,
                        "Save and exit? Your progress will be saved.",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    mascota.evolve();
                    mascota.savePet();
                    semanal.saveTask();
                    diaria.saveTask();
                    System.exit(0);
                }
            }
        });

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}