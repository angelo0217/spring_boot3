package com.example.demo.service.task;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.demo.constant.StockConst;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.entity.dto.StockSingleInfoDTO;
import com.example.demo.entity.dto.WatchStockDTO;
import com.example.demo.service.LineNotifyService;
import com.example.demo.service.StockDayInfoService;
import com.example.demo.service.WatchStockService;
import com.example.demo.utils.StockUtils;

import lombok.extern.slf4j.Slf4j;

@Scope("prototype")
@Component("CalculateStockTask")
@Slf4j
public class CalculateStockTask implements Runnable {

	private StockDayInfoService stockDayInfoService;
	private WatchStockService watchStockService;
	private LineNotifyService lineNotifyService;

	public CalculateStockTask(StockDayInfoService stockDayInfoService, WatchStockService watchStockService,
			LineNotifyService lineNotifyService) {
		this.stockDayInfoService = stockDayInfoService;
		this.watchStockService = watchStockService;
		this.lineNotifyService = lineNotifyService;
	}

	public void callSingleStock(StockInfoDTO stockInfoDTO) {
		RestTemplate restTemplate = new RestTemplate();
		String apiUrl = "https://ws.api.cnyes.com/ws/api/v1/charting/history?resolution=1&symbol=TWS:"
				+ stockInfoDTO.getStockCode() + ":STOCK&quote=1";

		try {
			ResponseEntity<StockSingleInfoDTO> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, null,
					StockSingleInfoDTO.class);
			if (responseEntity.getStatusCode() == HttpStatusCode.valueOf(200)) {
				StockSingleInfoDTO info = responseEntity.getBody();
				if (info.getData().getO() != null && info.getData().getO().size() > 0) {
					Double realTimePrice = info.getData().getO().get(0);
					if (realTimePrice != null) {
						Integer volumes = info.getData().getV().stream().filter(value -> value != null)
								.mapToInt(Double::intValue).sum();
						log.info("code: {}, volume: {}, total v: {}, last: {}, now: {}", stockInfoDTO.getStockCode(),
								stockInfoDTO.getVolume(), volumes, stockInfoDTO.getClose(), realTimePrice);
						if (volumes < StockConst.DEFAULT_VOLUMES)
							return;

						if ((volumes / stockInfoDTO.getVolume()) > StockConst.MAGNIFICATION
								&& realTimePrice > stockInfoDTO.getClose()) {
							boolean is_rise = (((realTimePrice - stockInfoDTO.getClose())
									/ stockInfoDTO.getClose()) > 0.085);
							var watch = WatchStockDTO.builder().stockCode(stockInfoDTO.getStockCode())
									.detectVolumes(volumes).detectMoney(realTimePrice)
									.lastDateMoney(stockInfoDTO.getClose()).lastDayVolumes(stockInfoDTO.getVolume())
									.happenDate(LocalDateTime.now()).is_rise(is_rise).build();
							lineNotifyService.send(watch);
							watchStockService.create(watch);
							log.info(" === 偵測到股票: {} 可能出現交易量爆表", stockInfoDTO.getStockCode());
						}
					}
				}
			} else {
				log.error("{} call api error {}}", stockInfoDTO.getStockCode(), responseEntity.getStatusCode());
			}
		} catch (Exception ex) {
			log.error("{} call api error", stockInfoDTO.getStockCode(), ex);
		}
	}

	// private static boolean isTPEXStock(String stockCode) {
	// // 上櫃股票代码以 "5" 开头
	// String tpexRegex = "^5\\d{3}$";
	// Pattern pattern = Pattern.compile(tpexRegex);
	// Matcher matcher = pattern.matcher(stockCode);
	// return matcher.matches();
	// }

	@Override
	public void run() {
		var dataDate = stockDayInfoService.getMaxDataDate();
		var infos = stockDayInfoService.getMatchInfoByDataDate(StockConst.B_MIN_CLOSE, StockConst.B_MAX_CLOSE,
				dataDate);
		infos.stream().filter(v -> !v.getStockCode().contains("&")).filter(v -> StockUtils.isEStock(v.getStockCode()))
				.forEach(this::callSingleStock);
		// Flux.fromIterable(infos)
		// .filter(v -> !v.getStockCode().contains("&"))
		// .filter(v -> isEStock(v.getStockCode()))
		// .subscribe(this::callSingleStock);
	}
}
