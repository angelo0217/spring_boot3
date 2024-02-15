package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.entity.db.StockDayInfo;
import com.example.demo.entity.dto.StockInfoDTO;
import com.example.demo.respository.StockDayInfoRepository;

@Service
public class StockDayInfoService extends BaseService<StockInfoDTO, StockDayInfo, Integer> {
	private final StockDayInfoRepository stockDayInfoRepository;

	public StockDayInfoService(StockDayInfoRepository stockDayInfoRepository) {
		super(stockDayInfoRepository);
		this.stockDayInfoRepository = stockDayInfoRepository;
	}

	public long getDataDateCnt(String dataDateStr) {
		return this.stockDayInfoRepository.countByDataDate(dataDateStr);
	}

	public Long getMaxTraceDate() {
		return stockDayInfoRepository.findMaxTraceDate();
	}

	public LocalDateTime getMaxDataDate() {
		return stockDayInfoRepository.findMaxDataDate();
	}

	public List<StockInfoDTO> getMatchInfoByClose(int greater, int less, Long traceDate) {
		List<StockDayInfo> data = this.stockDayInfoRepository
				.getAllByCloseGreaterThanAndCloseLessThanAndTradeDateOrderByTradeDateDesc(greater, less, traceDate);
		return data.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public List<StockInfoDTO> getMatchInfoByDataDate(int greater, int less, LocalDateTime dataDate) {
		String dataDateStr = dataDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		List<StockDayInfo> data = this.stockDayInfoRepository
				.findAllByCloseGreaterThanAndCloseLessThanAndTradeDateOrderByTradeDateDesc(greater, less, dataDateStr);
		return data.stream().map(this::convertToDto).collect(Collectors.toList());
	}

	@Override
	protected StockDayInfo convertToEntity(StockInfoDTO stockInfoDTO) {
		StockDayInfo stockDayInfo = new StockDayInfo();
		stockDayInfo.setStockCode(stockInfoDTO.getStockCode());
		stockDayInfo.setTradeDate(stockInfoDTO.getTradeDate());
		stockDayInfo.setTime(stockInfoDTO.getTime());
		stockDayInfo.setFlat(stockInfoDTO.getFlat());
		stockDayInfo.setFloor(stockInfoDTO.getFloor());
		stockDayInfo.setCeil(stockInfoDTO.getCeil());
		stockDayInfo.setOpen(stockInfoDTO.getOpen());
		stockDayInfo.setHigh(stockInfoDTO.getHigh());
		stockDayInfo.setLow(stockInfoDTO.getLow());
		stockDayInfo.setClose(stockInfoDTO.getClose());
		stockDayInfo.setVolume(stockInfoDTO.getVolume());
		stockDayInfo.setMillionAmount(stockInfoDTO.getMillionAmount());
		stockDayInfo.setPreviousClose(stockInfoDTO.getPreviousClose());
		stockDayInfo.setPreviousVolume(stockInfoDTO.getPreviousVolume());
		stockDayInfo.setMillionAmount(stockInfoDTO.getMillionAmount());
		stockDayInfo.setMillionAmount(stockInfoDTO.getMillionAmount());
		stockDayInfo.setDataDate(LocalDateTime.now());
		return stockDayInfo;
	}

	@Override
	protected StockInfoDTO convertToDto(StockDayInfo stockDayInfo) {
		var stockInfoDto = StockInfoDTO.builder().dbId(stockDayInfo.getId()).stockCode(stockDayInfo.getStockCode())
				.tradeDate(stockDayInfo.getTradeDate()).time(stockDayInfo.getTime()).flat(stockDayInfo.getFlat())
				.floor(stockDayInfo.getFloor()).ceil(stockDayInfo.getCeil()).open(stockDayInfo.getOpen())
				.high(stockDayInfo.getHigh()).low(stockDayInfo.getLow()).close(stockDayInfo.getClose())
				.volume(stockDayInfo.getVolume()).millionAmount(stockDayInfo.getMillionAmount())
				.previousClose(stockDayInfo.getPreviousClose()).previousVolume(stockDayInfo.getPreviousVolume())
				.millionAmount(stockDayInfo.getMillionAmount()).dataDate(stockDayInfo.getDataDate()).build();
		return stockInfoDto;
	}

	@Override
	protected Integer getDtoId(StockInfoDTO stockInfoDTO) {
		return stockInfoDTO.getDbId();
	}

}
