package Model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Created by Tommy on 09.03.2017.
 */
public class FileHandler {

    private static byte[][] def = new byte[1000][1000];

    public static byte[][] readFile(Reader reader, long length, String ext) throws IOException {

        char chars[] = new char[(int) length];
        reader.read(chars);
        String wholeFile = new String(chars);
        String[] file = wholeFile.split("\\n");

        switch (ext){
            case "rle" : return readRle(file);
            case "cells" : return readCells(file);

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

        for (String s: str) {System.out.println(s);
        }

        return def;
    }
}
