package vip.onetool.passcard.seasons;

import java.util.HashSet;
import java.util.Set;

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

}
