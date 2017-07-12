package de.afarber.fcmappcontext;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public interface Utils {
    public static final String FCM_URL                  = "https://fcm.googleapis.com/fcm/send";
    public static final String FCM_KEY                  = "key=REPLACE_BY_YOUR_KEY";
    public static final String FCM_RESULTS              = "results";
    public static final String FCM_ERROR                = "error";
    public static final String FCM_NOT_REGISTERED       = "NotRegistered";
    public static final String FCM_MISSING_REGISTRATION = "MissingRegistration";
    public static final String FCM_INVALID_REGISTRATION = "InvalidRegistration";

    public static final String DATABASE_URL             = "jdbc:postgresql://127.0.0.1:6432/test";
    public static final String DATABASE_USER            = "test";
    public static final String DATABASE_PASS            = "test";

    public static final String KEY_UID                  = "uid";
    public static final String KEY_FCM                  = "fcm";
    
    // delete invalid FCM token from the database
    public static final String SQL_DELETE_FCM           =
            "UPDATE users " +
            "SET fcm = NULL " +
            "WHERE uid = ?::int " +
            "AND fcm = ?::text";

    public static final Logger LOG                      = Log.getLogger("FcmAppConntext");
}
