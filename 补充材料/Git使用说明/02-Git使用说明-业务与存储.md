# Git 使用说明（个人提交件）

**姓名：** 【姓名2】　**学号：** 【学号2】
**角色：** 业务逻辑与数据存储模块（`service`、`repository` 包）

> 用途：按大作业要求"每人根据自己的情况单独提交一份"。请把【】中的内容替换为真实信息，
> 并用第四节给出的命令导出自己的真实提交记录后填入第三节表格。

## 一、本人负责的范围

| 文件 | 说明 |
| --- | --- |
| `service/LostFoundService.java` | 8 个业务方法：发布、查看、查询、详情、认领、删除、统计、帮助 |
| `repository/ItemRepository.java` | 数据访问接口（面向接口编程） |
| `repository/InMemoryItemRepository.java` | `ConcurrentHashMap` 线程安全仓库 + `AtomicLong` 编号 |
| `repository/SnapshotStore.java` | 快照存储接口 |
| `repository/FileSnapshotStore.java` | 对象流序列化读写 `data/lost-found.dat` |

## 二、本人使用的分支

| 分支名 | 用途 | 说明 |
| --- | --- | --- |
| `feature-service` | 业务规则与持久化开发 | 自测通过后合并回 `master` |
| `master` | 稳定版本 | 只接受已编译通过的合并 |

## 三、本人的提交记录

> 提交哈希与日期请用第四节命令导出后填写。

| # | 提交信息（建议格式：包名: 动作） | 涉及文件 | 提交哈希 | 日期 |
| --- | --- | --- | --- | --- |
| 1 | `repository: 定义 ItemRepository 与 SnapshotStore 接口` | repository/*.java | 【填写】 | 【填写】 |
| 2 | `repository: 使用 ConcurrentHashMap 实现线程安全仓库` | InMemoryItemRepository.java | 【填写】 | 【填写】 |
| 3 | `repository: 实现对象序列化快照的读写与目录创建` | FileSnapshotStore.java | 【填写】 | 【填写】 |
| 4 | `service: 实现发布、查询、认领、删除等业务方法` | LostFoundService.java | 【填写】 | 【填写】 |
| 5 | `service: 认领前校验状态，拒绝重复认领` | LostFoundService.java、LostItem.java | 【填写】 | 【填写】 |
| 6 | `service: 统计改为一次遍历，并新增已认领率等指标` | LostFoundService.java | 【填写】 | 【填写】 |

## 四、如何导出自己的提交记录（在项目目录下打开 Git Bash 执行）

```bash
# 1. 查看本人所有提交（哈希 + 日期 + 说明）
git log --author="你的姓名或邮箱" --pretty=format:"%h %ad %s" --date=short

# 2. 查看本人每次提交改动的文件与增删行数
git log --author="你的姓名或邮箱" --stat

# 3. 查看本人改动了哪些文件（按次数统计）
git log --author="你的姓名或邮箱" --name-only --pretty=format: | sort | uniq -c | sort -rn
```

## 五、本人参与解决的问题

1. **并发发布编号重复**：初版用普通 `long` 自增，多客户端同时发布会产生重复编号。解决：改用 `AtomicLong.getAndIncrement()`。
2. **重启后数据丢失**：只用内存集合时服务端一停数据就没了。解决：写操作后调用 `saveNow()` 序列化整个 `LinkedHashMap` 到 `.dat` 文件，启动时反序列化恢复，并用最大编号初始化 `AtomicLong` 保证编号不重复。
3. **统计性能**：初版为每个统计维度各做一次全表查询（4 次遍历）。解决：改为一次遍历累加全部维度，并用 `Math.round` 计算已认领率。

## 六、与队友代码的配合

- 业务方法通过 `@RemoteCommand` 注解暴露给【姓名3】实现的反射分发器，我改业务实现不需要动网络层。
- 仓库与快照都先定义接口再实现，【姓名1】的服务端只依赖接口，替换存储实现时互不影响。

## 七、Git 使用中的收获

- 把"接口先行"也用在 Git 上：先提交接口，再提交实现，方便队友并行开发；
- 修 bug 单独提交并写清 `fix:` 前缀，便于回溯。

**声明：** 以上内容真实，可通过远程仓库提交记录核验。　签名：__________　日期：__________
