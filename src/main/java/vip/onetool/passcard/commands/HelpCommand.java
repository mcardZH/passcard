package vip.onetool.passcard.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import vip.onetool.passcard.utils.ClickAbleTextBuilder;
import vip.onetool.passcard.utils.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mcard
 */
public class HelpCommand implements TabExecutor {

    private final String opHelpPermission = "passcard.help-op";
    private final String noOpHelpPermission = "passcard.help";

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
        String opPath = "command-help.help.op";
        String noOpPath = "command-help.help.no-op";

        if (args.length == 0) {
            //使用快速提示
            if (sender.hasPermission(opHelpPermission)) {
                //管理提示
                for (String help : Language.getList(opPath, null)) {
                    ClickAbleTextBuilder.send(sender, ClickAbleTextBuilder.fastBuildClickAbleCommand(help));
                }
            } else if (sender.hasPermission(noOpHelpPermission)) {
                //玩家提示
                for (String help : Language.getList(noOpPath, null)) {
                    ClickAbleTextBuilder.send(sender, ClickAbleTextBuilder.fastBuildClickAbleCommand(help));
                }
            } else {
                //无提示
                return false;
            }
        } else {
            //进入详细提示
            String path = "command-help.help.sub." + args[0];
            if (Language.isSet(path) && sender.hasPermission(opHelpPermission)) {
                //必须有权限且有设置的才在这里提示
                for (String help : Language.getList(path, null, args[0])) {
                    ClickAbleTextBuilder.send(sender, ClickAbleTextBuilder.fastBuildClickAbleCommand(help));
                }
            } else {
                if (!Language.isSet(path)) {
                    //如果没设置，提示default
                    path = "command-help.help.sub.default";
                    for (String help : Language.getList(path, null, args[0])) {
                        ClickAbleTextBuilder.send(sender, ClickAbleTextBuilder.fastBuildClickAbleCommand(help));
                    }
                    return true;
                }
                //未设置则检查是否是分开的
                if (sender.hasPermission(opHelpPermission)) {
                    path = "command-help.help.sub." + args[0] + "-op";
                } else if (sender.hasPermission(noOpHelpPermission)) {
                    path = "command-help.help.sub." + args[0] + "-no-op";
                } else {
                    path = "command-help.help.sub." + args[0] + "-none";
                }
                if (Language.isSet(path)) {
                    for (String help : Language.getList(path, null, args[0])) {
                        ClickAbleTextBuilder.send(sender, ClickAbleTextBuilder.fastBuildClickAbleCommand(help));
                    }
                } else {
                    //无提示
                    path = "command-help.help.sub." + args[0] + "-unknown-arguments";
                    for (String help : Language.getList(path, null, args[0])) {
                        ClickAbleTextBuilder.send(sender, ClickAbleTextBuilder.fastBuildClickAbleCommand(help));
                    }
                }
            }
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
        if (args.length != 0) {
            //只补全第一个参数
            return new ArrayList<>();
        }
        if (sender.hasPermission(opHelpPermission)) {
            //管理提示
            return Arrays.asList("help", "create", "edit", "delete", "start", "level", "jump", "done");
        } else if (sender.hasPermission(noOpHelpPermission)) {
            //玩家提示
            return Arrays.asList("help", "level", "jump");
        } else {
            //无提示
            return new ArrayList<>();
        }
    }
}
