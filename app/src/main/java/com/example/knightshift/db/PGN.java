package com.example.knightshift.db;

import org.bson.types.ObjectId;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class PGN extends RealmObject {
    @PrimaryKey
    private ObjectId _id = new ObjectId();
    @Required
    private String owner_id = DatabaseHandle.user.getId();

    private String event;
    private String site;
    private String date;
    private String round;
    private String black;
    private String white;
    private String result;
    private String moves;
    private String rawPGN;

    private RealmList<Blunder> blunders;
    private RealmList<MissedMate> missedMates;
    private RealmList<Puzzles> puzzles;

    private String color;

    public PGN() {}

    public PGN(String event, String site, String date, String round, String black,
               String white, String result, String moves, String rawPGN, String color,
               RealmList<Blunder> blunders, RealmList<MissedMate> missedMates,
               RealmList<Puzzles> puzzles) {
        this.event = event;
        this.site = site;
        this.date = date;
        this.round = round;
        this.black = black;
        this.white = white;
        this.result = result;
        this.moves = moves;
        this.rawPGN = rawPGN;
        this.color = color;
        this.blunders = blunders;
        this.missedMates = missedMates;
        this.puzzles = puzzles;
    }

    // --------------------- Getters and Setters ---------------------

    public String getOwnerID() { return owner_id; }
    public void setOwnerID(String ownerID) { this.owner_id = ownerID; }

    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }

    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getRound() { return round; }
    public void setRound(String round) { this.round = round; }

    public String getBlack() { return black; }
    public void setBlack(String black) { this.black = black; }

    public String getMoves() { return moves; }
    public void setMoves(String moves) { this.moves = moves; }

    public String getRawPGN() { return rawPGN; }
    public void setRawPGN(String rawPGN) { this.rawPGN = rawPGN; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getWhite() { return white; }
    public void setWhite(String white) { this.white = white; }

    public ObjectId get_id() { return _id; }
    public void set_id(ObjectId _id) { this._id = _id; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public RealmList<Blunder> getBlunders() { return blunders; }
    public void setBlunders(RealmList<Blunder> blunders) { this.blunders = blunders; }

    public RealmList<MissedMate> getMissedMates() { return missedMates; }
    public void setMissedMates(RealmList<MissedMate> missedMates) { this.missedMates = missedMates; }

    public RealmList<Puzzles> getPuzzles() { return puzzles; }
    public void setPuzzles(RealmList<Puzzles> puzzles) { this.puzzles = puzzles; }

}
