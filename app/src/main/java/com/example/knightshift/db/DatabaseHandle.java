package com.example.knightshift.db;

import android.util.Log;
import org.bson.types.ObjectId;
import java.util.Date;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;

public class DatabaseHandle {

    private static DatabaseHandle singleton = null;

    private static final String appId = "app_knight_shift-cgcew";
    private static App app = new App(new AppConfiguration.Builder(appId).build());
    public static User user = app.currentUser();
    private static SyncConfiguration config;
    private static Realm realm;

    //--------------------------------------------------------------------------------

    public static String getAppId() {
        return appId;
    }

    public static App getApp() {
        return app;
    }

    public static User getUser() {
        return user;
    }

    public static SyncConfiguration getConfig() {
        return config;
    }

    public static Realm getRealm() {
        return realm;
    }

    //--------------------------------------------------------------------------------

    private DatabaseHandle() {
        config = new SyncConfiguration.Builder(
                user,
                user.getId()
        ).allowQueriesOnUiThread(true)
                .errorHandler((session, error) -> {
                  if (session.isConnected()) {
                      Log.e(" :: TEST :: ", "");
                  }
                })
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();
        try {
            realm = Realm.getInstance(config);
        } catch (Exception e) {
            Log.e(" :: CTOR :: ", e.toString());
        }
    }

    public static DatabaseHandle getHandle() {
        if (singleton == null) {
            singleton = new DatabaseHandle();
        }
        return singleton;
    }

    //----------------------------------------------------------------------------------------------

    // Create
    public void createPGN(PGN pgn) {
        // blunders
        for (Blunder blunder : pgn.getBlunders()) {
            createBlunder(blunder);
        }

        // missed mates
        for (MissedMate mate : pgn.getMissedMates()) {
            createMissedMate(mate);
        }

        // puzzles
        for (Puzzles puzzle : pgn.getPuzzles()) {
            createPuzzle(puzzle);
        }

        realm.executeTransaction(transactionRealm -> {
            transactionRealm.insertOrUpdate(pgn);
        });
    }

    // Read
    public PGN getPGN(ObjectId id) {
        RealmResults<PGN> results = realm.where(PGN.class).equalTo("owner_id", user.getId()).equalTo("_id", id).findAll();
        if (results.isEmpty()) {
            return null;
        } else {
            return results.first();
        }
    }

    public RealmResults<PGN> getPGNs() {
        RealmResults<PGN> results = realm.where(PGN.class).equalTo("owner_id", user.getId()).findAll();
        return results;
    }

    // Update
    // Find a way to update an object inside a transaction block
    public void updatePGN(PGN pgn, String event, String site, String date, String round, String black, String white, String result, String moves, String color, RealmList<Blunder> blunders, RealmList<MissedMate> missedMates, RealmList<Puzzles> puzzles) {
        realm.executeTransaction(transactionRealm -> {
            if (event != null) {
                pgn.setEvent(event);
            }
            if (site != null) {
                pgn.setSite(site);
            }
            if (date != null) {
                pgn.setDate(date);
            }
            if (round != null) {
                pgn.setRound(round);
            }
            if (black != null) {
                pgn.setBlack(black);
            }
            if (white != null) {
                pgn.setWhite(white);
            }
            if (result != null) {
                pgn.setResult(result);
            }
            if (moves != null) {
                pgn.setMoves(moves);
            }
            if (color != null) {
                pgn.setColor(color);
            }
            if (blunders != null) {
                RealmList<Blunder> res = new RealmList<>();
                for (Blunder blunder : blunders) {
                    blunder = transactionRealm.copyToRealm(blunder);
                    res.add(blunder);
                }
                pgn.setBlunders(res);
            }
            if (missedMates != null) {
                RealmList<MissedMate> res = new RealmList<>();
                for (MissedMate mate : missedMates) {
                    mate = transactionRealm.copyToRealm(mate);
                    res.add(mate);
                }
                pgn.setMissedMates(res);
            }
            if (puzzles != null) {
                RealmList<Puzzles> res = new RealmList<>();
                for (Puzzles puzzle : puzzles) {
                    puzzle = transactionRealm.copyToRealm(puzzle);
                    res.add(puzzle);
                }
                pgn.setPuzzles(res);
            }
            transactionRealm.copyToRealmOrUpdate(pgn);
        });
    }

    // Delete
    public void deletePGN(PGN pgn) {
        realm.executeTransaction(transactionRealm -> {
            for (int i = 0; i < pgn.getBlunders().size(); i++) {
                Blunder blunder = pgn.getBlunders().get(i);
                blunder.deleteFromRealm();
            }
            for (int i = 0; i < pgn.getMissedMates().size(); i++) {
                MissedMate mate = pgn.getMissedMates().get(i);
                mate.deleteFromRealm();
            }
            for (int i = 0; i < pgn.getPuzzles().size(); i++) {
                Puzzles puzzle = pgn.getPuzzles().get(i);
                puzzle.deleteFromRealm();
            }
            pgn.deleteFromRealm();
        });
    }

    //----------------------------------------------------------------------------------------------

    public void createBlunder(Blunder blunder) {
        realm.executeTransaction(transaction -> {
            transaction.insert(blunder);
        });
    }

    public RealmResults<Blunder> getBlunders() {
        return realm.where(Blunder.class).equalTo("owner_id", user.getId()).findAll();
    }

    public void deleteBlunder(Blunder blunder) {
        realm.executeTransaction(tranasction -> {
            blunder.deleteFromRealm();
        });
    }

    //----------------------------------------------------------------------------------------------

    public void createMissedMate(MissedMate mate) {
        realm.executeTransaction(transaction -> {
            realm.insert(mate);
        });
    }

    public RealmResults<MissedMate> getMissedMates() {
        return realm.where(MissedMate.class).equalTo("owner_id", user.getId()).findAll();
    }

    public void deleteMissedMate(MissedMate mate) {
        realm.executeTransaction(transaction -> {
            mate.deleteFromRealm();
        });
    }

    //----------------------------------------------------------------------------------------------

    public void createPuzzle(Puzzles puzzle) {
        realm.executeTransaction(transaction -> {
            transaction.insert(puzzle);
        });
    }

    public RealmResults<Puzzles> getPuzzles() {
        return realm.where(Puzzles.class).findAll();
    }

    public RealmResults<Puzzles> getDuePuzzles() {
        RealmResults<Puzzles> puzzles = realm.where(Puzzles.class)
                .equalTo("owner_id", user.getId())
                .lessThanOrEqualTo("dueDate", new Date(System.currentTimeMillis()))
                .findAll();
        return puzzles;
    }

    public static void gradePuzzle(Puzzles puzzle, int grade) {
        realm.executeTransaction(transaction -> {
            puzzle.sm2(grade);
            transaction.insertOrUpdate(puzzle);
        });
    }

    public void deletePuzzle(Puzzles puzzle) {
        realm.executeTransaction(transaction -> {
            puzzle.deleteFromRealm();
        });
    }

    //----------------------------------------------------------------------------------------------
}
