package net.solmis.accountbalance;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

class HttpsRequestService {

    private CookieManager cookieManager;
    private String currentUrl;

    HttpsRequestService() {
        this.cookieManager = new CookieManager();
        CookieHandler.setDefault(this.cookieManager);
        this.currentUrl = "";
    }

    String sendGetRequest(String url) {
        try {
            URL urlObj = new URL(url);
            HttpsURLConnection urlConn = (HttpsURLConnection) urlObj.openConnection();
            urlConn.getResponseCode(); // submit request
            this.currentUrl = urlConn.getURL().toString();
            return getPageContent(urlConn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    String sendPostRequest(String url, String data) {
        try {
            URL urlObj = new URL(url);
            HttpsURLConnection urlConn = (HttpsURLConnection) urlObj.openConnection();

            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConn.setRequestProperty("Content-Length", Integer.toString(data.length()));

            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);

            DataOutputStream wr = new DataOutputStream(urlConn.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();

            urlConn.getResponseCode(); // submit request
            this.currentUrl = urlConn.getURL().toString();

            return getPageContent(urlConn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    String getCurrentUrl() {
        return this.currentUrl;
    }

    private String getPageContent(HttpsURLConnection urlConn) {
        if (urlConn == null)
            return "";
        BufferedReader in;
        StringBuilder sb = new StringBuilder();
        try {
            in = new BufferedReader(new InputStreamReader(
                    urlConn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                sb.append(inputLine);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
