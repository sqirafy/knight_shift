package com.example.knightshift;

public class EloToDepth {

    private static final int OFFSET = 4; // elo depth is index + offset
    private static final int[] elos = new int[] { 1000, 1500, 1966, 2033, 2099, 2165, 2231, 2298, 2364, 2430,
            2496, 2563, 2629, 2695, 2761, 2828, 2784 }; // 1000 -> depth 4, science

    public static int guessDepthFromElo(int elo) {
        int diff = Integer.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < elos.length; i++) {
            int x = elos[i];
            if (Math.abs(x - elo) < diff) {
                diff = Math.abs(x - elo);
                index = i;
            }
        }
        return index + OFFSET;
    }
}
