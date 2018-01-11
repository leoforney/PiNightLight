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
        } else if (args[0].equals("generateConfig")) {
            File file = ConfigFile.getInstance().fileLocation;
            Config conf = new Config();
            conf.latitude = "42.3703";
            conf.longitude = "-87.9020";
            conf.timezone = "America/Chicago";
            conf.brightnessSchedule = new HashMap<>();
            conf.brightnessSchedule.put("18:30", 50); //6:30
            conf.brightnessSchedule.put("20:00", 30); //8
            conf.brightnessSchedule.put("21:00", 20); //9
            conf.brightnessSchedule.put("22:00", 15); //10
            conf.brightnessSchedule.put("22:30", 0); //10:30
            try {
                String serialized = new Gson().toJson(conf);
                Files.write(serialized.getBytes(), file);
                System.out.println(serialized);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (args[0].equals("testConfig")) {
            Config config = ConfigFile.getInstance().getCurrentConfig();
            System.out.println("long: " + config.longitude + " lat: " + config.latitude + " timezone: " + config.timezone);
            config.brightnessSchedule.forEach((s, integer) -> System.out.println("at " + s + " b: " + integer));
        }

    }

}
