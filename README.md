# hbm-pk-performance-test

Simple performance comparison multithreaded test for the three main Hibernate identity strategies: Identity, Sequence and HiLo.

It is possible to fluctuate the tests cases by tuning the two constants: BEAN_NUMBER_PER_THREAD and THREAD_NUMBER, 
and the Hibernate property hibernate.batch_size. The two constants represents, respectively, the number of beans inserted by each 
thread and the number of threads per identifier strategy.
