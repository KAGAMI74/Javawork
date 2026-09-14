package cn.edu.finalproject.lostfound.service;

import cn.edu.finalproject.lostfound.model.ItemType;
import cn.edu.finalproject.lostfound.model.LostItem;
import cn.edu.finalproject.lostfound.repository.ItemRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 初始数据。
 * 系统首次运行（数据文件还不存在）时写入 5 条典型的校园失物招领信息，
 * 让使用者一启动就能查看列表、做条件查询和统计，不必先手工录入。
 * 数据文件一旦存在就不再写入，因此不会重复添加，也不影响正常使用中删除记录。
 */
public final class SeedDataInitializer {

    /** 工具类不允许实例化。 */
    private SeedDataInitializer() {
    }

    /**
     * 把初始数据写入仓库。
     *
     * @param repository 目标仓库
     * @return 实际写入的记录条数
     */
    public static int seed(ItemRepository repository) {
        List<LostItem> seeds = buildSeeds(repository);
        for (LostItem item : seeds) {
            repository.add(item);
        }
        return seeds.size();
    }

    /** 构造 5 条初始数据：2 条寻物 + 3 条招领，发布时间依次错开，更接近真实使用情况。 */
    private static List<LostItem> buildSeeds(ItemRepository repository) {
        long now = System.currentTimeMillis();
        List<LostItem> seeds = new ArrayList<>();
        seeds.add(new LostItem(repository.nextId(), ItemType.FOUND, "身份证",
                "图书馆一楼服务台", "张同学 13800000001", hoursAgo(now, 26)));
        seeds.add(new LostItem(repository.nextId(), ItemType.LOST, "饭卡（校园一卡通）",
                "第二食堂二楼", "董同学 13800000002", hoursAgo(now, 20)));
        seeds.add(new LostItem(repository.nextId(), ItemType.LOST, "黑色双肩书包",
                "第三教学楼 305 教室", "李同学 13800000003", hoursAgo(now, 9)));
        seeds.add(new LostItem(repository.nextId(), ItemType.FOUND, "保温水杯",
                "体育馆羽毛球场 3 号场", "王同学 13800000004", hoursAgo(now, 5)));
        seeds.add(new LostItem(repository.nextId(), ItemType.FOUND, "蓝牙耳机",
                "实验楼 B301", "赵同学 13800000005", hoursAgo(now, 2)));
        return seeds;
    }

    private static Date hoursAgo(long now, long hours) {
        return new Date(now - TimeUnit.HOURS.toMillis(hours));
    }
}
