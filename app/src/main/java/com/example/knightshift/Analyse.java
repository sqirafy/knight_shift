package com.example.knightshift;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.knightshift.db.Blunder;
import com.example.knightshift.db.DatabaseHandle;
import com.example.knightshift.db.MissedMate;
import com.example.knightshift.db.PGN;
import com.example.knightshift.db.Puzzles;
import com.example.knightshift.stockfish.AnalysisThread;
import com.example.knightshift.stockfish.Blunders;
import com.example.knightshift.stockfish.MissedMates;
import com.example.knightshift.stockfish.SetupStockfish;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.Side;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Stack;

import io.realm.RealmList;

public class Analyse extends AppCompatActivity {

    private static View[] images = new View[32];
    private ConstraintLayout[] blocks = new ConstraintLayout[64];
    private static int boardIndex;
    private final Stack<Move> moveStack = new Stack<>();
    private Blunders blunders;
    private MissedMates missedMates;
    private Boolean evaluated;
    private List<String> line;
    int linePtr;
    private int thisMove;
    private TextView textView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_analyse);

        textView = findViewById(R.id.textView6);
        TextView yourColour = findViewById(R.id.textView9);


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

        // alternative line, like a blunder or a missed mate, null indicates normal execution
        line = null;

        String id = getIntent().getExtras().getString("PGN_ID");
        PGN pgnObj = DatabaseHandle.getHandle().getPGN(new ObjectId(id));
        String pgn = pgnWrapper.getRawPGN(pgnObj);

        if(Objects.equals(pgnObj.getColor(), Side.WHITE.toString())){
            yourColour.setText("You are White");
        }else{
            yourColour.setText("You are Black");
        }

        Game game = ChesslibWrapper.stringToGame(pgn);

        MoveList moves = game.getHalfMoves();
        Iterator<Move> iterator = moves.iterator();
        Board board = new Board(); // initialised with standard

        //Get blunders and missed mates from db
        RealmList<Blunder> pgnBlunders = pgnObj.getBlunders();
        RealmList<MissedMate> pgnMissedMates = pgnObj.getMissedMates();

        //-----BLUNDERS-----------------------------------------------------------------------------
        Side colourFound;
        if(pgnObj.getColor().equalsIgnoreCase("white")){
            colourFound = Side.WHITE;
        }else{
            colourFound = Side.BLACK;
        }
        ArrayList<String> FENs = new ArrayList<>();
        ArrayList<Integer> MoveNumbers = new ArrayList<>();
        List<List<List<String>>> Lines = new ArrayList<>();

        int nextIdx;
        String currentMove, subMove;
        boolean next;
        for(int i = 0; i < pgnBlunders.size(); i++){
            FENs.add(pgnBlunders.get(i).getFen());
            MoveNumbers.add(pgnBlunders.get(i).getMoveNumber());
            List<List<String>> LinesSub = new ArrayList<>();
            for(int j = 0; j < pgnBlunders.get(i).getBlunderAltMoves().size(); j++){
                currentMove = pgnBlunders.get(i).getBlunderAltMoves().get(j);
                List<String> LinesSubSub = new ArrayList<>();
                if(currentMove.contains(" ")){
                    next = true;
                }else{
                    next = false;
                    if(!currentMove.isEmpty()){
                        LinesSubSub.add(currentMove);
                    }
                }
                while(next){
                    nextIdx = currentMove.indexOf(" ");
                    if(nextIdx > 0){
                        subMove = currentMove.substring(0,nextIdx);
                        LinesSubSub.add(subMove);
                        currentMove = currentMove.substring(currentMove.indexOf(" ")+1);
                    }else{
                        LinesSubSub.add(currentMove);
                        next = false;
                    }
                }
                LinesSub.add(LinesSubSub);
            }
            Lines.add(LinesSub);
        }
        Blunders blunderNew = new Blunders(colourFound, FENs, null, MoveNumbers, Lines);
        blunders = blunderNew;

        //-----MISSED MATES-------------------------------------------------------------------------
        ArrayList<String> mmFENs = new ArrayList<>();
        ArrayList<Integer> mateInX = new ArrayList<>();
        List<List<String>> LinesMM = new ArrayList<>();

        for(int i = 0; i < pgnMissedMates.size(); i++){
            mmFENs.add(pgnMissedMates.get(i).getFen());
            mateInX.add(pgnMissedMates.get(i).getMateInX());
            List<String> LinesSub = new ArrayList<>();
            currentMove = pgnMissedMates.get(i).getMissedMateAltMove();
            if(currentMove.contains(" ")){
                next = true;
            }else{
                next = false;
                if(!currentMove.isEmpty()){
                    LinesSub.add(currentMove);
                }
            }
            while(next){
                nextIdx = currentMove.indexOf(" ");
                if(nextIdx > 0){
                    subMove = currentMove.substring(0,nextIdx);
                    LinesSub.add(subMove);
                    currentMove = currentMove.substring(currentMove.indexOf(" ")+1);
                }else{
                    LinesSub.add(currentMove);
                    next = false;
                }
            }
            LinesMM.add(LinesSub);
        }
        MissedMates missedMatesNew = new MissedMates(colourFound, mmFENs, LinesMM, mateInX);
        missedMates = missedMatesNew;
        //------------------------------------------------------------------------------------------
