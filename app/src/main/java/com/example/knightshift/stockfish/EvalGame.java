package com.example.knightshift.stockfish;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO test what happens if depth is e.g. 20 but mate in 3
/**
 * Processes a chess game so that we can get information about each position in the game.
 */
public class EvalGame {

    private static final String NO_MATCH = "nomatch";

    private final String stockfishPath;
    private final int depth;
    private final int pv;
    private final List<String> fens; // list of positions
    private final List<List<String>> infos; // information strings from stock fish
    // 'cache' strings we are likely to look up several times in infos' strings
    private final List<List<String>> scoreTypes;
    private final List<List<Integer>> scoreVals; // list of scores
    private final List<List<List<String>>> lines; // sequence of moves stock fish found
    private final int numMoves;

    private Object key; // for synchronizing access to move counter
    private int moveCounter; // for calculating progress

    private Game game;

    /**
     * Processes a chess game and stores information about each board position. We can then query
     * this information by using the instantiated EvalBoard object.
     *
     * @param game a chess game
     * @param depth the number of moves ahead that the chess engine should search
     * @throws Exception
     */
    public EvalGame(String stockfishPath, Game game, int depth, int pv) throws Exception {
        // initialise lists for FENs and scores
        this.stockfishPath = stockfishPath;
        this.game = game;
        this.depth = depth;
        this.pv = pv;
        this.fens = new ArrayList<>();
        this.infos = new ArrayList<>();
        this.scoreTypes = new ArrayList<>();
        this.scoreVals = new ArrayList<>();
        this.lines = new ArrayList<>();

        // do some pre processing, which populates the above lists
        this.game.loadMoveText();
        key = new Object();
        synchronized (key) {
            this.numMoves = game.getCurrentMoveList().size();
            this.moveCounter = 0;
        }
    }

    /**
     * Analyse the game that was loaded into the object when it was constructed.
     */
    public void analyseGame() {
        analyseGame(stockfishPath, game.getHalfMoves(), depth, pv);
    }

    // Adds a FEN and its associated information string to this objects lists of information.
    private void addPosition(Board board, StockfishClient client, int depth, int pv) {
        // store FEN and list of info strings about the position
        this.fens.add(board.getFen());
        List<String> infos = client.getInfo(board.getFen(), depth, pv);
        this.infos.add(infos);

        // extract the types of the scores (cp, mate), associated values and
        // lines. TODO upper and lower bound come after cp val it seems...
        Pattern pattern = Pattern.compile("^.*score (\\w+) (-?\\d+).*pv (.*)$");
        List<String> types = new ArrayList<>();
        List<Integer> vals = new ArrayList<>();
        List<List<String>> sublines = new ArrayList<>();

        // intermediate storage for this fen
        for (String info : infos) {
            Matcher matcher = pattern.matcher(info);

            if (matcher.matches()) {
                types.add(matcher.group(1));
                vals.add(Integer.parseInt(matcher.group(2)));
                sublines.add(Arrays.asList(matcher.group(3).split(" ")));
            } else {
                types.add(NO_MATCH);
                vals.add(0);
                sublines.add(new ArrayList<>());
            }
        }

        // cache for efficient lookup
        scoreTypes.add(types);
        scoreVals.add(vals);
        lines.add(sublines);
    }

    // Analyses each board position in a game using the Stockfish client, and store the results so
    // that we can query them later.
    private void analyseGame(String stockfishPath, MoveList moves, int depth, int pv) {
        // try start a stockfish client
        StockfishClient client = new StockfishClient();
        if (!client.startEngine(stockfishPath)) {
            System.out.println("Could not start Stockfish.");
            return;
        }

        // let the engine know that we will be using uci
        client.sendUCI();

        // replay all the moves
        Board board = new Board(); // initialized with standard initial board position
        addPosition(board, client, depth, pv);
        for (Move move : moves) {
            synchronized (key) {
                moveCounter++;
            }
            board.doMove(move);
            addPosition(board, client, depth, pv);
        }

        // stop stockfish
        client.stopEngine();
    }

    /**
     * Returns the number of positions Stockfish has analysed as a percentage
     *
     * @return progress percentage
     */
    public double getProgress() {
        synchronized (key) {
            return (double) moveCounter / numMoves;
        }
    }

    /**
     * Returns the number of moves made in the game which was evaluated.
     *
     * @return number of moves made
     */
    public int getNumMoves() {
        return this.numMoves;
    }

    /**
     * Returns the number lines found for each board position.
     *
     * @return multi pv value
     */
    public int getPV() {
        return this.pv;
    }

