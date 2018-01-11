package tk.leoforney.pinightlight;

import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import spark.Request;
import spark.Response;
import spark.Route;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static com.pi4j.wiringpi.Gpio.wiringPiSetup;
import static spark.Spark.*;

/**
 * Created by Leo on 8/28/2017.
 */
public class RunnerClass implements Observer {

    SunriseSunsetCalculator calculator;
    Scheduler scheduler;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public enum CurrentTime {
        DAY,
        NIGHT
    }

    public void run() throws InterruptedException {
        wiringPiSetup();
        port(80);

        LedStrip ledStrip = LedStrip.getInstance();
        ledStrip.addObserver(this);

        scheduler = new Scheduler();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ledStrip.close();
            scheduler.gracefullyShutdown();
        }));

        staticFileLocation("/web");

        get("/color", (request, response) -> ledStrip.getLastHex());

        post("/color", (request, response) -> {
            String color = request.body();
            ledStrip.setRGBfromHex(color);
            response.body("Changing color to " + color);
            System.out.println("--Spark color request--: " + color);
            return response.body();
        });

        get("/brightness", (request, response) -> ledStrip.getBrightness());

        post("/brightness", (request, response) -> {
            String brightnessAsString = request.body();
            brightnessAsString = brightnessAsString.replace("%", "");
            response.body("Changing brightness to ");
            try {
                int brightness = Integer.parseInt(brightnessAsString);
                ledStrip.setBrightness(brightness);
                System.out.println("--Spark brightness request--: " + brightness);
                response.body(response.body() + ledStrip.getBrightness() + "%");
            } catch (NumberFormatException e) {
                response.body("Number is not valid");
            }

            return response.body();
        });

        get("/config", (request, response) -> ConfigFile.getInstance().getCurrentConfigAsString());

        post("/config", (request, response) -> {
            ConfigFile.getInstance().writeConfig(request.body());
            response.body("Writing new config...");
            return response.body();
        });

        ConfigFile.getInstance().getCurrentConfig().brightnessSchedule.forEach(new BiConsumer<String, Integer>() {
            @Override
            public void accept(String s, Integer integer) {
                Runnable setBrightessRunnable = new Runnable() {
                    @Override
                    public void run() {
                        scheduler.schedule(this, Schedules.executeAt(s));
                        ledStrip.setBrightness(integer);
                    }
                };
                scheduler.schedule(setBrightessRunnable, Schedules.executeAt(s));
            }
        });

        while (Thread.currentThread().isAlive()) {
            Thread.sleep(350);
        }

    }

    public void refreshCalculatorInstance() {
        Config config = ConfigFile.getInstance().getCurrentConfig();
        Location location = new Location(config.latitude, config.longitude);
        calculator = new SunriseSunsetCalculator(location, config.timezone);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    @Override
    public void update(Observable o, Object arg) {
        LedStrip strip = (LedStrip) arg;
        System.out.println("R: " + strip.getR() + " G: " + strip.getG() + " B: " + strip.getB());
    }
}
