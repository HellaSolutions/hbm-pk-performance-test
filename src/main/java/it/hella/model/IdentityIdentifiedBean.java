package it.hella.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The Class IdentityIdentifiedBean.
 */
@Entity(name = "IdentityTable")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class IdentityIdentifiedBean implements ThreadedBean {

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "IdentifiedId")
	private Long id;

	/** The thread id. */
	private int threadId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.hella.model.ThreadedBean#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.hella.model.ThreadedBean#setThreadNumber(int)
	 */
	@Override
	public void setThreadNumber(int threadId) {
		this.threadId = threadId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.hella.model.ThreadedBean#getThreadId()
	 */
	@Override
	public int getThreadId() {
		return threadId;
	}

}