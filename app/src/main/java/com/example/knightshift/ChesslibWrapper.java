package com.example.knightshift;

import android.content.res.AssetManager;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Methods that use the chess library.
 */
public class ChesslibWrapper {

    /**
     * Takes a filename of a PGN file in the assets directory and returns its contents as a String.
     *
     * @param fileName name of PGN file
     * @return contents as a String
     */
    public static String getPgnFromAsset(String fileName, AssetManager assetManager) throws IOException {
        BufferedReader reader;
        String pgn = null;
        reader = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
        String line;
        StringBuilder pgnBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            pgnBuilder.append(line).append("\n");
        }
        pgn = pgnBuilder.toString();
        return pgn;
    }


    /**
     * Converts a PGN contained in a string to a chesslib Game object.
     *
     * @param pgn string representation of the PGN file
     * @return the first game in the PGN
     */
    public static Game stringToGame(String pgn) {
        PgnHolder pgnHolder = new PgnHolder(null);
        pgnHolder.loadPgn(pgn);
        return pgnHolder.getGames().get(0);
    }

}
