package cn.edu.finalproject.lostfound.repository;

import cn.edu.finalproject.lostfound.model.LostItem;

import java.util.Map;

/** 本地快照存储接口。 */
public interface SnapshotStore {
    Map<Long, LostItem> load();

    void save(Map<Long, LostItem> items);

    /**
     * 快照文件是否已经存在。
     * 用于判断是否首次运行——首次运行没有历史快照，此时才写入初始示范数据。
     */
    boolean exists();
}
