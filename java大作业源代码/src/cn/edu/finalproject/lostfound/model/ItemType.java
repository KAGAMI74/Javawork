package cn.edu.finalproject.lostfound.model;

/** 物品信息的发布类型。 */
public enum ItemType {
    LOST("寻物"),
    FOUND("招领");

    private final String displayName;

    ItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ItemType from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("类型不能为空，应为 LOST 或 FOUND");
        }
        return ItemType.valueOf(value.trim().toUpperCase());
    }
}
