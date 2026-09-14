package cn.edu.finalproject.lostfound.command;

import cn.edu.finalproject.lostfound.protocol.Request;
import cn.edu.finalproject.lostfound.protocol.Response;
import cn.edu.finalproject.lostfound.service.LostFoundService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 通过反射扫描 @RemoteCommand 注解，并按命令名调用相应的业务方法。
 * 新增命令时只需在服务方法上添加注解，无需修改网络层的 if-else 分支。
 */
public class CommandDispatcher {
    private final LostFoundService service;
    private final Map<String, Method> commands = new HashMap<>();

    public CommandDispatcher(LostFoundService service) {
        this.service = service;
        for (Method method : LostFoundService.class.getDeclaredMethods()) {
            RemoteCommand annotation = method.getAnnotation(RemoteCommand.class);
            if (annotation != null && method.getParameterCount() == 1
                    && method.getParameterTypes()[0] == Request.class) {
                commands.put(annotation.value().toLowerCase(), method);
            }
        }
    }

    public Response dispatch(Request request) {
        Method method = commands.get(request.getCommand());
        if (method == null) {
            return Response.error("未知命令：" + request.getCommand() + "。可输入 help 查看支持的命令。");
        }
        try {
            return (Response) method.invoke(service, request);
        } catch (IllegalAccessException exception) {
            return Response.error("命令不可访问：" + exception.getMessage());
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            return Response.error(cause == null ? "命令执行失败" : cause.getMessage());
        }
    }
}
