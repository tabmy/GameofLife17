package Model;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileHandler {

    private static ArrayList<String> meta = new ArrayList<>();

    public static ArrayList<String> getMeta(){
        return meta;
    }

    private static byte[][] readFile(Reader reader) throws IOException, PatternFormatException {

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
            case 'x' : {
                String[] file1 = wholeFile.split("\\n");
                return readRle(file1);
            }
            case '!': {
                String[] file1 = wholeFile.split("\\n");
                return readCells(file1);
            }
            default:
                throw new PatternFormatException("Unsupported pattern format!");
        }
    }

    public static byte[][] readFromURL(String url) throws IOException, PatternFormatException {
        URL destination = new URL(url);
        URLConnection conn = destination.openConnection();
        return readFile(new BufferedReader(new InputStreamReader(conn.getInputStream())));
    }

    public static byte[][] readFromDisk(File file) throws IOException, PatternFormatException {
        return readFile(new FileReader(file));
    }

    private static void handleMeta(){
        char patternFormat = meta.get(0).charAt(0);

        String[] metaArr = new String[2];

        switch (patternFormat){
            case '!' : {
                Pattern name = Pattern.compile("(![nN].+)");
                Pattern author = Pattern.compile("(![aA].+)");
                for (String s : meta){
                    Matcher nameMatcher = name.matcher(s);
                    Matcher authorMatcher = author.matcher(s);
                    if (nameMatcher.find()){
                       metaArr[0] = s.replaceAll("(![nN][a-z]+\\W+)", "");
                    } else if (authorMatcher.find()) {
                        metaArr[1] = s.replaceAll("(![aA][a-z]+\\W+)", "");
                    }
                }
                break;
            }
            case '#' : {
                Pattern name = Pattern.compile("(#[nN])");
                Pattern author = Pattern.compile("(#[oO])");
                for (String s : meta){
                    Matcher nameMatcher = name.matcher(s);
                    Matcher authorMatcher = author.matcher(s);
                    if (nameMatcher.find()){
                        metaArr[0] = s.replaceAll("(#[nN]\\W)", "");
                    }
                    else if (authorMatcher.find()){
                        metaArr[1] = s.replaceAll("(#[oO]\\W)", "");
                    }
                }
                break;
            }

            default: meta.clear();
            break;
        }
        meta.clear();
        meta.add(metaArr[0]);
        meta.add(metaArr[1]);
    }

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
            }
            else if (str[i].charAt(0) == 'x') {
                Pattern pattern = Pattern.compile("(x.+ \\d+)");
                Matcher xyMatcher = pattern.matcher(str[i]);
                if (xyMatcher.find()) {
                    String xyString;
                    xyString = xyMatcher.group();
                    String[] xyStringArr = xyString.split(",");
                    xyStringArr[0] = xyStringArr[0].replaceAll("[^\\d]", "");
                    xyStringArr[1] = xyStringArr[1].replaceAll("[^\\d]", "");
                    width = Integer.parseInt(xyStringArr[0]);
                    height = Integer.parseInt(xyStringArr[1]);
                }
                comments++;
            } else {
                strBuild.append(str[i]);
            }
        }
        if (height == 0 || width == 0) throw new PatternFormatException("Cannot find x or y");

        String rle = strBuild.toString();
        String[] rlePattern = rle.split("[$]");
        byte[][] board = new byte[width][height];

        // Reading pattern from RLE-string, setting values to correct location in board[][] according to RLE
        // pattern file.
        Pattern pattern = Pattern.compile("[\\dbo!]");
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
                        number = Integer.parseInt(stringBuilder.toString());
                    }
                    for (int j = index; j < index + (number); j++) {
                        board[j][i] = match.equals("o") ? (byte) 1 : 0;
                    }
                    index += number;
                    stringBuilder = new StringBuilder();
                } else if (match.equals("!")) {
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

        for (int i = comments; i < str.length; i++) {
            for (int j = 0; j < str[i].length(); j++) {
                if (str[i].charAt(j) == 'O') {
                    board[j][(i - comments)] = 1;
                }
            }
        }
        handleMeta();
        return board;
    }
}
