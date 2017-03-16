package Model;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tommy on 09.03.2017.
 */
public class FileHandler {

    private static byte[][] def = new byte[1000][1000];

    public static byte[][] readFile(Reader reader) throws IOException, PatternFormatException {

        ArrayList<Integer> list = new ArrayList<>();

        int nextNum = reader.read();
        list.add(nextNum);
        while (nextNum > -1){
            nextNum = reader.read();
            list.add(nextNum);
        }
        list.remove(list.size()-1);

        char[] file = new char[list.size()];
        for (int i = 0; i < list.size(); i++) {
            file[i] = (char) ( + list.get(i));
        }

        String wholeFile = new String(file);

        char ext = wholeFile.charAt(0);

        System.out.println(wholeFile);

        switch (ext) {
            case '#': {

                String[] file1 = wholeFile.split("\\n");
                return readRle(file1);
            }
            case '!': {
               String[] file1 = wholeFile.split("\\n");
               return readCells(file1);
            }

            default:
                throw new PatternFormatException("Unsupported pattern!");
        }
    }

    public static byte[][] readFromURL(String url) throws IOException, PatternFormatException{

        URL destination = new URL(url);
        URLConnection conn = destination.openConnection();

        return readFile(new InputStreamReader(conn.getInputStream()));

    }

    public static byte[][] readFromDisk(File file) throws IOException, PatternFormatException {
          return readFile(new FileReader(file));
    }


    private static byte[][] readRle(String[] str) {
        return def;
    }

    private static byte[][] readCells(String[] str) {

        int comments = 0;
        int height = 0;
        int width = 0;

        for (String s : str) {
            if (s.charAt(0) != '!') {
                height++;
                width = width < s.length() - 1 ? s.length() - 1 : width;
            } else {
                comments++;
            }

        }

        System.out.printf("Height: %d \nWidth: %d", height,width);

        byte[][] board = new byte[1000][1000];

        for (int i = comments; i < str.length; i++) {
            for (int j = 0; j < str[i].length(); j++) {
                if (str[i].charAt(j) == 'O') {
                    board[j][(i - comments)] = 1;
                }
            }
        }

        return board;
    }
}
