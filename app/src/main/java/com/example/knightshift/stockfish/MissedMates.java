package com.example.knightshift.stockfish;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import java.util.ArrayList;
import java.util.List;

// TODO test for black and white, definite and faster mates

/**
 * Extracts missed mate opportunities from a game that has been processed by the Stockfish client.
 */
public class MissedMates {

    private final Side colour; // so that we can check who we analysed for
    private final List<String> fens; // board positions before missed mates
    private final List<List<String>> lines; // moves to checkmate the opposition
    private final List<Integer> moves; // stores how many moves the player can mate in

    public MissedMates(Side colour, List<String> fens, List<List<String>> lines, List<Integer> moves) {
        this.colour = colour;
        this.fens = fens;
        this.moves = moves;
        this.lines = lines;
    }
    /**
     * Instantiates a MissedMates object which examines information gathered by the Stockfish client
     * (stored in an EvalGame object) and stores positions where the player missed check mate
     * opportunities.
     *
     * @param game the preprocessed game
     * @param colour the colour of the player's pieces who we are analysing for
     */
    public MissedMates(EvalGame game, Side colour) {
        this.colour = colour;
        fens = new ArrayList<>();
        moves = new ArrayList<>();
        lines = new ArrayList<>();
        findMissedMates(game, colour); // populate the missed mates and moves lists
    }

    // Add a missed mate opportunity to the records.
    private void addMate(EvalGame game, Integer move, Side colour) {
        fens.add(game.getFen(move, colour));
        moves.add(game.getScoreVal(move, colour, 0));
        lines.add(game.getLine(move, colour, 0));

    }

    // Iterate over the game, looking for missed mate opportunities for a specified colour.
    private void findMissedMates(EvalGame game, Side colour) {
        for (int i = 0; i < game.getNumMoves(); i++) {
            boolean missed = false; // makes sure we only add missed mate once, not pv times

            for (int j = 0; j < game.getPV() && !missed; j++) {
                if ("mate".equals(game.getScoreType(i, colour, j))) { // might be a missed opportunity
                    for (int k = 0; k < game.getPV() && !missed; k++) {

                        if (!"mate".equals(game.getScoreType(i + 1, colour, k))) {
                            // definitely a missed opportunity
                            addMate(game, i, colour);
                            missed = true;

                        } else if (game.getScoreVal(i, colour, j) <= game.getScoreVal(i + 1,
                                colour, k)) {
                            // add to missed mates because it should have been a mate in fewer moves
                            addMate(game, i, colour);
                            missed = true;
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the number of check mate opportunities the player missed.
     *
     * @return number of missed mates
     */
    public int getNum() {
        return fens.size();
    }

    /**
     * Returns the board positions which could have led to a check mate.
     *
     * @return list of board positions
     */
    public List<String> getFens() {
        return (List<String>) ((ArrayList<String>) fens).clone();
    }

    /**
     * Returns a list containing the number of moves to check mate the opposition. List entries are
     * associated with board positions in mateFens.
     *
     * @return number of moves to check mate
     */
    public List<Integer> getMoves() {
        return (List<Integer>) ((ArrayList<Integer>) moves).clone();
    }

    /**
     * Returns a list containing the sequences of moves to check mate the opposition. List entries
     * are associated with board positions in mateFens.
     *
     * @return the sequences of moves to checkmate the opposition
     */
    public List<List<String>> getLines() {
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
     * Unit tests the MissedMates data type.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) throws Exception { // loadPgn and EvalGame throw stuff
        // load and process a game
        int DEPTH = 10;
        int PV = 3;
        PgnHolder pgn = new PgnHolder("src/test/resources/philidor.pgn");
        pgn.loadPgn();
        Game game = pgn.getGames().get(0); // get the first game in the collection
        EvalGame eval = new EvalGame("TODO", game, DEPTH, PV);

        // look for missed opportunities
        MissedMates missedMates = new MissedMates(eval, Side.WHITE);

        // query the data type
        Board board = new Board(); // for pretty printing
        System.out.println("Player: " + missedMates.getColour().value());
        System.out.println("Number of missed mates: " + missedMates.getNum() + "\n");
        List<String> fens = missedMates.getFens();
        List<Integer> moves = missedMates.getMoves();
        List<List<String>> lines = missedMates.getLines();
        for (int i = 0; i < missedMates.getNum(); i++) {
            board.loadFromFen(fens.get(i));
            System.out.println(board.toString());
            System.out.println(fens.get(i));
            System.out.println(moves.get(i));
            System.out.println(lines.get(i));
            System.out.println();

        }
    }
}
