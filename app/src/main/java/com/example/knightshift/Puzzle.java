package com.example.knightshift;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;

import com.example.knightshift.db.Blunder;
import com.example.knightshift.db.DatabaseHandle;
import com.example.knightshift.db.MissedMate;
import com.example.knightshift.db.Puzzles;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmResults;

public class Puzzle extends AppCompatActivity {

    private static View[] images = new View[32];
    private Button[] blocks = new Button[64];
    private static int boardIndex;
    private int firstClicked;
    private int secondClicked;
    private boolean reset, nextPuzzle;

    private RealmResults<Puzzles> puzzles;
    private int index = 0;
    private Puzzles currentPuzzle;

    private Board board;

    private int moveNumber = 0;
    private int moveSeqNumber;
    private int correctMoves = 0;
    List<Integer> highlightList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        reset = true;

        TextView textView = findViewById(R.id.textView5);
        TextView textViewTop = findViewById(R.id.textView8);

        images[0] = findViewById(R.id.wp0);
        images[1] = findViewById(R.id.wp1);
        images[2] = findViewById(R.id.wp2);
        images[3] = findViewById(R.id.wp3);
        images[4] = findViewById(R.id.wp4);
        images[5] = findViewById(R.id.wp5);
        images[6] = findViewById(R.id.wp6);
        images[7] = findViewById(R.id.wp7);
        images[8] = findViewById(R.id.wr0);
        images[9] = findViewById(R.id.wr1);
        images[10] = findViewById(R.id.wn0);
        images[11] = findViewById(R.id.wn1);
        images[12] = findViewById(R.id.wb0);
        images[13] = findViewById(R.id.wb1);
        images[14] = findViewById(R.id.wq);
        images[15] = findViewById(R.id.wk);
        images[16] = findViewById(R.id.bp0);
        images[17] = findViewById(R.id.bp1);
        images[18] = findViewById(R.id.bp2);
        images[19] = findViewById(R.id.bp3);
        images[20] = findViewById(R.id.bp4);
        images[21] = findViewById(R.id.bp5);
        images[22] = findViewById(R.id.bp6);
        images[23] = findViewById(R.id.bp7);
        images[24] = findViewById(R.id.br0);
        images[25] = findViewById(R.id.br1);
        images[26] = findViewById(R.id.bn0);
        images[27] = findViewById(R.id.bn1);
        images[28] = findViewById(R.id.bb0);
        images[29] = findViewById(R.id.bb1);
        images[30] = findViewById(R.id.bq);
        images[31] = findViewById(R.id.bk);

        blocks[0] = findViewById(R.id.a8);
        blocks[1] = findViewById(R.id.b8);
        blocks[2] = findViewById(R.id.c8);
        blocks[3] = findViewById(R.id.d8);
        blocks[4] = findViewById(R.id.e8);
        blocks[5] = findViewById(R.id.f8);
        blocks[6] = findViewById(R.id.g8);
        blocks[7] = findViewById(R.id.h8);
        blocks[8] = findViewById(R.id.a7);
        blocks[9] = findViewById(R.id.b7);
        blocks[10] = findViewById(R.id.c7);
        blocks[11] = findViewById(R.id.d7);
        blocks[12] = findViewById(R.id.e7);
        blocks[13] = findViewById(R.id.f7);
        blocks[14] = findViewById(R.id.g7);
        blocks[15] = findViewById(R.id.h7);
        blocks[16] = findViewById(R.id.a6);
        blocks[17] = findViewById(R.id.b6);
        blocks[18] = findViewById(R.id.c6);
        blocks[19] = findViewById(R.id.d6);
        blocks[20] = findViewById(R.id.e6);
        blocks[21] = findViewById(R.id.f6);
        blocks[22] = findViewById(R.id.g6);
        blocks[23] = findViewById(R.id.h6);
        blocks[24] = findViewById(R.id.a5);
        blocks[25] = findViewById(R.id.b5);
        blocks[26] = findViewById(R.id.c5);
        blocks[27] = findViewById(R.id.d5);
        blocks[28] = findViewById(R.id.e5);
        blocks[29] = findViewById(R.id.f5);
        blocks[30] = findViewById(R.id.g5);
        blocks[31] = findViewById(R.id.h5);
        blocks[32] = findViewById(R.id.a4);
        blocks[33] = findViewById(R.id.b4);
        blocks[34] = findViewById(R.id.c4);
        blocks[35] = findViewById(R.id.d4);
        blocks[36] = findViewById(R.id.e4);
        blocks[37] = findViewById(R.id.f4);
        blocks[38] = findViewById(R.id.g4);
        blocks[39] = findViewById(R.id.h4);
        blocks[40] = findViewById(R.id.a3);
        blocks[41] = findViewById(R.id.b3);
        blocks[42] = findViewById(R.id.c3);
        blocks[43] = findViewById(R.id.d3);
        blocks[44] = findViewById(R.id.e3);
        blocks[45] = findViewById(R.id.f3);
        blocks[46] = findViewById(R.id.g3);
        blocks[47] = findViewById(R.id.h3);
        blocks[48] = findViewById(R.id.a2);
        blocks[49] = findViewById(R.id.b2);
        blocks[50] = findViewById(R.id.c2);
        blocks[51] = findViewById(R.id.d2);
        blocks[52] = findViewById(R.id.e2);
        blocks[53] = findViewById(R.id.f2);
        blocks[54] = findViewById(R.id.g2);
        blocks[55] = findViewById(R.id.h2);
        blocks[56] = findViewById(R.id.a1);
        blocks[57] = findViewById(R.id.b1);
        blocks[58] = findViewById(R.id.c1);
        blocks[59] = findViewById(R.id.d1);
        blocks[60] = findViewById(R.id.e1);
        blocks[61] = findViewById(R.id.f1);
        blocks[62] = findViewById(R.id.g1);
        blocks[63] = findViewById(R.id.h1);

