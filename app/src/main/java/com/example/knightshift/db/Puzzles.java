package com.example.knightshift.db;

import com.example.knightshift.stockfish.Blunders;
import com.example.knightshift.stockfish.MissedMates;

import org.bson.types.ObjectId;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

public class Puzzles extends RealmObject {
    @PrimaryKey
    private ObjectId _id = new ObjectId();
    @Required
    private String owner_id = DatabaseHandle.user.getId();

    private String fen;
    private String color;
    private boolean fromBlunder;
    private int numCorrectRecalls = 0;
    private double easinessFactor = 2.5;
    private int repetitionInterval = 0;
    private Date dueDate = new Date(System.currentTimeMillis());
    @Required
    private RealmList<String> altMoves;     //

    //----------------------------------------------------------------------------------------------

    public Puzzles() {}

    public Puzzles(String fen, String color, boolean fromBlunder, RealmList<String> altMoves) {
        this.fen = fen;
        this.color = color;
        this.fromBlunder = fromBlunder;
        this.altMoves = altMoves;
    }

    //----------------------------------------------------------------------------------------------

    public String getFen() { return fen; }
    public void setFen(String fen) { this.fen = fen; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public boolean isFromBlunder() { return fromBlunder; }
    public void setFromBlunder(boolean fromBlunder) { this.fromBlunder = fromBlunder; }

    public RealmList<String> getAltMoves() { return altMoves; }
    public void setAltMoves(RealmList<String> altMoves) { this.altMoves = altMoves; }

    //----------------------------------------------------------------------------------------------

    public void sm2(int grade) {
        if (grade >= 3) {
            if (numCorrectRecalls == 0) {
                repetitionInterval = 1;
            } else if (numCorrectRecalls == 1) {
                repetitionInterval = 6;
            } else {
                repetitionInterval = (int) Math.round(repetitionInterval * easinessFactor);
            }
            numCorrectRecalls++;
        } else {
            numCorrectRecalls = 0;
            repetitionInterval = 1;
        }

        easinessFactor = easinessFactor + (0.1 - (5 - grade) * (0.08 + (5 - grade) * 0.02));
        if (easinessFactor < 1.3) {
            easinessFactor = 1.3;
        }

        // update review date
        Date currentDate = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, repetitionInterval);
        dueDate = new Date(calendar.getTimeInMillis());
    }

    //----------------------------------------------------------------------------------------------

    public static RealmList<Puzzles> fromAnalysis(Blunders blunders, MissedMates missedMates) {
        RealmList<Puzzles> res = new RealmList<>();

        for (int i = 0; i < blunders.getNum(); i++) {
            String fen = blunders.getFens().get(i);
            RealmList<String> altMoves = new RealmList<>();
            String color = blunders.getColour().value();
            for (List<String> moves : blunders.getLines().get(i)) {
                String moveSeq = String.join(" ", moves);
                altMoves.add(moveSeq);
            }
            Puzzles puzzle = new Puzzles(fen, color, true, altMoves);
            res.add(puzzle);
        }

        //Missed mates
        for (int i = 0; i < missedMates.getNum(); i++) {
            String fen = missedMates.getFens().get(i);
            RealmList<String> altMoves = new RealmList<>();
            String color = missedMates.getColour().value();
            for (List<String> moves : missedMates.getLines()) {
                String moveSeq = String.join(" ", moves);
                altMoves.add(moveSeq);
            }
            Puzzles puzzle = new Puzzles(fen, color, false, altMoves);
            res.add(puzzle);
        }

        return res;
    }

    //----------------------------------------------------------------------------------------------

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

    public int getNumCorrectRecalls() {
        return numCorrectRecalls;
    }

    public void setNumCorrectRecalls(int numCorrectRecalls) {
        this.numCorrectRecalls = numCorrectRecalls;
    }

    public double getEasinessFactor() {
        return easinessFactor;
    }

    public void setEasinessFactor(double easinessFactor) {
        this.easinessFactor = easinessFactor;
    }

    public int getRepetitionInterval() {
        return repetitionInterval;
    }

    public void setRepetitionInterval(int repetitionInterval) {
        this.repetitionInterval = repetitionInterval;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return String.format(
                ".\nPuzzle {\n" +
                        "\t\tColor: " + color + "\n" +
                        "\t\tFen: " + fen + "\n" +
                        "\t\tAltMoves:\n\t\t\t" + String.join("\n\t\t\t", altMoves) + "\n" +
                        "\t\tN: " + numCorrectRecalls + "\n" +
                        "\t\tEF: " + easinessFactor + "\n" +
                        "\t\tI: " + repetitionInterval + "\n" +
                        "\t\tDueDate: " + dueDate + "\n" +
                "\n}"
        );
    }
}
