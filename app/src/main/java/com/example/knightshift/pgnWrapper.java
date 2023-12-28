package com.example.knightshift;

import com.example.knightshift.db.Blunder;
import com.example.knightshift.db.DatabaseHandle;
import com.example.knightshift.db.MissedMate;
import com.example.knightshift.db.PGN;

import io.realm.RealmList;

import java.lang.reflect.Field;

public class pgnWrapper{

    public static boolean checkPgn(String name){
        int fileExtensionIdx = name.lastIndexOf('.');
        if(fileExtensionIdx != -1){
            String extensionFound = name.substring(fileExtensionIdx+1);
            String extensionPgn = new String("pgn");
            if(extensionFound.equals(extensionPgn)){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public static void parsePGN(String pgnStr, PGN pgnObj){
        boolean nextField = true;
        int endIdx, startIdx;
        String subString, field;

        pgnObj.setRawPGN(pgnStr);

        while(nextField){
            endIdx = pgnStr.indexOf("\"]");
            subString = pgnStr.substring(0, endIdx+3);
            pgnStr = pgnStr.replace(subString,"");

            startIdx = subString.indexOf("\"");
            field = subString.substring(1, subString.indexOf(" "));
            subString = subString.substring(startIdx+1, subString.length()-3);
            setField(pgnObj, field, subString);

            endIdx = pgnStr.indexOf("\"]");
            if(endIdx == -1) nextField = false;
        }

        pgnStr = pgnStr.replaceAll("\n"," ").trim();
        endIdx = pgnStr.lastIndexOf(" ");
        pgnStr = pgnStr.substring(0, endIdx);
        pgnObj.setMoves(pgnStr);

        //set extra fields:
        pgnObj.setOwnerID(DatabaseHandle.getUser().getId());

        addEmptyFields(pgnObj);
    }

    public static void setField(PGN pgn, String field, String value){
        field = field.toLowerCase().trim();
    switch(field){
        case "event":
            pgn.setEvent(value);
            break;
        case "site":
            pgn.setSite(value);
            break;
        case "date":
            pgn.setDate(value);
            break;
        case "round":
            pgn.setRound(value);
            break;
        case "white":
            pgn.setWhite(value);
            break;
        case "black":
            pgn.setBlack(value);
            break;
        case "result":
            pgn.setResult(value);
            break;
        case "color":
            pgn.setColor(value);
            break;
        default:
            break;
        }
    }

    public static void addEmptyFields(PGN pgn){
        if(pgn.getWhite() == null) pgn.setWhite("");
        if(pgn.getBlack() == null) pgn.setBlack("");
        if(pgn.getDate() == null) pgn.setDate("");
        if(pgn.getEvent() == null) pgn.setEvent("");
        if(pgn.getSite() == null) pgn.setSite("");
        if(pgn.getRound() == null) pgn.setRound("");
        if(pgn.getResult() == null) pgn.setResult("");
        if(pgn.getMoves() == null) pgn.setMoves("");
        if(pgn.getColor() == null) pgn.setColor("WHITE");
    }

    public static String getID(PGN pgn){
        return pgn.get_id().toString();
    }
    public static String getRawPGN(PGN pgn){
        return pgn.getRawPGN().toString();
    }
    public static String getNameFromID(PGN pgn){
        return pgn.getWhite().concat(" vs ").concat(pgn.getBlack()).concat(" ").concat(pgn.getDate());
    }

}
