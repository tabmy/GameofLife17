package Model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tommy on 09.03.2017.
 */
public class FileHandler {

    private static byte[][] def = new byte[1000][1000];

    public static byte[][] readFile(Reader reader, long length, String ext) throws IOException {

        char chars[] = new char[(int) length];
        reader.read(chars);
        String wholeFile = new String(chars);

        switch (ext){
            case "rle" : {

                String[] file = wholeFile.split("\\n");
                return readRle(file);
            }
            case "cells" : {
                Pattern pattern = Pattern.compile("[.O]{2,}");
                Matcher matcher = pattern.matcher(wholeFile);

                //System.out.println(wholeFile);

                StringBuilder sb = new StringBuilder();

                while (matcher.find())
                sb.append(matcher.group() + "\n");



                String[] file = sb.toString().split("\\n");
                return readCells(file);
            }

            default: throw new UnsupportedOperationException("Not coded yet");
        }
    }

    public static byte[][] readFromDisk(File file) throws IOException {
        String ext = file.toString();
        String[] token = ext.split("\\.");
        ext = (token[token.length-1]).toLowerCase();

        return readFile(new FileReader(file), file.length(), ext);
    }


    private static byte[][] readRle(String[] str) {
        return def;
    }

    private static byte[][] readCells(String[] str) {

        int height = str.length;
        int width = 0;

        for (String s: str) {
            System.out.println(s);
            width = width < s.length() ? s.length() : width;
        }

        System.out.println("Height: " + height + "\nWidth: " + width);

        byte[][] board = new byte[1000][1000];

        for (int i = 0; i < height ; i++) {
            for (int j = 0; j < str[i].length() ; j++) {
                if(str[i].charAt(j) == 'O'){
                    board[i + 20][j + 20] = 1;
                }
            }
        }

        return board;
    }
}
