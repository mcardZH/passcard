package vip.onetool.pass.card.seasons;

import vip.onetool.pass.card.PassCardPlugin;

public class SeasonLevel {

    private final SeasonPlayer player;

    SeasonLevel(SeasonPlayer player) {
        this.player = player;
    }

    public SeasonPlayer getPlayer() {
        return player;
    }

    /**
     * <b>慎用！如果玩家在当前服务器一般本地的是最新的</b>
     * <p>
     * 读取最新数据（一般仅用在数据库模式{@link PassCardPlugin#isDatabaseMode()}）
     * <p>
     * 如果插件运行在单独服务器，重载会导致丢失最新信息
     */
    public void reloadData() {

    }

    /**
     * 上传最新数据
     * @param noDiffUpdate <b>不推荐！</b>不使用差异更新，将当前所有数据覆盖旧的数据
     */
    public void saveData(boolean noDiffUpdate) {

    }

    /**
     * 获取玩家等级
     * @return 最小可能返回0（本赛季未做过任何任务）
     */
    public int getLevel() {
        return 0;
    }

    public void setLevel(int newLevel) {

    }

    public void addLevel(int level) {

    }

    public void reduceLevel(int level) {

    }

    public int getXp() {
        return 0;
    }

    public void setXp(int xp) {

    }

    public void addXp(int xp) {

    }

    public void reduceXp(int xp) {

    }



}
