package com.sectong.sparkapacheloganalysis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import kafka.serializer.StringDecoder;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import scala.Tuple2;

/**
 * 
 * @author ppl
 *
 */
public class LogAnalyzerKafkaStreaming {
	// Stats will be computed for the last window length of time.
	@SuppressWarnings("unused")
	private static final Duration WINDOW_LENGTH = new Duration(30 * 1000);
	// Stats will be computed every slide interval time.
	private static final Duration SLIDE_INTERVAL = new Duration(10 * 1000);

	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setAppName("Log Analyzer Kafka Streaming");
		JavaStreamingContext jssc = new JavaStreamingContext(conf, SLIDE_INTERVAL);

		// �����в������
		if (args.length < 2) {
			System.out.println("Usage: LogAnalyzerKafkaStreaming <brokers> <topics>\n"
					+ "  <brokers> is a list of one or more Kafka brokers\n"
					+ "  <topics> is a list of one or more kafka topics to consume from\n\n");
			System.exit(1);
		}

		String brokers = args[0];
		String topics = args[1];

		HashSet<String> topicsSet = new HashSet<String>(Arrays.asList(topics.split(",")));
		HashMap<String, String> kafkaParams = new HashMap<String, String>();
		kafkaParams.put("metadata.broker.list", brokers);

		// ����kafka Stream
		JavaPairInputDStream<String, String> messages = KafkaUtils.createDirectStream(jssc, String.class, String.class,
				StringDecoder.class, StringDecoder.class, kafkaParams, topicsSet);

		// ��ȡ����Ϣ
		JavaDStream<String> lines = messages.map(new Function<Tuple2<String, String>, String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 5266880065425088203L;

			public String call(Tuple2<String, String> tuple2) {
				return tuple2._2();
			}
		});

		// Apache Access Logs DStream ����.
		JavaDStream<ApacheAccessLog> accessLogDStream = lines.map(Functions.PARSE_LOG_LINE).cache();

		accessLogDStream.foreachRDD(new Function<JavaRDD<ApacheAccessLog>, Void>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 4773629242100965521L;

			public Void call(JavaRDD<ApacheAccessLog> accessLogs) {
				if (accessLogs.count() == 0) {
					System.out.println("No access logs in this time interval");
					return null;
				}

				// *** Note that this is code copied verbatim from
				// LogAnalyzer.java.

				// Calculate statistics based on the content size.
				JavaRDD<Long> contentSizes = accessLogs.map(Functions.GET_CONTENT_SIZE).cache();
				System.out.print("Content Size Avg: " + contentSizes.reduce(Functions.SUM_REDUCER)
						/ contentSizes.count());
				System.out.print(", Min: " + contentSizes.min(Functions.LONG_NATURAL_ORDER_COMPARATOR));
				System.out.println(", Max: " + contentSizes.max(Functions.LONG_NATURAL_ORDER_COMPARATOR));

				// Compute Response Code to Count.
				List<Tuple2<Integer, Long>> responseCodeToCount = accessLogs.mapToPair(Functions.GET_RESPONSE_CODE)
						.reduceByKey(Functions.SUM_REDUCER).take(100);
				System.out.println("Response code counts: " + responseCodeToCount);

				// Any IPAddress that has accessed the server more than
				// 10 times.

				List<String> ipAddresses = accessLogs.mapToPair(Functions.GET_IP_ADDRESS)
						.reduceByKey(Functions.SUM_REDUCER).filter(Functions.FILTER_GREATER_10)
						.map(Functions.GET_TUPLE_FIRST).take(100);
				System.out.println("IPAddresses > 10 times: " + ipAddresses);

				// Top Endpoints.
				List<Tuple2<String, Long>> topEndpoints = accessLogs.mapToPair(Functions.GET_ENDPOINT)
						.reduceByKey(Functions.SUM_REDUCER)
						.top(10, new Functions.ValueComparator<String, Long>(Functions.LONG_NATURAL_ORDER_COMPARATOR));
				System.out.println("Top Endpoints: " + topEndpoints);

				return null;
			}
		});

		// Start the streaming server.
		jssc.start(); // Start the computation
		jssc.awaitTermination(); // Wait for the computation to terminate
	}
}
