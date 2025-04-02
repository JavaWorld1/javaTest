package theory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Encodings {
    public static void main(String[] args) {

        // Создаем объекты Path для файлов, которые нужно удалить
        Path path1 = Paths.get("output1.txt");
        Path path2 = Paths.get("output2.txt");

        try {
            // Проверяем, существуют ли файлы перед удалением
            if (Files.exists(path1)) {
                // Удаляем первый файл
                Files.delete(path1);
                System.out.println("Файл output1.txt успешно удален.");
            } else {
                System.out.println("Файл output1.txt не существует.");
            }

            if (Files.exists(path2)) {
                // Удаляем второй файл
                Files.delete(path2);
                System.out.println("Файл output2.txt успешно удален.");
            } else {
                System.out.println("Файл output2.txt не существует.");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при удалении файлов: " + e.getMessage());
        }


        // запись в файл output1.txt
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream("output1.txt"), Charset.forName("windows-1251"))) {
//            System.out.println(Charset.defaultCharset());
//            System.out.println(outputStreamWriter.getEncoding());
            outputStreamWriter.write("привет願");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // запись в файл output2.txt
        try (FileWriter fileWriter = new FileWriter("output2.txt", Charset.forName("windows-1251"))) {
            fileWriter.write("привет願");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // чтение из файла output2.txt
        // при чтении из файла нужно в конструкторе InputStreamReader указать кодировку, которая будет использоваться
        // при преобразовании байтов в символы - Charset.forName("windows-1251") или StandardCharsets.UTF_8, но
        // StandardCharsets не поддерживает windows кодировки, там вообще около пяти кодировок
        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream("output2.txt"), Charset.forName("windows-1251"))) {

            char[] buffer = new char[1024];
            int bytesRead;
            // В рамках одного вызова метод read(char[] buffer) может прочитать несколько символов, а не по одному символу за один вызов метода.
            // Когда метод read(char[] buffer) вызывается второй раз, курсор останется на следующем символе за последним прочитанным.
            // Когда вы снова вызываете read, он продолжит чтение с этого места.
            // Когда метод read(char[] buffer) достигает конца файла (EOF - End of File), он возвращает -1 для указания, что больше данных для чтения нет.
//            bytesRead = inputStreamReader.read(buffer);
//            System.out.println(bytesRead);
//            System.out.println(buffer);
            while ((bytesRead = inputStreamReader.read(buffer)) != -1) {
                // индексация символов в строке начинается с 0
                // создается строка из символов, начиная с индекса 0 и заканчивая индексом bytesRead - 1, что в данном случае будет от 0 до 6 включительно
                // Если bytesRead изменяется в зависимости от количества реально прочитанных символов при каждом вызове read(char[] buffer),
                // то строка будет перезаписываться в каждой итерации цикла.
                // Это позволяет вам обрабатывать данные по мере их поступления и не хранить все символы в одной большой строке.
                String text = new String(buffer, 0, bytesRead);
                System.out.print(text);
            }
//            System.out.println(bytesRead);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
