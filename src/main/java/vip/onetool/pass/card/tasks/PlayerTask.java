package vip.onetool.pass.card.tasks;

import org.bukkit.entity.Player;
import vip.onetool.pass.card.exceptions.TaskConditionKeyErrorException;
import vip.onetool.pass.card.exceptions.TaskTypeUnknownException;
import vip.onetool.pass.card.seasons.SeasonPlayer;
import vip.onetool.pass.card.util.SqlYmlConfigurationUtils;

import java.util.*;

/**
 * @author mcard
 */
public class PlayerTask extends Task implements PlayerTaskInterface {

    private final Date startTime;
    private final SeasonPlayer seasonPlayer;
    private final Map<Integer, Set<PlayerTaskStepInterface>> playerTaskSteps = new HashMap<>();
    private TaskTypeEnum lastTypeCache;
    private Date endTime;
    private int nowStep = 1;
    private boolean isFinish = false;

    PlayerTask(SqlYmlConfigurationUtils config, Date startTime, SeasonPlayer seasonPlayer) throws TaskTypeUnknownException, TaskConditionKeyErrorException {
        super(config);
        this.startTime = startTime;
        this.seasonPlayer = seasonPlayer;
        this.lastTypeCache = getType();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.startTime);
        // 回到当天0点
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        // 往后调整时间
        calendar.add(Calendar.DATE, this.getType().getDays());
        this.endTime = calendar.getTime();
        // 处理step转换为playerStep
        for (Integer stepIndex : this.getAllSteps().keySet()) {
            Set<PlayerTaskStepInterface> playerTaskStepInterfaces = new HashSet<>();
            for (TaskStepInterface taskStepInterface : this.getAllSteps().get(stepIndex)) {
                PlayerTaskStep playerTaskStep = new PlayerTaskStep(this, taskStepInterface);
                playerTaskStepInterfaces.add(playerTaskStep);
            }
            this.playerTaskSteps.put(stepIndex, playerTaskStepInterfaces);
        }
    }

    PlayerTask(TaskInterface taskInterface, Date startTime, SeasonPlayer seasonPlayer) throws TaskTypeUnknownException, TaskConditionKeyErrorException {
        super(taskInterface);
        this.startTime = startTime;
        this.seasonPlayer = seasonPlayer;
        this.lastTypeCache = getType();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.startTime);
        // 回到当天0点
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        // 往后调整时间
        calendar.add(Calendar.DATE, this.getType().getDays());
        this.endTime = calendar.getTime();
    }

    /**
     * 获取任务绑定玩家，当玩家不在当前服务器时返回null
     *
     * @return 绑定玩家
     */
    @Override
    public Player getBukkitPlayer() {
        return seasonPlayer.getPlayer();
    }

    /**
     * 获取任务绑定玩家
     *
     * @return 绑定玩家
     */
    @Override
    public SeasonPlayer getPlayer() {
        return this.seasonPlayer;
    }

    /**
     * 获取任务开始时间
     *
     * @return 开始时间
     */
    @Override
    public Date getStartTime() {
        return this.startTime;
    }

    /**
     * 任务是否已经过期
     *
     * @return 任务是否已经过期
     */
    @Override
    public boolean isOutDate() {
        return new Date().after(this.endTime);
    }

    /**
     * 获取任务结束时间
     *
     * @return 结束时间
     */
    @Override
    public Date getEndTime() {
        if (this.getType() != this.lastTypeCache) {
            // 缓存无效，更新缓存
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.startTime);
            // 回到当天0点
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR, 0);
            // 往后调整时间
            calendar.add(Calendar.DATE, this.getType().getDays());
            this.endTime = calendar.getTime();
            this.lastTypeCache = this.getType();
        }
        return this.endTime;
    }

    /**
     * 获取当前步骤
     *
     * @return 当前步骤
     */
    @Override
    public int getNowStep() {
        return nowStep;
    }

    /**
     * 任务是否已经完成
     *
     * @return 是否已经完成
     */
    @Override
    public boolean isFinish() {
        return isFinish;
    }

    /**
     * 立刻完成
     */
    @Override
    public void done() {

    }

    /**
     * 跳过并重新获取
     */
    @Override
    public void jump() {

    }

    /**
     * 获取玩家任务的所有步骤
     *
     * @return 玩家任务步骤
     */
    @Override
    public Map<Integer, Set<PlayerTaskStepInterface>> getAllPlayerSteps() {
        return this.playerTaskSteps;
    }

    /**
     * 获取玩家任务的步骤
     *
     * @param step 从1到{@link #stepCount()}
     * @return 如果step不合法返回空列表
     */
    @Override
    public Set<PlayerTaskStepInterface> getPlayerSteps(int step) {
        return getAllPlayerSteps().getOrDefault(step, new HashSet<>());
    }


}
