package de.afarber.fcmappcontext;

import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class MyServlet extends WebSocketServlet implements Utils {
    private final SslContextFactory mSslFactory = new SslContextFactory();
    private final HttpClient mHttpClient = new HttpClient(mSslFactory);
    private final BufferingResponseListener mHttpListener = new BufferingResponseListener() {
        @Override
        public void onComplete(Result result) {
            if (!result.isSucceeded()) {
                LOG.warn("onComplete failure: {}", result.getFailure());
                return;
            }

            String body = getContentAsString(StandardCharsets.UTF_8);
            LOG.info("onComplete success: {}", body);

            try {
                Map<String, Object> resp = (Map<String, Object>) JSON.parse(body);
                Object[] results = (Object[]) resp.get(FCM_RESULTS);
                Map<String, Object> map = (Map<String, Object>) results[0];
                String error = (String) map.get(FCM_ERROR);
                // if status code 200 and one of the 3 errors were returned
                if (FCM_NOT_REGISTERED.equals(error) ||
                    FCM_MISSING_REGISTRATION.equals(error) ||
                    FCM_INVALID_REGISTRATION.equals(error)) {
                    // then delete the invalid FCM token from the database
                    int uid = (Integer) result.getRequest().getAttributes().get(KEY_UID);
                    String fcm = (String) result.getRequest().getAttributes().get(KEY_FCM);

                    try (Connection db = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
                            PreparedStatement st = db.prepareStatement(SQL_DELETE_FCM)) {
                        st.setInt(1, uid);
                        st.setString(2, fcm);
                        st.executeQuery();
                    }
                }
            } catch (Exception ex) {
                LOG.warn("onComplete exception: {}", ex);
            }
        }
    };

    @Override
    public void init() throws ServletException {
        super.init();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            throw new ServletException(ex);
        }

        try {
            mHttpClient.start();
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
        
        ServletContext ctx = getServletContext();
        ctx.setAttribute("http_client", mHttpClient);
    }

    @Override
    public void destroy() {
        try {
            mHttpClient.stop();
        } catch (Exception ignore) {
        }

        super.destroy();
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(MyListener.class);
    }

/*
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse httpResp) throws ServletException, IOException {
        try (Connection db = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
                PreparedStatement st = db.prepareStatement(SQL_UNBAN_USER)) {
            st.setInt(1, uid);
            st.executeQuery();
        }
    }
*/

}
