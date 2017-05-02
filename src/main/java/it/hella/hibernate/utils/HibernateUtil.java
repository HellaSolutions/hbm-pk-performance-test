package it.hella.hibernate.utils;

import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.google.common.base.Optional;

import it.hella.model.HiLoIdentifiedBean;
import it.hella.model.IdentityIdentifiedBean;
import it.hella.model.SequenceIdentifiedBean;

/**
 * The Class HibernateUtil.
 * 
 * Singleton factory pattern for the Hibernate Session Factory
 */
public class HibernateUtil {

	/** The session factory. */
	private static SessionFactory sessionFactory;

	/**
	 * Only static methods
	 */
	private HibernateUtil() {
	}

	/**
	 * Gets the session factory.
	 *
	 * @return the session factory
	 */
	public static SessionFactory getSessionFactory() {

		initFactory(Optional.absent());
		return sessionFactory;

	}

	/**
	 * Gets the session factory.
	 *
	 * @param interceptor
	 *            An Hibernate interceptor
	 * @return the session factory
	 */
	public static final SessionFactory getSessionFactory(Interceptor interceptor) {

		initFactory(Optional.of(interceptor));
		return sessionFactory;

	}

	/**
	 * Releases the Session Factory
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
	 *            an optional Hibernate interceptor
	 */
	private static void initFactory(Optional<Interceptor> interceptor) {

		if (sessionFactory == null) {
			synchronized (HibernateUtil.class) {
				if (sessionFactory == null) {
					try {

						StandardServiceRegistry registry = new StandardServiceRegistryBuilder().build();
						MetadataSources meta = new MetadataSources(registry).addAnnotatedClass(HiLoIdentifiedBean.class)
								.addAnnotatedClass(IdentityIdentifiedBean.class)
								.addAnnotatedClass(SequenceIdentifiedBean.class);
						if (interceptor.isPresent()) {
							sessionFactory = meta.buildMetadata().getSessionFactoryBuilder()
									.applyInterceptor(interceptor.get()).build();
						} else {
							sessionFactory = meta.buildMetadata().buildSessionFactory();
						}

					} catch (Exception ex) {
						throw new ExceptionInInitializerError(ex);

					}
				}
			}
		}

	}
}