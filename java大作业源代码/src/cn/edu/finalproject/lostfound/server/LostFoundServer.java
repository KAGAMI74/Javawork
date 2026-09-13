package cn.edu.finalproject.lostfound.server;

import cn.edu.finalproject.lostfound.command.CommandDispatcher;
import cn.edu.finalproject.lostfound.protocol.Request;
import cn.edu.finalproject.lostfound.protocol.Response;
import cn.edu.finalproject.lostfound.repository.FileSnapshotStore;
import cn.edu.finalproject.lostfound.service.LostFoundService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** TCP 服务端：工作线程处理客户端请求，定时线程保存数据快照。 */
public class LostFoundServer implements AutoCloseable {
    /** 服务端命令审计日志的时间格式（线程安全）。 */
    private static final DateTimeFormatter LOG_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final int port;
    private final LostFoundService service;
    private final CommandDispatcher dispatcher;
    private final ExecutorService clientWorkers = Executors.newFixedThreadPool(4);
    private final ScheduledExecutorService autoSaveWorker = Executors.newSingleThreadScheduledExecutor();
    private final CountDownLatch ready = new CountDownLatch(1);
    private volatile boolean running;
    private ServerSocket serverSocket;

    public LostFoundServer(int port, String dataFile) {
        this.port = port;
        this.service = new LostFoundService(new FileSnapshotStore(dataFile));
        this.dispatcher = new CommandDispatcher(service);
    }

    public void start() {
        try (ServerSocket socket = new ServerSocket(port)) {
            this.serverSocket = socket;
            this.running = true;
            ready.countDown();
            autoSaveWorker.scheduleAtFixedRate(service::saveNow, 45, 45, TimeUnit.SECONDS);
            System.out.println("校园失物招领服务已启动，监听端口：" + port);
            while (running) {
                try {
                    Socket client = socket.accept();
                    clientWorkers.execute(() -> handleClient(client));
                } catch (SocketException exception) {
                    if (running) {
                        System.err.println("接收客户端连接失败：" + exception.getMessage());
                    }
                }
            }
        } catch (IOException exception) {
            System.err.println("服务启动失败：" + exception.getMessage());
        } finally {
            ready.countDown();
            shutdownWorkers();
        }
    }

    private void handleClient(Socket client) {
        try (Socket ignored = client;
             ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream())) {
            output.flush();
            try (ObjectInputStream input = new ObjectInputStream(client.getInputStream())) {
                Object object = input.readObject();
                if (object instanceof Request) {
                    Request request = (Request) object;
                    // 服务端审计日志：记录每个客户端命令及其参数，便于演示与问题排查
                    System.out.println("[" + LocalTime.now().format(LOG_TIME) + "] 收到命令 "
                            + request.getCommand() + "，参数 " + request.getParameters());
                    output.writeObject(dispatcher.dispatch(request));
                } else {
                    output.writeObject(Response.error("协议错误：请求对象类型不正确"));
                }
                output.flush();
            }
        } catch (IOException | ClassNotFoundException exception) {
            System.err.println("处理客户端请求失败：" + exception.getMessage());
        }
    }

    public boolean awaitReady(long timeout, TimeUnit unit) throws InterruptedException {
        return ready.await(timeout, unit);
    }

    @Override
    public void close() {
        running = false;
        service.saveNow();
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException exception) {
                System.err.println("关闭服务端口失败：" + exception.getMessage());
            }
        }
        shutdownWorkers();
    }

    private void shutdownWorkers() {
        clientWorkers.shutdown();
        autoSaveWorker.shutdown();
    }
}
