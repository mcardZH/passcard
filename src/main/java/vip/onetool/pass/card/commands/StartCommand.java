package vip.onetool.pass.card.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import vip.onetool.pass.card.seasons.SeasonManager;
import vip.onetool.pass.card.util.ClickAbleTextBuilderUtils;
import vip.onetool.pass.card.util.LanguageUtils;

import java.util.*;

/**
 * @author mcard
 */
public class StartCommand implements TabExecutor {
    private final String PERMISSION = "passcard.start";
    private final Map<String, Long> confirm = new HashMap<>();

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
        String nameNotExist = "command-help.start.name-not-exist";
        String confirmTip = "command-help.start.confirm-tip";
        //10秒内重新输入以确认
        long confirmTime = 10000;
        if (!sender.hasPermission(PERMISSION)) {
            return false;
        }
        if (args.length != 1) {
            //虽然参数超过1个也可以运行不会有问题，但为了严谨仅支持1个参数
            ClickAbleTextBuilderUtils.send(sender, ClickAbleTextBuilderUtils.fastBuildClickAbleCommand(
                    LanguageUtils.get(argumentError, null, "create")));
            return true;
        }
        String name = args[0];
        if (!SeasonManager.isSeasonNameExist(name)) {
            ClickAbleTextBuilderUtils.send(sender, ClickAbleTextBuilderUtils.fastBuildClickAbleCommand(
                    LanguageUtils.get(nameNotExist, null, name)));
            return true;
        }
        if (System.currentTimeMillis() - confirm.getOrDefault(sender.getName(), 0L) >= confirmTime) {
            //更新确认时间
            confirm.put(sender.getName(), System.currentTimeMillis());
            String confirmTimeSec = String.valueOf(confirmTime / 1000L);
            String confirmCommand = "/passcard start " + name;
            ClickAbleTextBuilderUtils.send(sender, ClickAbleTextBuilderUtils.fastBuildClickAbleCommand(
                    LanguageUtils.get(confirmTip, null, confirmTimeSec, confirmCommand)));
            return true;
        }
        SeasonManager.startNewSeason(name);
        return true;
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
        if (!sender.hasPermission(PERMISSION)) {
            return new ArrayList<>();
        }
        return Arrays.asList(SeasonManager.getSeasonNameList().toArray(new String[0]));
    }
}
