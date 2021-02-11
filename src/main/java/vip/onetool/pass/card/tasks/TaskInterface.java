package vip.onetool.pass.card.tasks;

import vip.onetool.pass.card.util.SqlYmlConfigurationUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author mcard
 */
public interface TaskInterface {

    /**
     * 获取任务名称
     *
     * @return 任务名称
     */
    String getName();

    /**
     * 获取任务描述
     *
     * @return 任务描述
     */
    List<String> getDescription();

    /**
     * 设置任务描述
     *
     * @param description 任务描述
     */
    void setDescription(List<String> description);

    /**
     * 获取任务类型
     *
     * @return 任务类型
     */
    TaskTypeEnum getType();

    /**
     * 设置任务类型
     *
     * @param type 任务类型
     */
    void setType(TaskTypeEnum type);

    /**
     * 获取成功提示
     *
     * @return 提示
     */
    String getSuccessTip();

    /**
     * 设置成功提示
     *
     * @param tip 提示
     */
    void setSuccessTip(String tip);

    /**
     * 任务总步数
     *
     * @return 总步数
     */
    int stepCount();

    /**
     * 获取所有步骤
     *
     * @return key为1~{@link #stepCount()}。value为对应{@link TaskStepInterface}
     */
    Map<Integer, Set<TaskStepInterface>> getAllSteps();

    /**
     * 获取某一步的任务
     *
     * @param step 步
     * @return 该步所有步骤，步存在的步返回空列表
     */
    Set<TaskStepInterface> getSteps(int step);

    /**
     * 保存配置{@link SqlYmlConfigurationUtils#save()}
     */
    void save();

    /**
     * 获取配置文件
     *
     * @return 配置
     */
    SqlYmlConfigurationUtils getConfig();

}