//        AnalysisThread thread = null;
//
//        try {
//            // TODO: get player side as input
//            thread = new AnalysisThread(stockfishPath, game, Side.WHITE, 5, 3);
//            thread.start();
//            while (!thread.done()) {
//                if (!thread.isAlive() && !thread.done()) {
//                    System.out.println("Unable to analyse game.");
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Unable to create thread for analysis.");
//            System.exit(0);
//        }
//        blunders = thread.getBlunders();
//        missedMates = thread.getMissedMates();

        draw(board.getFen());
        thisMove = -1;

        Button nextButton = findViewById(R.id.buttonN);

        nextButton.setOnClickListener(v -> {
            Move move;
            if (line != null) {
                // view missed mate move
                if (linePtr != line.size()) {
                    board.doMove(line.get(linePtr));
                    draw(board.getFen());
                    linePtr++;
                }
                return;
            } else if (!moveStack.isEmpty()) {
                move = moveStack.pop();
                thisMove++;
            } else if (iterator.hasNext()) {
                move = iterator.next();
                thisMove++;
            } else {
                return; // no more moves
            }
            board.doMove(move);
            draw(board.getFen());
            draw(board.getFen());
        });

        Button backButton = findViewById(R.id.buttonB);

        backButton.setOnClickListener(v -> {
            if (line != null) {
                if (linePtr == 0) {
                    line = null;
                    for (int i = 0; i < 64; i++) {
                        makeDefaultColour(i);
                    }
                } else {
                    board.undoMove();
                    linePtr--;
                    draw(board.getFen());
                    return;
                }
            }
            if (board.getBackup().size() == 0) {
                // do nothing because there isn't a previous move
                return;
            }
            thisMove--;
            Move move = board.undoMove();
            moveStack.push(move);
            draw(board.getFen());
        });

        ImageButton blunderButton = findViewById(R.id.blunderButton);

        blunderButton.setOnClickListener(v -> {
            /* Reference: https://www.geeksforgeeks.org/popup-menu-in-android-with-example/#:~:te
             * xt=Go%20to%20app%20%3E%20res%20%3E%20right,and%20name%20it%20as%20popup_menu.
             */
            PopupMenu popupOptions = new PopupMenu(Analyse.this, blunderButton);
            popupOptions.getMenuInflater().inflate(R.menu.popup_blunder_options, popupOptions.getMenu());
            popupOptions.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    int chosenOption;
                    if (menuItem.getTitle().equals("Option 1")) {
                        chosenOption = 0;
                    } else if (menuItem.getTitle().equals("Option 2")) {
                        chosenOption = 1;
                    } else if (menuItem.getTitle().equals("Option 3")) {
                        chosenOption = 2;
                    } else {
                        chosenOption = 0;
                        System.out.println("Error occurred with blunder option popup menu.");
                    }
                    textView.setText("Showing moves for " + menuItem.getTitle());
                    for (int i = 0; i < 64; i++) {
                        makeOrange(i);
                    }
                    List<List<List<String>>> lines = blunders.getLines();
                    List<String> fens = blunders.getFens();
                    int i;
                    for (i = 0; i < fens.size(); i++) {
                        if (fens.get(i).equals(board.getFen())) {
                            break;
                        }
                    }
                    line = lines.get(i).get(chosenOption);
                    int linePtr = 0;
                    return true;
                }
            });
            popupOptions.show();
        });

        ImageButton missedMateButton = findViewById(R.id.missedMateButton);

        missedMateButton.setOnClickListener(v -> {
            for (int i = 0; i < 64; i++) {
                makeRed(i);
            }
            List<String> fens = missedMates.getFens();
            List<List<String>> lines = missedMates.getLines();
            int j;
            for (j = 0; j < fens.size(); j++) {
                if (fens.get(j).equals(board.getFen())) {
                    break;
                }
            }

            line = lines.get(j);
            int linePtr = 0;

        });


    }

    void makeRed(int blockIndex) {
        if (((((blockIndex >= 0) && (blockIndex < 8)) || ((blockIndex >= 16) && (blockIndex < 24))
                || ((blockIndex >= 32) && (blockIndex < 40)) || ((blockIndex >= 48) && (blockIndex < 56)))
                && ((blockIndex % 2) == 0)) || ((((blockIndex >= 8) && (blockIndex < 16))
                || ((blockIndex >= 24) && (blockIndex < 32)) || ((blockIndex >= 40) && (blockIndex < 48))
                || ((blockIndex >= 56) && (blockIndex < 64))) && ((blockIndex % 2) == 1))) {
            // block is white
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                blocks[blockIndex].setForeground(getResources().getDrawable(R.drawable.red_white_block));
            }
        } else {
            // block is grey
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                blocks[blockIndex].setForeground(getResources().getDrawable(R.drawable.red_grey_block));
            }
        }
    }

    void makeOrange(int blockIndex) {
        if (((((blockIndex >= 0) && (blockIndex < 8)) || ((blockIndex >= 16) && (blockIndex < 24))
                || ((blockIndex >= 32) && (blockIndex < 40)) || ((blockIndex >= 48) && (blockIndex < 56)))
                && ((blockIndex % 2) == 0)) || ((((blockIndex >= 8) && (blockIndex < 16))
                || ((blockIndex >= 24) && (blockIndex < 32)) || ((blockIndex >= 40) && (blockIndex < 48))
                || ((blockIndex >= 56) && (blockIndex < 64))) && ((blockIndex % 2) == 1))) {
            // block is white
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                blocks[blockIndex].setForeground(getResources().getDrawable(R.drawable.orange_white_block));
            }
        } else {
            // block is grey
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                blocks[blockIndex].setForeground(getResources().getDrawable(R.drawable.orange_grey_block));
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

    void display(int pieceIndex) {
        boardIndex++;
        try {
            images[pieceIndex].setVisibility(View.VISIBLE);
            images[pieceIndex].bringToFront();
            ConstraintLayout layout = findViewById(R.id.full);
//            ConstraintLayout position = blocks[boardIndex];
            ConstraintLayout position = blocks[boardIndex];
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(layout);
            constraintSet.connect(images[pieceIndex].getId(), ConstraintSet.START, position.getId(), ConstraintSet.START);
            constraintSet.connect(images[pieceIndex].getId(), ConstraintSet.END, position.getId(), ConstraintSet.END);
            constraintSet.connect(images[pieceIndex].getId(), ConstraintSet.TOP, position.getId(), ConstraintSet.TOP);
            constraintSet.connect(images[pieceIndex].getId(), ConstraintSet.BOTTOM, position.getId(), ConstraintSet.BOTTOM);
            constraintSet.applyTo(layout);

        } catch (Exception exception){
            System.err.println("Unable to display piece on block " + boardIndex);
        }


    }

    public void displayChessPiece(char p) {
        // TODO: make this function more efficient
        // https://stackoverflow.com/questions/4138527/how-to-call-a-java-method-using-a-variable-name

        int displayPiece = -1;

        switch(p) {
            case 'P':
                displayPiece = NextPiece.next_P();
                display(displayPiece);
//                System.out.println("Display white pawn");
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
    // Don't change for loop ~ will give problems with emulator
    static void makePiecesInvisible() {
        for (int i=0; i<32; i++) {
            images[i].setVisibility(View.INVISIBLE);
        }
    }

    void draw(String FEN) {
        System.out.println("FEN String is " + FEN);
        boardIndex = -1;
        ImageButton blunderButton = findViewById(R.id.blunderButton);
        blunderButton.setVisibility(View.GONE);
        ImageButton missedMateButton = findViewById(R.id.missedMateButton);
        missedMateButton.setVisibility(View.GONE);
        textView.setText("");
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

//        List<Integer> blunderMoveNumbers = blunders.getMoveNumbers();
//        for (Integer i : blunderMoveNumbers) {
//            if (thisMove == i + 1) {
//                blunderButton.setVisibility(View.VISIBLE);
//            }
//        }
        List<String> blunderFens = blunders.getFens();
        for (String fen : blunderFens) {
            if (FEN.equals(fen)) {
                blunderButton.setVisibility(View.VISIBLE);
                textView.setText("Blunder found");
            }
        }

        List<String> missedMateFENs = missedMates.getFens();
        for (String fen : missedMateFENs) {
            if (FEN.equals(fen)) {
                missedMateButton.setVisibility(View.VISIBLE);
                textView.setText("Missed mate found");
            }
        }

    }

}
