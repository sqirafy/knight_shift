package com.example.knightshift;

public class NextPiece {
    static boolean[] pieceVisibility = new boolean[32];

    public static int next_P() {
        for (int i = 0; i <=17; i++) {
            if (pieceVisibility[i] == false) {
                pieceVisibility[i] = true;
                return i;
            }
        }
        System.err.println("Too many P's in the FEN String");
        return -1;
    }

    public static int next_R() {
        for (int i = 8; i <=9; i++) {
            if (pieceVisibility[i] == false) {
                pieceVisibility[i] = true;
                return i;
            }
        }
        System.err.println("Too many R's in the FEN String");
        return -1;
    }

    public static int next_N() {
        for (int i = 10; i <=11; i++) {
            if (pieceVisibility[i] == false) {
                pieceVisibility[i] = true;
                return i;
            }
        }
        System.err.println("Too many N's in the FEN String");
        return -1;
    }

    public static int next_B() {
        for (int i = 12; i <=13; i++) {
            if (pieceVisibility[i] == false) {
                pieceVisibility[i] = true;
                return i;
            }
        }
        System.err.println("Too many B's in the FEN String");
        return -1;
    }

    public static int next_Q() {
        if (pieceVisibility[14] == false) {
            pieceVisibility[14] = true;
            return 14;
        }
        System.err.println("Too many Q's in the FEN String");
        return -1;
    }

    public static int next_K() {
        if (pieceVisibility[15] == false) {
            pieceVisibility[15] = true;
            return 15;
        }
        System.err.println("Too many Q's in the FEN String");
        return -1;
    }

    public static int next_p() {
        for (int i = 16; i <=23; i++) {
            if (pieceVisibility[i] == false) {
                pieceVisibility[i] = true;
                return i;
            }
        }
        System.err.println("Too many p's in the FEN String");
        return -1;
    }

    public static int next_r() {
        for (int i = 24; i <=25; i++) {
            if (pieceVisibility[i] == false) {
                pieceVisibility[i] = true;
                return i;
            }
        }
        System.err.println("Too many r's in the FEN String");
        return -1;
    }

    public static int next_n() {
        for (int i = 26; i <=27; i++) {
            if (pieceVisibility[i] == false) {
                pieceVisibility[i] = true;
                return i;
            }
        }
        System.err.println("Too many n's in the FEN String");
        return -1;
    }

    public static int next_b() {
        for (int i = 28; i <=29; i++) {
            if (pieceVisibility[i] == false) {
                pieceVisibility[i] = true;
                return i;
            }
        }
        System.err.println("Too many b's in the FEN String");
        return -1;
    }

    public static int next_q() {
        if (pieceVisibility[30] == false) {
            pieceVisibility[30] = true;
            return 30;
        }
        System.err.println("Too many Q's in the FEN String");
        return -1;
    }

    public static int next_k() {
        if (pieceVisibility[31] == false) {
            pieceVisibility[31] = true;
            return 31;
        }
        System.err.println("Too many Q's in the FEN String");
        return -1;
    }

}
