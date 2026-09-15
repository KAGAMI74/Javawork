# Git 使用说明（个人提交件）

**姓名：** 【姓名3】　**学号：** 【学号3】
**角色：** 命令分发与客户端模块（`command`、`client` 包）

> 用途：按大作业要求"每人根据自己的情况单独提交一份"。请把【】中的内容替换为真实信息，
> 并用第四节给出的命令导出自己的真实提交记录后填入第三节表格。

## 一、本人负责的范围

| 文件 | 说明 |
| --- | --- |
| `command/RemoteCommand.java` | 自定义运行期注解，标记可通过网络调用的业务方法 |
| `command/CommandDispatcher.java` | 反射扫描注解并按命令名分发，异常经 `InvocationTargetException` 还原 |
| `client/LostFoundClient.java` | Socket 短连接收发序列化对象 |
| `client/ClientMain.java` | CMD 菜单交互入口（8 项菜单 + 退出） |
| `README.md` | 项目说明、运行方式与命令清单 |

## 二、本人使用的分支

| 分支名 | 用途 | 说明 |
| --- | --- | --- |
| `feature-client` | 反射分发与命令行客户端开发 | 自测通过后合并回 `master` |
| `master` | 稳定版本 | 只接受已编译通过的合并 |

## 三、本人的提交记录

> 提交哈希与日期请用第四节命令导出后填写。

| # | 提交信息（建议格式：包名: 动作） | 涉及文件 | 提交哈希 | 日期 |
| --- | --- | --- | --- | --- |
| 1 | `command: 新增 @RemoteCommand 运行期注解` | RemoteCommand.java | 【填写】 | 【填写】 |
| 2 | `command: 反射扫描注解并实现命令分发` | CommandDispatcher.java | 【填写】 | 【填写】 |
| 3 | `client: 实现 Socket 短连接收发 Request/Response` | LostFoundClient.java | 【填写】 | 【填写】 |
| 4 | `client: 实现 CMD 菜单循环与发布、查询、认领等交互` | ClientMain.java | 【填写】 | 【填写】 |
| 5 | `client: 菜单新增查看帮助入口，与服务端 help 命令打通` | ClientMain.java | 【填写】 | 【填写】 |
| 6 | `docs: 更新 README（运行方式、命令清单、菜单说明）` | README.md | 【填写】 | 【填写】 |

## 四、如何导出自己的提交记录（在项目目录下打开 Git Bash 执行）

```bash
# 1. 查看本人所有提交（哈希 + 日期 + 说明）
git log --author="你的姓名或邮箱" --pretty=format:"%h %ad %s" --date=short

# 2. 查看本人每次提交改动的文件与增删行数
git log --author="你的姓名或邮箱" --stat

# 3. 查看本人对某个文件的全部改动记录
git log --author="你的姓名或邮箱" -p -- src/cn/edu/finalproject/lostfound/client/ClientMain.java
```

## 五、本人参与解决的问题

1. **客户端连不上服务端时程序崩溃**：初版直接抛出异常。解决：`request()` 内部捕获 `IOException`，返回带提示的 `Response.error(...)`，菜单可以继续使用。
2. **中文乱码**：控制台正常、重定向到文件后乱码（Windows 默认 GBK）。解决：运行参数加 `-Dstdout.encoding=UTF-8`。
3. **帮助命令无法触达**：服务端支持 `help` 但菜单没有入口。解决：菜单新增第 8 项"查看帮助"，与 `help` 命令打通。

## 六、与队友代码的配合

- 反射分发只依赖【姓名2】业务方法上的注解与 `Request` 参数，新增命令零改动网络层；
- 客户端与服务端共用 `protocol` 包的对象（【姓名1】定义），保证两端序列化结构一致。

## 七、Git 使用中的收获

- 用 `git show <hash>` 复查自己每次改动的实际内容，避免提交了调试代码；
- 提交前先用 `git status` 确认没有把 `out/`、`data/` 等生成物加进版本库。

**声明：** 以上内容真实，可通过远程仓库提交记录核验。　签名：__________　日期：__________
