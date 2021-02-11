package vip.onetool.pass.card.rewards;

/**
 * @author mcard
 */
public interface RewardEditFinishInterface {

    /**
     * 编辑完成请调用此方法
     *
     * @param success 编辑是否成功，如果传入false则不会修改当前的用新config替换旧的
     * @param config  新的config
     */
    void finish(boolean success, Object config);

}
