import java.io.*;
import java.util.List;
import java.util.Scanner;

public class FileOperator { // for read & write file

    public static void StringToFile(String path, List<String> str_list) {
        try {
            System.out.println("Output -> " + path);
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            for (String str : str_list) {
                writer.write(str);
                writer.write("\n");
            }

            writer.close();

        } catch (IOException e) {
            System.out.println("ERROR TO WRITE FILE!!!");
        }
    }

    public static String FileToString(String path) {
        StringBuilder data = new StringBuilder();
        try {
            File file = new File(path);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                data.append(reader.nextLine()).append(" \n ");
            }
            data.append(" \n ");
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND!!!");
        }
        return data.toString();
    }
}