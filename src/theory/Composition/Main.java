package theory.Composition;

import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        Person person = new Person("John");
        person.doActivities();

        String s1 = "ef";
        String s2 = new String("ef");

        System.out.println(s1.equals(s2)); // true, because str1 and str2 have the same content
        System.out.println(Objects.equals(s1,s2)); // true, because str1 and str2 have the same content
        System.out.println(s1==s2); // because str1 and str2 have not the same address
    }
}
