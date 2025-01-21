//clase de LISTAS SEMANALES
package org.example;

import java.io.*;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.ArrayList;

public class weeklyTasks implements TaskList, Serializable {

    private static final long serialVersionUID = 3L; // cada vez q se cambia algo esto se tiene q actualizar
    // Attributes
    private ArrayList<Task> weeklyTasksArraylist;
    private Pet pet;
    private LocalDate weekEndDate; // Fecha límite dinámica de la semana

    // Constructor
    public weeklyTasks(Pet petInstance) {
        this.weeklyTasksArraylist = new ArrayList<>();
        pet = petInstance;
        this.weekEndDate = calculateWeekEndDate(); // Calcula el próximo domingo
    }

    // Methods
    public void addTask(Task task) {
        weeklyTasksArraylist.add(task);
    }

    @Override
    public void removeTask(Task task) {
        weeklyTasksArraylist.remove(task);
    }

    //metodo para serializar
    @Override
    public void saveTask() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("weeklyTasks.ser"))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    //metodo para deserializar
    public static weeklyTasks  loadTask(Pet petInstance) {
        File file = new File("weeklyTasks.ser");
        if (!file.exists()) {
            return new weeklyTasks(petInstance);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            weeklyTasks loadedTasks = (weeklyTasks) ois.readObject();
            loadedTasks.pet = petInstance; // Asocia la mascota cargada
            return loadedTasks;
        } catch (IOException | ClassNotFoundException e) {
            return new weeklyTasks(petInstance);
        }
    }

    @Override
    public void clearTask() {
        LocalDate today = LocalDate.now();
        if (today.isAfter(weekEndDate)) {
            weeklyTasksArraylist.clear();
            weekEndDate = calculateWeekEndDate(); // Actualiza el fin de la semana para la proxima semana

            //https://javautodidacta.es/tiempo-en-java-localdate-localtime/
            //para saber como funciona el manejo de fechas en java
        }
    }

    @Override
    public void completeTask(Task task) {
        if (weeklyTasksArraylist.contains(task) && !task.isComplete()) {
            task.completeTask();
            pet.setPoints(30); // Add XP for the task to the pet
        }
    }

    // metodo para calcular el siguiente domingo
    private LocalDate calculateWeekEndDate() {
        LocalDate today = LocalDate.now();
        int daysUntilSunday = DayOfWeek.SUNDAY.getValue() - today.getDayOfWeek().getValue();
        if (daysUntilSunday < 0) {
            daysUntilSunday += 7; // Ajusta para la próxima semana si hoy es domingo
        }
        return today.plusDays(daysUntilSunday);
    }

    public ArrayList<Task> getTasks() {
        return weeklyTasksArraylist;
    }

    public String[] getTaskListAsArray() {//este metodo es para la interfaz
        return weeklyTasksArraylist.stream()
                .map(Task::toString)
                .toArray(String[]::new);
    }
}
