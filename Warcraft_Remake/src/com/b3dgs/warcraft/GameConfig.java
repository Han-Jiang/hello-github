package com.b3dgs.warcraft;

import com.b3dgs.warcraft.gameplay.Race;

public class GameConfig {

    private static Race playerRace = Race.humans;
    private static Race opponentRace = Race.orcs;
    private static String map = "2_bridges.map";
    private static boolean hide, fog;

    private GameConfig() {
    }

    public static void setConfig(Race playerRace, Race opponentRace, String map, boolean hide, boolean fog) {
        GameConfig.playerRace = playerRace;
        GameConfig.opponentRace = opponentRace;
        GameConfig.map = map;
        GameConfig.hide = hide;
        GameConfig.fog = fog;
    }

    public static Race getPlayerRace() {
        return playerRace;
    }

    public static Race getOpponentRace() {
        return opponentRace;
    }

    public static String getMap() {
        return map;
    }

    public static boolean getHide() {
        return hide;
    }

    public static boolean getFog() {
        return fog;
    }
}
