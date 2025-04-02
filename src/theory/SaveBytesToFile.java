package theory;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SaveBytesToFile {
    public static void main(String[] args) {
        // Пример байтов, которые вы хотите сохранить
        byte[] bytesToSave = {65, 66, 67, 68, 69}; // Пример: ASCII коды A, B, C, D, E

        try {
            // Создаем объект DataOutputStream для записи данных в бинарном формате
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream("saveBytesToFile.txt"));

            // Записываем байты в файл
//            dataOutputStream.write(2);
            dataOutputStream.writeInt(16909060);

            // Закрываем потоки
            dataOutputStream.close();

            System.out.println("Байты успешно сохранены в файл: " + "saveBytesToFile.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
