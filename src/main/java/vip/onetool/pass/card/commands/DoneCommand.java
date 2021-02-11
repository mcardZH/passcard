package vip.onetool.pass.card.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import vip.onetool.pass.card.seasons.SeasonManager;
import vip.onetool.pass.card.seasons.SeasonPlayer;
import vip.onetool.pass.card.util.ClickAbleTextBuilderUtils;
import vip.onetool.pass.card.util.LanguageUtils;

import java.util.ArrayList;
import java.util.List;

public class DoneCommand implements TabExecutor {
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
        String onlyPlayer = "command-help.done.only-player";
        String noTarget = "command-help.done.no-target";
        String permission = "passcard.finish.done";
        if (sender.hasPermission(permission)) {
            // 没权限跳过任务
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(LanguageUtils.get(onlyPlayer, null));
            return true;
        }
        SeasonPlayer player = SeasonManager.getPlayer(sender.getName());
        // 自己会不存在？
        assert player != null;
        if (!player.hasTargetTask()) {
            ClickAbleTextBuilderUtils.send(sender, ClickAbleTextBuilderUtils.fastBuildClickAbleCommand(
                    LanguageUtils.get(noTarget, null)
            ));
            return true;
        }
        player.getTargetTask().done();
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
        return new ArrayList<>();
    }
}
