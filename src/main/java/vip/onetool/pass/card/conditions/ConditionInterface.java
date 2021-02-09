package vip.onetool.pass.card.conditions;

/**
 * @author mcard
 */
public interface ConditionInterface {

    /**
     * 跳过当前任务
     */
    void jump();

    /**
     * 完成当前任务
     */
    void done();
}
