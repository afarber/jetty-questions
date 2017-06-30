package de.afarber.fcmnotregistered;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.ajax.JSON;

public class Main {
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String FCM_KEY = "key=REPLACE_BY_YOUR_KEY";
    private static final String FCM_RESULTS = "results";
    private static final String FCM_ERROR = "error";
    private static final String FCM_NOT_REGISTERED = "NotRegistered";
    private static final String NOTIFICATION = "{\n" +
"    \"to\" : \"APA91bHun4MxP5egoKMwt2KZFBaFUH-1RYqx...\",\n" +
"    \"notification\" : {\n" +
"      \"body\" : \"great match!\",\n" +
"      \"title\" : \"Portugal vs. Denmark\",\n" +
"      \"icon\" : \"myicon\"\n" +
"    },\n" +
"    \"data\" : {\n" +
"      \"Nick\" : \"Mario\",\n" +
"      \"Room\" : \"PortugalVSDenmark\"\n" +
"    }\n" +
"  }";

    private static final HttpClient sHttpClient = new HttpClient();
    private static final Response.ContentListener sFcmListener = new Response.ContentListener() {
        @Override
        public void onContent(Response response, ByteBuffer content) {
            if (response.getStatus() != 200) {
                return;
            }
            
            String body = StandardCharsets.UTF_8.decode(content).toString();
            System.out.printf("onContent: %s\n", body);
            
            Map resp = (Map) JSON.parse(body);
            if (resp == null) {
                System.err.println("onContent: can not parse");
                return;
            }

            Object[] results = (Object[]) resp.get("results");
            if (results == null || results.length != 1) {
                System.err.println("onContent: can not read results");
                return;
            }
            
            if (FCM_NOT_REGISTERED.equals(results[0])) {
                // TODO delete FCM token from the database
            }
        }
    };
    
    
    public static void main(String[] args) throws Exception {
        sHttpClient.start();
        sHttpClient.POST(FCM_URL)
            .header(HttpHeader.AUTHORIZATION, FCM_KEY)
            .header(HttpHeader.CONTENT_TYPE, "application/json")
            .content(new StringContentProvider(NOTIFICATION))
            .onResponseContent(sFcmListener)
            .send();
    }
}
