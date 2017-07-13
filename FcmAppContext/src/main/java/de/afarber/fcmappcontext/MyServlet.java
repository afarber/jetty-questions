package de.afarber.fcmappcontext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class MyServlet extends WebSocketServlet implements Utils {
    private final SslContextFactory mSslFactory = new SslContextFactory();
    private final HttpClient mHttpClient = new HttpClient(mSslFactory);

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
        ctx.setAttribute(KEY_HTTP_CLIENT, mHttpClient);
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
        factory.setCreator(new MyCreator(mHttpClient));
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