    // Get the move number based on the move in PGN notation and the player whose turn is next
    private int getMoveNumber(int move, Side colour) {
        return 2*move + (colour.equals(Side.WHITE) ? 0 : 1);
    }

    // Returns true if the move number, colour and variation is valid, else false.
    private boolean isValid(int move, Side colour, int variation) {
        if (move < 0 || getMoveNumber(move, colour) > getNumMoves()) {
            return false;
        } else if (variation < 0 || variation > pv) {
            return false;
        } else {
            return true;
        }
    }

    // Returns true if the move number and colour is valid, else false.
    private boolean isValid(int move, Side colour) {
        if (move < 0 || getMoveNumber(move, colour) > getNumMoves()) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * Get the FEN associated with the board after some moves and it is "WHITE" or "BLACK" to play.
     *
     * @param move move number
     * @param colour of the player whose turn it is
     * @return a FEN representation of the board if the move is valid, else null.
     */
    public String getFen(int move, Side colour) {
        if (isValid(move, colour, 0)) {
            return this.fens.get(getMoveNumber(move, colour));
        } else {
            return null;
        }
    }

    /**
     * Get the type of the score stored after some moves and it is "WHITE" or "BLACK" to play.
     *
     * @param move move number
     * @param colour of the player whose turn it is
     * @return score type (cp, mate, upperbound, lowerbound) if the move is valid, else null
     */
    public String getScoreType(int move, Side colour, int variation) {
        if (isValid(move, colour, variation)) {
            if (variation >= this.scoreTypes.get(getMoveNumber(move, colour)).size()) {
                // TODO variation bug
                System.out.println("Something went wrong!");
                System.out.println("EvalGame.java, line 185");
                variation = 0;
            }
            return this.scoreTypes.get(getMoveNumber(move, colour)).get(variation);
        } else {
            return null;
        }
    }

    /**
     * Get the value associated with the score type after some moves and it is white or black
     * to play.
     *
     * @param move move number
     * @param colour of the player whose turn it is
     * @return score of the position if the move number is valid, else null.
     */
    public Integer getScoreVal(int move, Side colour, int variation) {
        if (isValid(move, colour, variation)) {
            return this.scoreVals.get(getMoveNumber(move, colour)).get(variation);
        } else {
            return null;
        }
    }

    /**
     * Get the sequence of moves that stock fish found to be optimal.
     *
     * @param move move number
     * @param colour of the player whose turn it is
     * @return sequence of moves
     */
    public List<String> getLine(int move, Side colour, int variation) {
        if (isValid(move, colour, variation)) {
            return this.lines.get(getMoveNumber(move, colour)).get(variation);
        } else {
            return null;
        }
    }

    /**
     * Gets all the sequences of moves that Stockfish found to be optimal.
     *
     * @param move move number
     * @param colour of the player whose turn it is
     * @return list of sequences of moves
     */
    public List<List<String>> getLines(int move, Side colour) {
        if (isValid(move, colour)) {
            return this.lines.get(getMoveNumber(move, colour));
        } else {
            return null;
        }
    }

    /**
     * Unit tests the EvalBoard data type.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) throws Exception {
        // load a game
        PgnHolder pgn = new PgnHolder("src/test/resources/philidor.pgn");
        pgn.loadPgn();
        Game game = pgn.getGames().get(0); // get the first game in the collection
        int DEPTH = 5;
        int PV = 3;

        // process the game
        EvalGame eval = new EvalGame("TODO", game, DEPTH, PV);
        eval.analyseGame();

        // check what we extracted
        int numMoves = game.getCurrentMoveList().size();
        Side colour;
        int j;
        Board board = new Board();
        for (int i = -2; i <= numMoves+2; i++) { // bigger range to check for null pointer exceptions
            j = (i < 0 ? i-1 : i)/2; // yay for floor division
            colour = (i%2==0 ? Side.WHITE : Side.BLACK); // the player whose turn it is

            // sanity check
            if (eval.getFen(j, colour) != null) {
                board.loadFromFen(eval.getFen(j, colour));
                System.out.println(board.toString());
            } else {
                System.out.println("Invalid move number. FEN can't be loaded.");
            }

            // actual 'unit tests'
            System.out.println(eval.getFen(j, colour));
            for (int k = 0; k < eval.getPV(); k++) {
                System.out.println(eval.getScoreType(j, colour, k));
                System.out.println(eval.getScoreVal(j, colour, k));
                System.out.println(eval.getLine(j, colour, k));
            }
            System.out.println();
        }
    }
}
