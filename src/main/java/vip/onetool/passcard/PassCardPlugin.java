package vip.onetool.passcard;


import org.bukkit.plugin.java.JavaPlugin;
import vip.onetool.passcard.utils.CommandManager;
import vip.onetool.passcard.utils.Language;


public final class PassCardPlugin extends JavaPlugin {

    private CommandManager passCardCommand;

    @Override
    public void onEnable() {
        Language.init(this, true, true, "zh_CN.yml");
        passCardCommand = CommandManager.createCommandManager("passcard");

    }

    @Override
    public void onDisable() {

        passCardCommand.unregisterAll();

    }

    public static boolean isDatabaseMode() {
        return false;
    }
}
