package com.example.demo;

import com.example.demo.service.task.StockInfoAPITask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private StockInfoAPITask stockInfoAPIService;

	@Test
	void contextLoads() {
		stockInfoAPIService.getStockInfo();
//		try {
//			// 設定要請求的 URL
//			String url = "https://www.wantgoo.com/investrue/all-quote-info";
//
//			// 創建 URL 物件
//			URL obj = new URL(url);
//
//			// 打開一個 HttpURLConnection 連線
//			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//			// 設定請求方式為 GET
//			con.setRequestMethod("GET");
//
//			// 取得回應碼
//			int responseCode = con.getResponseCode();
//			System.out.println("Response Code: " + responseCode);
//
//			// 讀取回應內容
//			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//			String inputLine;
//			StringBuffer response = new StringBuffer();
//
//			while ((inputLine = in.readLine()) != null) {
//				response.append(inputLine);
//			}
//			in.close();
//
//			// 輸出回應內容
//			System.out.println("Response Content: " + response.toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}
