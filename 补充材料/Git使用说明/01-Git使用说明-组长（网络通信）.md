# Git 使用说明（个人提交件）

**姓名：** 【】　**学号：** 20241113191
**角色：** 组长，负责系统总体设计与网络通信模块（`server`、`protocol` 包）

> 用途：按大作业要求"每人根据自己的情况单独提交一份"。请把【】中的内容替换为真实信息，
> 并用第四节给出的命令导出自己的真实提交记录后填入第三节表格。

## 一、本人负责的范围

| 文件 | 说明 |
| --- | --- |
| `protocol/Request.java` | 客户端命令对象：命令名 + 参数表，实现序列化 |
| `protocol/Response.java` | 服务端结果对象：消息 + 记录列表 + 统计表 |
| `server/LostFoundServer.java` | ServerSocket 监听、线程池处理连接、定时快照、命令审计日志 |
| `server/ServerMain.java` | 服务端入口与端口参数校验 |
| `DemoMain.java` | 一键演示主流程 |

## 二、本人使用的分支

| 分支名 | 用途 | 说明 |
| --- | --- | --- |
| `feature-server` | 服务端与协议开发 | 在自测通过后合并回 `master` |
| `master` | 稳定版本 | 只接受已编译通过的合并 |

## 三、本人的提交记录

> 提交哈希与日期请用第四节命令导出后填写，不要凭记忆填写。

| # | 提交信息（建议格式：包名: 动作） | 涉及文件 | 提交哈希 | 日期 |
| --- | --- | --- | --- | --- |
| 1 | `protocol: 新增 Request/Response 序列化传输对象` | protocol/*.java | 【填写】 | 【填写】 |
| 2 | `server: 实现 ServerSocket 监听与固定线程池处理客户端` | server/LostFoundServer.java | 【填写】 | 【填写】 |
| 3 | `server: 增加每 45 秒定时快照与优雅关闭` | server/LostFoundServer.java | 【填写】 | 【填写】 |
| 4 | `fix: 对象输出流建立后立即 flush，解决双方互相等待的死锁` | server/、client/ | 【填写】 | 【填写】 |
| 5 | `server: 新增命令审计日志，打印收到的命令与参数` | server/LostFoundServer.java | 【填写】 | 【填写】 |
| 6 | `server: 端口参数合法性校验，非法输入回退默认端口` | server/ServerMain.java | 【填写】 | 【填写】 |
| 7 | `demo: 新增一键演示入口 DemoMain` | DemoMain.java | 【填写】 | 【填写】 |

## 四、如何导出自己的提交记录（在项目目录下打开 Git Bash 执行）

```bash
# 1. 查看本人所有提交（哈希 + 日期 + 说明）
git log --author="你的姓名或邮箱" --pretty=format:"%h %ad %s" --date=short

# 2. 查看本人每次提交改动的文件与增删行数
git log --author="你的姓名或邮箱" --stat

# 3. 查看全组提交次数排名（用于报告中的 Git 使用情况）
git shortlog -sn --all

# 4. 查看分支图（报告里附这张截图最直观）
git log --oneline --graph --all --decorate
```

把第 4 条命令的输出截图插入报告"五、Git 版本管理使用情况"的截图位置。

## 五、本人参与解决的问题

1. **对象流死锁**：客户端与服务端都先创建 `ObjectInputStream` 等待对方发送流头，导致双方互相阻塞。解决：建立 `ObjectOutputStream` 后立即 `flush()` 再创建输入流。
2. **并发连接处理**：单个连接阻塞会导致其他客户端无法接入。解决：`Executors.newFixedThreadPool(4)` 交给线程池处理。
3. **数据持久化时机**：只在退出时保存会丢失数据。解决：写操作成功后立即保存 + 定时线程每 45 秒兜底快照。

## 六、与队友代码的配合

- 与林明智对齐了 `LostFoundService` 的方法签名，网络层通过董佳滢实现的 `CommandDispatcher` 反射调用业务方法，新增命令无需改动 `LostFoundServer`。
- 与董佳滢共同定义了通信约定：一次连接一次问答（短连接），请求与响应都是可序列化对象。

## 七、Git 使用中的收获

- 用分支隔离各自模块，避免直接改 `master` 造成互相覆盖；
- 合并前先 `git pull`，冲突时用 `git diff` 逐处确认，由模块负责人决定保留哪一版；
- 提交信息写清"包名 + 动作"，回溯问题时能快速定位。

**声明：** 以上内容真实，可通过远程仓库提交记录核验。　签名：__________　日期：__________
