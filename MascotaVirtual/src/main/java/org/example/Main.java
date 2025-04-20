package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    private static JList<Task> createTaskList(DefaultListModel<Task> model, TaskList taskManager) {
        JList<Task> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Task task = (Task) value;
                setText((task.isComplete() ? "[✓] " : "[ ] ") + task.toString());
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
                            list.repaint();
                        } else {
                            taskManager.clearTask();
                            model.clear();
                            taskManager.getTasks().forEach(model::addElement);
                        }
                    }
                }
            }
        });
        return list;
    }

    public static void main(String[] args) {
        Pet mascota = Pet.loadPet();
        weeklyTasks semanal = weeklyTasks.loadTask(mascota);
        dailyTasks diaria = dailyTasks.loadTask(mascota);

        JFrame frame = new JFrame("Pet Task Manager");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Create models
        DefaultListModel<Task> dailyModel = new DefaultListModel<>();
        diaria.getTasks().forEach(dailyModel::addElement);

        DefaultListModel<Task> weeklyModel = new DefaultListModel<>();
        semanal.getTasks().forEach(weeklyModel::addElement);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Daily Tasks Tab
        JPanel dailyPanel = new JPanel(new BorderLayout());
        JList<Task> dailyList = createTaskList(dailyModel, diaria);
        dailyPanel.add(new JScrollPane(dailyList), BorderLayout.CENTER);
        tabbedPane.addTab("Daily Tasks (+10 XP)", dailyPanel);

        // Weekly Tasks Tab
        JPanel weeklyPanel = new JPanel(new BorderLayout());
        JList<Task> weeklyList = createTaskList(weeklyModel, semanal);
        weeklyPanel.add(new JScrollPane(weeklyList), BorderLayout.CENTER);
        tabbedPane.addTab("Weekly Tasks (+30 XP)", weeklyPanel);

        // Add Task Button
        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(e -> {
            String taskName = JOptionPane.showInputDialog(frame, "Enter task name:");
            if (taskName != null && !taskName.trim().isEmpty()) {
                Task newTask = new Task(taskName);

                if (tabbedPane.getSelectedIndex() == 0) { // Daily tab
                    diaria.addTask(newTask);
                    dailyModel.addElement(newTask);
                } else { // Weekly tab
                    semanal.addTask(newTask);
                    weeklyModel.addElement(newTask);
                }
            }
        });

        // Layout
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.add(addButton, BorderLayout.SOUTH);

        // Window closing handler
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to exit?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    mascota.savePet();
                    semanal.saveTask();
                    diaria.saveTask();
                    System.exit(0);
                }
            }
        });

        frame.setVisible(true);
    }
}