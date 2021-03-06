package com.sectong.sparkapacheloganalysis;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class Functions {
	public static Function2<Long, Long, Long> SUM_REDUCER = new Function2<Long, Long, Long>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Long call(Long a, Long b) throws Exception {
			return a + b;
		}
	};

	public static class ValueComparator<K, V> implements Comparator<Tuple2<K, V>>, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Comparator<V> comparator;

		public ValueComparator(Comparator<V> comparator) {
			this.comparator = comparator;
		}

		public int compare(Tuple2<K, V> o1, Tuple2<K, V> o2) {
			return comparator.compare(o1._2(), o2._2());
		}
	}

	public static class LongComparator implements Comparator<Long>, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public int compare(Long a, Long b) {
			if (a > b)
				return 1;
			if (a.equals(b))
				return 0;
			return -1;
		}
	}

	public static Comparator<Long> LONG_NATURAL_ORDER_COMPARATOR = new LongComparator();

	public static Function<String, ApacheAccessLog> PARSE_LOG_LINE = new Function<String, ApacheAccessLog>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ApacheAccessLog call(String logline) throws Exception {
			return ApacheAccessLog.parseFromLogLine(logline);
		}
	};

	public static Function<ApacheAccessLog, Long> GET_CONTENT_SIZE = new Function<ApacheAccessLog, Long>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Long call(ApacheAccessLog apacheAccessLog) throws Exception {
			return apacheAccessLog.getContentSize();
		}
	};

	public static PairFunction<ApacheAccessLog, Integer, Long> GET_RESPONSE_CODE = new PairFunction<ApacheAccessLog, Integer, Long>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Tuple2<Integer, Long> call(ApacheAccessLog log) throws Exception {
			return new Tuple2<Integer, Long>(log.getResponseCode(), 1L);
		}
	};

	public static PairFunction<ApacheAccessLog, String, Long> GET_IP_ADDRESS = new PairFunction<ApacheAccessLog, String, Long>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Tuple2<String, Long> call(ApacheAccessLog log) throws Exception {
			return new Tuple2<String, Long>(log.getIpAddress(), 1L);
		}
	};

	public static Function<Tuple2<String, Long>, Boolean> FILTER_GREATER_10 = new Function<Tuple2<String, Long>, Boolean>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Boolean call(Tuple2<String, Long> tuple) throws Exception {
			return tuple._2() > 10;
		}
	};

	public static Function<Tuple2<String, Long>, String> GET_TUPLE_FIRST = new Function<Tuple2<String, Long>, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public String call(Tuple2<String, Long> tuple) throws Exception {
			return tuple._1();
		}
	};

	public static PairFunction<ApacheAccessLog, String, Long> GET_ENDPOINT = new PairFunction<ApacheAccessLog, String, Long>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Tuple2<String, Long> call(ApacheAccessLog log) throws Exception {
			return new Tuple2<String, Long>(log.getEndpoint(), 1L);
		}
	};
}
