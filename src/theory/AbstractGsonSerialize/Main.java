package theory.AbstractGsonSerialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Dog dog = new Dog("Rex", "Labrador", LocalDate.of(2015, 5, 20));

        Gson gson1 = new GsonBuilder()
                .registerTypeAdapter(Animal.class, new AnimalAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();

        // Сериализация
        String json = gson1.toJson(dog,  Animal.class);
        System.out.println("Serialized JSON main: ");
        System.out.println(json);

        // Десериализация
        try {
            Animal animal = gson1.fromJson(json, Animal.class);
            System.out.println("Deserialized object: ");
            System.out.println(animal);

            if (animal instanceof Dog) {
                Dog deserializedDog = (Dog) animal;
                System.out.println("Breed: " + deserializedDog.getBreed());
                System.out.println("Birth Date: " + deserializedDog.getBirthDate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
