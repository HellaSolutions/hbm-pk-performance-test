package it.hella.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * The Class HiLoIdentifiedBean.
 */
@Entity(name = "HiloTable")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HiLoIdentifiedBean implements ThreadedBean {

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hilo_sequence_generator")
	@GenericGenerator(name = "hilo_sequence_generator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "hilo_sequence"),
			@Parameter(name = "initial_value", value = "1"), @Parameter(name = "increment_size", value = "100"),
			@Parameter(name = "optimizer", value = "hilo") })
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
