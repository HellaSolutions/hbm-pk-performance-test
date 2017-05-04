package it.hella.hibernate.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import it.hella.model.HiLoIdentifiedBean;
import it.hella.model.IdentityIdentifiedBean;
import it.hella.model.SequenceIdentifiedBean;

public class SessionFactoryBuilder {

	private SessionFactory sessionFactory;

	public SessionFactoryBuilder() {
		StandardServiceRegistry registry = new StandardServiceRegistryBuilder().build();
		MetadataSources meta = new MetadataSources(registry).addAnnotatedClass(IdentityIdentifiedBean.class)
				.addAnnotatedClass(HiLoIdentifiedBean.class).addAnnotatedClass(SequenceIdentifiedBean.class);
		sessionFactory = meta.buildMetadata().buildSessionFactory();
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}
