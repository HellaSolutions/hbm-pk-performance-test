package it.hella.hibernate.utils;

import org.hibernate.SessionFactory;

/**
 * The Class HibernateUtil.
 * 
 * Singleton factory pattern for the Hibernate Session Factory
 */
public class HibernateUtil {
	/**
	 * Only static methods
	 */
	private HibernateUtil() {
	}

	@SuppressWarnings("unused")
	private static class SessionFactoryHolder {
		static SessionFactory sessionFactory = new SessionFactoryBuilder().getSessionFactory();

		private SessionFactoryHolder() {
		}
	}

	/**
	 * Gets the session factory.
	 *
	 * @return the session factory
	 */
	public static SessionFactory getSessionFactory() {
		return SessionFactoryHolder.sessionFactory;
	}

	/**
	 * Releases the Session Factory
	 */
	public static void shutdown() {
		SessionFactoryHolder.sessionFactory.close();
	}

}