package DecayMongo.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 2014年10月26日
 * 
 * @author decaywood
 * 
 * 
 * 
 *         线程池的作用: 线程池作用就是限制系统中执行线程的数量。
 *         根据系统的环境情况，可以自动或手动设置线程数量，达到运行的最佳效果；少了浪费了系统资源
 *         ，多了造成系统拥挤效率不高。用线程池控制线程数量，其他线程
 *         排队等候。一个任务执行完毕，再从队列的中取最前面的任务开始执行。若队列中没有等待进程
 *         ，线程池的这一资源处于等待。当一个新任务需要运行时，如果线程 池中有等待的工作线程，就可以开始运行了；否则进入等待队列。
 * 
 *         为什么要用线程池: 减少了创建和销毁线程的次数，每个工作线程都可以被重复利用，可执行多个任务
 *         可以根据系统的承受能力，调整线程池中工作线线程的数目
 *         ，防止因为因为消耗过多的内存，而把服务器累趴下(每个线程需要大约1MB内存，线程开的越多，消耗的内存也就越大，最后死机)
 * 
 *         1.1 public static ExecutorService newCachedThreadPool()
 *         创建一个可根据需要创建新线程的线程池
 *         ，但是在以前构造的线程可用时将重用它们。对于执行很多短期异步任务的程序而言，这些线程池通常可提高程序性能。
 * 
 *         1.2 public static ExecutorService newFixedThreadPool(int nThreads)
 *         创建一个可重用固定线程数的线程池，以共享的无界队列方式来运行这些线程。
 * 
 *         1.3 public static ExecutorService newSingleThreadExecutor() 创建一个使用单个
 *         worker 线程的 Executor，以无界队列方式来运行该线程。
 * 
 */
public class ThreadManager {

    private ExecutorService executor = null;

    
    /**
     * 
     * 
     *         1.1 public static ExecutorService newCachedThreadPool()
     *         创建一个可根据需要创建新线程的线程池，但是在以前构造的线程可用时将重用它们。
     *         对于执行很多短期异步任务的程序而言，这些线程池通常可提高程序性能。
     * 
     *         1.2 public static ExecutorService newFixedThreadPool(int nThreads)
     *         创建一个可重用固定线程数的线程池，以共享的无界队列方式来运行这些线程。
     * 
     *         1.3 public static ExecutorService newSingleThreadExecutor() 创建一个使用单个
     *         worker 线程的 Executor，以无界队列方式来运行该线程。
     *         
     *         default thread pool size: 2 * cpu + 1
     * 
     */
    
    private ThreadManager() {
        int threadPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    private static class InnerClass {
        private static final ThreadManager instance = new ThreadManager();
    }

    public static ThreadManager getInstance() {
        return InnerClass.instance;
    }

    public void destroy() {
        closeThreadPool(executor);
    }

    /**
     * 30秒内没关闭则强制关闭连接池
     *
     */
    private void closeThreadPool(ExecutorService pool) {
        if (pool != null) {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public ExecutorService getExecutor() {
        return executor;
    }
}
