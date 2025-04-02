package theory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExceptionHandlingFalse {
    private static final Logger logger = LogManager.getLogger(ExceptionHandlingFalse.class);

    public static void main(String[] args) {
        //Configurator.initialize(null, "log4j2.xml");

        // В Java, после того как блок catch поймал и обработал исключение, программа будет продолжать выполнение сразу после блока catch,
        // если не было других инструкций, которые прерывают выполнение программы внутри блока catch. Поэтому, если в блоке catch не было других инструкций, которые прерывают выполнение программы
        // (например, выбрасывание исключения), то программа продолжит выполнение сразу после блока catch.

        // Создание файла и запись в него строки
        try {
//            System.out.println("java.class.path");
            String content = "шестнадцатеричный код";
            Files.write(Paths.get("example.txt"), content.getBytes());
            logger.info("Файл успешно создан."); // Логирование информации о процессе обработки исключения с использованием Log4j
        } catch (IOException e) {
            logger.error("Ошибка создания файла", e); // Логирование ошибки
        }

        // внутренний блок catch не ловит NumberFormatException, поэтому, если NumberFormatException произойдет во внутреннем блоке try,
        // управление будет передано во внешний блок catch, так как он охватывает оба типа исключений (NumberFormatException и IOException).
        // Таким образом, в случае возникновения NumberFormatException, обработка будет происходить во внешнем блоке catch, где будет выведено сообщение об ошибке чтения файла.
        try (BufferedReader br = new BufferedReader(new FileReader("example.txt"))) {
            try {
                String line = br.readLine(); // Попытка чтения строки из файла
                logger.info("Прочитанная строка: " + line); // Логирование информации о прочитанной строке

                // Исключение, которое может возникнуть после чтения строки
                int number = Integer.parseInt(line);
                logger.info("Число: " + number); // Логирование информации о числе
            } catch (IOException e) {
                logger.error("Ошибка преобразования строки в число", e); // Логирование ошибки с использованием Log4j
            }
        } catch (NumberFormatException | IOException e) {
            if (e instanceof NumberFormatException nfe) {
                logger.error("Ошибка преобразования строки в число", nfe); // Логирование ошибки с использованием Log4j
            } else if (e instanceof IOException ioe) {
                logger.error("Ошибка чтения файла", ioe); // Логирование ошибки с использованием Log4j
            }
            logger.info("Информация о процессе обработки исключения: " + e.getMessage()); // Логирование информации о процессе обработки исключения
        }
    }
}