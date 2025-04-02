package theory;

import java.io.FileInputStream;
import java.io.IOException;

public class Encodings1 {
    public static void main(String[] args) {


//        int number = 0b11101100110010010;
//        // число 121234 в двоичной системе счисления (binary)
//        // при записи в файл будет записан только младший байт числа 0b11101100110010010, то есть 10010010
//        byte lowerByte = (byte) number;
//        System.out.println(lowerByte);


        System.out.printf(Integer.toBinaryString(110));
        System.out.println(String.format("%8s", Integer.toBinaryString(-110)).replace(' ', '0'));
        System.out.println(String.format("%8s", Integer.toBinaryString(-120 & 0xFF)).replace(' ', '0'));
        System.out.println("__________________________________________");

//        // запись в файл output1.txt
//        try (FileOutputStream fileOutputStream = new FileOutputStream("output1.txt")) {
//            fileOutputStream.write(121234);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        // чтение из файла output1.txt 1ый вариант
//        try (InputStreamReader fileInputStream = new InputStreamReader(new FileInputStream("output1.txt"), Charset.forName("windows-1251"))) {
//            char[] buffer = new char[1024];
//            int bytesRead;
//            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
//                String text = new String(buffer, 0, bytesRead);
//                System.out.print(text);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        //        // чтение из файла output1.txt 2ой вариант
        try (FileInputStream fis = new FileInputStream("output1.txt")) {
            int byteRead;

            while ((byteRead = fis.read()) != -1) {
                System.out.println(Integer.toBinaryString(byteRead));
                System.out.println(byteRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

