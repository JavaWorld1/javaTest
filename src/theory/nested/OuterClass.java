package theory.nested;

public class OuterClass {
    // Статический вложенный класс
    public static class NestedStaticClass {
        public void display() {
            System.out.println("This is a static theory.nested class");
        }
    }

    // Обычный (нестатический) вложенный класс
    public class InnerClass {
        public void display() {
            System.out.println("This is an inner class");
        }
    }
}


