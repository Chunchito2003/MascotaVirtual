//esta interfaz se implementa por las dos listas(semanales y diarias)
// ya que las dos tienen practimente los mismos metodos pero con un par de cambios
package org.example;

import java.util.ArrayList;

public interface TaskList {

    void addTask(Task task);

    void removeTask(Task task);

    void saveTask();

    void clearTask();

    void completeTask(Task task);

    ArrayList<Task> getTasks();
}
