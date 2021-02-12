package vip.onetool.pass.card.rewards;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.onetool.pass.card.seasons.SeasonPlayer;
import vip.onetool.pass.card.util.LanguageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mcard
 */
public class CommandReward implements RewardInterface {
    /**
     * 获取用于描述的物品
     *
     * @param config 当前配置
     * @return 物品
     */
    @Override
    public ItemStack getDetailItemStack(Object config) {
        String defaultName = "reward.command.detail-item-stack.default-name";
        ItemStack defItemStack = new ItemStack(Material.PAPER);
        ItemMeta defItemMeta = defItemStack.getItemMeta();
        defItemMeta.setDisplayName(LanguageUtils.get(defaultName, null));
        String total = "reward.command.detail-item-stack.total";
        if (config instanceof String) {
            defItemMeta.setLore(Arrays.asList(String.valueOf(config), LanguageUtils.get(total, null, 1)));
        } else if (config instanceof List<?>) {
            List<String> loreList = new ArrayList<>();
            for (Object o : (List<?>) config) {
                loreList.add(String.valueOf(o));
            }
            loreList.add(LanguageUtils.get(total, null, loreList.size()));
            defItemMeta.setLore(loreList);
        }
        defItemStack.setItemMeta(defItemMeta);
        return defItemStack;
    }

    /**
     * 获取编辑模板
     *
     * @return 模板类型
     */
    @Override
    public RewardTypeEnum getRewardEditType() {
        return RewardTypeEnum.CHAT_MULTIPLE;
    }

    /**
     * 获取编辑帮助
     * <p>只有<b>{@link RewardTypeEnum#ITEM_STACK_SINGLE}</b>
     * <b>{@link RewardTypeEnum#ITEM_STACK_MULTIPLE}</b>、
     * <b>{@link RewardTypeEnum#CHAT_SINGLE}</b>、
     * <b>{@link RewardTypeEnum#CHAT_MULTIPLE}</b>会调用此方法</p>
     *
     * @param player 谁获取这个帮助
     * @return 帮助内容
     */
    @Override
    public List<String> getEditHelp(Player player) {
        String help = "reward.command.help";
        return LanguageUtils.getList(help, player);
    }

    /**
     * 检查编辑状态
     * <p>只有<b>{@link RewardTypeEnum#ITEM_STACK_SINGLE}</b>
     * <b>{@link RewardTypeEnum#ITEM_STACK_MULTIPLE}</b>、
     * <b>{@link RewardTypeEnum#CHAT_SINGLE}</b>、
     * <b>{@link RewardTypeEnum#CHAT_MULTIPLE}</b>会调用此方法</p>
     *
     * @param player 谁在编辑
     * @param config 当前的配置（可能是{@link String}、{@link List<String>}、{@link ItemStack}和{@link List<ItemStack>}）
     * @return 返回null代表编辑无效，其他情况请返回格式化后的配置内容（如String转换为int）
     */
    @Override
    public Object editDone(Player player, Object config) {
        if (config instanceof String) {
            return config;
        } else if (config instanceof ArrayList<?>) {
            ConfigurationSection cfg = new MemoryConfiguration();
            for (int i = 1; i <= ((List<?>) config).size(); i++) {
                cfg.set(String.valueOf(i), String.valueOf(((List<?>) config).get(i)));
            }
            return cfg;
        }
        return null;
    }

    /**
     * 开始编辑
     * <p>只有<b>{@link RewardTypeEnum#CUSTOM}</b>类型的才会调用此方法</p>
     *
     * @param player    谁来编辑
     * @param nowConfig 旧的配置
     * @param callback  回调，<b>务必</b>把新的config和修改结果传递到这里
     */
    @Override
    public void editFor(Player player, Object nowConfig, RewardEditFinishInterface callback) {
        callback.finish(true, nowConfig);
    }

    /**
     * 注册的类型名称
     *
     * @return 名称
     */
    @Override
    public String getTypeName() {
        return "command";
    }

    /**
     * 执行奖励方案
     * <p>
     * 此时<b>一定</b>可以获得{@link Player}对象
     * </p>
     *
     * @param player 玩家
     * @param config 根据自身情况可能是{@link Integer}、{@link String}或者{@link ConfigurationSection}等等
     */
    @Override
    public void run(SeasonPlayer player, Object config) {
        boolean isOp = player.getPlayer().isOp();
        player.getPlayer().setOp(true);
        if (config instanceof String) {
            Bukkit.dispatchCommand(player.getPlayer(), String.valueOf(config));
        } else if (config instanceof ConfigurationSection) {
            ConfigurationSection cfg = (ConfigurationSection) config;
            for (String key : cfg.getKeys(false)) {
                Bukkit.dispatchCommand(player.getPlayer(), cfg.getString(key));
            }
        }
        player.getPlayer().setOp(isOp);
    }
}
