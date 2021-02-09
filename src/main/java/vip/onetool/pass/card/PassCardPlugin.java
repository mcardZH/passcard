package vip.onetool.pass.card;


import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import vip.onetool.pass.card.commands.*;
import vip.onetool.pass.card.conditions.ConditionManager;
import vip.onetool.pass.card.util.CommandManagerUtils;
import vip.onetool.pass.card.util.LanguageUtils;
import vip.onetool.pass.card.util.SqlYmlConfigurationUtils;

import java.io.File;


public final class PassCardPlugin extends JavaPlugin {

    private CommandManagerUtils passCardCommand;
    private static boolean isDatabaseMode = false;

    @Override
    public void onEnable() {

        init();

    }

    @Override
    public void onDisable() {
        // 方便重载
        passCardCommand.unregisterAll();

    }

    public void init() {
        // 保存配置
        saveDefaultConfig();
        isDatabaseMode = getConfig().getBoolean("database.enable");
        // 初始化语言
        // TODO：发布时关闭debug选项
        LanguageUtils.init(this, true, true, "zh_CN.yml");
        LanguageUtils.setLanguage(getConfig().getString("lang") + ".yml");
        // 初始化命令
        passCardCommand = CommandManagerUtils.createCommandManager("passcard");
        assert passCardCommand != null;
        passCardCommand.registerSubCommand("create", new CreateCommand(), false);
        passCardCommand.registerSubCommand("open", new DefaultCommand(), true);
        passCardCommand.registerSubCommand("delete", new DeleteCommand(), false);
        passCardCommand.registerSubCommand("done", new DoneCommand(), false);
        passCardCommand.registerSubCommand("edit", new EditCommand(), false);
        passCardCommand.registerSubCommand("help", new HelpCommand(), false);
        passCardCommand.registerSubCommand("jump", new JumpCommand(), false);
        passCardCommand.registerSubCommand("level", new LevelCommand(), false);
        passCardCommand.registerSubCommand("new", new NewCommand(), false);
        passCardCommand.registerSubCommand("start", new StartCommand(), false);
        // 初始化任务、赛季、玩家、奖励信息
        File taskFolder = new File(getDataFolder(), "tasks");
        if (!taskFolder.exists()) {
            taskFolder.mkdirs();
            saveResource("tasks/每日_在线30分钟.yml", true);

        }
        //ConditionManager.init();
    }

    public static boolean isDatabaseMode() {
        return isDatabaseMode;
    }
}
