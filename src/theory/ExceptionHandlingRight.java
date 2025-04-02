package theory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExceptionHandlingRight {
    public static void main(String[] args) {
        // Создание файла и запись в него строки
        try {
            String content = "шестнадцатеричный код";
            Files.write(Paths.get("example.txt"), content.getBytes());
            System.out.println("Файл успешно создан.");
        } catch (IOException e) {
            System.err.println("Ошибка создания файла: " + e.getMessage());
        }

        try (BufferedReader br = new BufferedReader(new FileReader("example.txt"))) {
            try {
                String line = br.readLine(); // Попытка чтения строки из файла
                System.out.println("Прочитанная строка: " + line);

                // Исключение, которое может возникнуть после чтения строки
                int number = Integer.parseInt(line);
                System.out.println("Число: " + number);
            } catch (NumberFormatException e) {
                System.err.println("Ошибка преобразования строки в число: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }
}
