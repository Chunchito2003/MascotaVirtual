package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {

    public static void main(String[] args) {
        // Cargar la mascota y las tareas
        Pet mascota = Pet.loadPet();
        weeklyTasks semanal = weeklyTasks.loadTask(mascota);
        dailyTasks diaria = dailyTasks.loadTask(mascota);

        JFrame frame = new JFrame("Mi App Swing");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Crear modelo para JList
        DefaultListModel<Task> taskListModel = new DefaultListModel<>();
        for (Task task : semanal.getTasks()) {
            taskListModel.addElement(task);
        }

        // Crear JList con el modelo
        JList<Task> taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(taskList);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Botón para agregar una tarea
        JButton addTaskButton = new JButton("Agregar Tarea");
        addTaskButton.addActionListener(e -> {
            String newTaskName = JOptionPane.showInputDialog(frame, "Nueva tarea:");
            if (newTaskName != null && !newTaskName.isEmpty()) {
                Task newTask = new Task(newTaskName);
                semanal.addTask(newTask);
                taskListModel.addElement(newTask);
            }
        });
        frame.add(addTaskButton, BorderLayout.SOUTH);

        // agregar MouseListener para detectar doble clic y asi completar las tareas
        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Detectar doble clic
                    int selectedIndex = taskList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        Task selectedTask = taskListModel.get(selectedIndex);
                        if (!selectedTask.isComplete()) {
                            selectedTask.completeTask();
                            mascota.setPoints(30);
                            taskList.repaint(); // Refrescar la lista para reflejar el cambio
                        } else {
                            semanal.clearTask();
                        }
                    }
                }
            }
        });

        // Agregar WindowListener para guardar datos al cerrar
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                        frame,
                        "¿Estás seguro de que deseas salir?",
                        "Confirmar salida",
                        JOptionPane.YES_NO_OPTION
                );
                if (opcion == JOptionPane.YES_OPTION) {
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
