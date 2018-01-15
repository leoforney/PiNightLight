package tk.leoforney.pinightlight;

import com.google.gson.Gson;

import java.util.Observable;
import java.util.Observer;

import static com.pi4j.wiringpi.Gpio.wiringPiSetup;
import static spark.Spark.*;

/**
 * Created by Leo on 8/28/2017.
 */
public class RunnerClass implements Observer {

    ColorGuide guide;
    Gson gson;


    public void run() throws InterruptedException {
        wiringPiSetup();
        port(80);

        gson = new Gson();

        guide = new ColorGuide();

        LedStrip ledStrip = LedStrip.getInstance();
        ledStrip.addObserver(this);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ledStrip.close();
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

        post("/webhook", (request, response) -> {
            tk.leoforney.pinightlight.Request desRequest = gson.fromJson(request.body(), tk.leoforney.pinightlight.Request.class);
            String action = desRequest.result.action;
            switch (action) {
                case "brightness":
                    String brightnessString = desRequest.result.parameters.get("percentage").replace("%", "");
                    int brightness = Integer.parseInt(brightnessString);
                    response.body(makeResponse("Brightness set to " + brightness));
                    System.out.println("--Google brightness request--: " + brightness);
                    ledStrip.setBrightness(brightness);
                    break;
                case "color":
                    String color = desRequest.result.parameters.get("color").toLowerCase();
                    String hex = guide.getColor(color);
                    if (hex.equals("")) {
                        response.body(makeResponse("Could not find the color " + color));
                    } else {
                        ledStrip.setRGBfromHex(hex);
                        response.body(makeResponse("Color set to " + color));
                        System.out.println("--Google color request--: " + color);
                    }
                    break;
            }
            return response.body();
        });

        while (Thread.currentThread().isAlive()) {
            Thread.sleep(350);
        }

    }

    public static String makeResponse(String speech) {
        tk.leoforney.pinightlight.Response response = new tk.leoforney.pinightlight.Response(speech);
        return new Gson().toJson(response);
    }

    @Override
    public void update(Observable o, Object arg) {
        LedStrip strip = (LedStrip) arg;
        System.out.println("R: " + strip.getR() + " G: " + strip.getG() + " B: " + strip.getB());
    }
}
