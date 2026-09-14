package cn.edu.finalproject.lostfound.client;

import cn.edu.finalproject.lostfound.protocol.Request;
import cn.edu.finalproject.lostfound.protocol.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/** TCP 客户端通信类。每次操作建立一次短连接，发送序列化的 Request 并接收 Response。 */
public class LostFoundClient {
    private final String host;
    private final int port;

    public LostFoundClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Response request(Request request) {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {
            output.flush();
            try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
                output.writeObject(request);
                output.flush();
                Object object = input.readObject();
                return object instanceof Response
                        ? (Response) object : Response.error("协议错误：服务端返回对象类型不正确");
            }
        } catch (IOException | ClassNotFoundException exception) {
            return Response.error("无法连接服务端（" + host + ":" + port + "）：" + exception.getMessage());
        }
    }
}
