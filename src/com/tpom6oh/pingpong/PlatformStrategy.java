package com.tpom6oh.pingpong;

public abstract class PlatformStrategy {

    private static PlatformStrategy uniqueInstance = null;

    public static PlatformStrategy instance() {
        return uniqueInstance;
    }

    public static PlatformStrategy instance(PlatformStrategy platform) {
        return uniqueInstance = platform;
    }

    public abstract void begin();

    public abstract void print(String outputString);

    public abstract void done();

    public abstract void awaitDone();

    public abstract String platformName();

    public abstract void errorLog(String javaFile, String errorMessage);

    protected PlatformStrategy() {
    }
}

