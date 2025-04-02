package theory.nested;

public class Tools {
    public static void main(String[] args) {
        // Создание объекта статического вложенного класса
        OuterClass.NestedStaticClass nestedObject = new OuterClass.NestedStaticClass(); // OuterClass.NestedStaticClass() это статический вложенный класс, просто такое длинное имя
        nestedObject.display();

        // Создание объекта внутреннего (нестатического) вложенного класса
        OuterClass.InnerClass outerObject = new OuterClass().new InnerClass();

//        Синтаксис OuterClass.InnerClass outerObject = new OuterClass().InnerClass(); некорректен, потому что он не соответствует правилам создания объекта внутреннего (нестатического) класса в Java.
//        Внутренние классы зависят от экземпляра внешнего класса, и поэтому они не могут быть созданы напрямую через new OuterClass().InnerClass();.

    }
}
