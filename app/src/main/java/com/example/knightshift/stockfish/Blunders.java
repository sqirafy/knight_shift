package com.example.knightshift.stockfish;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

// TODO test for black aswell! Missed mates aswell!
// Then we can store lines which were better than the blunder?
// TODO store move number and not the fen probably, should only store fen in eval game

/**
 * Looks for positions where the specified player made blunders.
 */
public class Blunders {

    // the minimum centipawn difference to qualify as a blunder
    // TODO base this off skill/elo rating OR don't make constant
    private static final int MIN_DIFFERENCE = 150;

    private final Side colour;
    private final List<String> fens; // board positions before blunders
    private final List<Integer> differences; // the difference between pre and post blunder scores
    private final List<Integer> moveNumbers;
    private final List<List<List<String>>> lines;

    public Blunders(Side colour, List<String> fens, List<Integer> differences, List<Integer> moveNumbers, List<List<List<String>>> lines) {
        this.colour = colour;
        this.fens = fens;
        this.differences = differences;
        this.moveNumbers = moveNumbers;
        this.lines = lines;
    }
    // fen, score difference pair, useful for priority queue
    private class FenScorePair {
        private String fen;
        private int difference;
        private int moveNumber;

        public FenScorePair(String fen, int difference, int moveNumber) {
            this.fen = fen;
            this.difference = difference;
            this.moveNumber = moveNumber;
        }
    }

    /**
     * Examines an evaluated game and finds positions where the player blundered. These can be
     * queried using the instantiated Blunders object.
     *
     * @param game the preprocessed game
     * @param colour the colour of the player's pieces who we are analysing for
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Blunders(EvalGame game, Side colour) {
        this.colour = colour;
        fens = new ArrayList<>();
        differences = new ArrayList<>();
        moveNumbers = new ArrayList<>();
        lines = new ArrayList<>();

        findBlunders(game, colour);
    }

    // Add a position where the user blundered to the records.
    private void addBlunder(String fen, int difference, int moveNumber, List<List<String>> lines) {
        fens.add(fen);
        differences.add(difference);
        moveNumbers.add(moveNumber);
        this.lines.add(lines);
    }

    // Iterates over all the score differences between moves and adds those which are worse than
    // some threshold to the list of blunders.
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void findBlunders(EvalGame game, Side colour) {
        // priority queue, ordered by maximum difference in scores (or minimum for black)
        Queue<FenScorePair> pq = new PriorityQueue<>((p1, p2) -> colour == Side.WHITE ?
            p2.difference - p1.difference : p1.difference - p2.difference);

        // add all fen score pairs to the priority queue
        for (int i = 0; i < game.getNumMoves() - 1; i++) { // TODO work out when this will be null?
            if (game.getFen(i, colour) != null && game.getFen(i + 1, colour) != null) {
                String fen = game.getFen(i, colour);
                int variation = (colour == Side.WHITE) ? 0 : 0; // TODO: Liam must fix this
                Integer difference = game.getScoreVal(i, colour, variation) -
                    game.getScoreVal(i + 1, colour, variation);
                pq.add(new FenScorePair(fen, difference, i));
            }
        }

        // only add positions whose score difference is greater than MIN_DIFFERENCE constant
        // (or less than if the player is playing as black) to field variables
        while (!pq.isEmpty() && ((colour == Side.WHITE && pq.peek().difference >= MIN_DIFFERENCE) ||
            (colour == Side.BLACK && pq.peek().difference <= -MIN_DIFFERENCE))) {
            FenScorePair pair = pq.poll();
            addBlunder(pair.fen, pair.difference, pair.moveNumber, game.getLines(pair.moveNumber, colour));
        }
    }

    /**
     * Returns the number of blunders the player made.
     *
     * @return number of blunders
     */
    public int getNum() {
        return fens.size();
    }

    /**
     * Returns the board positions where the player blundered.
     *
     * @return list of board positions
     */
    public List<String> getFens() {
        return (List<String>) ((ArrayList<String>) fens).clone();
    }

    public void setFens(List<String> FENs){
        //this.fens
    }
    /**
     * Returns the differences between pre and post blunder moves associated with the board in fens.
     *
     * @return list of score differences
     */
    public List<Integer> getDifferences() {
        return (List<Integer>) ((ArrayList<Integer>) differences).clone();
    }

    /**
     * Returns the move numbers where the player blundered. Move numbers start at 0.
     *
     * @return list of move numbers associated with blunders
     */
    public List<Integer> getMoveNumbers() {
        return (List<Integer>) ((ArrayList<Integer>) moveNumbers).clone();
    }

    /**
     * Returns the alternative move sequences that stockfish found to be optimal.
     *
     * @return the alternative sequences for each blunder
     */
    public List<List<List<String>>> getLines() {
        return lines;
    }

    /**
     * Returns the colour of the player's pieces who we analysed for.
     *
     * @return player's piece colour
     */
    public Side getColour() {
        return colour;
    }

    /**
     * Unit tests the Blunders data type.
     *
     * @param args command line arguments
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void main(String[] args) throws Exception {
        // evaluate a game
        PgnHolder pgn = new PgnHolder("src/test/resources/philidor.pgn");
        pgn.loadPgn();
        Game game = pgn.getGames().get(0); // get the first game in the collection
        EvalGame eval = new EvalGame("TODO", game, 10, 3);

        // find blunders
        Blunders blunders = new Blunders(eval, Side.WHITE);

        // query new data type
        // query the data type
        Board board = new Board(); // for pretty printing
        System.out.println("Player: " + blunders.getColour().value());
        System.out.println("Number of missed mates: " + blunders.getNum() + "\n");
        List<String> fens = blunders.getFens();
        List<Integer> diffs = blunders.getDifferences();
        List<Integer> moveNumbers = blunders.getMoveNumbers();
        List<List<List<String>>> lines = blunders.getLines();
        for (int i = 0; i < blunders.getNum(); i++) {
            board.loadFromFen(fens.get(i));
            System.out.println(board.toString());
            System.out.println(fens.get(i));
            System.out.println("diff " + diffs.get(i));
            System.out.println("move " + moveNumbers.get(i));
            for (List<String> line : lines.get(i)) {
                System.out.println("line " + line.toString());
            }

            System.out.println();

        }
    }
}
