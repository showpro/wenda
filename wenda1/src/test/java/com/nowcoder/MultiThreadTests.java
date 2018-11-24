package com.nowcoder;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程讲解
 * Created by zhan on 2018/9/24.
 */

class MyThread extends Thread {
    private int tid;

    public MyThread(int tid) {
        this.tid = tid;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; ++i) {
                Thread.sleep(1000);
                System.out.println(String.format("%d:%d", tid, i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

////////////////////////////////////////////////////////////////

//消费队列线程
class Consumer implements Runnable {
    private BlockingQueue<String> q;//阻塞队列
    public Consumer(BlockingQueue<String> q) {
        this.q = q;
    }
    @Override
    public void run() {
        try {
            while (true) {//不停的取出
                System.out.println(Thread.currentThread().getName() + ":" + q.take());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//插入队列线程
class Producer implements Runnable {
    private BlockingQueue<String> q;
    public Producer(BlockingQueue<String> q) {
        this.q = q;
    }
    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; ++i) {
                Thread.sleep(1000);
                q.put(String .valueOf(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

////////////////////////////////////////////////////////////

public class MultiThreadTests {
    public static void testThread() {
        for (int i = 0; i < 10; ++i) {
            //new MyThread(i).start();//创建10个线程
        }

        for (int i = 0; i < 10; ++i) {
            final int finalI = i;
            new Thread(new Runnable() {//匿名内部类实现
                @Override
                public void run() {
                    try {
                        for (int j = 0; j < 10; ++j) {
                            Thread.sleep(1000);
                            System.out.println(String.format("T2 %d: %d:", finalI, j));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

///////////////////////////////////////////////////////////////////////
    //锁对象
    private static Object obj = new Object();

    public static void testSynchronized1() {
        synchronized (obj) {
            try {
                for (int j = 0; j < 10; ++j) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T3 %d", j));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void testSynchronized2() {
        synchronized (new Object()) {
            try {
                for (int j = 0; j < 10; ++j) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T4 %d", j));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//////////////////////////////////////////////////////////////////////////
    
    public static void testSynchronized() {
        for (int i = 0; i < 10; ++i) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    testSynchronized1();
                    testSynchronized2();
                }
            }).start();
        }
    }

    public static void testBlockingQueue() {
        BlockingQueue<String> q = new ArrayBlockingQueue<String>(10);
        new Thread(new Producer(q)).start();
        new Thread(new Consumer(q), "Consumer1").start();
        new Thread(new Consumer(q), "Consumer2").start();
    }

//////////////////////////////////////////////////////////////////////////////
    
    //线程局部变量用法
    private static ThreadLocal<Integer> threadLocalUserIds = new ThreadLocal<>();
    private static int userId;

    public static void testThreadLocal() {
        for (int i = 0; i < 10; ++i) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        threadLocalUserIds.set(finalI);
                        Thread.sleep(1000);
                        System.out.println("ThreadLocal:" + threadLocalUserIds.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        for (int i = 0; i < 10; ++i) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        userId = finalI;
                        Thread.sleep(1000);
                        System.out.println("UserId:" + userId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

//////////////////////////////////////////////////////////////////////////////    
    
    //线程池
    public static void testExecutor() {
    	//单线程：执行完线程任务1后，才执行线程任务2
        //ExecutorService service = Executors.newSingleThreadExecutor();
        
    	//多线程：定义两条线程的线程池      两个任务交叉进行
    	ExecutorService service = Executors.newFixedThreadPool(2);
        
        service.submit(new Runnable() {//提交线程任务1
            @Override
            public void run() {
                for (int i = 0; i < 10; ++i) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor1:" + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        service.submit(new Runnable() {//提交线程任务2
            @Override
            public void run() {
                for (int i = 0; i < 10; ++i) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor2:" + i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //任务执行完后关闭
        service.shutdown();
        while (!service.isTerminated()) {
            //每隔1秒查看线程是否关闭
        	try {
                Thread.sleep(1000);
                System.out.println("Wait for termination.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//////////////////////////////////////////////////////////////////////////////
    
    //静态变量计数器
    private static int counter = 0;
    //原子静态变量
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    //没有原子性
    public static void testWithoutAtomic() {
        for (int i = 0; i < 10; ++i) {
            new Thread(new Runnable() {//创建10个线程
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        for (int j = 0; j < 10; ++j) {
                            counter++;
                            System.out.println(counter);//结果最多到99
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    //有原子性
    public static void testWithAtomic() {
        for (int i = 0; i < 10; ++i) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        for (int j = 0; j < 10; ++j) {
                            System.out.println(atomicInteger.incrementAndGet());//结果到100
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static  void testAtomic() {
        //testWithoutAtomic();
        testWithAtomic();
    }

///////////////////////////////////////////////////////////////////////////////////////
    
    public static void testFuture() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                //Thread.sleep(1000);
                throw new IllegalArgumentException("异常");
                //return 1;
             }
        });

        service.shutdown();
        try {
            System.out.println(future.get());
            //System.out.println(future.get(100, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] argv) {
        //testThread();
        //testSynchronized();
        //testBlockingQueue();
        //testThreadLocal();
        //testExecutor();
        //testAtomic();
        testFuture();
    }
}
