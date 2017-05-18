package com.sofaking.iconpack.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nadavfima on 6/20/15.
 */
public class RoundsExecutor {

    public static ExecutorService sPool =  Executors.newCachedThreadPool();

    public static void execute(Runnable r){
        get().execute(r);
    }

    private static ExecutorService get(){

        return sPool;
    }

}
