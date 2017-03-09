package Model;

/**
 * Created by Tommy on 24.02.2017.
 */

/**
 * This class provides {@code byte} arrays for the different starting shapes available for Game Of Life. The users can
 * draw their own shapes, but these are the best known ones.
 * */
public class Shapes {

    /**
     * Implements the {@code byte} array that contains the shape "Gosper Glider Gun". The array dimensions are fixed.
     *
     * @return
     *          Jagged {@code byte} array that contains the "Gosper Glider Gun" shape
     * */
    public static byte[][] gosperGliderGun(){

        // set fixed values to new array
        byte[][] g = new byte[1000][1000];

        // set values to the array so that the shape becomes the "GGG"
        g[10][26] = 1;
        g[11][24] = g[11][26] = 1;
        g[12][14] = g[12][15] = g[12][22] = g[12][23] = g[12][36] = g[12][37] = 1;
        g[13][13] = g[13][17] = g[13][22] = g[13][23] = g[13][36] = g[13][37] = 1;
        g[14][2] = g[14][3] = g[14][12] = g[14][18] = g[14][22] = g[14][23] = 1;
        g[15][2] = g[15][3] = g[15][12] = g[15][16] = g[15][18] = g[15][19] = g[15][24] = g[15][26] = 1;
        g[16][12] = g[16][18] = g[16][26] = 1;
        g[17][13] = g[17][17] = 1;
        g[18][14] = g[18][15] = 1;

        return g;
    }

}

/*
........................O
......................O.O
............OO......OO............OO
...........O...O....OO............OO
OO........O.....O...OO
OO........O...O.OO....O.O
..........O.....O.......O
...........O...O
............OO
*/