package vip.onetool.pass.card.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

/**
 * @author mcard
 */
public class LanguageUtils {

    private static String defaultLanguage = "zh_CN";
    private static YamlConfiguration defaultLanguageConfig;
    private static String language = "zh_CN";
    private static YamlConfiguration languageConfig;

    private static Plugin plugin;
    private static boolean usePlaceholderAPI;

    /**
     * 初始化
     *
     * @param plugin            插件
     * @param usePlaceholderAPI 是否使用placeholderAPI
     * @param debug             是否覆盖旧的语言文件
     * @param fileNames         文件名列表
     */
    public static void init(Plugin plugin, boolean usePlaceholderAPI, boolean debug, String... fileNames) {
        LanguageUtils.plugin = plugin;
        LanguageUtils.usePlaceholderAPI = usePlaceholderAPI;
        File languageFolder = new File(plugin.getDataFolder(), "language");
        if (!languageFolder.exists()) {
            languageFolder.mkdirs();
        }
        for (String fileName : fileNames) {
            File languageFile = new File(languageFolder, fileName);
            if (!languageFile.exists()) {
                plugin.saveResource("language/" + fileName, debug);
            }
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            usePlaceholderAPI = false;
        }
        LanguageUtils.usePlaceholderAPI = usePlaceholderAPI;
    }

    /**
     * 如果设定语言找不到对应字段则使用默认语言
     *
     * @param language 默认语言
     * @return 返回false如果文件不存在
     */
    public static boolean setDefaultLanguage(String language) {
        LanguageUtils.defaultLanguage = language;
        File languageFolder = new File(plugin.getDataFolder(), "language");
        File languageFile = new File(languageFolder, language + ".yml");
        if (!languageFile.exists()) {
            return false;
        }
        LanguageUtils.defaultLanguageConfig = YamlConfiguration.loadConfiguration(languageFile);
        return true;
    }

    /**
     * @return 默认zh_CN
     */
    public static String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * @param language 语言
     * @return 返回false如果文件不存在
     */
    public static boolean setLanguage(String language) {
        LanguageUtils.language = language;
        File languageFolder = new File(plugin.getDataFolder(), "language");
        File languageFile = new File(languageFolder, language + ".yml");
        if (!languageFile.exists()) {
            return false;
        }
        LanguageUtils.languageConfig = YamlConfiguration.loadConfiguration(languageFile);
        return true;
    }

    public static String getLanguage() {
        return language;
    }

    public static boolean isUsePlaceholderAPI() {
        return usePlaceholderAPI;
    }

    public static void setUsePlaceholderAPI(boolean usePlaceholderAPI) {
        LanguageUtils.usePlaceholderAPI = usePlaceholderAPI;
    }

    public static boolean isSet(String path) {
        return languageConfig.isSet(path);
    }

    /**
     * @param path    路径
     * @param player  如果需要使用PlaceholderAPI
     * @param replace 一一对应
     * @return 自动替换了颜色代码
     */
    public static String get(String path, Player player, Object... replace) {
        YamlConfiguration config = languageConfig.contains(path) ? languageConfig : defaultLanguageConfig;
        String temp = config.getString(path, path);
        for (int i = 0; i < replace.length; i++) {
            temp = temp.replace("%" + i, String.valueOf(replace[i]));
        }
        temp = ChatColor.translateAlternateColorCodes('&', temp);
        if (usePlaceholderAPI) {
            return PlaceholderAPI.setPlaceholders(player, temp);
        } else {
            return temp;
        }
    }

    /**
     *
     * @param path    路径
     * @param player  如果需要使用PlaceholderAPI
     * @param replace 一一对应，支持List或Obj
     * @return 自动替换了颜色代码
     */
    public static List<String> getList(String path, Player player, Object... replace) {
        YamlConfiguration config = languageConfig.contains(path) ? languageConfig : defaultLanguageConfig;
        List<String> temp = config.getStringList(path);
        for (int i = 0; i < temp.size(); i++) {
            if (replace.length >= temp.size()) {
                break;
            }
            String tempStr = temp.get(i);
            if (replace[i] instanceof List) {
                try {
                    //如果是list则逐个替换
                    List<String> tempList = (List<String>) replace[i];
                    for (int m = 0; m < tempList.size(); m++) {
                        tempStr = tempStr.replace("%" + m, tempList.get(m));
                    }
                } catch (Exception ignored) {
                }
            } else {
                //其他的认为是文本
                tempStr = tempStr.replace("%0", String.valueOf(replace[i]));
            }
            tempStr = ChatColor.translateAlternateColorCodes('&', tempStr);
            temp.set(i, tempStr);
        }
        if (usePlaceholderAPI) {
            return PlaceholderAPI.setPlaceholders(player, temp);
        } else {
            return temp;
        }
    }

    public static String replace(String raw, Object... replace) {
        for (int i = 0; i < replace.length; i++) {
            raw = raw.replace("%" + i, String.valueOf(replace[i]));
        }
        return raw;
    }

}
