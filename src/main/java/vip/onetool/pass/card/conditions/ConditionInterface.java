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

    /**
     * 进行一次检测
     *
     * @return 返回是否已经完成
     */
    boolean run();
}
