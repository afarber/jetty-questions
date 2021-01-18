package de.afarber;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class VideochatServlet extends WebSocketServlet implements Common {

    private final static String TOP                  = "/top";

    private final static String TEST_PLAYER1         = "abcde";
    private final static String TEST_PLAYER2         = "ghijk";
    private final static String TEST_PLAYER3         = "klmnop";
    private final static String TEST_PLAYER4         = "xyzok";

    private final SslContextFactory mSslFactory = new SslContextFactory();
    private final HttpClient mHttpClient = new HttpClient(mSslFactory);
    private final Map<Integer, Session> mSessions = new ConcurrentHashMap<>();
    
    @Override
    public void init() throws ServletException {
        super.init();

        try {
            mHttpClient.start();
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    public void destroy() {
        try {
            mHttpClient.stop();
        } catch (Exception ex) {
            LOG.warn("destroy", ex);
        }

        mSessions.clear();
        super.destroy();
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(60 * 60 * 1000);
        factory.getPolicy().setMaxBinaryMessageSize(1);
        factory.getPolicy().setMaxTextMessageSize(64 * 1024);
        factory.register(VideochatListener.class);
    }

    @Override
    protected void doGet(HttpServletRequest httpReq, HttpServletResponse httpResp) throws ServletException, IOException {
        LOG.info("doGet: {} {}", httpReq.getServletPath(), httpReq.getParameterMap());
        switch (httpReq.getServletPath()) {
            case TOP: {
                handleTop(httpResp);
                break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest httpReq, HttpServletResponse httpResp) throws ServletException, IOException {
        LOG.info("doPost: {} {}", httpReq.getServletPath(), httpReq.getParameterMap());
        switch (httpReq.getServletPath()) {
            case TOP: {
                handleTop(httpResp);
                break;
            }
        }
    }

    private void handleTop(HttpServletResponse httpResp) throws ServletException, IOException {
        String jsonStr = "{\"data\": []}";
 
        httpResp.setStatus(HttpServletResponse.SC_OK);
        httpResp.setContentType(APPLICATION_JSON_UTF8);
        httpResp.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        httpResp.getWriter().println(jsonStr);
    }
}
