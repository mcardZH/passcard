package vip.onetool.pass.card.exceptions;

public class TaskTypeUnknownException extends Exception {

    private final String type;

    public TaskTypeUnknownException(String type) {
        super("任务类型 \"%0\" 未定义，仅支持 \"daily\"、\"weekly\"和\"monthly\"".replace("%0", type));
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
