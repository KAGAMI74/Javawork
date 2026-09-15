# 校园失物招领管理系统

本项目是一个基于 C/S 架构的 Java 命令行系统。服务端维护失物和招领记录；客户端通过 TCP Socket 发布、查询、认领、删除和统计信息。

## 技术点

- 集合：`ConcurrentHashMap`、`ArrayList`、`LinkedHashMap` 管理业务记录与请求参数。
- 反射：`CommandDispatcher` 扫描 `@RemoteCommand` 注解，动态路由网络命令。
- 序列化：`Request`、`Response` 和 `LostItem` 经对象流传输；数据快照保存为 `.dat` 文件。
- 网络编程：基于 TCP Socket 的客户端/服务端通信。
- 多线程：固定线程池并发处理客户端；定时线程周期性保存快照。
- 常用类：`String`（文本格式化与处理）、`Date`（发布时间与认领时间）、`Math`（`Math.round` 计算已认领率）。

项目刻意未依赖 Spring、SSH 等框架，也未要求额外的数据库驱动，克隆后即可运行。

## 运行方式

1. 在 IDEA 中先运行 `cn.edu.finalproject.lostfound.server.ServerMain`。
2. 再运行 `cn.edu.finalproject.lostfound.client.ClientMain`，按菜单操作（菜单第 8 项为查看帮助）。
3. 需要快速验证时，直接运行 `cn.edu.finalproject.lostfound.DemoMain`。

也可以直接用命令行编译运行：

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -Dstdout.encoding=UTF-8 -cp out cn.edu.finalproject.lostfound.server.ServerMain 9876
java -Dstdout.encoding=UTF-8 -cp out cn.edu.finalproject.lostfound.client.ClientMain 127.0.0.1 9876
java -Dstdout.encoding=UTF-8 -cp out cn.edu.finalproject.lostfound.DemoMain
```

默认端口为 `9876`（可通过启动参数 `args[0]` 覆盖，非法端口会回退到默认值）。服务端运行产生的数据在 `data/lost-found.dat`；一键演示的数据在 `data/demo-lost-found.dat`，每次运行前会自动清空以保证演示结果可复现。

## 初始数据

系统**首次运行**（`data/lost-found.dat` 不存在）时，会自动写入 5 条校园失物招领初始信息，启动后即可直接查看列表、做条件查询和统计，不必先手工录入：

| 编号 | 类型 | 物品 | 地点 |
| --- | --- | --- | --- |
| 1 | 招领 | 身份证 | 图书馆一楼服务台 |
| 2 | 寻物 | 饭卡（校园一卡通） | 第二食堂二楼 |
| 3 | 寻物 | 黑色双肩书包 | 第三教学楼 305 教室 |
| 4 | 招领 | 保温水杯 | 体育馆羽毛球场 3 号场 |
| 5 | 招领 | 蓝牙耳机 | 实验楼 B301 |

初始数据由 `service/SeedDataInitializer` 生成，发布时间会依次错开，更接近真实使用情况。数据文件一旦存在就不再重复写入，因此正常使用中删除记录后重启不会"复活"。

想恢复出厂状态：停止服务端，删除 `data/lost-found.dat`，再重新启动即可。

服务端控制台会输出命令审计日志，例如：

```
[14:02:28] 收到命令 publish，参数 {type=FOUND, title=黑色钱包, location=图书馆三楼, ...}
```

## 主要包说明

| 包 | 作用 |
| --- | --- |
| `model` | 失物记录及枚举实体 |
| `protocol` | 客户端与服务端传输对象 |
| `repository` | 集合存储和序列化快照 |
| `service` | 核心业务规则与首次运行的初始数据初始化 |
| `command` | 反射命令分发 |
| `server` | TCP 服务端与多线程处理 |
| `client` | TCP 客户端与 CMD 菜单 |

## 支持的命令

`publish`（发布）、`list`（查看全部）、`search`（条件查询）、`detail`（查看详情）、`claim`（认领）、`remove`（删除）、`stats`（统计，含已认领率与待领取招领物）、`help`（帮助）。

## 提交说明

`data/`、`out/`、`.idea/` 属于运行数据、编译输出与 IDE 配置，已被 `.gitignore` 排除，提交前无需保留。
