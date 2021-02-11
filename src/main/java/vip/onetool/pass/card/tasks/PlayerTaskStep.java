package vip.onetool.pass.card.tasks;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

/**
 * @author mcard
 */
public class PlayerTaskStep extends TaskStep implements PlayerTaskStepInterface {

    private final PlayerTaskInterface fatherPlayerTask;
    private boolean isFinish = false;

    PlayerTaskStep(PlayerTask fatherPlayerTask, TaskStepInterface taskStep) {
        super(taskStep, taskStep.getConfig());
        this.fatherPlayerTask = fatherPlayerTask;
    }

    PlayerTaskStep(PlayerTaskInterface fatherPlayerTask, TaskInterface fatherTask, ConfigurationSection config, int step) {
        super(fatherTask, config, step);
        this.fatherPlayerTask = fatherPlayerTask;
    }

    /**
     * 此步骤是否已经完成
     *
     * @return 是否已经完成
     */
    @Override
    public boolean isFinish() {
        return isFinish;
    }

    /**
     * 获取父任务
     *
     * @return 任务
     */
    @Override
    public PlayerTaskInterface getFatherPlayerTask() {
        return fatherPlayerTask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlayerTaskStep)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PlayerTaskStep that = (PlayerTaskStep) o;
        return isFinish() == that.isFinish() && Objects.equals(getFatherPlayerTask(), that.getFatherPlayerTask());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFatherPlayerTask(), isFinish());
    }
}
