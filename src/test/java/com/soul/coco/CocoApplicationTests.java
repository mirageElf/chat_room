package com.soul.coco;

import com.soul.coco.common.utils.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CocoApplication.class)
public class CocoApplicationTests {

    @Autowired
    protected RedisUtil redisUtil;

    private ReentrantLock reentrantLock = new ReentrantLock();

    @Test
    public void testRedis() {
        /*for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " 已开启");
                try {
                    // 先查询redis中是否存在该key
                    String value = redisUtil.getString("编号1");
                    if (value == null || value == "") {
                        // redis中不存在 则获取锁
                        if (reentrantLock.tryLock()) {
                            Thread.sleep(2000);
                            System.out.println(Thread.currentThread().getName() + "获取到锁");
                            redisUtil.setString("编号1", "9968");
                        } else {
                            System.out.println(Thread.currentThread().getName() + "没有获取到锁");
                            Thread.sleep(3000);
                            value = redisUtil.getString("编号1");
                            if (value != null && value != "") {
                                System.out.println("已有其他线程处理，直接退出");
                            }
                        }
                    } else {
                        System.out.println("已有其他线程处理，直接退出");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }, "线程" + i).start();*/
//        }


    }

    /*@Test
    public void test() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            System.out.println(stackTraceElement.toString());
        }
    }*/

    @Test
    public void test() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        Future<?> future = executorService.submit(() -> {
//            while (!Thread.interrupted()) {
//                System.out.println("rung.....");
//                /*try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    System.out.println("interrupted");
//                }*/
//            }
//        });
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//        }
//
//        future.cancel(true);
//        try {
//            future.get();
//        } catch (InterruptedException | CancellationException | ExecutionException e) {
//            e.printStackTrace();
//        }
//        try {
//            Thread.sleep(1000 * 6);
//        } catch (InterruptedException e) {
//        }



    }

    @Test
    public void test1(String type, Function function) {
        String a = "a";
        String b = new String("a");
        System.out.println(a == b);
    }

}
