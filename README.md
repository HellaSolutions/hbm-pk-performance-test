# hbm-pk-performance-test

Simple performance comparison multithreaded test for the three main Hibernate identity strategies: Identity, Sequence and HiLo.

It is possible to fluctuate the tests cases by tuning the two constants: BEAN_NUMBER_PER_THREAD and THREAD_NUMBER, 
the Hibernate property *hibernate.batch_size*, the parameter *increment_size* for HiLo strategy, and *allocationSize* for the Sequence strategy. The two constants represents, respectively, the number of beans inserted by each thread and the number of threads per identifier strategy.

**Sample output**: database MySQL, BEAN_NUMBER_PER_THREAD = 1000, THREAD_NUMBER = 50, increment_size = 100, allocationSize = 100, hibernate.batch_size = 50

[INFO ] 2017-05-05 15:15:51.673 [main] BaseTests - it.hella.model.IdentityIdentifiedBean total time > 1158 milliseconds

[INFO ] 2017-05-05 15:15:51.674 [main] BaseTests - it.hella.model.SequenceIdentifiedBean total time > 334 milliseconds

[INFO ] 2017-05-05 15:15:51.674 [main] BaseTests - it.hella.model.HiLoIdentifiedBean total time > 182 milliseconds

**Timings refers to the overall time needed by the Hibernate save operations, not to the time elapsed between the start and the end of the transactions**

