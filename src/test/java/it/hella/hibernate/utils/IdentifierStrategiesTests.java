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

/**
 * The Class IdentifierStrategiesTests.
 */
public class IdentifierStrategiesTests extends BaseTests {

	/** The number of insert operations per thread. */
	private static int BEAN_NUMBER_PER_THREAD = 1000;

	/** The numbe of threads. */
	private static int THREAD_NUMBER = 50;

	/** The maximum number of insert operations before session flush. */
	private static int BATCH_CASH_SIZE = 50;

	/**
	 * A reentrant lock that guards the timing evaluation code section. To avoid
	 * noise due to multithreading interrupts.
	 * 
	 * @see IdentifierStrategiesTests#createAndSaveBean
	 */
	private static final ReentrantLock lock = new ReentrantLock();

	/**
	 * Sequence insert.
	 *
	 * @throws Exception
	 *             any exception
	 */
	@Test
	public void sequenceInsert() throws Exception {
		multithreadedInsert(SequenceIdentifiedBean.class);
	}

	/**
	 * Identity insert.
	 *
	 * @throws Exception
	 *             any exception
	 */
	@Test
	public void identityInsert() throws Exception {
		multithreadedInsert(IdentityIdentifiedBean.class);
	}

	/**
	 * Hi lo insert.
	 *
	 * @throws Exception
	 *             any exception
	 */
	@Test
	public void HiLoInsert() throws Exception {
		multithreadedInsert(HiLoIdentifiedBean.class);
	}

	/**
	 * Creates the and save bean.
	 *
	 * @param threadId
	 *            the id of the thread
	 * @param clazz
	 *            the ThreadedBean implementation
	 * @return the primary key inserted
	 * @throws Exception
	 *             any exception
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
	 *            the id of the thread
	 * @param clazz
	 *            the ThreadedBean implementation
	 * @return the list of primary key values inserted
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
	 *            the ThreadedBean implementation
	 * @return the list of primary key values inserted per Future
	 * @throws Exception
	 *             any exception
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
