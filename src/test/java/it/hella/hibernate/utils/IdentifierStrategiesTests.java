package it.hella.hibernate.utils;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import it.hella.model.HiLoIdentifiedBean;
import it.hella.model.IdentityIdentifiedBean;
import it.hella.model.SequenceIdentifiedBean;
import it.hella.model.ThreadedBean;

/**
 * The Class IdentifierStrategiesTests.
 */
public class IdentifierStrategiesTests extends BaseTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/** The number of insert operations per thread. */
	private static int BEAN_NUMBER_PER_THREAD = 1000;

	/** The numbe of threads. */
	private static int THREAD_NUMBER = 50;

	/** The maximum number of insert operations before session flush. */
	private static int BATCH_CASH_SIZE = 50;

	/**
	 * A reentrant lock that guards the timing evaluation code section. To avoid
	 * timing bias due to multithreading interrupts.
	 * 
	 * @see IdentifierStrategiesTests#createAndSaveBean
	 */
	private static final ReentrantLock lock = new ReentrantLock();

	ExecutorService executorService;

	@Before
	public void before() {
		final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Batch-%d").setDaemon(true)
				.build();
		executorService = Executors.newFixedThreadPool(THREAD_NUMBER, threadFactory);
	}

	/**
	 * Sequence identified bean insert test.
	 *
	 * @throws Exception
	 *             any exception
	 */
	@Test
	public void sequenceInsert() throws Exception {
		traceThreadExecution(multithreadedInsert(SequenceIdentifiedBean.class));
	}

	/**
	 * Identity identified bean insert test.
	 *
	 * @throws Exception
	 *             any exception
	 */
	@Test
	public void identityInsert() throws Exception {
		traceThreadExecution(multithreadedInsert(IdentityIdentifiedBean.class));
	}

	/**
	 * Hi lo identified bean insert test.
	 *
	 * @throws Exception
	 *             any exception
	 */
	@Test
	public void HiLoInsert() throws Exception {
		traceThreadExecution(multithreadedInsert(HiLoIdentifiedBean.class));
	}

	/**
	 * Creates and saves a bean instance. Uses a stopwatch to add a delta to the
	 * timings maps
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
		Long k = 0L;
		lock.lock();
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			sessionFactory.getCurrentSession().save(idBean1);
			addTimeElapsed(clazz, stopwatch.stop().elapsed(TimeUnit.NANOSECONDS));
		} finally {
			lock.unlock();
		}
		return idBean1.getId();

	}

	/**
	 * The thread callable body.
	 * 
	 * Executes a transaction that inserts BEAN_NUMBER_PER_THREAD beans
	 *
	 * @param threadId
	 *            the id of the thread
	 * @param clazz
	 *            the ThreadedBean implementation
	 * @return the list of primary keys values inserted
	 * @throws Exception
	 *             the exception
	 */
	private List<Long> threadTransaction(int threadId, Class<? extends ThreadedBean> clazz) throws Exception {

		Session session = sessionFactory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		List<Long> ret = new ArrayList<Long>();
		try {
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
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			session.close();
		}
		return ret;

	}

	/**
	 * Prepares THREAD_NUMBER threads for execution
	 *
	 * @param clazz
	 *            the ThreadedBean implementation
	 * @return the lists of primary keys inserted for each Future
	 * @throws Exception
	 *             any exception
	 * 
	 * @see IdentifierStrategiesTests#threadTransaction
	 */
	public List<Future<List<Long>>> multithreadedInsert(Class<? extends ThreadedBean> clazz) throws Exception {

		List<Callable<List<Long>>> tasks = new ArrayList<>();
		for (int i = 0; i < THREAD_NUMBER; i++) {
			final int threadId = new Integer(i);
			tasks.add(() -> threadTransaction(threadId, clazz));
		}
		return executorService.invokeAll(tasks);

	}

	private void traceThreadExecution(List<Future<List<Long>>> features) {
		features.parallelStream().forEach(f -> {
			try {
				f.get();
			} catch (Exception e) {
				Throwable ex = e.getCause() != null ? e.getCause() : e;
				logger.error(ex);
				executorService.shutdownNow();
				fail("Thread error > " + ex.getClass() + ", " + ex.getMessage());
			}
		});
	}

}
