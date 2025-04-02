package theory.Composition;

public class Person {
    private String name;
    private Heart heart;
    private Brain brain;

    public Person(String name) {
        this.name = name;
        this.heart = new Heart();
        this.brain = new Brain();
    }

    public void doActivities() {
        heart.beat();
        brain.think();
    }
}


