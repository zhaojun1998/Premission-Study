package im.zhaojun.session.listener;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.SessionListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * Shiro 会话监听器
 */
@Component
public class MySessionListener2 extends SessionListenerAdapter {

    private static final Logger logger = Logger.getLogger(MySessionListener2.class);

    @Override
    public void onStart(Session session) {
        logger.info("create session : " + session.getId());
    }

    @Override
    public void onStop(Session session) {
        logger.info("session stop : " + session.getId());
    }

    @Override
    public void onExpiration(Session session) {
        logger.info("session expiration : " + session.getId());
    }
}
