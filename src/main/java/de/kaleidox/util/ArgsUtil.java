package de.kaleidox.util;

import java.util.concurrent.ConcurrentHashMap;

public class ArgsUtil extends ConcurrentHashMap<String, String> {
    public ArgsUtil(String[] args, char define) {
        super();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.charAt(0) == define)
                put(arg, args[++i]);
            else
                put(arg, String.valueOf(i));
        }
    }

    @Override
    public String get(Object key) {
        if (key instanceof String || key instanceof Integer) {
            return super.get(key.toString());
        } else return super.get(key);
    }
}
