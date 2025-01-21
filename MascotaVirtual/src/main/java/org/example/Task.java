//clase de TAREAS
package org.example;

import java.io.Serializable;

public class Task implements Serializable {
    //attributes
    private String description;
    private boolean complete;

    //constructor
    public Task(String description) {
        this.complete = false;
        this.description = description;
    }

    //methods
    public void writeTask(String description){
        this.description = description;
    }

    public void completeTask() {
        this.complete = true;
    }

    public boolean isComplete() {
        return complete;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return (complete ? "[✔] " : "[ ] ") + description;
    }
}
