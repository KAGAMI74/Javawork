package cn.edu.finalproject.lostfound;

import cn.edu.finalproject.lostfound.client.ClientMain;
import cn.edu.finalproject.lostfound.client.LostFoundClient;
import cn.edu.finalproject.lostfound.protocol.Request;
import cn.edu.finalproject.lostfound.protocol.Response;
import cn.edu.finalproject.lostfound.server.LostFoundServer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 一键演示入口：在同一进程内启动服务端线程，再通过 TCP 客户端执行发布、查询、认领和统计。
 * 正式使用时，请分别运行 ServerMain 与 ClientMain。
 */
public class DemoMain {
    public static void main(String[] args) throws Exception {
        // 端口可通过启动参数覆盖，避免固定端口被占用时演示失败
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 9899;
        String demoDataFile = "data/demo-lost-found.dat";
        // 每次演示都从干净数据开始，保证编号从 1 递增、演示结果可复现
        Files.deleteIfExists(Path.of(demoDataFile));
        LostFoundServer server = new LostFoundServer(port, demoDataFile);
        Thread serverThread = new Thread(server::start, "lost-found-server-thread");
        serverThread.start();
        if (!server.awaitReady(3, TimeUnit.SECONDS)) {
            throw new IllegalStateException("演示服务启动超时");
        }

        LostFoundClient client = new LostFoundClient("127.0.0.1", port);
        Map<String, String> publish = new LinkedHashMap<>();
        publish.put("type", "FOUND");
        publish.put("title", "黑色校园卡");
        publish.put("location", "图书馆一楼自习区");
        publish.put("contact", "张同学 13800000000");
        Response published = client.request(new Request("publish", publish));
        ClientMain.printResponse(published);
        // 用发布返回的真实编号去认领，不再写死 "1"
        long publishedId = published.getItems().isEmpty() ? 1L : published.getItems().get(0).getId();
        ClientMain.printResponse(client.request(new Request("search", Map.of("keyword", "校园卡", "type", "", "status", "OPEN"))));
        ClientMain.printResponse(client.request(new Request("claim", Map.of("id", String.valueOf(publishedId), "claimer", "李同学"))));
        ClientMain.printResponse(client.request(new Request("stats", null)));
        // 顺手演示重复认领会被拒绝
        ClientMain.printResponse(client.request(new Request("claim", Map.of("id", String.valueOf(publishedId), "claimer", "王同学"))));

        server.close();
        serverThread.join(2000);
        System.out.println("演示结束，数据已保存至 " + demoDataFile + "。");
    }
}
