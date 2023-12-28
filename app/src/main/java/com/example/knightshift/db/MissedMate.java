package com.example.knightshift.db;

import com.example.knightshift.stockfish.MissedMates;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

public class MissedMate extends RealmObject {
    @PrimaryKey
    private ObjectId _id = new ObjectId();
    @Required
    private String owner_id = DatabaseHandle.user.getId();

    private int mateInX;
    private String fen;
    private String missedMateAltMove;
    private String color;

    public MissedMate() {}

    public MissedMate(int moveNumber, String fen, String missedMateAltMove, String color) {
        this.mateInX = moveNumber;
        this.fen = fen;
        this.missedMateAltMove = missedMateAltMove;
        this.color = color;
    }

    //---------------------------------------------------------------------------------------


    public ObjectId get_id() { return _id; }
    public void set_id(ObjectId _id) { this._id = _id; }

    public String getOwner_id() { return owner_id; }
    public void setOwner_id(String owner_id) { this.owner_id = owner_id; }

    public int getMateInX() { return mateInX; }
    public void setMateInX(int mateInX) { this.mateInX = mateInX; }

    public String getFen() { return fen; }
    public void setFen(String fen) { this.fen = fen; }

    public String getMissedMateAltMove() { return missedMateAltMove; }
    public void setMissedMateAltMove(String altMove) { this.missedMateAltMove = altMove; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    //---------------------------------------------------------------------------------------

    public static RealmList<MissedMate> fromAnalysis(MissedMates missedMates) {
        RealmList<MissedMate> res = new RealmList<>();

        for (int i = 0; i < missedMates.getNum(); i++) {
            int moveNumber = missedMates.getMoves().get(i);
            String fen = missedMates.getFens().get(i);
            String color = missedMates.getColour().value();
            String altMove = String.join(" ", missedMates.getLines().get(0));
            MissedMate missedMate = new MissedMate(moveNumber, fen, altMove, color);
            res.add(missedMate);
        }

        return res;
    }

    //---------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return String.format(".\nMissedMate {\n" +
                "\t\tColor: " + color + "\n" +
                "\t\tMoveNumber: " + mateInX + "\n" +
                "\t\tFen: " + fen + "\n" +
                "\t\tAltMoves: " + missedMateAltMove +
                "\n}"
        );
    }
}
