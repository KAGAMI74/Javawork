package cn.edu.finalproject.lostfound.protocol;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/** 客户端发送给服务端的命令对象。 */
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String command;
    private final Map<String, String> parameters;

    public Request(String command, Map<String, String> parameters) {
        this.command = command == null ? "" : command.trim().toLowerCase();
        this.parameters = parameters == null ? new LinkedHashMap<>() : new LinkedHashMap<>(parameters);
    }

    public String getCommand() {
        return command;
    }

    public String get(String key) {
        return parameters.get(key);
    }

    public Map<String, String> getParameters() {
        return new LinkedHashMap<>(parameters);
    }
}
