package com.example.knightshift;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.knightshift.db.Blunder;
import com.example.knightshift.db.DatabaseHandle;
import com.example.knightshift.db.MissedMate;
import com.example.knightshift.db.PGN;
import com.example.knightshift.db.Puzzles;
import com.example.knightshift.ocr.OcrThread;
import com.example.knightshift.stockfish.AnalysisThread;
import com.example.knightshift.stockfish.Blunders;
import com.example.knightshift.stockfish.MissedMates;
import com.example.knightshift.stockfish.SetupStockfish;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.game.Game;
import com.google.android.material.textfield.TextInputLayout;
import android.view.View;

import io.realm.RealmList;
import io.realm.RealmResults;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DatabaseGui extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //----Image button Zanelle
    private static final int PERMISSION = 1;
    private static final int REQUESTG = 200;
    TextView file_name;
    String file_path = null;
    //-----------------------

    ArrayList<String> db_Name = new ArrayList<String>();
    ArrayList<String> db_ID = new ArrayList<String>();
    PGN targetPGN;
    boolean loggedIn = false;
    ProgressBar progressBar;
    private String eloString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_gui);

        DatabaseHandle dbHandle = DatabaseHandle.getHandle();

        if(getIntent().getExtras() != null){
            String event = getIntent().getExtras().getString("event");
            String site = getIntent().getExtras().getString("site");
            String date = getIntent().getExtras().getString("date");
            String round = getIntent().getExtras().getString("round");
            String black = getIntent().getExtras().getString("black");
            String white = getIntent().getExtras().getString("white");
            String result = getIntent().getExtras().getString("result");
            String moves = getIntent().getExtras().getString("moves");
            String rawPGN = getIntent().getExtras().getString("rawPGN");
            String colour = getIntent().getExtras().getString("colour");
            PGN pgnObj = new PGN(event,site,date,round,black,white,result,moves,rawPGN,colour,null,null,null);
            int elo;
            if (eloString.equals("")) {
                elo = 1500;
            } else {
                elo = Integer.parseInt(eloString);
            }
            createAnalysedPGN(pgnObj, elo);
        }

        //get Ids from database
        refreshId(db_Name, db_ID, dbHandle);

        //initialise UI component variables
        TextInputLayout pgnTil = (TextInputLayout) findViewById(R.id.pgnInput);
        Button pgnButton = findViewById(R.id.pgnEnter);
        Spinner pgnSelector = (Spinner) findViewById(R.id.pgnSelect);
        Button delButton = findViewById(R.id.pgnDelete);
        Button cardButton = findViewById(R.id.scorecardBtn);
        Button analyseButton = findViewById(R.id.pgnAnalyse);
        Button manualButton = findViewById(R.id.pgnManual);
        Button puzzleButton = findViewById(R.id.puzzleButton);
        progressBar = findViewById(R.id.progressBar);

        //Progress bar
        progressBar.bringToFront();
        progressBar.setVisibility(View.INVISIBLE);

        //Spinner
        pgnSelector.setOnItemSelectedListener(this);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, db_Name);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pgnSelector.setAdapter(adapter);

        //Text Input Layer check with Button Press
        pgnButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (eloString.equals("")) {
                    popupEloInput();
                } else {
                    String pgnFileName = String.valueOf(pgnTil.getEditText().getText());
                    boolean validFile = pgnWrapper.checkPgn(pgnFileName);
                    if(!validFile){
                        pgnTil.setErrorEnabled(true);
                        pgnTil.setError("Please provide a PGN file");
                    }else{
                        pgnTil.setErrorEnabled(false);
                        String pgn = null;
                        try {
                            pgn = ChesslibWrapper.getPgnFromAsset(pgnFileName, getAssets());
                            PGN pgnObj = new PGN();
                            pgnWrapper.parsePGN(pgn, pgnObj);
                            int elo;
                            if (eloString.equals("")) {
                                elo = 1500;
                            } else {
                                elo = Integer.parseInt(eloString);
                            }
                            createAnalysedPGN(pgnObj, elo);
                            targetPGN = pgnObj;
                            refreshId(db_Name, db_ID, dbHandle);
                            pgnSelector.setAdapter(adapter);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        //Deletes an instance from the database
        delButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                RealmResults<PGN> pgnList = dbHandle.getPGNs();
                if(pgnList.size() > 0){
                    String findID = pgnSelector.getSelectedItem().toString();
                    int idx = db_Name.indexOf(findID);
                    findID = db_ID.get(idx);
                    PGN pgnObj = null;
                    for(int i = 0; i < pgnList.size(); i++){
                        assert pgnList.get(i) != null;
                        if(findID.equals(pgnWrapper.getID(pgnList.get(i)))){
                            pgnObj = pgnList.get(i);
                            break;
                        }
                    }
                    dbHandle.deletePGN(pgnObj);
                    refreshId(db_Name, db_ID, dbHandle); //refresh spinner
                    pgnSelector.setAdapter(adapter);
                }
            }
        });

