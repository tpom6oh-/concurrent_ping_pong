package com.tpom6oh.pingpong;

public class Main {

    public static void main(String[] args) {
        PlatformStrategy.instance(
                new PlatformStrategyFactory(System.out, null).makePlatformStrategy());

        Options.instance().parseArgs(args);

        PlayPingPong pingPong = new PlayPingPong(PlatformStrategy.instance(),
                                                 Options.instance().maxIterations(),
                                                 Options.instance().maxTurns(),
                                                 Options.instance().syncMechanism());

        new Thread(pingPong).start();
    }
}
