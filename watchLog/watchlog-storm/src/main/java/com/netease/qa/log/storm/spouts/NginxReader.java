package com.netease.qa.log.storm.spouts;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.netease.qa.log.storm.util.Const;
import com.netease.qa.log.storm.util.Regex;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

@SuppressWarnings("serial")
public class NginxReader implements IRichSpout {

	private SpoutOutputCollector collector;

	private static final Logger logger = LoggerFactory.getLogger(NginxReader.class);
	private static Channel channel;
	private static Connection connection;
	private static String queueName;
	private static String host;
	private static int port;
	private static AtomicLong count;
	private static int nginxLimitNum;
	private static int nginxSleepTime;

	@SuppressWarnings("rawtypes")
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		queueName = conf.get(Const.MQ_QUEUE).toString();
		host = conf.get(Const.MQ_HOST).toString();
		port = Integer.parseInt(conf.get(Const.MQ_PORT).toString());
		nginxLimitNum = Integer.parseInt(conf.get(Const.NGINX_LIMIT_NUM).toString());
		nginxSleepTime = Integer.parseInt(conf.get(Const.NGINX_SLEEP_TIME).toString());
		this.collector = collector;
		count = new AtomicLong();
		ScheduledExecutorService POOL = Executors.newScheduledThreadPool(1);
		POOL.scheduleWithFixedDelay(new SumTask(), 1, 1, TimeUnit.SECONDS);
	}

	class SumTask implements Runnable {
		@Override
		public void run() {
			logger.info("NginxReader read and emit msg = " + count.get());
			count.getAndSet(0);
		}
	}

	public void close() {
		Channel channel = getChannel();
		
	}

	public void writeMQ(){
		
	}
	
	public void nextTuple() {
		while (true) {
			try {
				logger.info("=====get a new channel======");
				Channel channel = getChannel();
//				channel.queueDeclare(queueName, false, false, false, null);
				QueueingConsumer consumer = new QueueingConsumer(channel);
				channel.basicConsume(queueName, false, consumer);
				channel.basicQos(1);
				// readCount表示读了多少行
				int readCount = 0;
				while (true) {
					QueueingConsumer.Delivery delivery = consumer.nextDelivery();
					consumer.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
					String message = new String(delivery.getBody());
					Map<String, Object> headers = delivery.getProperties().getHeaders();
					String hostname = "";
					String path = "";
					String filePattern = "";
					try {
						hostname = headers.get("__DS_.fields.hostname").toString();
						path = headers.get("__DS_.fields._ds_target_dir").toString();
						filePattern = headers.get("__DS_.fields._ds_file_pattern").toString();
						//预处理input,因为MQ会转义input，这里需要我们在转义回去。
						String initMessage = Regex.initMQinput(message);
						
						this.collector.emit(new Values(hostname, path, filePattern, initMessage), initMessage);
						readCount++;
						count.getAndIncrement();
						logger.debug("Consume: " + message);
						logger.debug("hostname: " + hostname + ", path: " + path + ", filePattern: " + filePattern);
					} catch (NullPointerException e) {
						logger.error("can't get header, hostname: " + hostname + ", path: " + path + ", file: "
								+ filePattern, e);
					}
					if (readCount >= nginxLimitNum) {
						try {
							logger.debug("---------read " + nginxLimitNum + " msg, reader sleep " + nginxSleepTime + "ms-----");
							Thread.sleep(nginxSleepTime);
						} catch (InterruptedException e) {
							logger.error("error", e); 
						}finally{
							readCount = 0;
						}
					}
				}
			} catch (Exception e) {
				logger.error("consume error, close connction", e);
				if (channel != null) {
					try {
						channel.close();
					} catch (IOException e1) {
						channel = null;
					}
				}
			}
		}

	}

	public void ack(Object msgId) {
		// TODO Auto-generated method stub
	}

	public void fail(Object msgId) {
		// TODO Auto-generated method stub
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("hostname", "path", "filePattern", "line"));
	}

	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	public void activate() {
		// TODO Auto-generated method stub

	}

	public void deactivate() {
		// TODO Auto-generated method stub

	}

	private Connection getConnection() throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setPort(port);
		connection = factory.newConnection();
		return connection;
	}

	private Channel getChannel() {
		int count = 3;
		while (count-- > 0) {
			try {
				if (connection == null) {
					connection = getConnection();
				}
				return connection.createChannel();
			} catch (Exception e) {
				logger.error("get channel error, try left: " + count, e);
				if (connection != null) {
					try {
						connection.close();
					} catch (Exception e1) {
					}
				}
				connection = null;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {
				}
			}
		}
		return null;
	}

}
