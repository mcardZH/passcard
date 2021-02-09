package vip.onetool.pass.card.util;

import org.bukkit.Bukkit;
import org.bukkit.command.*;

import java.util.*;

/**
 * @author mcard
 */
public class CommandManagerUtils implements TabExecutor {

    /**
     * @param command 要注册的命令
     * @return 如果命令不存在返回null
     */
    public static CommandManagerUtils createCommandManager(String command) {
        if (Bukkit.getPluginCommand(command) == null) {
            return null;
        }
        return new CommandManagerUtils(command);
    }

    private final String command;
    private final CommandExecutor defaultExecutor;
    private final TabCompleter defaultTabCompleter;
    private final Map<String, TabExecutor> handlers = new HashMap<>();
    private String defaultHandlerName = null;

    private CommandManagerUtils(String command) {
        this.command = command;
        this.defaultExecutor = Bukkit.getPluginCommand(command).getExecutor();
        this.defaultTabCompleter = Bukkit.getPluginCommand(command).getTabCompleter();

        Bukkit.getPluginCommand(command).setExecutor(this);
        Bukkit.getPluginCommand(command).setTabCompleter(this);
    }

    /**
     * @param name           子命令名
     * @param handler        处理
     * @param defaultHandler 如果为true，未知指令将执行此方法；如果有多个为true，仅保留最后一个
     * @return 如果name重复则返回false
     */
    public boolean registerSubCommand(String name, TabExecutor handler, boolean defaultHandler) {
        if (handlers.containsKey(name)) {
            return false;
        }
        handlers.put(name, handler);
        if (defaultHandler) {
            this.defaultHandlerName = name;
        }
        return true;
    }

    public void unregisterSubCommand(String name) {
        handlers.remove(name);
    }

    public Set<String> registeredSubCommand() {
        return handlers.keySet();
    }

    public void unregisterAll() {
        for (String key : registeredSubCommand()) {
            unregisterSubCommand(key);
        }
        Bukkit.getPluginCommand(this.command).setExecutor(this.defaultExecutor);
        Bukkit.getPluginCommand(this.command).setTabCompleter(this.defaultTabCompleter);
    }

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
        if (args.length == 0) {
            if (this.defaultHandlerName == null) {
                //不存在默认处理器
                return false;
            }
            return this.handlers.get(this.defaultHandlerName).onCommand(sender, command, label, args);
        } else {
            String[] arguments = new String[args.length - 1];
            System.arraycopy(args, 1, arguments, 0, args.length - 1); //复制从第1项到最后一项
            if (this.registeredSubCommand().contains(args[0])) {
                return this.handlers.get(args[0]).onCommand(sender, command, label, arguments);
            } else {
                return this.handlers.get(this.defaultHandlerName).onCommand(sender, command, label, arguments);
            }
        }
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
        if (args.length == 0) {
            if (this.defaultHandlerName == null) {
                //不存在默认处理器
                return new ArrayList<>();
            }

            return this.handlers.get(this.defaultHandlerName).onTabComplete(sender, command, alias, args);
        } else {
            String[] arguments = new String[args.length - 1];
            System.arraycopy(args, 1, arguments, 0, args.length - 1); //复制从第1项到最后一项
            if (this.registeredSubCommand().contains(args[0])) {
                return this.handlers.get(args[0]).onTabComplete(sender, command, alias, arguments);
            } else {
                return this.handlers.get(this.defaultHandlerName).onTabComplete(sender, command, alias, arguments);
            }
        }
    }
}
