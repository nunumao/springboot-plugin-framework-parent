package com.gitee.starblues.factory.process.post.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.classs.group.WebSocketGroup;
import com.gitee.starblues.factory.process.post.PluginPostProcessor;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.servlet.ServletContext;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

import com.gitee.starblues.utils.ClassUtils;
import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 插件中websocket处理者
 *
 * @author sousouki
 * @version 2.4.2
 */
public class PluginWebSocketProcessor implements PluginPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(PluginWebSocketProcessor.class);

    public static final String KEY = "PluginWsConfigProcessor";

    private final ApplicationContext applicationContext;

    public PluginWebSocketProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        ServerContainer serverContainer = getServerContainer();
        if (serverContainer == null) return;
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            List<Class<?>> websocketClasses = pluginRegistryInfo.getGroupClasses(WebSocketGroup.GROUP_ID);
            String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
            websocketClasses.forEach(websocketClass -> {
                ServerEndpoint serverEndpoint = websocketClass.getDeclaredAnnotation(ServerEndpoint.class);
                if (serverEndpoint == null) {
                    log.warn("WebSocket class {} doesn't has annotation {}", websocketClass.getName(), ServerEndpoint.class.getName());
                    return;
                }
                String value = serverEndpoint.value();
                if (StringUtils.isNullOrEmpty(value)) {
                    return;
                }
                if(!value.startsWith("/")){
                    value = "/".concat(value);
                }
                String newWebsocketPath = "/".concat(pluginId).concat(value);
                try {
                    Map<String, Object> annotationsUpdater = ClassUtils.getAnnotationsUpdater(serverEndpoint);
                    annotationsUpdater.put("value", newWebsocketPath);
                    serverContainer.addEndpoint(websocketClass);
                    pluginRegistryInfo.addWebsocketPath(newWebsocketPath);
                    log.info("Succeed to create websocket service for path {}", newWebsocketPath);
                } catch (Exception e) {
                    log.error("Create websocket service for websocket class " + websocketClass.getName() + " failed.", e);
                }
            });
        }
    }

    @Override
    public void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        ServerContainer serverContainer = getServerContainer();
        if (serverContainer == null) {
            log.warn("Not found ServerContainer, So websocket can't used!");
            return;
        }
        Map<String, Object> configExactMatchMap = ClassUtils.getReflectionField(serverContainer, "configExactMatchMap");
        Map<Integer, ConcurrentSkipListMap<String, Object>> configTemplateMatchMap =
                ClassUtils.getReflectionField(serverContainer, "configTemplateMatchMap");
        Map<String, Object> endpointSessionMap = ClassUtils.getReflectionField(serverContainer, "endpointSessionMap");
        Map<Session, Session> sessions = ClassUtils.getReflectionField(serverContainer, "sessions");

        pluginRegistryInfos.forEach(pluginRegistryInfo -> {
            List<String> websocketPaths = pluginRegistryInfo.getWebsocketPaths();
            websocketPaths.forEach(websocketPath -> {
                configExactMatchMap.remove(websocketPath);
                log.debug("Removed websocket config for path {}", websocketPath);
                configTemplateMatchMap.forEach((key, value) -> {
                    value.remove(websocketPath);
                });
                endpointSessionMap.remove(websocketPath);
                log.debug("Removed websocket session for path {}", websocketPath);

                for (Map.Entry<Session, Session> entry : sessions.entrySet()) {
                    Session session = entry.getKey();
                    try {
                        if(closeSession(session, websocketPath)){
                            sessions.remove(session);
                            log.debug("Removed websocket session {} for path {}", session.getId(), websocketPath);
                        }
                    } catch (Exception e) {
                        log.debug("Close websocket session {} for path {} failure", session.getId(), websocketPath, e);
                    }
                }
                log.info("Remove websocket for path {} success.", websocketPath);
            });
        });
    }

    /**
     * 得到 Tomcat ServerContainer
     * @return ServerContainer
     */
    private ServerContainer getServerContainer() {
        try {
            applicationContext.getBean(ServerEndpointExporter.class);
        } catch (BeansException e) {
            log.debug("The required bean of {} not found, if you want to use plugin websocket, please create it.", ServerEndpointExporter.class.getName());
            return null;
        }
        if (!(applicationContext instanceof WebApplicationContext)) {
            return null;
        }
        WebApplicationContext webApplicationContext = (WebApplicationContext) applicationContext;
        ServletContext servletContext = webApplicationContext.getServletContext();
        if (servletContext == null) {
            log.warn("Servlet context is null.");
            return null;
        }
        Object obj = servletContext.getAttribute("javax.websocket.server.ServerContainer");
        if (!(obj instanceof ServerContainer)) {
            return null;
        }
        return (ServerContainer) obj;
    }

    /**
     * 关闭session
     * @param session session
     * @param websocketPath websocketPath 路径
     * @return 如果需要关闭并且关闭成功, 则返回true。 否则返回false
     * @throws Exception 关闭异常
     */
    private boolean closeSession(Session session, String websocketPath) throws Exception{
        EndpointConfig endpointConfig = ClassUtils.getReflectionField(session, "endpointConfig");
        ServerEndpointConfig perEndpointConfig = ClassUtils.getReflectionField(endpointConfig, "perEndpointConfig");
        String path = ClassUtils.getReflectionField(perEndpointConfig, "path");
        if (path.equals(websocketPath)) {
            session.close();
            log.debug("Closed websocket session {} for path {}", session.getId(), websocketPath);
            return true;
        }
        return false;
    }


}
