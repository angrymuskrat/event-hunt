package com.eventhunt.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Shiplayer on 02.06.18.
 */

public class ExecutorUtil {
    public static final Executor THREAD_POOL_EXECUTOR = Executors.newCachedThreadPool();
}
