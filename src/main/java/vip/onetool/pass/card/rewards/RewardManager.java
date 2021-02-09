package vip.onetool.pass.card.rewards;

/**
 * @author mcard
 */
public class RewardManager {

    private static boolean inited = false;

    public static void init() {
        inited = true;

    }

    public static void unInit() {
        inited = false;
    }

}
