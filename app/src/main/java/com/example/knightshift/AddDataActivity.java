package com.example.knightshift;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;

import java.util.ArrayList;
import java.util.Iterator;

import io.realm.RealmList;
import io.realm.RealmResults;

public class AddDataActivity extends AppCompatActivity {

    private EditText eventEdit, siteEdit, dateEdit, roundEdit, blackEdit, whiteEdit, resultEdit, movesEdit, colorEdit;
    private Button addButton;
    private RadioButton whiteRadio, blackRadio;
    private String moves = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        eventEdit = findViewById(R.id.pgnEventEdit);
        siteEdit = findViewById(R.id.pgnSiteEdit);
        dateEdit = findViewById(R.id.pgnDateEdit);
        roundEdit = findViewById(R.id.pgnRoundEdit);
        blackEdit = findViewById(R.id.pgnBlackEdit);
        whiteEdit = findViewById(R.id.pgnWhiteEdit);
        resultEdit = findViewById(R.id.pgnResultEdit);
        movesEdit = findViewById(R.id.pgnMovesEdit);
        addButton = findViewById(R.id.pgnAddButton);
        whiteRadio = findViewById(R.id.radioWhite);
        blackRadio = findViewById(R.id.radioBlack);

        if(getIntent().getExtras() != null){
            moves = getIntent().getExtras().getString("pgnMoves");

        }
        movesEdit.setText(moves);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String event = eventEdit.getText().toString();
                String site = siteEdit.getText().toString();
                String date = dateEdit.getText().toString();
                String round = roundEdit.getText().toString();
                String black = blackEdit.getText().toString();
                String white = whiteEdit.getText().toString();
                String result = resultEdit.getText().toString();
                moves = movesEdit.getText().toString().trim();
                String colour = "";

                if (whiteRadio.isChecked()) {
                    colour = whiteRadio.getText().toString();
                } else if (blackRadio.isChecked()) {
                    colour = blackRadio.getText().toString();
                }

                final String errorMsg = "Field Required";

                if (TextUtils.isEmpty(event)) {
                    eventEdit.setError(errorMsg);
                    eventEdit.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(site)) {
                    siteEdit.setError(errorMsg);
                    siteEdit.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(date)) {
                    dateEdit.setError(errorMsg);
                    dateEdit.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(round)) {
                    roundEdit.setError(errorMsg);
                    roundEdit.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(black)) {
                    blackEdit.setError(errorMsg);
                    blackEdit.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(white)) {
                    whiteEdit.setError(errorMsg);
                    whiteEdit.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(result)) {
                    resultEdit.setError(errorMsg);
                    resultEdit.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(moves)) {
                    movesEdit.setError(errorMsg);
                    movesEdit.requestFocus();
                    return;
                } else if (colour.isEmpty()) {
                    blackRadio.setError(errorMsg);
                    blackRadio.requestFocus();
                    return;
                }

                if(moves.charAt(moves.length() - 1) != '-' || moves.charAt(moves.length() - 1) != '/'){
                    moves = moves + result;
                }

                colour = colour.toUpperCase();

                // TODO: test validity of entered fields

                // TODO analyse game
                String rawPGN = "[Event \""+event+"\"]\n[Site \""+site+"\"]\n[Date \""+date+
                                "\"]\n[Round \""+round+"\"]\n[Black \""+black+"\"]\n[White \""+
                                white+"\"]\n[Result \""+result+"\"]\n\n"+moves;

                //-------------------------------------------------------------------------------------
                Intent i = new Intent(AddDataActivity.this, DatabaseGui.class);
                i.putExtra("event", event);
                i.putExtra("site", site);
                i.putExtra("date", date);
                i.putExtra("round", round);
                i.putExtra("black", black);
                i.putExtra("white", white);
                i.putExtra("result", result);
                i.putExtra("moves", moves);
                i.putExtra("rawPGN", rawPGN);
                i.putExtra("colour", colour);
                startActivity(i);
                finish();
            }
        });
    }

    public String onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radioWhite:
                if(checked)
                    return "WHITE";
                break;
            case R.id.radioBlack:
                if(checked)
                    return "BLACK";
                break;
        }
        return "error";
    }
}
