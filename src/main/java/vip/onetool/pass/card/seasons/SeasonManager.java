package vip.onetool.pass.card.seasons;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author mcard
 */
public class SeasonManager {

    public static Set<String> getSeasonNameList() {
        return new HashSet<>();
    }

    public static boolean isSeasonNameExist(String name) {
        return getSeasonNameList().contains(name);
    }

    public static void startNewSeason(String name) {

    }

    public static Set<SeasonPlayer> getSeasonPlayer() {
        return new HashSet<>();
    }

    public static SeasonPlayer getPlayer(UUID uuid) {
        return null;
    }

    public static SeasonPlayer getPlayer(String playerName) {
        return null;
    }

    public static Season createSeason(String name) {
        return null;
    }

    public static Season getNowSeason() {
        return null;
    }

    public static Set<Season> getSeasons() {
        return null;
    }

    public static Season getSeason(String name) {
        return null;
    }

    private SeasonManager() {

    }
}
