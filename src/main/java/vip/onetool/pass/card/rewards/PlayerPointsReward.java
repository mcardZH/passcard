package vip.onetool.pass.card.rewards;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vip.onetool.pass.card.seasons.SeasonPlayer;
import vip.onetool.pass.card.util.LanguageUtils;

import java.util.List;

/**
 * @author mcard
 */
public class PlayerPointsReward implements RewardInterface {
    /**
     * 获取用于描述的物品
     *
     * @param config 当前配置
     * @return 物品
     */
    @Override
    public ItemStack getDetailItemStack(Object config) {
        String defaultName = "reward.player-points.detail-item-stack.default-name";
        ItemStack defItemStack = new ItemStack(Material.EMERALD);
        ItemMeta defItemMeta = defItemStack.getItemMeta();
        if ((config instanceof Integer) || (config instanceof String)) {
            defItemMeta.setDisplayName(LanguageUtils.get(defaultName, null, config));
        } else {
            String wait = "reward.player-points.detail-item-stack.wait";
            defItemMeta.setDisplayName(LanguageUtils.get(defaultName, null
                    , LanguageUtils.get(wait, null)));
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
        return RewardTypeEnum.CHAT_SINGLE;
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
        String help = "reward.player-points.help";
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
        if (!(config instanceof String)) {
            return null;
        }
        try {
            return Integer.parseInt(String.valueOf(config));
        } catch (Exception e) {
            return null;
        }
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
        return "points";
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
        PlayerPoints playerPointsPlugin = (PlayerPoints) Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints");
        if (playerPointsPlugin.getAPI() == null) {
            String playerPointsPath = "reward.player-points.depend-plugin-no-found";
            player.getPlayer().sendMessage(LanguageUtils.get(playerPointsPath, null));
            return;
        }
        try {
            playerPointsPlugin.getAPI().give(player.getUniqueId(), (int) config);
        } catch (Exception e) {
            String configErrorPath = "reward.player-points.config-error";
            player.getPlayer().sendMessage(LanguageUtils.get(configErrorPath, null, config));
        }
    }
}
