package vip.onetool.pass.card.tasks;

import org.bukkit.configuration.ConfigurationSection;
import vip.onetool.pass.card.conditions.ConditionInterface;

import java.util.Objects;

/**
 * @author mcard
 */
public class TaskStep implements TaskStepInterface {

    private final TaskInterface fatherTask;
    private final ConfigurationSection config;
    private final int step;

    TaskStep(TaskInterface fatherTask, ConfigurationSection config, int step) {
        this.fatherTask = fatherTask;
        this.config = config;
        this.step = step;
    }

    protected TaskStep(TaskStepInterface taskStepInterface, ConfigurationSection config) {
        this.fatherTask = taskStepInterface.getFatherTask();
        this.step = taskStepInterface.getThisStep();
        this.config = config;
    }

    /**
     * 获取父任务类
     *
     * @return 父任务类
     */
    @Override
    public TaskInterface getFatherTask() {
        return fatherTask;
    }

    /**
     * 获取当前步骤
     *
     * @return 步骤
     */
    @Override
    public int getThisStep() {
        return step;
    }

    /**
     * 获取处理器名称
     *
     * @return 名称
     */
    @Override
    public String getHandlerName() {
        return config.getString("type");
    }

    /**
     * 获取处理器
     *
     * @return 处理器
     */
    @Override
    public ConditionInterface getHandler() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskStep)) {
            return false;
        }
        TaskStep taskStep = (TaskStep) o;
        return step == taskStep.step && Objects.equals(getFatherTask(), taskStep.getFatherTask()) && Objects.equals(config, taskStep.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFatherTask(), config, step);
    }

    @Override
    public ConfigurationSection getConfig() {
        return config;
    }
}
