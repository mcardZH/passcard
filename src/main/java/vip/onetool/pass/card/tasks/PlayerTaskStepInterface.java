package vip.onetool.pass.card.tasks;

/**
 * @author mcard
 */
public interface PlayerTaskStepInterface extends TaskStepInterface {

    /**
     * 此步骤是否已经完成
     *
     * @return 是否已经完成
     */
    boolean isFinish();

    /**
     * 获取父任务
     *
     * @return 任务
     */
    PlayerTaskInterface getFatherPlayerTask();
}
