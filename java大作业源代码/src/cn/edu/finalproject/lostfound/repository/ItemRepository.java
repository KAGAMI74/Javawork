package cn.edu.finalproject.lostfound.repository;

import cn.edu.finalproject.lostfound.model.LostItem;

import java.util.List;
import java.util.Map;

/** 内存集合的数据访问接口。 */
public interface ItemRepository {
    long nextId();

    void add(LostItem item);

    LostItem find(long id);

    LostItem remove(long id);

    List<LostItem> findAll();

    List<LostItem> search(String keyword, String type, String status);

    Map<Long, LostItem> snapshot();
}
