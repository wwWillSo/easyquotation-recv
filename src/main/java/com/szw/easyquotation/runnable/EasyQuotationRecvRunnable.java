package com.szw.easyquotation.runnable;

import java.util.Date;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.szw.easyquotation.entity.RealTimeMarketdata;
import com.szw.easyquotation.repository.RealTimeMarketdataRepository;

public class EasyQuotationRecvRunnable implements Callable<EasyQuotationRecvRunnable>{
	
	//队列名称  
    private String QUEUE_NAME = "cc";  
    
    private RealTimeMarketdataRepository realTimeMarketdataRepository = null ;
    
    private Channel channel = null ;
    
    private RedisTemplate redisTemplate = null ;
    
    public EasyQuotationRecvRunnable(RedisTemplate redisTemplate, RealTimeMarketdataRepository realTimeMarketdataRepository, Channel channel, String queueName) {
    	this.realTimeMarketdataRepository = realTimeMarketdataRepository ;
    	this.channel = channel ;
    	this.QUEUE_NAME = queueName ;
    	this.redisTemplate = redisTemplate ;
    }
	
	@Override
	public EasyQuotationRecvRunnable call() {
		try {
    		
    		//声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。  
    		channel.queueDeclare(QUEUE_NAME, false, false, false, null);  
    		System.out.println(" [线程"+Thread.currentThread().getId()+"] for " + QUEUE_NAME + " Waiting for messages. To exit press CTRL+C");  
    		
    		//创建队列消费者  
    		QueueingConsumer consumer = new QueueingConsumer(channel);  
    		//指定消费队列  
    		channel.basicConsume(QUEUE_NAME, true, consumer);  
    		
    		System.out.println(" [线程"+Thread.currentThread().getId()+"] for " + QUEUE_NAME + " 接收数据中...");  
    		
    		while (true)  
    		{  
    			//nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）  
    			QueueingConsumer.Delivery delivery = consumer.nextDelivery();  
    			String message = new String(delivery.getBody());  
//    			System.out.println(" [线程"+Thread.currentThread().getId()+"] Received '" + message + "'");  
    			
    			JSONObject obj = JSONObject.parseObject(message) ;
    			RealTimeMarketdata marketdata = obj.toJavaObject(RealTimeMarketdata.class) ;
    			marketdata.setUpdateTime(new Date());
//    			if (QUEUE_NAME.equals("mq-1"))
//    				System.out.println(" [线程"+Thread.currentThread().getId()+"] for " + QUEUE_NAME + " Received '" + message + "'");  
//    			realTimeMarketdataRepository.save(marketdata) ;
    			redisTemplate.opsForValue().set(marketdata.getStockcode(), marketdata) ;
    		}  
    	} catch (Exception e) {
    		System.out.println(" [线程"+Thread.currentThread().getId()+"] for " + QUEUE_NAME + " 报错...");  
    		e.printStackTrace();
    	}
		return null;
	}

}
