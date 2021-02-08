package vip.onetool.passcard.seasons;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SeasonPlayer {

    private final OfflinePlayer offlinePlayer;

    SeasonPlayer(UUID uniqueId) {
        this.offlinePlayer = Bukkit.getOfflinePlayer(uniqueId);
    }

    /**
     * 获取 {@link OfflinePlayer} 对象，
     * <p>
     * 如果玩家从未再此服务器出现过则返回null，通常出现此情况的原因是此插件运行在<b>群组服</b>中
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
}
