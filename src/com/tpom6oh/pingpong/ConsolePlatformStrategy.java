package com.tpom6oh.pingpong;

import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

public class ConsolePlatformStrategy extends PlatformStrategy {

    private static CountDownLatch mLatch = new CountDownLatch(2);
    PrintStream mOutput;

    ConsolePlatformStrategy(Object output) {
        mOutput = (PrintStream) output;
    }

    public void begin() {
        mLatch = new CountDownLatch(2);
    }

    public void print(String outputString) {
        System.out.println(outputString);
    }

    public void done() {
        mLatch.countDown();
    }

    public void awaitDone() {
        try {
            mLatch.await();
        } catch (java.lang.InterruptedException ignored) {
        }
    }

    public String platformName() {
        return System.getProperty("java.specification.vendor");
    }

    public void errorLog(String javaFile, String errorMessage) {
        System.out.println(javaFile + " " + errorMessage);
    }
}
