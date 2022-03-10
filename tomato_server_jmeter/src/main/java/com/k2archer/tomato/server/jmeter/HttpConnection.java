package com.k2archer.tomato.server.jmeter;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import demo.LoginInfo;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.samplers.SampleResult;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HttpConnection {
    public static String doPost(Map<String, Object> map) {
        OutputStream os = null;
        BufferedReader in = null;
        HttpURLConnection conn = null;
        URL url = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
//            conn.addRequestProperty("key", "value");
            conn.connect();

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            LoginInfo info = new LoginInfo();
            info.setUsername("kwei");

            String body = JSON.toJSONString(info);
//            new Gson().toJson(info);
            writer.write(body);
            writer.flush();

            conn.getResponseMessage();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (in != null) {
                    in.close();
                    conn.disconnect();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return "";
    }

    public static String post(String url, Map<String, String> header, String body) {
        HttpURLConnection conn = null;
        String response = "";

        try {
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");


            // requestHeader
            for (Map.Entry<String, String> entry : header.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            Map<String, List<String>> requestProperties = conn.getRequestProperties();
            // connect
            conn.connect();

            // write body
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(body);
            writer.flush();

            // read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\r\n");
            }
            bufferedReader.close();

            Map<String, List<String>> headers = conn.getHeaderFields();
            conn.disconnect();

            response = stringBuilder.toString();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String doPost(String path, String body, SampleResult result) {
        HttpURLConnection conn = null;
        URL url = null;
        String message = "";

        try {
            url = new URL((String) path);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            Map<String, List<String>> requestProperties = conn.getRequestProperties();
            StringBuilder requestHeaderBuilder = new StringBuilder();
            for (Map.Entry<String, List<String>> stringListEntry : requestProperties.entrySet()) {
                requestHeaderBuilder.append(stringListEntry.getKey()).append(": ").append(stringListEntry.getValue()).append("\n\n");
            }
            result.setRequestHeaders(requestHeaderBuilder.toString());

            result.setSamplerData(body);

            conn.connect();

            //
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(body);
            writer.flush();

//            StandardCharsets.UTF_8
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\r\n");
            }
            message = stringBuilder.toString();
            bufferedReader.close();

            Map<String, List<String>> headers = conn.getHeaderFields();
            StringBuilder responseHeaderBuilder = new StringBuilder();
            for (Map.Entry<String, List<String>> stringListEntry : headers.entrySet()) {
                responseHeaderBuilder.append(stringListEntry.getKey()).append(": ").append(stringListEntry.getValue()).append("\n\n");
            }
            result.setResponseHeaders(responseHeaderBuilder.toString());

            conn.disconnect();

        } catch (Exception e) {
            String exceptionMessage = path + " 请求失败";
            result.setSuccessful(false);
            result.setResponseMessage("\n\r" + e.getClass().getSimpleName() + ": " + e.getLocalizedMessage() + "\n\r" + exceptionMessage);
            AssertionResult assertionResult = new AssertionResult();
            assertionResult.setFailure(false);
            assertionResult.setFailureMessage(exceptionMessage);
            result.addAssertionResult(assertionResult);
        } finally {
        }
        return message;
    }
}
