package cn.edu.finalproject.lostfound.repository;

import cn.edu.finalproject.lostfound.model.LostItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于 Java 序列化的本地持久化实现。
 * 文件不存在时返回空集合，首次运行不需要额外配置数据库或第三方驱动。
 */
public class FileSnapshotStore implements SnapshotStore {
    private final File file;

    public FileSnapshotStore(String path) {
        this.file = new File(path);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Map<Long, LostItem> load() {
        if (!file.exists()) {
            return new LinkedHashMap<>();
        }
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            Object value = input.readObject();
            if (value instanceof Map) {
                return new LinkedHashMap<>((Map<Long, LostItem>) value);
            }
        } catch (IOException | ClassNotFoundException exception) {
            System.err.println("读取历史数据失败，将使用空数据启动：" + exception.getMessage());
        }
        return new LinkedHashMap<>();
    }

    @Override
    public synchronized void save(Map<Long, LostItem> items) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("无法创建数据目录：" + parent.getAbsolutePath());
        }
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file))) {
            output.writeObject(new LinkedHashMap<>(items));
        } catch (IOException exception) {
            throw new IllegalStateException("保存数据失败：" + exception.getMessage(), exception);
        }
    }

    @Override
    public boolean exists() {
        return file.isFile();
    }
}
