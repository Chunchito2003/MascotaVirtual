//clase de la MASCOTA
package org.example;
import java.io.*;

public class Pet implements Serializable {

    private static final long serialVersionUID = 2L;


    private int level;
    private int points;
    private int condition;

    public Pet() {
        this.level = 1;
        this.points = 0;
        this.condition= 50;
    }
    //aca se pasana los puntos de las tareas para ser guardados en la mascota
    public void setPoints(int points){
        this.points += points;
    }

    public int getPoints() {
        return points;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void evolve(){
        while (points >= condition) { // mientras los puntos sean suficientes para evolucionar
            level++;
            points -= condition; // restamos los puntos utilizados para evolucionar
            condition += 50; // incrementamos la condicion para el siguiente nivel
        }
    }

    public void savePet(){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("pet.ser"))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static Pet loadPet() {
        File file = new File("pet.ser");
        if (!file.exists()) {
            System.out.println("Archivo de mascota no encontrado. Creando nueva mascota...");
            return new Pet(); // Retorna una nueva instancia si el archivo no existe
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Pet) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar la mascota: " + e.getMessage());
            return new Pet(); // Retorna una nueva instancia en caso de error
        }
    }
}