//        Moves to analyse screen with chessboard (send selected PGN object)
        analyseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //find instance fields from database
                //load fields into PGN object
                RealmResults<PGN> pgnList = dbHandle.getPGNs();
                if(pgnList.size() > 0){
                    String findID = pgnSelector.getSelectedItem().toString();
                    int idx = db_Name.indexOf(findID);
                    findID = db_ID.get(idx);
                    PGN pgnObj = null;
                    for(int i = 0; i < pgnList.size(); i++){
                        assert pgnList.get(i) != null;
                        if(findID.equals(pgnWrapper.getID(pgnList.get(i)))){
                            pgnObj = pgnList.get(i);
                            targetPGN = pgnObj;
                            break;
                        }
                    }

                    Intent i = new Intent(DatabaseGui.this, Analyse.class);
                    i.putExtra("PGN_ID", targetPGN.get_id().toHexString());
                    startActivity(i);
                }
            }
        });

        //Moves to manually add PGN page Siphesihle
        manualButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (eloString.equals("")) {
                    popupEloInput();
                } else {
                    Intent i = new Intent(DatabaseGui.this, AddDataActivity.class);
                    startActivity(i);
                }
            }
        });

        //Zanelle button
        //Load database instance with Button Press
        cardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (eloString.equals("")) {
                    popupEloInput();
                } else {
                    //check permission greater than equal to marshmallow we used run time permission
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermission()) {
                            selectImage();
                        } else {
                            requestPermission();
                        }
                    }
                }
            }
        });

        puzzleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(DatabaseHandle.getHandle().getDuePuzzles().size() == 0){
                    Toast.makeText(getApplicationContext(), "No Puzzles", Toast.LENGTH_LONG).show();
                }else{
                    Intent i = new Intent(DatabaseGui.this, Puzzle.class);
                    startActivity(i);
                }
            }
        });
