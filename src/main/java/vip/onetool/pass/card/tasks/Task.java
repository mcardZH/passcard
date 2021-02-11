package vip.onetool.pass.card.tasks;

import org.bukkit.configuration.ConfigurationSection;
import vip.onetool.pass.card.exceptions.TaskConditionKeyErrorException;
import vip.onetool.pass.card.exceptions.TaskTypeUnknownException;
import vip.onetool.pass.card.util.SqlYmlConfigurationUtils;

import java.util.*;

/**
 * @author mcard
 */
public class Task implements TaskInterface {

    private final SqlYmlConfigurationUtils config;
    private final Map<Integer, Set<TaskStepInterface>> allSteps = new HashMap<>();
    private TaskTypeEnum taskTypeEnum;

    Task(SqlYmlConfigurationUtils config) throws TaskTypeUnknownException, TaskConditionKeyErrorException {
        this.config = config;
        switch (config.getString("type").toLowerCase()) {
            case "daily": {
                taskTypeEnum = TaskTypeEnum.DAILY;
                break;
            }
            case "weekly": {
                taskTypeEnum = TaskTypeEnum.WEEKLY;
                break;
            }
            case "monthly": {
                taskTypeEnum = TaskTypeEnum.MONTHLY;
                break;
            }
            default: {
                throw new TaskTypeUnknownException(config.getString("type").toLowerCase());
            }
        }
        // 加载TaskStep
        String conditionPath = "conditions";
        for (String key : config.getConfigurationSection(conditionPath).getKeys(false)) {
            int step;
            try {
                step = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                if (!"tip".equalsIgnoreCase(key)) {
                    throw new TaskConditionKeyErrorException(this, key);
                }
                continue;
            }
            ConfigurationSection path = config.getConfigurationSection(conditionPath).getConfigurationSection(key);
            Set<TaskStepInterface> taskStepInterfaceList = new HashSet<>();
            if (path.contains("type")) {
                // 不存在并行任务
                TaskStep taskStep = new TaskStep(this, path, step);
                taskStepInterfaceList.add(taskStep);
            } else {
                // 存在并行任务
                for (String pathKey : path.getKeys(false)) {
                    ConfigurationSection pathSection = path.getConfigurationSection(pathKey);
                    TaskStep taskStep = new TaskStep(this, pathSection, step);
                    taskStepInterfaceList.add(taskStep);
                }
            }
            allSteps.put(step, taskStepInterfaceList);
        }
    }

    /**
     * 获取任务名称
     *
     * @return 任务名称
     */
    @Override
    public String getName() {
        return config.getString("name");
    }

    /**
     * 获取任务描述
     *
     * @return 任务描述
     */
    @Override
    public List<String> getDescription() {
        return config.getStringList("description");
    }

    /**
     * 设置任务描述
     *
     * @param description 任务描述
     */
    @Override
    public void setDescription(List<String> description) {
        config.set("description", description);
    }

    /**
     * 获取任务类型
     *
     * @return 任务类型
     */
    @Override
    public TaskTypeEnum getType() {
        return taskTypeEnum;
    }

    /**
     * 设置任务类型
     *
     * @param type 任务类型
     */
    @Override
    public void setType(TaskTypeEnum type) {
        this.taskTypeEnum = type;
    }

    /**
     * 获取成功提示
     *
     * @return 提示
     */
    @Override
    public String getSuccessTip() {
        return config.getString("tip");
    }

    @Override
    public void setSuccessTip(String tip) {
        config.set("tip", tip);
    }

    /**
     * 任务总步数
     *
     * @return 总步数
     */
    @Override
    public int stepCount() {
        return config.getConfigurationSection("conditions").getKeys(false).size();
    }

    /**
     * 获取所有步骤
     *
     * @return key为1~{@link #stepCount()}。value为对应{@link TaskStepInterface}
     */
    @Override
    public Map<Integer, Set<TaskStepInterface>> getAllSteps() {
        return this.allSteps;
    }

    /**
     * 获取某一步的任务
     *
     * @param step 步
     * @return 该步所有步骤，步存在的步返回空列表
     */
    @Override
    public Set<TaskStepInterface> getSteps(int step) {
        return this.allSteps.getOrDefault(step, new HashSet<>());
    }

    /**
     * 保存配置{@link SqlYmlConfigurationUtils#save()}
     */
    @Override
    public void save() {
        config.save();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(config, task.config) && taskTypeEnum == task.taskTypeEnum && Objects.equals(getAllSteps(), task.getAllSteps());
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, taskTypeEnum, getAllSteps());
    }
}
