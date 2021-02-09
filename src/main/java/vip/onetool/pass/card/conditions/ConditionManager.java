package vip.onetool.pass.card.conditions;

import org.bukkit.entity.Player;
import vip.onetool.pass.card.util.SqlYmlConfigurationUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author mcard
 * TODO: 完成此类
 */
public class ConditionManager {

    private static boolean inited = false;

    public static void init(List<SqlYmlConfigurationUtils> config) {
        inited = true;

    }

    public static void unInit() {
        inited = false;
    }

    public static Set<String> getConditionNameList() {
        return new HashSet<>();
    }

    public static boolean isConditionNameExist(String name) {
        return getConditionNameList().contains(name);
    }

    public static boolean createCondition(String name) {
        return false;
    }

    public static void openEditGui(Player player, String name) {

    }

    public static void saveDeleteCondition(String name) {

    }

    public static void deleteCondition(String name) {

    }


}
