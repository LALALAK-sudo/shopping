import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JavaBase {
    public interface Base1{
        public void print();
    }
    public interface Base2{
        public void print();
        public void print2();
    }
    public class ZiLei implements Base1{
        public void print() {
            System.out.println("Zilei");
        }
    }
    public class ZiLei2 implements Base1,Base2 {

        @Override
        public void print() {

        }

        @Override
        public void print2() {

        }
    }

    @Test
    public void test() {
        ZiLei ziLei = new ZiLei();
        ziLei.print();
        new ZiLei2().print();
    }

    public class ThreadPool {
        public void test2() {
            ExecutorService pool = Executors.newFixedThreadPool(2);
            Future<?> submit = pool.submit(new RunnableThread());
            pool.submit(new RunnableThread());
            pool.shutdown();
        }
    }

    class RunnableThread implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName()+":"+i);
            }
        }
    }
}
