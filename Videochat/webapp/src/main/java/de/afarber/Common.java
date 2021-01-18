package de.afarber;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public interface Common {
    String LOG_TAG                     = "videochat";
    Logger LOG                         = Log.getLogger(LOG_TAG);

    String TEXT_HTML                   = "text/html; charset=utf-8";
    String TEXT_PLAIN                  = "text/plain; charset=utf-8";
    String IMAGE_PNG                   = "image/png";
    String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    String APPLICATION_JSON            = "application/json";
    String APPLICATION_JSON_UTF8       = "application/json; charset=utf-8";
    String APPLICATION_URLENCODED      = "application/x-www-form-urlencoded";
    String PASSWORD                    = "password";

    Pattern GIVEN_SPACE_FAMILY         = Pattern.compile("^(\\p{IsAlphabetic}+) (\\p{IsAlphabetic}+)");
    Pattern FIRST_IP_ADDRESS           = Pattern.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");

    String LOADER_GIF                  = "/videochat/images/loader.gif";
    String PHOTO_HAPPY                 = "/videochat/images/female_happy.png";
    String PHOTO_SAD                   = "/videochat/images/female_sad.png";
    String PHOTO_ERROR_HAPPY           = "/videochat/images/male_happy.png";
    String PHOTO_ERROR_SAD             = "/videochat/images/male_sad.png";
    String DETAILS_OPEN                = "/videochat/images/details_open.png";
    String DETAILS_CLOSE               = "/videochat/images/details_close.png";

    String THUMB_UP                    = "&#x1f44d;";
    String THUMB_DOWN                  = "&#x1f44e;";

    public static boolean isEmpty(String str) {
        return (str == null || str.isEmpty());
    }

    public static boolean isValid(String str) {
        return (str != null && str.length() >= 32);
    }

    public static String md5(String str) {
        MessageDigest md;
        byte[] ba;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
        try {
            ba = md.digest(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
        StringBuilder sb = new StringBuilder(32);
        for (byte b: ba) {
            sb.append(String.format("%02x", b & 255));
        }
        return sb.toString();
    }

    // parse X-Forwarded-For header, which can be a list of comma-separated IP addresses
    public static String parseXff(HttpServletRequest httpReq) {
        String xff = httpReq.getHeader("X-Forwarded-For");
        if (xff != null) {
            Matcher matcher = FIRST_IP_ADDRESS.matcher(xff);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return "127.0.0.1";
    }
}
