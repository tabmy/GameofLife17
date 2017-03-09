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

                if (matcher.find())
                wholeFile = matcher.group();

                System.out.println(wholeFile);

                String[] file = wholeFile.split("\\n");
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

        int height = 0;
        int width = 0;

        for (String s: str) {
            if (s.charAt(0) != '!') {
                height++;
            }
            width = width > s.length() ? s.length() : width;
        }

        byte[][]board = new byte[height][width];


        return def;
    }
}
