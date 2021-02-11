package vip.onetool.pass.card.tasks;

import org.bukkit.entity.Player;
import vip.onetool.pass.card.seasons.SeasonPlayer;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author mcard
 */
public interface PlayerTaskInterface extends TaskInterface {

    /**
     * 获取任务绑定玩家，当玩家不在当前服务器时返回null
     *
     * @return 绑定玩家
     */
    Player getBukkitPlayer();

    /**
     * 获取任务绑定玩家
     *
     * @return 绑定玩家
     */
    SeasonPlayer getPlayer();

    /**
     * 获取任务开始时间
     *
     * @return 开始时间
     */
    Date getStartTime();

    /**
     * 任务是否已经过期
     *
     * @return 任务是否已经过期
     */
    boolean isOutDate();

    /**
     * 获取任务结束时间
     *
     * @return 结束时间
     */
    Date getEndTime();

    /**
     * 获取当前步骤
     *
     * @return 当前步骤
     */
    int getNowStep();

    /**
     * 任务是否已经完成
     *
     * @return 是否已经完成
     */
    boolean isFinish();

    /**
     * 立刻完成
     */
    void done();

    /**
     * 跳过并重新获取
     */
    void jump();

    /**
     * 获取玩家任务的所有步骤
     *
     * @return 玩家任务步骤
     */
    Map<Integer, Set<PlayerTaskStepInterface>> getAllPlayerSteps();

    /**
     * 获取玩家任务的步骤
     *
     * @param step 从1到{@link #stepCount()}
     * @return 如果step不合法返回空列表
     */
    Set<PlayerTaskStepInterface> getPlayerSteps(int step);
}
