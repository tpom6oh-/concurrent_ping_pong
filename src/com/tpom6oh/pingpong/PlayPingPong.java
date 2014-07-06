package com.tpom6oh.pingpong;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PlayPingPong implements Runnable {

    public static enum SyncMechanism {
        SEMA, COND
    }

    private static volatile int maxIterations;
    private static volatile int maxTurns = 1;
    private static volatile PlatformStrategy platformStrategy;
    private static SyncMechanism syncMechanism = SyncMechanism.SEMA;

    private final static int PING_THREAD = 0;
    private final static int PONG_THREAD = 1;

    static abstract class PingPongThread extends Thread {

        protected String stringToPrint;

        PingPongThread(String stringToPrint) {
            this.stringToPrint = stringToPrint;
        }

        abstract void acquire();

        abstract void release();

        void setOtherThreadId(long id) {
        }

        public void run() {
            for (int loopsDone = 1; loopsDone <= maxIterations; ++loopsDone) {
                acquire();
                platformStrategy.print(stringToPrint + "(" + loopsDone + ")");
                release();
            }

            platformStrategy.done();
        }
    }

    static class PingPongThreadSema extends PingPongThread {
        private Semaphore mSemas[] = new Semaphore[2];

        private final static int FIRST_SEMA = 0;
        private final static int SECOND_SEMA = 1;

        PingPongThreadSema(String stringToPrint, Semaphore firstSema, Semaphore secondSema) {
            super(stringToPrint);
            mSemas[FIRST_SEMA] = firstSema;
            mSemas[SECOND_SEMA] = secondSema;
        }

        void acquire() {
            mSemas[FIRST_SEMA].acquireUninterruptibly();
        }

        void release() {
            mSemas[SECOND_SEMA].release();
        }

    }

    static class PingPongThreadCond extends PingPongThread {

        private Condition mConds[] = new Condition[2];
        private ReentrantLock mLock = null;
        private int mTurnCountDown = 0;
        public long mOtherThreadId = 0;
        private static long mTurnOwner;

        public void setOtherThreadId(long otherThreadId) {
            this.mOtherThreadId = otherThreadId;
        }

        private final static int FIRST_COND = 0;
        private final static int SECOND_COND = 1;

        PingPongThreadCond(String stringToPrint, ReentrantLock lock, Condition firstCond,
                           Condition secondCond, boolean isOwner) {
            super(stringToPrint);
            mTurnCountDown = maxTurns;
            mLock = lock;
            mConds[FIRST_COND] = firstCond;
            mConds[SECOND_COND] = secondCond;
            if (isOwner) {
                mTurnOwner = this.getId();
            }
        }

        void acquire() {
            mLock.lock();

            while (mTurnOwner != this.getId()) {
                mConds[FIRST_COND].awaitUninterruptibly();
            }

            mLock.unlock();
        }

        void release() {
            mLock.lock();

            --mTurnCountDown;

            if (mTurnCountDown == 0) {
                mTurnOwner = mOtherThreadId;
                mTurnCountDown = maxTurns;
                mConds[SECOND_COND].signal();
            }
            mLock.unlock();
        }
    }

    public PlayPingPong(PlatformStrategy platformStrategy, int maxIterations, int maxTurns,
                        SyncMechanism syncMechanism) {
        PlayPingPong.platformStrategy = platformStrategy;
        PlayPingPong.maxIterations = maxIterations;
        PlayPingPong.maxTurns = maxTurns;
        PlayPingPong.syncMechanism = syncMechanism;
    }

    private void makePingPongThreads(SyncMechanism schedMechanism,
                                     PingPongThread[] pingPongThreads) {
        if (schedMechanism == SyncMechanism.SEMA) {
            Semaphore pingSema = new Semaphore(1);
            Semaphore pongSema = new Semaphore(0);

            pingPongThreads[PING_THREAD] = new PingPongThreadSema("ping", pingSema, pongSema);
            pingPongThreads[PONG_THREAD] = new PingPongThreadSema("pong", pongSema, pingSema);
        } else if (schedMechanism == SyncMechanism.COND) {
            ReentrantLock lock = new ReentrantLock();
            Condition pingCond = lock.newCondition();
            Condition pongCond = lock.newCondition();
            int numberOfTurnsEach = 2;

            pingPongThreads[PING_THREAD] = new PingPongThreadCond("ping", lock, pingCond, pongCond,
                                                                  true);
            pingPongThreads[PONG_THREAD] = new PingPongThreadCond("pong", lock, pongCond, pingCond,
                                                                  false);
            pingPongThreads[PING_THREAD].setOtherThreadId(pingPongThreads[PONG_THREAD].getId());
            pingPongThreads[PONG_THREAD].setOtherThreadId(pingPongThreads[PING_THREAD].getId());
        }
    }

    public void run() {
        platformStrategy.begin();

        platformStrategy.print("Ready...Set...Go!");

        PingPongThread pingPongThreads[] = new PingPongThread[2];
        pingPongThreads[PING_THREAD] = null;
        pingPongThreads[PONG_THREAD] = null;

        makePingPongThreads(syncMechanism, pingPongThreads);

        pingPongThreads[PING_THREAD].start();
        pingPongThreads[PONG_THREAD].start();

        platformStrategy.awaitDone();

        platformStrategy.print("Done!");
    }
}

