package cn.edu.finalproject.lostfound.server;

/** 服务端程序入口。 */
public class ServerMain {
    /** 默认监听端口。 */
    public static final int DEFAULT_PORT = 9876;

    public static void main(String[] args) {
        int port = args.length > 0 ? parsePort(args[0]) : DEFAULT_PORT;
        LostFoundServer server = new LostFoundServer(port, "data/lost-found.dat");
        Runtime.getRuntime().addShutdownHook(new Thread(server::close));
        server.start();
    }

    /** 端口参数校验：非法输入时回退到默认端口，而不是抛异常中断启动。 */
    private static int parsePort(String value) {
        try {
            int port = Integer.parseInt(value.trim());
            if (port < 1 || port > 65535) {
                throw new NumberFormatException("端口超出范围");
            }
            return port;
        } catch (NumberFormatException exception) {
            System.err.println("端口参数非法：" + value + "，已改用默认端口 " + DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }
}
