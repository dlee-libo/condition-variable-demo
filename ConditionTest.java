import java.util.Random;

/*
 * 考虑这样一个问题, 两个线程t1, t2
 * t1生成一堆随机数, 往一个累加器上累加, 当累加结果达到阈值, 就退出
 *
 * t2要打印达到阈值后的结果
 *
 * 并且我们假设, t1和t2是两个人开发的, t2无法得知这个阈值是多少, 即t2不能自己去检测这个值
 * */

public class ConditionTest {
    private Integer x = 0;
    private boolean shouldPrint = false;

    public static void main(String argv[]) {
        ConditionTest y = new ConditionTest();
        y.demo();
    }

    public void demo() {
        final Object lock = new Object();

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    synchronized (lock) {
                        while (!shouldPrint) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println(x);
                        break;
                    }
                }
            }
        });

        t2.start();
        

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                int max = 1024;
                while (true) {
                    Random rand = new Random();
                    int n = rand.nextInt(100) + 1;
                    System.out.println("generate n:" + n);
                    x += n;
                    if (x > max) {
                        synchronized (lock) {
                            shouldPrint = true;
                            lock.notify();
                        }
                        break;
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                System.out.println("done my own work!");
            }
        });

        t1.start();
    }
}

