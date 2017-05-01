package it.hella.model;

/**
 * The Interface ThreadedBean.
 */
public interface ThreadedBean {

	/**
	 * Sets the thread number.
	 *
	 * @param threadId
	 *            the new thread number
	 */
	void setThreadNumber(int threadId);

	/**
	 * Gets the thread id.
	 *
	 * @return the thread id
	 */
	int getThreadId();

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	Long getId();
}
