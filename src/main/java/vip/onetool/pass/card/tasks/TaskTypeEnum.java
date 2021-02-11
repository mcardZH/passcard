package vip.onetool.pass.card.tasks;

/**
 * @author mcard
 */
public enum TaskTypeEnum {
    /**
     * 每日任务
     */
    DAILY("每日任务", "daily", 1),
    /**
     * 每周任务
     */
    WEEKLY("每周任务", "weekly", 7),
    /**
     * 每月任务
     */
    MONTHLY("每月任务", "monthly", 30);

    private final String name;
    private final String key;
    private final int days;

    private TaskTypeEnum(String name, String key, int days) {
        this.name = name;
        this.key = key;
        this.days = days;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getKey() {
        return key;
    }

    public int getDays() {
        return days;
    }
}
