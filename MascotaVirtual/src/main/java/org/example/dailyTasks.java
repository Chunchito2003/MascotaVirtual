//clase de LISTA DIARIA
package org.example;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class dailyTasks implements TaskList, Serializable {

    private static final long serialVersionUID = 3L;
    // attributes
    private ArrayList<Task> taskArrayList;
    private Pet pet;
    private LocalDate currentDay; // Fecha de hoy

    // constructor
    public dailyTasks(Pet petInstance) {
        taskArrayList = new ArrayList<>();
        pet = petInstance;
        this.currentDay = LocalDate.now();
    }

    // methods
    public void addTask(Task task) {
        taskArrayList.add(task);
    }

    @Override
    public void removeTask(Task task) {
        taskArrayList.remove(task);
    }

    // metodo para serializar
    public void saveTask() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dailyTasks.ser"))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    // metodo para deserializar
    public static dailyTasks loadTask(Pet petInstance) {
        File file = new File("dailyTasks.ser");
        if (!file.exists()) {
            return new dailyTasks(petInstance);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            dailyTasks loadedTasks = (dailyTasks) ois.readObject();
            loadedTasks.pet = petInstance; // Asocia la mascota cargada
            return loadedTasks;
        } catch (IOException | ClassNotFoundException e) {
            return new dailyTasks(petInstance);
        }
    }

    @Override
    public void clearTask() {
        // este metodo va con un if preguntando si el dia ya paso
        LocalDate today = LocalDate.now();
        if (!today.equals(currentDay)) {
            currentDay = today; // actualizar la fecha
            taskArrayList.clear();
        }
    }

    @Override
    public void completeTask(Task task) {
        if (taskArrayList.contains(task) && !task.isComplete()) {
            task.completeTask(); // marca la tarea como hecha
            pet.setPoints(10); // Add XP for the task to the pet
        }
    }

    public String[] getTaskListAsArray() {// este metodo es para la interfaz
        return taskArrayList.stream()
                .map(Task::toString)
                .toArray(String[]::new);
    }

    public ArrayList<Task> getTasks() {
        return taskArrayList;
    }
}
