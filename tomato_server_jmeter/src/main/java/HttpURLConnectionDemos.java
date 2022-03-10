import com.google.gson.Gson;
//import com.k2archer.server.tomato.bean.dto.LoginInfo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpURLConnectionDemos {
    public String doPost(Map<String, Object> map) {
        OutputStream os = null;
        BufferedReader in = null;
        HttpURLConnection conn = null;
        URL url = null;
        try {
            // 判断交易的url，第一个交易执行path1,第2，3，4交易执行path
            if((Boolean)map.get("judge") == true) {
                url = new URL((String) map.get("path1"));
            } else {
                url = new URL((String) map.get("path"));
            }

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.addRequestProperty("key","value");

//            LoginInfo loginInfo = new LoginInfo();
//            loginInfo.setUsername("kwei");
//            loginInfo.setPassword("123");
//
//            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
//            writer.write(new Gson().toJson(loginInfo));

//            conn.setRequestProperty("X-ABX-ModuleName", ((String)map.get("modulName")));
//            conn.setRequestProperty("X-ABX-SessionID", (String) map.get("sessionID"));
//            conn.setRequestProperty("X-ABX-TradeName","trade%2F%E5%AE%A2%E6%88%B7%E4%BF%A1%E6%81%AF%E7%BB%B4%E6%8A%A4");
//            conn.setRequestProperty("X-ABX-TradeTitle", "%E5%AE%A2%E6%88%B7%E4%BF%A1%E6%81%AF%E7%BB%B4%E6%8A%A4");
//            conn.setRequestProperty("X-ABX-StepKey", URLEncoder.encode(((String) map.get("StepKey")), "UTF-8"));
//
//            conn.setRequestProperty("X-ABX-OID", (String) map.get("OID"));// 新增
//			conn.setRequestProperty("X-ABX-OID", "%7B%0A%09%22A%22%3A%22"+map.get("OID")+"%22%0A%7D");
//            conn.setRequestProperty("X-ABX-MemberOIDS", "%7B%22"+map.get("OID")+"%22%3A%22A%22%7D");
//			conn.setRequestProperty("X-ABX-MemberOIDS", "%7B%22newId11%22%3A%22A%22%7D");
            conn.connect();
            os = conn.getOutputStream();

            StringBuffer strbuffer = new StringBuffer();

            try(  // 使用语法糖，使用语法糖可以 使代码简洁可观，即只需写try即可，不用加cath和fianlly
//					FileInputStream fis = new FileInputStream("/home/abs/data/" + map.get("url"));
                  FileInputStream fis = new FileInputStream("r:/temp/" + map.get("url"));
                  InputStreamReader inputStreamReader = new InputStreamReader(fis, "UTF-8");
                  BufferedReader inBR = new BufferedReader(inputStreamReader)
            ) {

                String str = null;
                while ((str = inBR.readLine()) != null) {
                    strbuffer.append(str);
                }
                inBR.close();
            }
            String jsonString = strbuffer.toString();
            os.write(jsonString.getBytes(StandardCharsets.UTF_8));
            os.flush();
            conn.getResponseMessage();
        } catch (Exception e) {
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
}
