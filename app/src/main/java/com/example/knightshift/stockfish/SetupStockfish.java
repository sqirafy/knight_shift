package com.example.knightshift.stockfish;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Methods for moving Stockfish executable in assets to a folder where we have permission to make
 * it executable.
 */
public class SetupStockfish {

    /**
     * Moves the Stockfish executable to the /data/ directory. This is the only place we can write
     * files and give them executable permissions as far as I know.
     *
     * @param assetManager for getting the Stockfish executale from assets
     * @param stockfishPath path of the file we should move stockfish to
     * @throws IOException if we can't write to file in data directory
     */
    public static void moveToData(AssetManager assetManager, String stockfishPath) {
        // try with resources so that we always close the streams
        try (InputStream in = assetManager.open("stockfish.android.armv7");
             FileOutputStream out = new FileOutputStream(stockfishPath)) {

            // move in to out essentially
            byte[] arr = new byte[1024];
            while (in.read(arr) != -1) {
                out.write(arr);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gives the Stockfish executable in "/data/" directory executable permissions so that we can
     * run the Stockfish client.
     *
     * @param stockfishPath file path of stockfish executable
     * @return output of the chmod process
     */
    public static String makeExecutable(String stockfishPath) {
        try {
            // execute chmod to give stockfish executable permissions
            String command = "/system/bin/chmod 744 " + stockfishPath;
            Process process = Runtime.getRuntime().exec(command);

            // capture output to stdout
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            // wait for the process to finish
            process.waitFor();

            return builder.toString();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
