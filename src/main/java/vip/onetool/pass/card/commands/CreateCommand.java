package vip.onetool.pass.card.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import vip.onetool.pass.card.conditions.ConditionManager;
import vip.onetool.pass.card.util.ClickAbleTextBuilderUtils;
import vip.onetool.pass.card.util.LanguageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mcard
 */
public class CreateCommand implements TabExecutor {

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
        String permission = "passcard.create";
        String argumentError = "command-help.argument-error";
        String nameExist = "command-help.create.name-exist";
        String createResult = "command-help.create.create-result";
        if (!sender.hasPermission(permission)) {
            return false;
        }
        if (args.length != 1) {
            //虽然参数超过1个也可以运行不会有问题，但为了严谨仅支持1个参数
            ClickAbleTextBuilderUtils.send(sender, ClickAbleTextBuilderUtils.fastBuildClickAbleCommand(
                    LanguageUtils.get(argumentError, null, "create")));
            return true;
        }
        String name = args[0];
        if (ConditionManager.isConditionNameExist(name)) {
            ClickAbleTextBuilderUtils.send(sender, ClickAbleTextBuilderUtils.fastBuildClickAbleCommand(
                    LanguageUtils.get(nameExist, null, name)));
            return true;
        }
        if (ConditionManager.createCondition(name)) {
            sender.sendMessage(LanguageUtils.get(createResult, null, "成功"));
        } else {
            sender.sendMessage(LanguageUtils.get(createResult, null, "失败"));
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
        // 本命令无需补全
        return new ArrayList<>();
    }
}
