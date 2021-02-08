package vip.onetool.passcard.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import vip.onetool.passcard.conditions.ConditionManager;
import vip.onetool.passcard.utils.ClickAbleTextBuilder;
import vip.onetool.passcard.utils.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditCommand implements TabExecutor {

    private final String permission = "passcard.edit";

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
        String nameNotExist = "command-help.edit.name-not-exist";
        String onlyPlayer = "command-help.edit.only-player";
        if (!sender.hasPermission(permission)) {
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.get(onlyPlayer, null));
            return true;
        }
        Player player = ((Player) sender);
        if (args.length != 1) {
            //虽然参数超过1个也可以运行不会有问题，但为了严谨仅支持1个参数
            ClickAbleTextBuilder.send(sender, ClickAbleTextBuilder.fastBuildClickAbleCommand(
                    Language.get(argumentError, null, "create")));
            return true;
        }
        String name = args[0];
        if (!ConditionManager.isConditionNameExist(name)) {
            ClickAbleTextBuilder.send(sender, ClickAbleTextBuilder.fastBuildClickAbleCommand(
                    Language.get(nameNotExist, null, name)));
            return true;
        }
        ConditionManager.openEditGui(player, name);
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
        if (!sender.hasPermission(permission)) {
            return new ArrayList<>();
        }
        return Arrays.asList(ConditionManager.getConditionNameList().toArray(new String[0]));
    }
}
