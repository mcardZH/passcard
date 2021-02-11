package vip.onetool.pass.card.tasks;

import org.bukkit.configuration.ConfigurationSection;
import vip.onetool.pass.card.conditions.ConditionInterface;

/**
 * @author mcard
 */
public interface TaskStepInterface {

    /**
     * 获取父任务类
     *
     * @return 父任务类
     */
    TaskInterface getFatherTask();

    /**
     * 获取当前步骤
     *
     * @return 步骤
     */
    int getThisStep();

    /**
     * 获取处理器名称
     *
     * @return 名称
     */
    String getHandlerName();

    /**
     * 获取处理器
     *
     * @return 处理器
     */
    ConditionInterface getHandler();

    /**
     * 获取配置文件
     *
     * @return 配置
     */
    ConfigurationSection getConfig();
}
