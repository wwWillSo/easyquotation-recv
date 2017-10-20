package com.szw.easyquotation;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.processor.EasyQuotationRecvProcessor;
import com.szw.easyquotation.repository.RealTimeMarketdataRepository;

@SpringBootApplication
public class EasyquotationRecvApplication {
	
	//队列名称  
    private final static String QUEUE_NAME = "cc";  
	
	@Autowired
	private RealTimeMarketdataRepository realTimeMarketdataRepository ;
	
	@Autowired
	private EasyQuotationRecvProcessor easyQuotationRecvProcessor ;
	
	@Autowired
	private RedisTemplate redisTemplate ;
	
	@PostConstruct
	public void init() {
		
		easyQuotationRecvProcessor.execute();
		
		new Thread(()->{
			BigDecimal now = BigDecimal.ZERO ;
			while (true) {
				RealTimeMarketdata data = (RealTimeMarketdata)redisTemplate.opsForValue().get("000001") ;
				try {
					if (now.compareTo(data.getNow()) != 0) {
//						System.out.println(data.getStockcode() + ":" + data.getNow());
						now = data.getNow() ;
					}
				} catch (Exception e) {
					
				}
			}
		}).start() ;
		
	}
	
	public static void main(String[] args) {
		SpringApplication.run(EasyquotationRecvApplication.class, args);
	}
	
}
