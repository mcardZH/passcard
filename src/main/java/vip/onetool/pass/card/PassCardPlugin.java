package vip.onetool.pass.card;


import org.bukkit.plugin.java.JavaPlugin;
import vip.onetool.pass.card.util.CommandManagerUtils;
import vip.onetool.pass.card.util.LanguageUtils;


public final class PassCardPlugin extends JavaPlugin {

    private CommandManagerUtils passCardCommand;

    @Override
    public void onEnable() {
        LanguageUtils.init(this, true, true, "zh_CN.yml");
        passCardCommand = CommandManagerUtils.createCommandManager("passcard");

    }

    @Override
    public void onDisable() {

        passCardCommand.unregisterAll();

    }

    public static boolean isDatabaseMode() {
        return false;
    }
}
