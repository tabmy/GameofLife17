package Model;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tommy on 09.03.2017.
 */
public class FileHandler {

    private static byte[][] def = new byte[1000][1000];

    public static byte[][] readFile(Reader reader, long length, String ext) throws IOException, PatternFormatException {

        char chars[] = new char[(int) length];
        reader.read(chars);
        String wholeFile = new String(chars);

        switch (ext) {
            case "rle": {

                String[] file = wholeFile.split("\\n");
                return readRle(file);
            }
            case "cells": {
               String[] file = wholeFile.split("\\n");
               return readCells(file);
            }

            default:
                throw new PatternFormatException("Unsupported pattern!");
        }
    }

    public static byte[][] readFromURL(String input) throws IOException, PatternFormatException {
        URL url = new URL(input);
        URLConnection urlConnection = url.openConnection();

        String[] token = url.toString().split("\\.");
        String extension = token[token.length - 1];

        Path saveLocation = File.createTempFile("golPattern", extension).toPath();

        BufferedWriter writer;
        BufferedReader reader;

        reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        writer = Files.newBufferedWriter(saveLocation);
        String line;

        while ((line = reader.readLine()) != null) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();

        return readFile(new FileReader(saveLocation.toFile()), saveLocation.toFile().length(), extension);
    }

    public static byte[][] readFromDisk(File file) throws IOException, PatternFormatException {
        String ext = file.toString();
        String[] token = ext.split("\\.");
        ext = (token[token.length - 1]).toLowerCase();

        return readFile(new FileReader(file), file.length(), ext);
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
