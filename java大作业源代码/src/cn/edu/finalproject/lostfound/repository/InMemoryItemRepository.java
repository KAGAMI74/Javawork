package cn.edu.finalproject.lostfound.repository;

import cn.edu.finalproject.lostfound.model.LostItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** 以 ConcurrentHashMap 管理失物记录的线程安全仓库。 */
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, LostItem> items = new ConcurrentHashMap<>();
    private final AtomicLong sequence;

    public InMemoryItemRepository(Map<Long, LostItem> loadedItems) {
        if (loadedItems != null) {
            items.putAll(loadedItems);
        }
        long maxId = items.keySet().stream().mapToLong(Long::longValue).max().orElse(0L);
        sequence = new AtomicLong(maxId + 1);
    }

    @Override
    public long nextId() {
        return sequence.getAndIncrement();
    }

    @Override
    public void add(LostItem item) {
        items.put(item.getId(), item);
    }

    @Override
    public LostItem find(long id) {
        return items.get(id);
    }

    @Override
    public LostItem remove(long id) {
        return items.remove(id);
    }

    @Override
    public List<LostItem> findAll() {
        List<LostItem> result = new ArrayList<>(items.values());
        result.sort(Comparator.comparingLong(LostItem::getId));
        return result;
    }

    @Override
    public List<LostItem> search(String keyword, String type, String status) {
        List<LostItem> result = new ArrayList<>();
        for (LostItem item : findAll()) {
            if (item.matches(keyword, type, status)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public Map<Long, LostItem> snapshot() {
        return new LinkedHashMap<>(items);
    }
}
