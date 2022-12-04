package com.tainted.client;

public class IsLateData {

    private static boolean playerSleptLate;

    public static void set(boolean playerSleptLate) {
        IsLateData.playerSleptLate = playerSleptLate;
    }

    public static boolean getPlayerSleptLate() {
        return playerSleptLate;
    }

}
