package cn.edu.finalproject.lostfound.protocol;

import cn.edu.finalproject.lostfound.model.LostItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 服务端返回给客户端的结果对象。 */
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String message;
    private final List<LostItem> items;
    private final Map<String, Integer> statistics;

    private Response(boolean success, String message, List<LostItem> items, Map<String, Integer> statistics) {
        this.success = success;
        this.message = message;
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
        this.statistics = statistics == null ? new LinkedHashMap<>() : new LinkedHashMap<>(statistics);
    }

    public static Response ok(String message) {
        return new Response(true, message, null, null);
    }

    public static Response items(String message, List<LostItem> items) {
        return new Response(true, message, items, null);
    }

    public static Response statistics(String message, Map<String, Integer> statistics) {
        return new Response(true, message, null, statistics);
    }

    public static Response error(String message) {
        return new Response(false, message, null, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<LostItem> getItems() {
        return new ArrayList<>(items);
    }

    public Map<String, Integer> getStatistics() {
        return new LinkedHashMap<>(statistics);
    }
}
