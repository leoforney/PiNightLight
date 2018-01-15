package tk.leoforney.pinightlight;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.pi4j.wiringpi.SoftPwm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/**
 * Created by Leo on 8/28/2017.
 */
public class Main {

    public static void main(String[] args) {

        if (args.length == 0) {
            Runnable mainRunnable;
            ExecutorService service;

            mainRunnable = () -> {
                try {
                    new RunnerClass().run();
                } catch (InterruptedException e) {
                    LedStrip.getInstance().close();
                }
            };
            service = Executors.newFixedThreadPool(1);
            service.submit(mainRunnable);
        }

    }

}
