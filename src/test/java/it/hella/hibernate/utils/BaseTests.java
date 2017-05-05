package it.hella.hibernate.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import it.hella.model.ThreadedBean;

/**
 * The Class BaseTests.
 * 
 * Manages the Hibernate session factory and the map of the timings for the bean
 * operations. The map associates a total execution time to a ThreadedBean
 * implementation class and is accessible with the method addTimeElapsed. By
 * calling this method, each executor adds the time elapsed while executing a
 * operation involving a ThreadedBean object
 * 
 */
public class BaseTests {

	/** The logger. */
	protected static Logger logger = LogManager.getLogger();

	/** The session factory. */
	protected static SessionFactory sessionFactory;

	/** The timings. */
	private static Map<Class<? extends ThreadedBean>, Long> timings = new HashMap<>();

	/**
	 * Before Class.
	 */
	@BeforeClass
	public static void beforeClass() {
		sessionFactory = HibernateUtil.getSessionFactory();
	}

	/**
	 * After class.
	 */
	@AfterClass
	public static void afterClass() {
		HibernateUtil.shutdown();
		timings.forEach((key, value) -> {
			logger.info(key.getCanonicalName() + " total time > "
					+ TimeUnit.MILLISECONDS.convert(value, TimeUnit.NANOSECONDS) + " milliseconds");
		});
	}

	/**
	 * Adds the time elapsed while executing a traced ThreadedBean operation
	 * 
	 * synchronized keyword is not strictly necessary here as the default
	 * implementation calls this method in a region guarded by a reentrant lock.
	 *
	 * @param clazz
	 *            the ThreadedBean implementation
	 * @param nano
	 *            time elapsed in nanoseconds
	 */
	protected synchronized void addTimeElapsed(Class<? extends ThreadedBean> clazz, long nano) {
		timings.put(clazz, timings.getOrDefault(clazz, 0L) + nano);
	}

}