//        file_name = findViewById(R.id.pgnInputEdit);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(getApplicationContext(), db_Name.get(position), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //stub
    }

    private void popupEloInput() {
        /* Reference: https://stackoverflow.com/questions/32050647/how-to-create-simple-android-stu
         * dio-pop-up-window-with-edittext-field-for-data-i
         */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText eloInput = new EditText(this);
        eloInput.setHint("ELO rating, e.g. 2000");
        alertDialogBuilder.setView(eloInput);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Enter",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String newElo = eloInput.getText().toString();
                updateElo(newElo);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void updateElo(String newElo) {
        eloString = newElo;
    }

    public void refreshId(ArrayList<String> pgnName, ArrayList<String> pgnID, DatabaseHandle dbHandle){
        RealmResults<PGN> pgnList = dbHandle.getPGNs(); //pulls all PGNs from db
        //iterate through list and extract all IDs into a new arraylist to return (db_IDs)
        if(pgnList.size() < 1){
            pgnName.clear();
            pgnID.clear();
            pgnName.add(0, "None");
        } else{
            pgnName.clear();
            pgnID.clear();
            for(int i = 0; i < pgnList.size(); i++){
                assert pgnList.get(i) != null;
                pgnName.add(i, pgnWrapper.getNameFromID(pgnList.get(i)));
                pgnID.add(i, pgnWrapper.getID(pgnList.get(i)));
            }
        }
    }

    public void createAnalysedPGN(PGN pgn, int elo){
        Game game = ChesslibWrapper.stringToGame(pgn.getRawPGN());
        if (game.getHalfMoves().size() == 0) {
            Toast.makeText(getApplicationContext(), "Invalid PGN", Toast.LENGTH_LONG).show();
            return;
        }

        String stockfishPath = getApplicationContext().getFilesDir().getPath() + "/stockfish";
        SetupStockfish.moveToData(getAssets(), stockfishPath);
        SetupStockfish.makeExecutable(stockfishPath);

        Toast.makeText(getApplicationContext(), "Starting Analysis", Toast.LENGTH_LONG).show();

        Side side;
        if (pgn.getColor().equalsIgnoreCase("white"))
            side = Side.WHITE;
        else
            side = Side.BLACK;

        int depth = EloToDepth.guessDepthFromElo(elo);
        Toast.makeText(getApplicationContext(), "Starting analysis at depth " + depth, Toast.LENGTH_LONG).show();

        AnalysisThread thread;
        try {
            thread = new AnalysisThread(stockfishPath, game, side, depth, 3, pgn);
            thread.start();

            Handler handler = new Handler();
            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, db_Name);
            Runnable runnable = new Runnable() {
                Spinner pgnSelector = (Spinner) findViewById(R.id.pgnSelect);
                @Override
                public void run() {
                    if (thread.done()) {
                        DatabaseHandle.getHandle().createPGN(thread.getPgn());
                        refreshId(db_Name, db_ID, DatabaseHandle.getHandle());
                        pgnSelector.setAdapter(adapter);
                    } else {
                        handler.postDelayed(this, 1000);
                    }
                }
            };
            handler.postDelayed(runnable, 1000);

//            progressBar.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unable To Run Analysis on PGN", Toast.LENGTH_LONG).show();
            Log.e("AddData", e.toString());
        }
    }

    //Zanelle add image code:
    //button select Image
    public void selectImage() {
        //.Now Permission Working
        //Toast.makeText(DatabaseGui.this, "get Image", Toast.LENGTH_SHORT).show();
        //Let's Pick File
        Intent opengallery = new Intent(Intent.ACTION_PICK);
        opengallery.setType("image/*");
       /* works without changing
             startActivityforresult to ActivityResultLauncher
             along with - protected void onActivityResult
        */
        startActivityForResult(opengallery, REQUESTG);
    }

    // Get permission from user
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(DatabaseGui.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(DatabaseGui.this, " allow access to upload files", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(DatabaseGui.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION);
        }
    }

    // Check permission from user
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(DatabaseGui.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(DatabaseGui.this, "Permission Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DatabaseGui.this, "Permission Unsuccessful", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTG && resultCode == Activity.RESULT_OK) {
            String filePath = getRealPathFromUri(data.getData(), DatabaseGui.this);
            Log.d("File Path : ", " " + filePath);
            this.file_path = filePath;

//            file_name.setText(filePath);

            OcrThread thread = null;
            try {
                InputStream in = new FileInputStream(filePath);
                thread = new OcrThread("http://ec2-44-210-126-90.compute-1.amazonaws.com:80/scoresheet", in, filePath);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (thread != null) {
                // the thread is running, store it in a global var or something
                Intent i = new Intent(DatabaseGui.this, AddDataActivity.class);

                Handler handler = new Handler();
                OcrThread finalThread = thread;
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (finalThread.done()) {
                            System.out.println("OCR: done");
                            i.putExtra("pgnMoves", finalThread.getResponse());
                            startActivity(i);
                        } else {
                            System.out.println("OCR: waiting");
                            handler.postDelayed(this, 1000);
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);

            }
        }
    }

    // creates filepath
    public String getRealPathFromUri(Uri uri, Activity activity) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, proj, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int id = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(id);
        }
    }

}