package it.hella.model;

import static javax.persistence.GenerationType.SEQUENCE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 * The Class SequenceIdentifiedBean.
 */
@Entity(name = "SequenceTable")
public class SequenceIdentifiedBean implements ThreadedBean {

	/** The id. */
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "my_sequence")
	@SequenceGenerator(name = "my_sequence", allocationSize = 100)
	@Column(name = "sequenceId")
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
