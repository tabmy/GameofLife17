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

        int nextNum = 0;
        while (nextNum > -1) {
            list.add(nextNum);
            nextNum = reader.read();
        }
        list.remove(0);

        char[] file = new char[list.size()];
        for (int i = 0; i < list.size(); i++) {
            file[i] = (char) (+list.get(i));
        }

        String wholeFile = new String(file);

        char ext = wholeFile.charAt(0);
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

    public static byte[][] readFromURL(String url) throws IOException, PatternFormatException {

        URL destination = new URL(url);
        URLConnection conn = destination.openConnection();

        return readFile(new InputStreamReader(conn.getInputStream()));

    }

    public static byte[][] readFromDisk(File file) throws IOException, PatternFormatException {
        return readFile(new FileReader(file));
    }

    private static byte[][] readRle(String[] str) throws PatternFormatException {

        int height = 0;
        int width = 0;
        int comments = 0;

        // Finding the height and width of the pattern using regex
        for(String s : str){
            if (s.charAt(0) == '#') comments++;
            if (s.charAt(0) == 'x'){
                Pattern pattern = Pattern.compile("(x.+ \\d)");
                Matcher xyMatcher = pattern.matcher(s);
                if (xyMatcher.find()) {
                    String xyString;
                    xyString = xyMatcher.group();
                    String[] xyStringArr = xyString.split(",");
                    xyStringArr[0] = xyStringArr[0].replaceAll("[^\\d+]", "");
                    xyStringArr[1] = xyStringArr[1].replaceAll("[^\\d+]", "");
                    height = Integer.parseInt(xyStringArr[0]);
                    width = Integer.parseInt(xyStringArr[1]);
                }
                comments++;
            }
        }
        if (height == 0 || width == 0) throw new PatternFormatException("Cannot find x or y");

        //Todo include the foreach loop in this loop, so that it only reads the file once
        StringBuilder strBuild = new StringBuilder();
        for (int i = comments; i < str.length ; i++) {
            str[i] = str[i].replaceAll("[^bo\\d+$]", "");
            strBuild.append(str[i]);
        }
        String rle = strBuild.toString();

        String[] rlePattern = rle.split("[$]");

        //System.out.printf("Height: %d\nWidth: %d\n", height,width);
        //System.out.println(rle);

        byte[][] board = new byte[height][width];

        Pattern numPattern = Pattern.compile("(\\d+)");
        Pattern cellPattern = Pattern.compile("[bo]");
        for (int i = 0; i < rlePattern.length; i++) {
            Matcher numMatcher = numPattern.matcher(rlePattern[i]);
            Matcher cellMatcher = cellPattern.matcher(rlePattern[i]);
            //Parse strengverdien til tallets lengde og legge på indeksering, så jeg vet hvor i strengen jeg er.
            int index = 0;
            int number = 0;
            int strIndex = 0;
            int cellIndex = 0;
            int numIndex = 0;
            boolean cellFound = cellMatcher.find();
            boolean numFound = numMatcher.find();

            // Løkken her skal gå igjennom strengen med bokstav og tallsymboliseringen av mønstrene i RLE-format
            // Meningen er at den skal finne et tall (om det er der), så en bokstav: o eller b, som symboliserer en
            // levende eller død celle. Tallet skal representere antall av den neste bokstaven (levende eller døde
            // celler etter hverandre)
            // Problemet nå er at det ikke er noen oversikt over hvor i strengen man er, så det hender at to tall
            // blir parset etter hverandre uten å gå igjennom enkeltbokstavene.

            // Hjelpevariabler som trenges:
            // En teller til å holde indeks til selve tabellen, så vi setter inn riktig verdi på riktig plass
            // En teller til å sjekke tallet som parses til int fra streng
            // En streng til å kunne bli parset til int, samt for å vite lengden til strengen(antall siffer i tallet)
            // En (to?) teller til å holde styr på hvor i rle-pattern-strengen vi er, så man kan sjekke om det neste
            // vi
            // skal legge inn i arrayet er en enkeltverdi eller en tall-løkke-verdi (flere enn en om gangen)


            while (cellFound || numFound) {
                if (cellFound && cellIndex <= numIndex){
                    byte cell = cellMatcher.group().equals("o") ? (byte) 1 : 0;
                    board[index][i] = cell;
                    cellFound = cellMatcher.find();
                    if (cellFound)cellIndex = cellMatcher.start();
                    else cellIndex++;
                    index++;
                }
                if (numFound) {
                    strIndex = numMatcher.start();
                    String s = numMatcher.group();
                    number = Integer.parseInt(s) -1;
                    byte cell = rlePattern[i].charAt(strIndex + s.length()) == 'o' ? (byte) 1 : 0;
                    for (int j = strIndex; j < strIndex + number; j++) {
                        board[j][i] = cell;
                    }
                    index += number;

                    numFound = numMatcher.find();
                    if (numFound)numIndex = numMatcher.start();
                }
                else break;
            }
        }

        return board;
    }

    private static byte[][] readCells(String[] str) throws PatternFormatException {

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
        if (height == 0 || width == 0) throw new PatternFormatException("Cannot find height or width of pattern!");

        System.out.printf("Height: %d \nWidth: %d", height, width);

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
