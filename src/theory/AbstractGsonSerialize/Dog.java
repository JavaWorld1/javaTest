package theory.AbstractGsonSerialize;

import java.time.LocalDate;

class Dog extends Animal {
    private String breed;
    private LocalDate birthDate;

    public Dog(String name, String breed, LocalDate birthDate) {
        super(name);
        this.breed = breed;
        this.birthDate = birthDate;
    }

    public String getBreed() {
        return breed;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public String toString() {
        return "Dog{name='" + getName() + "', breed='" + breed + "', birthDate=" + birthDate + "}";
    }
}