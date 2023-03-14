package org.example.utilities;
import org.example.models.Message;

import java.lang.instrument.Instrumentation;
public class MessageSizeFetcher {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object o) {
        return instrumentation.getObjectSize(o);
    }
}
