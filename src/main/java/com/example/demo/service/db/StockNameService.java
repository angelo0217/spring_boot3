package com.example.demo.service.db;

import com.example.demo.entity.db.StockName;
import com.example.demo.entity.dto.StockNameDTO;
import com.example.demo.respository.StockNameRepository;
import com.example.demo.service.BaseService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StockNameService extends BaseService<StockNameDTO, StockName, Integer> {

	private final StockNameRepository stockNameRepository;

	public StockNameService(StockNameRepository stockNameRepository) {
		super(stockNameRepository);
		this.stockNameRepository = stockNameRepository;
	}

	public List<StockName> findAll(){
		return this.stockNameRepository.findAll();
	}

	@Override
	protected StockName convertToEntity(StockNameDTO stockInfoDTO) {
		var stockName = new StockName();
		stockName.setStockCode(stockInfoDTO.getStockCode());
		stockName.setStockName(stockInfoDTO.getStockName());
		return stockName;
	}

	@Override
	protected StockNameDTO convertToDto(StockName stockName) {
		var stockInfoDto = StockNameDTO
				.builder().id(stockName.getId()).stockCode(stockName.getStockCode())
				.stockName(stockName.getStockName()).build();
		return stockInfoDto;
	}

	@Override
	protected Integer getDtoId(StockNameDTO stockNameDTO) {
		return stockNameDTO.getId();
	}

}
