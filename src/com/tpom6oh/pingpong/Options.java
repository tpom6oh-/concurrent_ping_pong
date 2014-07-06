package com.tpom6oh.pingpong;

public class Options {
    private static Options uniqueInstance = null;
    private int maxIterations = 10;
    private int maxTurns = 1;

    private PlayPingPong.SyncMechanism syncMechanism = PlayPingPong.SyncMechanism.SEMA;

    public static Options instance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Options();
        }

        return uniqueInstance;
    }

    public int maxIterations() {
        return maxIterations;
    }

    public int maxTurns() {
        return maxTurns;
    }

    public PlayPingPong.SyncMechanism syncMechanism() {
        return syncMechanism;
    }

    public boolean parseArgs(String argv[]) {
        for (int argc = 0; argc < argv.length; argc += 2) {
            if (argv[argc].equals("-i")) {
                maxIterations = Integer.parseInt(argv[argc + 1]);
            } else if (argv[argc].equals("-s")) {
                if (argv[argc + 1].equals("SEMA")) {
                    syncMechanism = PlayPingPong.SyncMechanism.SEMA;
                } else if (argv[argc + 1].equals("COND")) {
                    syncMechanism = PlayPingPong.SyncMechanism.COND;
                }
            } else if (argv[argc].equals("-t")) {
                maxTurns = Integer.parseInt(argv[argc + 1]);
            } else {
                printUsage();
                return false;
            }
        }

        return true;
    }

    public void printUsage() {
        PlatformStrategy platform = PlatformStrategy.instance();
        platform.errorLog("Options", "\nHelp Invoked on ");
        platform.errorLog("Options", "[-hist] ");
        platform.errorLog("", "");
        platform.errorLog("", "");

        platform.errorLog("Options", "Usage: ");
        platform.errorLog("Options", "-h: invoke help ");
        platform.errorLog("Options", "-i max-number-of-iterations ");
        platform.errorLog("Options", "-s sync-mechanism (e.g., \"SEMA\" or \"COND\"");
        platform.errorLog("Options", "-t max-number-of-turns");
    }

    private Options() {
    }
}
