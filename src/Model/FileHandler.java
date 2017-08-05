package Model;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for handling pattern files related to Conway's Game of Life.
 * All methods in this class are static, making it possible to load files without instantiating an object of this type.
 *
 * @author Abelsen, Tommy
 * @author Petrovic, Branislav
 */
public class FileHandler {

    private static ArrayList<String> meta = new ArrayList<>();

    public static ArrayList<String> getMeta() {
        return meta;
    }


    /**
     * Reads the file using the Reader. Then calls upon the corresponding read method depending on file type. Returns
     * the 2-d byte-array returned by the aforementioned method call.
     * @param reader - The <code>Reader</code> used to read the file.
     * @return byte[][]
     * @throws IOException
     * @throws PatternFormatException
     */
    private static byte[][] readFile(Reader reader) throws IOException, PatternFormatException {

        // Dynamic data structure making it possible to read and store every character from any file
        // independent of the file length.
        ArrayList<Integer> list = new ArrayList<>();
        int nextNum = 0;

        while (nextNum > -1) {
            list.add(nextNum);
            nextNum = reader.read();
        }
        list.remove(0);

        // Creating a string from the data read into the list ArrayList
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer integer : list){
            //Casting the integer to a character value, representing characters instead of only numbers.
            stringBuilder.append((char)+integer);
        }

        String wholeFile = stringBuilder.toString();
        char ext = wholeFile.charAt(0);

