package it.hella.hibernate.utils;

import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import it.hella.model.HiLoIdentifiedBean;
import it.hella.model.IdentityIdentifiedBean;
import it.hella.model.SequenceIdentifiedBean;

// TODO: Auto-generated Javadoc
/**
 * The Class HibernateUtil.
 */
public class HibernateUtil {

	/** The session factory. */
	private static SessionFactory sessionFactory;

	/**
	 * Instantiates a new hibernate util.
	 */
	private HibernateUtil() {
	}

	/**
	 * Gets the session factory.
	 *
	 * @return the session factory
	 */
	public static SessionFactory getSessionFactory() {
		return getSessionFactory(null);
	}

	/**
	 * Gets the session factory.
	 *
	 * @param interceptor
	 *            the interceptor
	 * @return the session factory
	 */
	public static SessionFactory getSessionFactory(Interceptor interceptor) {
		if (sessionFactory == null) {
			synchronized (HibernateUtil.class) {
				if (sessionFactory == null) {
					initFactory(interceptor);
				}
			}
		}
		return sessionFactory;
	}

	/**
	 * Shutdown.
	 */
	public static void shutdown() {
		if (sessionFactory != null) {
			synchronized (HibernateUtil.class) {
				if (sessionFactory != null) {
					sessionFactory.close();
					sessionFactory = null;
				}
			}
		}
	}

	/**
	 * Inits the factory.
	 *
	 * @param interceptor
	 *            the interceptor
	 */
	private static void initFactory(Interceptor interceptor) {

		try {

			StandardServiceRegistry registry = new StandardServiceRegistryBuilder().build();
			MetadataSources meta = new MetadataSources(registry).addAnnotatedClass(HiLoIdentifiedBean.class)
					.addAnnotatedClass(IdentityIdentifiedBean.class).addAnnotatedClass(SequenceIdentifiedBean.class);
			if (interceptor != null) {
				sessionFactory = meta.buildMetadata().getSessionFactoryBuilder().applyInterceptor(interceptor).build();
			} else {
				sessionFactory = meta.buildMetadata().buildSessionFactory();
			}

		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);

		}

	}
}