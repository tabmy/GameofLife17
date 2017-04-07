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
            return readFile(new BufferedReader(new InputStreamReader(conn.getInputStream())));
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

        /*for(String s : rlePattern){
            System.out.println(s);
        }*/

        System.out.printf("Height: %d\nWidth: %d\n", height,width);
        //System.out.println(rle);

        //byte[][] board = new byte[/*width*/ 100][/*height*/ 100];
        byte[][] board = new byte[height][width];

        Pattern pattern = Pattern.compile("(\\d+)");
        for (int i = 0; i < rlePattern.length; i++) {
            System.out.println();
            Matcher matcher = pattern.matcher(rlePattern[i]);
            //Parse strengverdien til tallets lengde og legge på indeksering, så jeg vet hvor i strengen jeg er.
            int index = 0;
            int number = 0;
            int strindex = 0;
            while (matcher.find()) {
                strindex = matcher.start();
                String s = matcher.group();
                number = Integer.parseInt(s);
                byte cell = rlePattern[i].charAt(strindex + s.length()) == 'b' ? (byte)0 : 1;
                for (int j = strindex; j < strindex + number ; j++) {
                    board[j][i] = cell;
                }
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