        // A switch case on the file type. This makes sure the pattern type is sent to the corresponding method for
        // proper handling.
        // Splits into a String array, using the regular expression term for new line, and sends it to the correct
        // method.
        switch (ext) {
            case '#': {
                return readRle(wholeFile.split("\\n"));
            }
            case 'x': {
                return readRle(wholeFile.split("\\n"));
            }
            case '!': {
                return readCells(wholeFile.split("\\n"));
            }
            default:
                throw new PatternFormatException("Unsupported pattern format!");
        }
    }


    /**
     * Opens a connection to the specified URL given as parameter in. Calls upon the readFile method to read the
     * document in the given URL, returning the 2-d byte array given from readFile.
     * @param url - The <code>URL</code> stored as a <code>String</code>
     * @return byte[][]
     * @throws IOException
     * @throws PatternFormatException
     * @see #readFile(Reader)
     */
    public static byte[][] readFromURL(String url) throws IOException, PatternFormatException {

        URL destination = new URL(url);
        URLConnection conn = destination.openConnection();

        return readFile(new BufferedReader(new InputStreamReader(conn.getInputStream())));
    }

    /**
     * Sends the file given as parameter to the readFile method, returning the 2-d byte array from readFile.
     * @param file - The <code>File</code> to be read and handled by the readFile methods.
     * @return byte[][]
     * @throws IOException
     * @throws PatternFormatException
     * @see #readFile(Reader)
     */
    public static byte[][] readFromDisk(File file) throws IOException, PatternFormatException {
        return readFile(new FileReader(file));
    }

    /**
     * Handles the data stored in the meta-ArrayList. Stores the name of the file in position 1, index 0, and the
     * author in position 2, index 1.
     * @see #readCells(String[])
     * @see #readRle(String[])
     */
    private static void handleMeta() {

        char patternFormat = meta.get(0).charAt(0);
        String[] metaArr = new String[2];

        switch (patternFormat) {
            case '!': {

                Pattern name = Pattern.compile("(![nN](ame).+)");
                Pattern author = Pattern.compile("(![aA](uthor).+)");

                for (String s : meta) {

                    Matcher nameMatcher = name.matcher(s);
                    Matcher authorMatcher = author.matcher(s);

                    if (nameMatcher.find()) {
                        metaArr[0] = s.replaceAll("(![nN][a-z]+\\W+)", "");
                    } else if (authorMatcher.find()) {
                        metaArr[1] = s.replaceAll("(![aA][a-z]+\\W+)", "");
                    }
                }
                break;
            }
            case '#': {

                Pattern name = Pattern.compile("(#[nN])");
                Pattern author = Pattern.compile("(#[oO])");

                for (String s : meta) {

                    Matcher nameMatcher = name.matcher(s);
                    Matcher authorMatcher = author.matcher(s);

                    if (nameMatcher.find()) {
                        metaArr[0] = s.replaceAll("(#[nN]\\W)", "");
                    } else if (authorMatcher.find()) {
                        metaArr[1] = s.replaceAll("(#[oO]\\W)", "");
                    }
                }
                break;
            }
            default: {
                meta.clear();
                break;
            }
        }

        meta.clear();
        meta.add(metaArr[0]);
        meta.add(metaArr[1]);
    }

    /**
     * Reads the str String array containing the RLE-file, creates a 2-d byte array and puts the corresponding dead
     * and alive cells on the appropriate place in the byte array.
     * Returns the array with the parsed pattern.
     * @param str - The <code>String</code> 2-d array containing the files lines.
     * @return byte[][]
     * @throws PatternFormatException
     */
    private static byte[][] readRle(String[] str) throws PatternFormatException {

        meta.clear();
        int height = 0;
        int width = 0;
        int comments = 0;

        // Finding the height and width of the pattern using regex
        StringBuilder strBuild = new StringBuilder();
        for (int i = comments; i < str.length; i++) {
            if (str[i].charAt(0) == '#') {
                comments++;
                meta.add(str[i]);
            } else if (str[i].charAt(0) == 'x') {

                Pattern pattern = Pattern.compile("(x.+ \\d+)");
                Matcher xyMatcher = pattern.matcher(str[i]);

                if (xyMatcher.find()) {
                    //finds the regex that matches with "x = \d+, y = \d+"
                    String xyString = xyMatcher.group();
                    String[] xyStringArr = xyString.split(",");

                    //replaces everything not a number with blank, so that there is only the numbers representing
                    //the height and with in this array.
                    xyStringArr[0] = xyStringArr[0].replaceAll("[^\\d]", "");
                    xyStringArr[1] = xyStringArr[1].replaceAll("[^\\d]", "");
                    width = Integer.parseInt(xyStringArr[0]);
                    height = Integer.parseInt(xyStringArr[1]);
                }
                comments++;
            } else {
                //appends the string containing the rle patterns (o, b, $ and numbers).
                strBuild.append(str[i]);
            }
        }

        if (height == 0 || width == 0){
            //throws exception if there is no character sequence matching "x = (number), y = (number)" in the file.
            throw new PatternFormatException("Cannot find x or y");
        }

        //Forms a string with all the characters supported by RLE pattern format.
        String rle = strBuild.toString();
        //Splits the string into an array on "end of line"-character $, so that each String in the array represents a
        // line in the pattern.
        String[] rlePattern = rle.split("[$]");
        byte[][] board = new byte[width][height];

        // Reading pattern from RLE-string, setting values to correct location in board[][] according to RLE
        // pattern file.
        Pattern pattern = Pattern.compile("(\\d+)|[bo!]");
        for (int i = 0; i < rlePattern.length; i++) {

            Matcher matcher = pattern.matcher(rlePattern[i]);
            boolean found = matcher.find();
            StringBuilder stringBuilder = new StringBuilder();
            int index = 0;

            while (found) {

                String match = matcher.group();

                if (match.equals("o") || match.equals("b")) {

                    int number = 1;

                    if (!stringBuilder.toString().equals("")) {
                        //parses the number found, so that the for loop loops through and places the correct number
                        // of the corresponding cell on the next index in the String.
                        number = Integer.parseInt(stringBuilder.toString());
                    }
                    for (int j = index; j < index + (number); j++) {
                        board[j][i] = match.equals("o") ? (byte) 1 : 0;
                    }
                    index += number;
                    stringBuilder = new StringBuilder();
                } else if (match.equals("!")) {
                    //calls the method to handle meta information.
                    handleMeta();
                    return board;
                } else {
                    stringBuilder.append(match);
                }
                found = matcher.find();
            }
            if (index != width)
                throw new PatternFormatException("Incorrect number of cells in row " + (i + 1));
        }
        throw new PatternFormatException("Cannot find end symbol \"!\" ");
    }

    /**
     * Reads the String array containing the cells file, creates a 2-d byte array and puts the corresponding dead
     * and alive cells on the appropriate place in the byte array.
     * Returns the array with the parsed pattern.
     * @param str - The <code>String</code> 2-d array containing the files lines.
     * @return  byte[][]
     * @throws PatternFormatException
     */
    private static byte[][] readCells(String[] str) throws PatternFormatException {

        meta.clear();
        int comments = 0;
        int height = 0;
        int width = 0;

        for (String s : str) {
            if (s.charAt(0) != '!') {
                height++;
                width = width < s.length() - 1 ? s.length() - 1 : width;
            } else {
                comments++;
                meta.add(s);
            }
        }
        if (height == 0 || width == 0) throw new PatternFormatException("Cannot find height or width of pattern!");

        byte[][] board = new byte[width][height];

        //iterates through the board 2d array
        for (int i = comments; i < str.length; i++) {
            for (int j = 0; j < str[i].length(); j++) {
                if (str[i].charAt(j) == 'O') {
                    // places a '1', a living cell, on the board in position (j,i) if the character on the
                    // corresponding position in the cells file is equal to 'O'.
                    board[j][(i - comments)] = 1;
                }
            }
        }
        //calls the method to handle the meta information.
        handleMeta();
        return board;
    }
}
