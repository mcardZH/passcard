package vip.onetool.pass.card.seasons;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import vip.onetool.pass.card.conditions.ConditionInterface;

import java.util.Set;
import java.util.UUID;

/**
 * @author mcard
 */
public class SeasonPlayer {

    private final OfflinePlayer offlinePlayer;
    private final String name;

    SeasonPlayer(UUID uniqueId, String name) {
        this.offlinePlayer = Bukkit.getOfflinePlayer(uniqueId);
        this.name = name;
    }

    /**
     * 获取 {@link OfflinePlayer} 对象，
     * <p>
     * 如果玩家从未再此服务器出现过则返回null，通常出现此情况的原因是此插件运行在<b>群组服</b>中
     *
     * @return Offline Player
     */
    public OfflinePlayer getOfflinePlayer() {
        if (this.offlinePlayer.hasPlayedBefore()) {
            return null;
        }
        return this.offlinePlayer;
    }

    /**
     * Gets a {@link Player} object that this represents, if there is one
     * <p>
     * If the player is online, this will return that player. Otherwise,
     * it will return null.
     *
     * @return Online player
     */
    public Player getPlayer() {
        return offlinePlayer.getPlayer();
    }

    /**
     * 获取玩家等级信息
     *
     * @return 玩家等级
     */
    public SeasonLevel getPlayerLevel() {
        return null;
    }

    public String getName() {
        return this.name;
    }

    public boolean hasTargetCondition() {
        return false;
    }

    /**
     * 如果玩家没用设置追踪的任务返回null
     *
     * @return 玩家追踪的任务
     */
    public ConditionInterface getTargetCondition() {
        if (hasTargetCondition()) {
            return null;
        }
        return null;
    }

    public Set<ConditionInterface> getConditions() {
        return null;
    }

    public void openGuiFor() {

    }
}
