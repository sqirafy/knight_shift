package com.example.knightshift.stockfish;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Make sure you have a stockfish engine executable at engine/stockfish. You need to download the
 * source code from https://stockfishchess.org/download/ and compile and move the executable
 * locally.
 * Universal Chess Interface (UCI) reference:
 * https://web.archive.org/web/20190203093326/http://wbec-ridderkerk.nl/html/UCIProtocol.html
 */
public class StockfishClient {

    public Process engineProcess;
    // TODO change these back to private, just doing some debugging
    public BufferedReader processReader;
    //public OutputStreamWriter processWriter;
    public BufferedWriter processWriter;

    /**
     * Starts the Stockfish engine as a process.
     *
     * @return true if the process was successfully started, else false
     */
    public boolean startEngine(String stockfishPath) {
        try {
            Process engineProcess = Runtime.getRuntime().exec(stockfishPath); // start Stockfish
            this.engineProcess = engineProcess;
            processReader = new BufferedReader(new InputStreamReader(
                engineProcess.getInputStream()));
            System.out.println(processReader.toString());
            System.out.println(processReader.readLine());
            processWriter = new BufferedWriter(new OutputStreamWriter(
                engineProcess.getOutputStream()));
            System.out.println(processWriter.toString());
            sendUCI();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Stops the process running Stockfish.
     */
    public void stopEngine() {
        this.engineProcess.toString(); // TODO just trying to stop the thing from being orphaned
        sendCommand("quit");
        try {
            processReader.close();
            processWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a command to the Stockfish engine.
     *
     * @param command the command to be executed
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendCommand(String command) {
        try {
            System.out.println("COMMAND: " + command);
            System.out.println("ALIVE: " + engineProcess.isAlive());
            processWriter.write(command + "\n");
            processWriter.flush(); // TODO why can't we flush
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets and returns the Stockfish engines response.
     *
     * @param maxWait the number of milliseconds to wait for
     * @return the engine's response
     */
    public String getOutput(int maxWait) {
        StringBuilder buffer = new StringBuilder();
        try {
            Thread.sleep(maxWait);
            sendCommand("isready");
            while (true) {
                String text = processReader.readLine();
                if (text.equals("readyok")) { // finished scanning response
                    break;
                } else { // keep scanning stockfish response
                    buffer.append(text).append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * Gets the whole info string after searching the game tree. It's better to use this for info
     * strings than getOutput because getOutput sometimes interrupts the engine.
     *
     * @return information returned by the engine after analysing a position
     */
    public String getAllInfo() {
        StringBuilder buffer = new StringBuilder();
        try {
            boolean searching = true;
            while (searching) {
                String text = processReader.readLine();
                buffer.append(text).append("\n");
                searching = !text.startsWith("bestmove");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * Let the engine know that we will be using Universal Chess Engine protocol, and discard the
     * output.
     */
    public void sendUCI() {
        sendCommand("uci");
        getOutput(1000);
    }

    /**
     * Updates the engines multi pv (primary variations) value so that we can find a few good moves
     * from a position.
     *
     * @param val the new multi pv value
     */
    public void setMultiPV(int val) {
        sendCommand("setoption name MultiPV value " + val);
    }

    /**
     * Tell Stockfish to evaluate a position and return the score it calculated after searching the
     * game tree for a while.
     *
     * @param fen string representation of the board (Forsyth-Edwards notation)
     * @param fen string representation of the board (Forsyth-Edwards notation)
     * @param depth the number of moves ahead that the engine should search
     * @return the (hopefully centipawn) value of the specified position
     */
    public float getScore(String fen, int depth) {
        sendCommand("position fen " + fen);
        setMultiPV(1);
        sendCommand("go depth " + depth);

        float evalScore = 0.0f;
        String[] dump = getAllInfo().split("\n");

        // TODO we need to account for cp, mate etc. Mate would be really good for finding puzzles
        Pattern pattern = Pattern.compile("^.*score (cp|mate|upperbound|lowerbound) (-?\\d+).*$");
        for (String s : dump) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.matches()) {
                evalScore = Float.parseFloat(matcher.group(2));
            }
        }

        return evalScore/100;
    }

    /**
     * Tells Stockfish to evaluate a board position and returns the info strings it created after
     * searching the game tree for a while.
     *
     * @param fen string representation of the board (Forsyth-Edwards notation)
     * @param depth the number of moves ahead that the engine should search
     * @param pv number of principal variations
     * @return the info strings Stockfish created
     */
    public List<String> getInfo(String fen, int depth, int pv) {
        sendCommand("position fen " + fen);
        setMultiPV(pv);
        sendCommand("go depth " + depth);

        // get all the strings that start with info and discard the rest
        String[] dump = getAllInfo().split("\n");
        List<String> info = new ArrayList<>();
        for (String s : dump) {
            if (s.startsWith("info")) {
                info.add(s);
            }
        }

        // grab the last few info strings if possible
        List<String> sublist;
        if (info.size() - pv >= 0) {
            sublist = info.subList(info.size() - pv, info.size());
        } else {
            sublist = info;
        }

        return sublist;
    }

    /**
     * Unit tests the StockfishClient data type.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        StockfishClient client = new StockfishClient();
        String FEN = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2";
        int DEPTH = 15;
        int PV = 3;

        if (!client.startEngine("TODO")) {
            System.out.println("Could not start engine.");
            System.exit(1);
        } else {
            System.out.println("Started engine.");
        }

        // let stockfish know we will be using the universal chess interface
        client.sendUCI();

        // get the board score
        System.out.println("Eval: " + client.getScore(FEN, DEPTH));

        List<String> info = client.getInfo(FEN, DEPTH, PV);
        for (String s : info) {
            System.out.println(s);
        }

        client.stopEngine();
        System.out.println("Stopped engine.");
    }
}
