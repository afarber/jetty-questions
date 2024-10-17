package my.servlet;

import static j2html.tags.InlineStaticResource.getFileAsString;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.server.JettyWebSocketServlet;
import org.eclipse.jetty.websocket.server.JettyWebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyServlet extends JettyWebSocketServlet {
    private static Logger LOG = LoggerFactory.getLogger(MyServlet.class);
    private final static Pattern SLASH_COUNTRY_CODE = Pattern.compile("^/(?<country>de|en)");
    private final static String INDEX = "";

    @Override
    public void configure(JettyWebSocketServletFactory factory) {
        factory.setIdleTimeout(Duration.ofSeconds(300));
        factory.setMaxBinaryMessageSize(0);
        factory.setMaxTextMessageSize(64 * 1024);
        factory.setCreator(new MyCreator(this));
    }

    @Override
    public void init() throws ServletException {
        super.init();

        // try to extract the country code from the servlet context path
        String contextPath = getServletContext().getContextPath();
        Matcher matcher = SLASH_COUNTRY_CODE.matcher(contextPath);
        if (!matcher.find()) {
            throw new ServletException("Invalid context path: " + contextPath);
        }

        String language = matcher.group("country");
        LOG.info("init language: {}", language);
    }

    @Override
    public void destroy() {
        Client.clear();
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest httpReq, HttpServletResponse httpResp)
            throws ServletException, IOException {
        LOG.info("doGet: {} {} {}", httpReq.getRequestURI(), httpReq.getServletPath(), httpReq.getParameterMap());
        // remove any trailing slashes
        String path = httpReq.getServletPath().replaceAll("/+$", "");
        switch (path) {
            case INDEX: {
                httpResp.setStatus(HttpServletResponse.SC_OK);
                httpResp.setContentType("text/html;charset=UTF-8");
                httpResp.getWriter().println(getFileAsString("/index.html"));
                break;
            }
            default: {
                LOG.warn("doGet path no found: " + path);
                // redirect to the front page
                httpResp.sendRedirect("/");
            }
        }
    }
}
