package vip.onetool.passcard.seasons;

import vip.onetool.passcard.PassCardPlugin;

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
     */
    public void reloadData() {

    }

    /**
     * 上传最新数据
     */
    public void uploadData() {

    }
}
