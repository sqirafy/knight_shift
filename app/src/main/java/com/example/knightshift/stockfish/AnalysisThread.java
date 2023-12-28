package com.example.knightshift.stockfish;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.knightshift.db.Blunder;
import com.example.knightshift.db.DatabaseHandle;
import com.example.knightshift.db.MissedMate;
import com.example.knightshift.db.PGN;
import com.example.knightshift.db.Puzzles;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import java.util.List;

import io.realm.RealmList;

public class AnalysisThread extends Thread {

    private String stockfishPath;
    private Game game;
    private int depth;
    private int pv;
    private Side colour;
    private Blunders blunders;
    private MissedMates missedMates;
    private Boolean evaluated;
    private EvalGame eval;
    private PGN pgn;

//    public PgnHolder gamePGN;       // Can't figure out how to have public variable

    public AnalysisThread(String stockfishPath, Game game, Side colour, int depth, int pv, PGN pgn)
            throws Exception {
        this.stockfishPath = stockfishPath;
        this.game = game;
        this.colour = colour;
        this.depth = depth;
        this.pv = pv;
        this.evaluated = false;
//<<<<<<< HEAD
        eval = null;
//=======
        this.pgn = pgn;
    }

    public Side getSide() {
        return this.colour;
    }

    public Blunders getBlunders() {
        synchronized (evaluated) {
            if (evaluated) {
                return blunders;
            } else {
                return null;
            }
        }
    }

    public MissedMates getMissedMates() {
        synchronized (evaluated) {
            if (evaluated) {
                return missedMates;
            } else {
                return null;
            }
        }
    }

    /**
     * Get the progress of the analysis thread as a percentage.
     *
     * @return progress percentage
     */
    public double getProgress() {
        if (eval == null) {
            return 0;
        } else {
            return eval.getProgress();
        }
    }

    /**
     * Returns true if the thread analysed the game. Use instead of Thread.isAlive() in case there
     * was an exception while analysing the game.
     */
    public Boolean done() {
        synchronized (evaluated) {
            return this.evaluated;
        }
    }

    public PGN getPgn() {
        return this.pgn;
    }

    /**
     * Analyses a chess game for a player and stores the results as instance variables so that the
     * results can then be queried.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void run() {
        try {
            eval = new EvalGame(this.stockfishPath, this.game, this.depth, this.pv);
            eval.analyseGame();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        this.blunders = new Blunders(eval, this.colour);
        this.missedMates = new MissedMates(eval, this.colour);

        // add to database
        RealmList<Blunder> pgnBlunders = Blunder.fromAnalysis(blunders);
        RealmList<MissedMate> pgnMissedMates = MissedMate.fromAnalysis(missedMates);
        RealmList<Puzzles> pgnPuzzles = Puzzles.fromAnalysis(blunders, missedMates);
        pgn.setBlunders(pgnBlunders);
        pgn.setMissedMates(pgnMissedMates);
        pgn.setPuzzles(pgnPuzzles);
        //DatabaseHandle.getHandle().createPGN(pgn);

        synchronized (this.evaluated) {
            this.evaluated = true;
        }

        System.out.println("ANALYSIS THREAD: done");
    }

    /**
     * Unit tests the AnalysisThread data type.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) throws Exception {
//        PgnHolder pgn = new PgnHolder("src/test/resources/philidor.pgn");
        PgnHolder gamePGN = new PgnHolder("src/test/resources/philidor.pgn");
        gamePGN.loadPgn();
        Game game = gamePGN.getGames().get(0); // get the first game in the collection
        AnalysisThread thread = new AnalysisThread("TODO", game, Side.WHITE, 10, 3, null);
        thread.start();
        while (!thread.done()) {
            System.out.println(thread.getProgress());
            sleep(100);
        } // don't do this in practise because it's not guaranteed that done
                                // will finish (thread.isAlive = false, and done = false indicates
                                // something went wrong)

        // Blunders
        Blunders blunders = thread.getBlunders();
        List<String> fens = blunders.getFens();
        List<Integer> diffs = blunders.getDifferences();
        List<Integer> moveNumbers = blunders.getMoveNumbers();
        List<List<List<String>>> lines = blunders.getLines();
        System.out.println("BLUNDERS");
        System.out.println("colour " + blunders.getColour());
        System.out.println("number of blunders " + blunders.getNum());
        System.out.println();
        for (int i = 0; i < blunders.getNum(); i++) {
            System.out.println("blunder # " + i);
            System.out.println("fen " + fens.get(i));
            System.out.println("diff " + diffs.get(i));
            System.out.println("move " + moveNumbers.get(i));
            for (List<String> line : lines.get(i)) {
                System.out.println("line " + line.toString());
            }
            System.out.println();
            System.out.println();
        }

        // Missed mates
        MissedMates missedMates = thread.getMissedMates();
        List<String> fens2 = missedMates.getFens();
        List<Integer> moves = missedMates.getMoves();
        List<List<String>> lines2 = missedMates.getLines();
        System.out.println("MISSED MATES");
        System.out.println("colour: " + missedMates.getColour());
        System.out.println("number of missed mates: " + missedMates.getNum());
        System.out.println();
        for (int i = 0; i < missedMates.getNum(); i++) {
            System.out.println("fen " + fens2.get(i));
            System.out.println("mate in " + moves.get(i));
            System.out.println("line " + lines2.get(i).toString());
            System.out.println();
        }

    }
}
