package com.netease.qa.log.storm.spouts;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.netease.qa.log.storm.util.Const;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class MQConsumer extends BaseRichSpout {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(MQConsumer.class);

	private SpoutOutputCollector collector;

	private static Channel channel;
	private static Connection connection;
	private static String queueName;
	private static String host;
	private static int port;
	private static int exceptionLimitNum;
	private static int exceptionSleepTime;

	public void ack(Object msgId) {
		// logger.info("OK:" + msgId);
	}

	public void close() {
	}

	public void fail(Object msgId) {
		logger.error("FAIL:" + msgId);
	}

	public static void main(String[] args) throws IOException, ShutdownSignalException, ConsumerCancelledException,
			InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setPort(port);

		connection = factory.newConnection();
		channel = connection.createChannel();
		// channel.queueDeclare(queueName, false, false, false, null);

		QueueingConsumer consumer = new QueueingConsumer(channel);
		// channel.basicConsume(queueName, true, consumer);
		channel.basicConsume(queueName, true, consumer);
		channel.basicQos(1);

		String message = "";
		QueueingConsumer.Delivery delivery = null;
		int i = 0;
		while (true) {
			delivery = consumer.nextDelivery();
			message = new String(delivery.getBody());
			Map<String, Object> headers = delivery.getProperties().getHeaders();
			String hostname = "";
			String path = "";
			String filePattern = "";
			String dsTime = "";
			try {
				hostname = headers.get("__DS_.fields.hostname").toString();
				path = headers.get("__DS_.fields._ds_target_dir").toString();
				filePattern = headers.get("__DS_.fields._ds_file_pattern").toString();
				dsTime = headers.get("__DS_.timestamp").toString();
			} catch (NullPointerException e) {
				logger.error("can't get header, hostname: " + hostname + ", path: " + path + ", file: " + filePattern
						+ ", dsTime: " + dsTime);
			}

			logger.debug("Consume: " + message);
			logger.debug("hostname: " + hostname + ", path: " + path + ", filePattern: " + filePattern);
			logger.debug("" + ++i);
			// channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		}

	}

	/**
	 * The only thing that the methods will do It is emit each file line
	 */
	public void nextTuple() {
		/**
		 * The nextuple it is called forever, so if we have been readed the file
		 * we will wait and then return
		 */
		while (true) {
			try {
				logger.info("=====get a new channel======");
				Channel channel = getChannel();
				QueueingConsumer consumer = new QueueingConsumer(channel);
				channel.basicConsume(queueName, true, consumer);
				channel.basicQos(1);
				// readCount表示读了多少行
				int readCount = 0;
				while (true) {
					QueueingConsumer.Delivery delivery = consumer.nextDelivery();
					String message = new String(delivery.getBody());
					Map<String, Object> headers = delivery.getProperties().getHeaders();
					String hostname = "";
					String path = "";
					String filePattern = "";
					String dsTime = "";
					try {
						hostname = headers.get("__DS_.fields.hostname").toString();
						path = headers.get("__DS_.fields._ds_target_dir").toString();
						filePattern = headers.get("__DS_.fields._ds_file_pattern").toString();
						dsTime = headers.get("__DS_.timestamp").toString();
						this.collector.emit(new Values(message, hostname, path, filePattern, dsTime), message);
						readCount++;
						logger.debug("Consume: " + message);
						logger.debug("hostname: " + hostname + ", path: " + path + ", filePattern: " + filePattern
								+ ", dstime: " + dsTime);
					} catch (NullPointerException e) {
						logger.error("can't get header, hostname: " + hostname + ", path: " + path + ", file: "
								+ filePattern, e);
					}
					if (readCount >= exceptionLimitNum) {
						try {
							logger.info("---------read " + exceptionLimitNum + " msg, reader sleep 50ms-----");
							Thread.sleep(exceptionSleepTime);
						} catch (InterruptedException e) {
							logger.error("error", e);
						} finally {
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

	/**
	 * We will create the file and get the collector object
	 */
	@SuppressWarnings("rawtypes")
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		queueName = conf.get(Const.MQ_QUEUE).toString();
		host = conf.get(Const.MQ_HOST).toString();
		port = Integer.parseInt(conf.get(Const.MQ_PORT).toString());
		exceptionLimitNum = Integer.parseInt(conf.get(Const.EXCEPTION_LIMIT_NUM).toString());
		exceptionSleepTime = Integer.parseInt(conf.get(Const.EXCEPTION_SLEEP_TIME).toString());
		this.collector = collector;
	}

	/**
	 * Declare the output field "word"
	 */
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("line", "hostname", "path", "filePattern", "dsTime"));
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
