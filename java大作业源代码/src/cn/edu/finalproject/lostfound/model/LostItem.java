package cn.edu.finalproject.lostfound.model;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 校园失物招领记录。
 * 记录物品名称、类型、丢失/拾到地点与联系方式；使用 Date 保存发布时间，
 * 并实现 Serializable，便于通过 Socket 传输和本地快照持久化。
 */
public class LostItem implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * DateTimeFormatter 是不可变、线程安全的实现，可被多个客户端处理线程共享；
     * （SimpleDateFormat 内部持有可变 Calendar，多线程共用会产生错误结果，因此不使用）
     */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId ZONE = ZoneId.systemDefault();

    private final long id;
    private final ItemType type;
    private final String title;
    private final String location;
    private final String contact;
    private final Date publishTime;
    private ItemStatus status;
    private String claimant;
    private Date claimTime;

    public LostItem(long id, ItemType type, String title, String location, String contact) {
        this(id, type, title, location, contact, new Date());
    }

    /**
     * 指定发布时间的构造器，供导入历史记录、写入初始数据时使用。
     * 正常发布走上面的构造器，发布时间取当前时间。
     */
    public LostItem(long id, ItemType type, String title, String location, String contact, Date publishTime) {
        this.id = id;
        this.type = type;
        this.title = clean(title, "物品名称");
        this.location = clean(location, "地点");
        this.contact = clean(contact, "联系方式");
        this.publishTime = publishTime == null ? new Date() : new Date(publishTime.getTime());
        this.status = ItemStatus.OPEN;
    }

    private static String clean(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
        return value.trim();
    }

    /** Date 与线程安全格式化器之间的桥接。 */
    private static String format(Date date) {
        return date == null ? "" : DATE_FORMAT.format(date.toInstant().atZone(ZONE));
    }

    public synchronized boolean claim(String claimer) {
        if (status == ItemStatus.CLAIMED) {
            return false;
        }
        this.claimant = clean(claimer, "认领人");
        this.claimTime = new Date();
        this.status = ItemStatus.CLAIMED;
        return true;
    }

    /**
     * 关键词匹配物品名称与地点；类型、状态为可选筛选条件，留空表示不限。
     * 三个条件之间是「与」的关系。
     */
    public boolean matches(String keyword, String typeValue, String statusValue) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        boolean keywordMatched = normalizedKeyword.isEmpty()
                || title.toLowerCase().contains(normalizedKeyword)
                || location.toLowerCase().contains(normalizedKeyword);
        boolean typeMatched = typeValue == null || typeValue.trim().isEmpty()
                || type.name().equalsIgnoreCase(typeValue.trim());
        boolean statusMatched = statusValue == null || statusValue.trim().isEmpty()
                || status.name().equalsIgnoreCase(statusValue.trim());
        return keywordMatched && typeMatched && statusMatched;
    }

    public long getId() {
        return id;
    }

    public ItemType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getContact() {
        return contact;
    }

    public Date getPublishTime() {
        return new Date(publishTime.getTime());
    }

    public ItemStatus getStatus() {
        return status;
    }

    public String getClaimant() {
        return claimant;
    }

    public Date getClaimTime() {
        return claimTime == null ? null : new Date(claimTime.getTime());
    }

    /** 列表与详情共用的单行展示文本。 */
    public String toDisplayText() {
        String claimedText = status == ItemStatus.CLAIMED
                ? "，认领人：" + claimant + "，认领时间：" + format(claimTime) : "";
        return String.format("[%d] %s | %s | %s | 地点：%s | 联系：%s | 发布时间：%s%s",
                id, type.getDisplayName(), status.getDisplayName(), title, location, contact,
                format(publishTime), claimedText);
    }
}
