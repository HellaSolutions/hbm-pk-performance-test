package it.hella.hibernate.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import it.hella.model.ThreadedBean;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseTests.
 */
public class BaseTests {

	/** The logger. */
	private static Logger logger = LogManager.getLogger();

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
			logger.info(key.getCanonicalName() + " total time > " + Math.round(value / 10E6) + " milliseconds");
		});
	}

	/**
	 * Adds the time elapsed.
	 *
	 * @param clazz
	 *            the clazz
	 * @param nano
	 *            the nano
	 */
	protected synchronized void addTimeElapsed(Class<? extends ThreadedBean> clazz, long nano) {
		timings.put(clazz, timings.getOrDefault(clazz, 0L) + nano);
	}

}
