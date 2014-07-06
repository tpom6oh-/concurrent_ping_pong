
package com.tpom6oh.pingpong;

import java.util.HashMap;

public class PlatformStrategyFactory {

    private static interface IPlatformStrategyFactoryStrategy
    {
        public PlatformStrategy execute();
    }

    private HashMap<String, IPlatformStrategyFactoryStrategy> platformStrategyMap =
            new HashMap<String, IPlatformStrategyFactoryStrategy>();

    public PlatformStrategyFactory(final Object output,
                                   final Object activity)
    {
        platformStrategyMap.put("Sun Microsystems Inc.", new IPlatformStrategyFactoryStrategy() {
            public PlatformStrategy execute() {
                return new ConsolePlatformStrategy(output);
            }
        });

        platformStrategyMap.put("Oracle Corporation", new IPlatformStrategyFactoryStrategy() {
            public PlatformStrategy execute() {
                return new ConsolePlatformStrategy(output);
            }
        });
    }

    public PlatformStrategy makePlatformStrategy()
    {
        String name = System.getProperty("java.specification.vendor");

        return platformStrategyMap.get(name).execute();
    }
}

