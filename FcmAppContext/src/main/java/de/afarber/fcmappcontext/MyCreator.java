package de.afarber.fcmappcontext;

import static de.afarber.fcmappcontext.Utils.DATABASE_PASS;
import static de.afarber.fcmappcontext.Utils.DATABASE_URL;
import static de.afarber.fcmappcontext.Utils.DATABASE_USER;
import static de.afarber.fcmappcontext.Utils.FCM_ERROR;
import static de.afarber.fcmappcontext.Utils.FCM_INVALID_REGISTRATION;
import static de.afarber.fcmappcontext.Utils.FCM_MISSING_REGISTRATION;
import static de.afarber.fcmappcontext.Utils.FCM_NOT_REGISTERED;
import static de.afarber.fcmappcontext.Utils.FCM_RESULTS;
import static de.afarber.fcmappcontext.Utils.KEY_FCM;
import static de.afarber.fcmappcontext.Utils.KEY_UID;
import static de.afarber.fcmappcontext.Utils.LOG;
import static de.afarber.fcmappcontext.Utils.SQL_DELETE_FCM;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;
import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class MyCreator implements WebSocketCreator {
    private final HttpClient mHttpClient;
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

    MyCreator(HttpClient httpClient) {
        mHttpClient = httpClient;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        return null;
    }
    
}
