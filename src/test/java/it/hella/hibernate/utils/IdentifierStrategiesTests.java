package it.hella.hibernate.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import com.google.common.base.Stopwatch;

import it.hella.model.HiLoIdentifiedBean;
import it.hella.model.IdentityIdentifiedBean;
import it.hella.model.SequenceIdentifiedBean;
import it.hella.model.ThreadedBean;

// TODO: Auto-generated Javadoc
/**
 * The Class IdentifierStrategiesTests.
 */
public class IdentifierStrategiesTests extends BaseTests {

	/** The bean number per thread. */
	private static final int BEAN_NUMBER_PER_THREAD = 1000;

	/** The thread number. */
	private static final int THREAD_NUMBER = 50;

	/** The thread number. */
	private static final int BATCH_CASH_SIZE = 50;

	/** The lock. */
	private static final ReentrantLock lock = new ReentrantLock();

	/**
	 * Sequence insert.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void sequenceInsert() throws Exception {
		multithreadedInsert(SequenceIdentifiedBean.class);
	}

	/**
	 * Identity insert.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void identityInsert() throws Exception {
		multithreadedInsert(IdentityIdentifiedBean.class);
	}

	/**
	 * Hi lo insert.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void HiLoInsert() throws Exception {
		multithreadedInsert(HiLoIdentifiedBean.class);
	}

	/**
	 * Creates the and save bean.
	 *
	 * @param threadId
	 *            the thread id
	 * @param clazz
	 *            the clazz
	 * @return the long
	 * @throws Exception
	 *             the exception
	 */
	private Long createAndSaveBean(int threadId, Class<? extends ThreadedBean> clazz) throws Exception {

		ThreadedBean idBean1 = clazz.newInstance();
		idBean1.setThreadNumber(threadId);
		lock.lock();
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			sessionFactory.getCurrentSession().save(idBean1);
			stopwatch.stop();
			addTimeElapsed(clazz, stopwatch.elapsed(TimeUnit.NANOSECONDS));
		} finally {
			lock.unlock();
		}
		return idBean1.getId();

	}

	/**
	 * Thread transaction.
	 *
	 * @param threadId
	 *            the thread id
	 * @param clazz
	 *            the clazz
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	private List<Long> threadTransaction(int threadId, Class<? extends ThreadedBean> clazz) throws Exception {

		Session session = sessionFactory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		List<Long> ret = new ArrayList<Long>();
		for (int i = 0; i < BEAN_NUMBER_PER_THREAD; i++) {
			ret.add(createAndSaveBean(threadId, clazz));
			if (i % BATCH_CASH_SIZE == 0) {
				session.flush();
				session.clear();
			}
		}
		session.flush();
		session.clear();
		tx.commit();
		session.close();
		return ret;

	}

	/**
	 * Multithreaded insert.
	 *
	 * @param clazz
	 *            the clazz
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	public List<Future<List<Long>>> multithreadedInsert(Class<? extends ThreadedBean> clazz) throws Exception {

		List<Callable<List<Long>>> tasks = new ArrayList<>();
		for (int i = 0; i < THREAD_NUMBER; i++) {
			final int threadId = new Integer(i);
			tasks.add(() -> threadTransaction(threadId, clazz));
		}
		final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUMBER);
		return executorService.invokeAll(tasks);

	}

}
