package vip.onetool.pass.card.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import vip.onetool.pass.card.seasons.SeasonManager;
import vip.onetool.pass.card.util.LanguageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mcard
 */
public class NewCommand implements TabExecutor {

    private final String PERMISSION = "passcard.new";

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
        String onlyPlayer = "command-help.new.only-player";
        if (sender.hasPermission(PERMISSION)) {
            // 没权限
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(LanguageUtils.get(onlyPlayer, null));
            return true;
        }
        String name = args[0];
        Player target = ((Player) sender);
        if (SeasonManager.isSeasonNameExist(name)) {
            // 编辑
            SeasonManager.getSeason(name).editFor(target);
        } else {
            // 创建并编辑
            SeasonManager.createSeason(name).editFor(target);
        }
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
        if (sender.hasPermission(PERMISSION)) {
            // 没权限跳过任务
            return new ArrayList<>();
        }
        if (args.length == 0) {
            return Arrays.asList(SeasonManager.getSeasonNameList().toArray(new String[0]));
        }
        return new ArrayList<>();
    }
}
