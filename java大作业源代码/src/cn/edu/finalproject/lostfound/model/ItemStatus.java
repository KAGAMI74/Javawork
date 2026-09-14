package cn.edu.finalproject.lostfound.model;

/** 物品记录的处理状态。 */
public enum ItemStatus {
    OPEN("待认领"),
    CLAIMED("已认领");

    private final String displayName;

    ItemStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
