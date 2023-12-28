package com.example.knightshift.db;

import com.example.knightshift.stockfish.Blunders;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.RealmField;
import io.realm.annotations.Required;

public class Blunder extends RealmObject {
    @PrimaryKey
    private ObjectId _id = new ObjectId();
    @Required
    private String owner_id = DatabaseHandle.user.getId();

    private int moveNumber;
    private String fen;
    private String color;
    @Required
    private RealmList<String> blunderAltMoves;

    public Blunder() {}

    public Blunder(int moveNumber, String fen, RealmList<String> blunderAltMoves, String color) {
        this.moveNumber = moveNumber;
        this.fen = fen;
        this.blunderAltMoves = blunderAltMoves;
        this.color = color;
    }

    //---------------------------------------------------------------------------------------


    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public int getMoveNumber() { return moveNumber; }
    public void setMoveNumber(int moveNumber) { this.moveNumber = moveNumber; }

    public String getFen() { return fen; }
    public void setFen(String fen) { this.fen = fen; }

    public RealmList<String> getBlunderAltMoves() { return blunderAltMoves; }
    public void setBlunderAltMoves(RealmList<String> altMoves) { this.blunderAltMoves = altMoves; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    //---------------------------------------------------------------------------------------

    public String toString() {
        return String.format(".\nBlunder {\n" +
                "\t\tColor: " + color + "\n" +
                "\t\tMoveNumber: " + moveNumber +  "\n" +
                "\t\tFen: " + fen + "\n" +
                "\t\tAltMoves:\n\t\t\t" + String.join("\n\t\t\t", blunderAltMoves) + "\n" +
                "\n}"
        );
    }

    //---------------------------------------------------------------------------------------

    public static RealmList<Blunder> fromAnalysis(Blunders blunders) {
        RealmList<Blunder> res = new RealmList<>();

        for (int i = 0; i < blunders.getNum(); i++) {
            String fen = blunders.getFens().get(i);
            int moveNumber = blunders.getMoveNumbers().get(i);
            RealmList<String> altMoves = new RealmList<String>();
            String color = blunders.getColour().value();
            for (List<String> moves : blunders.getLines().get(i)) {
                String moveSeq = String.join(" ", moves);
                altMoves.add(moveSeq);
            }
            Blunder blunder = new Blunder(moveNumber, fen, altMoves, color);
            res.add(blunder);
        }

        return res;
    }

}
