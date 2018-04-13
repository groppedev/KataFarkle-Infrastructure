package tdd.milano.pooler;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread pooler per la gestione dei Thread in FUI.
 * 
 * @author GROMAS
 */
public class ThreadPooler implements AutoCloseable
{
	private ExecutorService executorPool;
	private final int maxThread;
	private final int threadPriority;
	private final boolean daemon;
	private final String poolerName;
	
	private final AtomicInteger count = new AtomicInteger(0);
	
	public ThreadPooler(int threadPriority, int maxThread, boolean daemon, String poolerName)
	{
		this.poolerName = poolerName;
		this.threadPriority = threadPriority;
		this.maxThread = maxThread;
		this.daemon = daemon;
	}

	/**
	 * Inizializzazione del pooler.
	 */
	public void init()
	{
		this.executorPool = Executors.newFixedThreadPool(maxThread, new ThreadFactory()
		{
			public Thread newThread(Runnable r)
			{
				final Thread t = new Thread(r);
				t.setDaemon(daemon);
				t.setPriority(threadPriority);
				t.setName(poolerName + "-" + count.incrementAndGet());
				return t;
			}
		});
	}
	
	public <T> Future<T> schedule(Callable<T> task)
	{
		return executorPool.submit(task);
	}
	
	public void close()
	{
		executorPool.shutdown();
	}
}
