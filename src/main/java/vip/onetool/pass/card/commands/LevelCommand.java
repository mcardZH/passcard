package vip.onetool.pass.card.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import vip.onetool.pass.card.seasons.SeasonLevel;
import vip.onetool.pass.card.seasons.SeasonManager;
import vip.onetool.pass.card.seasons.SeasonPlayer;
import vip.onetool.pass.card.util.ClickAbleTextBuilderUtils;
import vip.onetool.pass.card.util.LanguageUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mcard
 */
public class LevelCommand implements TabExecutor {
    private final String PERMISSION_SELF = "passcard.level.self";
    private final String PERMISSION_OTHER = "passcard.level.other";
    private final String PERMISSION_CHANGE = "passcard.level.change";
    private final List<String> ACTION_LIST = Arrays.asList("add", "reduce", "set");

    /**
     * Executes the given command, returning its success
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String argumentError = "command-help.argument-error";
        String noFoundPlayer = "command-help.level.no-found-player";
        String playerLevelInfo = "command-help.level.level-info";
        String integerError = "command-help.level.need-integer";
        String target = sender.getName();
        if (args.length == 0) {
            // 没权限查看自己
            if (sender.hasPermission(PERMISSION_SELF)) {
                return false;
            }
        } else if (args.length == 1) {
            if (ACTION_LIST.contains(args[0])) {
                // 属于操作类型，但是参数不足
                ClickAbleTextBuilderUtils.send(sender, ClickAbleTextBuilderUtils.fastBuildClickAbleCommand(
                        LanguageUtils.get(argumentError, null, "level")));
                return true;
            } else if (!sender.hasPermission(PERMISSION_OTHER)) {
                // 无权限查看他人
                return false;
            }
            // 有权查看
            SeasonPlayer player = SeasonManager.getPlayer(target);
            if (player == null) {
                ClickAbleTextBuilderUtils.send(sender, ClickAbleTextBuilderUtils.fastBuildClickAbleCommand(
                        LanguageUtils.get(noFoundPlayer, null, target)));
                return true;
            }
            sender.sendMessage(LanguageUtils.get(playerLevelInfo, null, target, player.getPlayerLevel().getLevel()));
            return true;
        } else if (args.length != 4) {
            // 参数不足
            ClickAbleTextBuilderUtils.send(sender, ClickAbleTextBuilderUtils.fastBuildClickAbleCommand(
                    LanguageUtils.get(argumentError, null, "level")));
            return true;
        } else {
            if (!sender.hasPermission(PERMISSION_CHANGE)) {
                // 无权限修改
                return false;
            }
            // 有充足的参数
            // /passcard level <add|reduce|set> <玩家名> <xp|level> <值>
            String action = args[0];
            String playerName = args[1];
            target = args[2];
            int value;
            try {
                value = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                ClickAbleTextBuilderUtils.send(sender, ClickAbleTextBuilderUtils.fastBuildClickAbleCommand(
                        LanguageUtils.get(integerError, null)));
                return true;
            }
            SeasonPlayer player = SeasonManager.getPlayer(playerName);
            if (player == null) {
                ClickAbleTextBuilderUtils.send(sender, ClickAbleTextBuilderUtils.fastBuildClickAbleCommand(
                        LanguageUtils.get(noFoundPlayer, null, target)));
                return true;
            }
            // 使用反射调用修改方法
            // 合成setLevel一类方法名
            String methodName = action.toLowerCase() + target.substring(0, 1).toUpperCase() + target.substring(1).toLowerCase();
            Class<? extends SeasonLevel> clazz = player.getPlayerLevel().getClass();
            try {
                Method method = clazz.getDeclaredMethod(methodName, Integer.class);
                method.invoke(player.getPlayerLevel(), value);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                ClickAbleTextBuilderUtils.send(sender, ClickAbleTextBuilderUtils.fastBuildClickAbleCommand(
                        LanguageUtils.get(argumentError, null, "level")));
                return true;
            }
        }
        return false;
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside of a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param alias   The alias used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed and command label
     * @return A List of possible completions for the final argument, or null
     * to default to the command executor
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        /*
            /passcard level <add|reduce|set> <玩家名> <xp|level> <值>
            /passcard level [玩家名]

        */
        if (!sender.hasPermission(PERMISSION_SELF)) {
            return new ArrayList<>();
        }
        switch (args.length) {
            case 0: {
                /*
                提示内容：
                    1、玩家名（注意是SeasonPlayer）
                    2、add|reduce|set
                */
                List<String> playerNames = new ArrayList<>();
                if (sender.hasPermission(PERMISSION_CHANGE)) {
                    playerNames.addAll(ACTION_LIST);
                }
                if (sender.hasPermission(PERMISSION_OTHER)) {
                    for (SeasonPlayer seasonPlayer : SeasonManager.getSeasonPlayer().toArray(new SeasonPlayer[0])) {
                        playerNames.add(seasonPlayer.getName());
                    }
                }
                return playerNames;
            }
            case 1: {
                /*
                提示内容：
                    玩家名
                 */
                List<String> playerNames = new ArrayList<>();
                if (sender.hasPermission(PERMISSION_CHANGE)) {
                    for (SeasonPlayer seasonPlayer : SeasonManager.getSeasonPlayer().toArray(new SeasonPlayer[0])) {
                        playerNames.add(seasonPlayer.getName());
                    }
                }
                return playerNames;
            }
            case 2: {
                /*
                提示内容：
                    xp|level
                 */
                List<String> action = new ArrayList<>();
                if (sender.hasPermission(PERMISSION_CHANGE)) {
                    action.add("xp");
                    action.add("level");
                }
                return action;
            }
            default: {
                return new ArrayList<>();
            }
        }

    }
}
