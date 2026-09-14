package cn.edu.finalproject.lostfound.client;

import cn.edu.finalproject.lostfound.model.LostItem;
import cn.edu.finalproject.lostfound.protocol.Request;
import cn.edu.finalproject.lostfound.protocol.Response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/** 命令行客户端入口，适合在 IDEA 的第二个运行窗口中启动。 */
public class ClientMain {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 9876;
        LostFoundClient client = new LostFoundClient(host, port);
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = ask(scanner, "请选择：");
                switch (choice) {
                    case "1":
                        publish(client, scanner);
                        break;
                    case "2":
                        printResponse(client.request(new Request("list", null)));
                        break;
                    case "3":
                        search(client, scanner);
                        break;
                    case "4":
                        detail(client, scanner);
                        break;
                    case "5":
                        claim(client, scanner);
                        break;
                    case "6":
                        remove(client, scanner);
                        break;
                    case "7":
                        printResponse(client.request(new Request("stats", null)));
                        break;
                    case "8":
                        printResponse(client.request(new Request("help", null)));
                        break;
                    case "0":
                        running = false;
                        break;
                    default:
                        System.out.println("无效选择，请重新输入。");
                }
            }
        }
        System.out.println("客户端已退出。");
    }

    private static void publish(LostFoundClient client, Scanner scanner) {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("type", ask(scanner, "类型（LOST=寻物，FOUND=招领）："));
        parameters.put("title", ask(scanner, "物品名称："));
        parameters.put("location", ask(scanner, "拾到/丢失地点："));
        parameters.put("contact", ask(scanner, "联系方式："));
        printResponse(client.request(new Request("publish", parameters)));
    }

    private static void search(LostFoundClient client, Scanner scanner) {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("keyword", ask(scanner, "关键词（可留空）："));
        parameters.put("type", ask(scanner, "类型 LOST/FOUND（可留空）："));
        parameters.put("status", ask(scanner, "状态 OPEN/CLAIMED（可留空）："));
        printResponse(client.request(new Request("search", parameters)));
    }

    private static void detail(LostFoundClient client, Scanner scanner) {
        printResponse(client.request(new Request("detail", Map.of("id", ask(scanner, "记录编号：")))));
    }

    private static void claim(LostFoundClient client, Scanner scanner) {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("id", ask(scanner, "记录编号："));
        parameters.put("claimer", ask(scanner, "认领人姓名："));
        printResponse(client.request(new Request("claim", parameters)));
    }

    private static void remove(LostFoundClient client, Scanner scanner) {
        printResponse(client.request(new Request("remove", Map.of("id", ask(scanner, "记录编号：")))));
    }

    private static String ask(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static void printMenu() {
        System.out.println("\n========== 校园失物招领管理系统 ==========");
        System.out.println("1. 发布寻物/招领信息   2. 查看全部信息");
        System.out.println("3. 条件查询            4. 查看详细信息");
        System.out.println("5. 办理认领            6. 删除信息");
        System.out.println("7. 查看统计            8. 查看帮助");
        System.out.println("0. 退出客户端");
    }

    public static void printResponse(Response response) {
        System.out.println((response.isSuccess() ? "[成功] " : "[失败] ") + response.getMessage());
        for (LostItem item : response.getItems()) {
            System.out.println(item.toDisplayText());
        }
        for (Map.Entry<String, Integer> entry : response.getStatistics().entrySet()) {
            System.out.println(entry.getKey() + "：" + entry.getValue());
        }
    }
}
