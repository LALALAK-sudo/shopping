public class ThreadTest {
    public static void main(String[] args) {
        Thread thread1 = new Thread(new testRun1());
        Thread thread2 = new Thread(new testRun2());
        thread1.start();
        thread2.start();
//        thread1.run();
//        thread2.run();
        for (int i = 0; i < 5; i++) {
            System.out.println(1111);
        }
    }


}
class testRun1 implements Runnable{

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("线程1");
        }
    }
}
class testRun2 implements Runnable{

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("线程2");
        }
    }
}