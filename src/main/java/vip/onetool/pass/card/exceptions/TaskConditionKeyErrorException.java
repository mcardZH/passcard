package vip.onetool.pass.card.exceptions;

import vip.onetool.pass.card.tasks.Task;

public class TaskConditionKeyErrorException extends Exception {

    private final Task task;
    private final String key;

    public TaskConditionKeyErrorException(Task task, String key) {
        super("任务 \"%0\" 存在不合规conditions字段 %1".replace(task.getName(), key));
        this.task = task;
        this.key = key;
    }

    public Task getTask() {
        return task;
    }

    public String getKey() {
        return key;
    }
}
