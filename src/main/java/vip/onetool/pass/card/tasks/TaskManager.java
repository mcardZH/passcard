package vip.onetool.pass.card.tasks;

import org.bukkit.Bukkit;
import vip.onetool.pass.card.exceptions.TaskConditionKeyErrorException;
import vip.onetool.pass.card.exceptions.TaskTypeUnknownException;
import vip.onetool.pass.card.util.LanguageUtils;
import vip.onetool.pass.card.util.SqlYmlConfigurationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mcard
 */
public class TaskManager {

    private static final Map<String, TaskInterface> tasks = new HashMap<>();

    private TaskManager() {
    }

    /**
     * 初始化方法，从配置文件加载（无需外部调用）
     *
     * @param configs 配置文件
     */
    public static void init(List<SqlYmlConfigurationUtils> configs) {
        for (SqlYmlConfigurationUtils config : configs) {
            Task task;
            try {
                task = new Task(config);
            } catch (TaskTypeUnknownException e) {
                String taskTypeError = "init-help.error.task-type-error";
                Bukkit.getConsoleSender().sendMessage(LanguageUtils.get(taskTypeError, null,
                        config.getName(), e.getType()));
                continue;
            } catch (TaskConditionKeyErrorException e) {
                e.printStackTrace();
                continue;
            }
            tasks.put(task.getName(), task);
        }
    }

    /**
     * 保存所有配置
     */
    public static void uninit() {
        for (TaskInterface task : tasks.values()) {
            task.save();
        }
    }

    /**
     * 获取一个任务
     *
     * @param name 名称
     * @return 如果不存在返回null
     */
    public static TaskInterface getTask(String name) {
        return tasks.get(name);
    }

    public static Map<String, TaskInterface> getTasks() {
        return tasks;
    }

    public static void registerTask(TaskInterface task) {
        tasks.put(task.getName(), task);
    }

    public static void unregisterTask(String taskName) {
        if (tasks.get(taskName) != null) {
            // 保存配置
            tasks.get(taskName).save();
        }
        tasks.remove(taskName);
    }

    public static void unregisterTask(TaskInterface task) {
        task.save();
        tasks.remove(task.getName());
    }
}