        //------------------------------------------------------------------------------------------

        puzzles = DatabaseHandle.getHandle().getDuePuzzles();
        //puzzles = DatabaseHandle.getHandle().getPuzzles();
        index = 0;
        currentPuzzle = puzzles.get(index);
        draw(currentPuzzle.getFen());
        board = new Board();
        board.loadFromFen(currentPuzzle.getFen());
        Side ourSide = Side.fromValue(currentPuzzle.getColor());
        if(currentPuzzle.isFromBlunder()){
            if(ourSide == Side.WHITE){
                textViewTop.setText("Find best sequence of moves\n" +
                        "You are White");
            }else{
                textViewTop.setText("Find best sequence of moves\n" +
                        "You are Black");
            }
        }else{
            if(ourSide == Side.WHITE){
                textViewTop.setText("Mate the opponent\n" +
                        "You are White");
            }else{
                textViewTop.setText("Mate the opponent\n" +
                        "You are Black");
            }
        }
        textView.setText("");

        Log.i("Puzzles", "Expected Moves:\n" + String.join("\n", currentPuzzle.getAltMoves()));

        for (int i = 0; i < 64; i++) {
            int finalI = i;
            blocks[i].setOnClickListener(v -> {
                if (reset) {
                    reset = false;
                    firstClicked = finalI;
                    //check if the selected piece belongs to them:
                    String srcTile = getTile(firstClicked).toUpperCase();
                    Piece piece = board.getPiece(Square.fromValue(srcTile));
                    if(piece.getPieceSide() != Side.fromValue(currentPuzzle.getColor())){
                        reset = true;
                    }else{
                        makeGreen(firstClicked);
                        highlightMoves();
                        for (Integer tile : highlightList) {
                            makeGreen(tile);
                        }
                    }
                } else {
                    reset = true;
                    secondClicked = finalI;

                    // move validation
                    String move = genMove(firstClicked, secondClicked);
                    Log.i("Puzzle", "Move: " + move);

                    if (validMove(move)) {
                        // move valid
                        Log.i("Puzzle", "Valid Move: " + move);
                        board.doMove(move);
                        draw(board.getFen());
                        movePiece(blocks[firstClicked], blocks[secondClicked]);
                        makeDefaultColour(firstClicked);
                        for (Integer tile : highlightList) {
                            makeDefaultColour(tile);
                        }
                        textView.setText("Good Move!");
                        correctMoves++;

                        // get opponents move
                        if (moveNumber-1 < currentPuzzle.getAltMoves().get(moveSeqNumber).split(" ").length) {
                            String opponentsMove = getOpponentsMove();
                            Log.i("Puzzle", "Opponents Move: " + opponentsMove + "\nMaking Move Opponents Move . . .");


                            Handler handler = new Handler(Looper.myLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("THREAD: start");
                                    board.doMove(opponentsMove);
                                    draw(board.getFen());
                                    if(moveNumber < currentPuzzle.getAltMoves().get(moveSeqNumber).split(" ").length){
                                        textView.setText("Try the next move");
                                    }
                                    System.out.println("THREAD: end");
                                }
                            }, 1000);

                            makeDefaultColour(firstClicked);
                            for (Integer tile : highlightList) {
                                makeDefaultColour(tile);
                            }

                            // check if there are moves left
                            if (moveNumber >= currentPuzzle.getAltMoves().get(moveSeqNumber).split(" ").length) {
                                // load next puzzle
                                reset = true;
                                textViewTop.setText("Correct Answer");
                                index++;
                                if (index < puzzles.size()) {
                                    nextPuzzle = true;
                                    textView.setText("Press Next For New Puzzle");
                                } else {
                                    // no more puzzles left
                                    nextPuzzle = false;
                                    textView.setText("No More Puzzles\n" +
                                            "Press Next To Exit");
                                }
                                index--;
                            }
                        } else {
                            Log.i("Puzzle", "No Opponents Move Available");
                            // load next puzzle
                            reset = true;
                            textViewTop.setText("Correct Answer");
                            index++;
                            if (index < puzzles.size()) {
                                nextPuzzle = true;
                                textView.setText("Press Next For New Puzzle");
                            } else {
                                // no more puzzles left
                                nextPuzzle = false;
                                textView.setText("No More Puzzles\n" +
                                        "Press Next To Exit");
                            }
                            index--;
                        }

                    } else {
                        makeDefaultColour(firstClicked);
                        for (Integer tile : highlightList) {
                            makeDefaultColour(tile);
                        }
                        Move moveCheck = new Move(move, Side.fromValue(currentPuzzle.getColor()));
                        List<Move> legalMoveList = board.legalMoves();
                        if(legalMoveList.contains(moveCheck)){
                            if(firstClicked != secondClicked){
                                board.doMove(move);
                                draw(board.getFen());
                            }

                            if(moveNumber == 0){
                                textViewTop.setText("Incorrect Answer");
                            }else{
                                textViewTop.setText("Partially Correct Answer");
                            }
                            index++;
                            if (index < puzzles.size()) {
                                nextPuzzle = true;
                                textView.setText("Press Next For New Puzzle");
                            } else {
                                // no more puzzles left
                                nextPuzzle = false;
                                textView.setText("No More Puzzles\n" +
                                        "Press Next To Exit");
                            }
                            index--;
                        }else{
                            Toast.makeText(this, "Invalid Move", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        //------------------------------------------------------------------------------------------


        Button nextButton = findViewById(R.id.buttonN);

        nextButton.setOnClickListener(v -> {
            reset = true;
            scorePuzzle();
            index++;
            if (index < puzzles.size()) {
                currentPuzzle = puzzles.get(index);
                draw(currentPuzzle.getFen());
                if(currentPuzzle.isFromBlunder()){
                    if(Side.fromValue(currentPuzzle.getColor()) == Side.WHITE){
                        textViewTop.setText("Find best sequence of moves\n" +
                                "You are White");
                    }else{
                        textViewTop.setText("Find best sequence of moves\n" +
                                "You are Black");
                    }
                }else{
                    if(Side.fromValue(currentPuzzle.getColor()) == Side.WHITE){
                        textViewTop.setText("Mate the opponent\n" +
                                "You are White");
                    }else{
                        textViewTop.setText("Mate the opponent\n" +
                                "You are Black");
                    }
                }
                textView.setText("");
                moveNumber = 0;
                moveSeqNumber = 0;
                correctMoves = 0;
                board.loadFromFen(currentPuzzle.getFen());
                Log.i("Puzzles", "Expected Moves:\n" + String.join("\n", currentPuzzle.getAltMoves()));
            } else {
                Toast.makeText(this, "Puzzle Review Done", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        //-----------------------------------------------------------------------------------------

        Log.i("Puzzle", puzzles.size() + " puzzles found\n");

        //-----------------------------------------------------------------------------------------

    }

    private void scorePuzzle(){
        int numMovesMax = currentPuzzle.getAltMoves().get(moveSeqNumber).length();
        if(numMovesMax % 2 != 0){
            numMovesMax--;
        }
        if(numMovesMax < 8){
            numMovesMax = 1;
        }else {
            numMovesMax = Math.floorDiv(numMovesMax,8);
        }
        double scorePre = ((double) correctMoves / (double) numMovesMax)*6;
        int score;
        if((correctMoves /  numMovesMax) == 1){
            score = (int) Math.floor(scorePre)-1;
        }else{
            score = (int) Math.floor(scorePre);
        }
        DatabaseHandle.gradePuzzle(currentPuzzle,score);
    }

    private boolean validMove(String move) {
        int i = 0;
        for (String moveSeq : currentPuzzle.getAltMoves()) {
            String[] moves = moveSeq.split(" ");
            if (moveNumber < moves.length) {
                // moves still left
                if (moves[moveNumber].equals(move)) {
                    if (moveNumber == 0) {
                        // save selected move sequence
                        moveSeqNumber = i;
                    }
                    // move found
                    moveNumber += 2;
                    return true;
                }
            }
            i++;
        }
        return false;
    }

    private String getOpponentsMove() {
        return currentPuzzle.getAltMoves().get(moveSeqNumber).split(" ")[moveNumber-1];
    }

    private String getTile(int pos) {
        String[] cols = {"a", "b", "c", "d", "e", "f", "g", "h"};
        int i = 8 - (int) (pos / 8);
        int j = pos % 8;

        return cols[j] + i;
    }


    private String genMove(int fromTile, int toTile) {
        return getTile(fromTile) + getTile(toTile);
    }

    /**
     * Finds the piece located on the "from" Button and moves it to the "to" Button.
     * @param from The Button (block) where the piece is currently situated.
     * @param to   The Button (block) that the piece needs to move to.
     */
    void movePiece(Button from, Button to) {
        for (int i = 0; i < 32; i++) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) images[i].getLayoutParams();
            if (from.getId() == layoutParams.topToTop) {
                ConstraintLayout layout = findViewById(R.id.puzzle_full);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(images[i].getId(), ConstraintSet.START, to.getId(), ConstraintSet.START);
                constraintSet.connect(images[i].getId(), ConstraintSet.END, to.getId(), ConstraintSet.END);
                constraintSet.connect(images[i].getId(), ConstraintSet.TOP, to.getId(), ConstraintSet.TOP);
                constraintSet.connect(images[i].getId(), ConstraintSet.BOTTOM, to.getId(), ConstraintSet.BOTTOM);
                constraintSet.applyTo(layout);
            }
        }
    }



    void display(int pieceIndex) {
        boardIndex++;
        try {
            images[pieceIndex].setVisibility(View.VISIBLE);
            images[pieceIndex].bringToFront();
            ConstraintLayout layout = findViewById(R.id.puzzle_full);
            Button position = blocks[boardIndex];
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(layout);
            constraintSet.connect(images[pieceIndex].getId(), ConstraintSet.START, position.getId(), ConstraintSet.START);
            constraintSet.connect(images[pieceIndex].getId(), ConstraintSet.END, position.getId(), ConstraintSet.END);
            constraintSet.connect(images[pieceIndex].getId(), ConstraintSet.TOP, position.getId(), ConstraintSet.TOP);
            constraintSet.connect(images[pieceIndex].getId(), ConstraintSet.BOTTOM, position.getId(), ConstraintSet.BOTTOM);
            constraintSet.applyTo(layout);
            System.out.println("Putting piece " + pieceIndex + " on block " + blocks[boardIndex]);
        } catch (Exception exception) {
            System.err.println("Unable to display piece on block " + boardIndex);
        }
    }

    public void displayChessPiece(char p) {
        // TODO: make this function more efficient
        // https://stackoverflow.com/questions/4138527/how-to-call-a-java-method-using-a-variable-name

        int displayPiece = -1;

        switch (p) {
            case 'P':
                displayPiece = NextPiece.next_P();
                display(displayPiece);
                break;

            case 'R':
                displayPiece = NextPiece.next_R();
                display(displayPiece);
                break;

            case 'N':
                displayPiece = NextPiece.next_N();
                display(displayPiece);
                break;

            case 'B':
                displayPiece = NextPiece.next_B();
                display(displayPiece);
                break;

            case 'Q':
                displayPiece = NextPiece.next_Q();
                display(displayPiece);
                break;

            case 'K':
                displayPiece = NextPiece.next_K();
                display(displayPiece);
                break;

            case 'p':
                displayPiece = NextPiece.next_p();
                display(displayPiece);
                break;

            case 'r':
                displayPiece = NextPiece.next_r();
                display(displayPiece);
                break;

            case 'n':
                displayPiece = NextPiece.next_n();
                display(displayPiece);
                break;

            case 'b':
                displayPiece = NextPiece.next_b();
                display(displayPiece);
                break;

            case 'q':
                displayPiece = NextPiece.next_q();
                display(displayPiece);
                break;

            case 'k':
                displayPiece = NextPiece.next_k();
                display(displayPiece);
                break;

            case '/':
                break;

            default:
                System.err.println("Invalid char " + p + " in FEN.");
        }

    }

    static void makePiecesInvisible() {
        for (int i = 0; i < 32; i++) {
            images[i].setVisibility(View.INVISIBLE);
        }
    }

    void makeGreen(int blockIndex) {
        if (((((blockIndex >= 0) && (blockIndex < 8)) || ((blockIndex >= 16) && (blockIndex < 24))
                || ((blockIndex >= 32) && (blockIndex < 40)) || ((blockIndex >= 48) && (blockIndex < 56)))
                && ((blockIndex % 2) == 0)) || ((((blockIndex >= 8) && (blockIndex < 16))
                || ((blockIndex >= 24) && (blockIndex < 32)) || ((blockIndex >= 40) && (blockIndex < 48))
                || ((blockIndex >= 56) && (blockIndex < 64))) && ((blockIndex % 2) == 1))) {
            // block is white
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                blocks[blockIndex].setForeground(getResources().getDrawable(R.drawable.green_white_block));
            }
        } else {
            // block is grey
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                blocks[blockIndex].setForeground(getResources().getDrawable(R.drawable.green_grey_block));
            }
        }
    }

    void makeDefaultColour(int blockIndex) {
        if (((((blockIndex >= 0) && (blockIndex < 8)) || ((blockIndex >= 16) && (blockIndex < 24))
                || ((blockIndex >= 32) && (blockIndex < 40)) || ((blockIndex >= 48) && (blockIndex < 56)))
                && ((blockIndex % 2) == 0)) || ((((blockIndex >= 8) && (blockIndex < 16))
                || ((blockIndex >= 24) && (blockIndex < 32)) || ((blockIndex >= 40) && (blockIndex < 48))
                || ((blockIndex >= 56) && (blockIndex < 64))) && ((blockIndex % 2) == 1))) {
            // block is white
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                blocks[blockIndex].setForeground(getResources().getDrawable(R.drawable.white_block));
            }
        } else {
            // block is grey
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                blocks[blockIndex].setForeground(getResources().getDrawable(R.drawable.grey_block));
            }
        }
    }

    void draw(String FEN) {
        System.out.println("FEN String is " + FEN);
        boardIndex = -1;
        Arrays.fill(NextPiece.pieceVisibility, Boolean.FALSE);
        makePiecesInvisible();

        char[] charArray = FEN.toCharArray();
        int totalChars = charArray.length;

        for (int i = 0; i < totalChars; i++) {
            int add = Character.getNumericValue(charArray[i]);
            if ((add >= 1) && (add <= 8)) {
                boardIndex += add;
            } else {
                displayChessPiece(charArray[i]);
            }
            if (boardIndex >= 63) {
                break;
            }
        }

    }

    void highlightMoves(){
        List<Move> legalMoveList = board.legalMoves();
        String currentPos = getTile(firstClicked);
        highlightList.clear();
        for (Move move : legalMoveList) {
            if(move.toString().startsWith(currentPos)){
                highlightList.add(getPieceIdx(move.toString().substring(2)));
            }
        }
    }

    int getPieceIdx(String pos){
        return ((pos.charAt(0) - 'a') + (7 - (pos.charAt(1) - '1')) * 8);
    }

}
