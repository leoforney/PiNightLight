package tk.leoforney.pinightlight;

import com.pi4j.wiringpi.SoftPwm;

import java.awt.*;

/**
 * Created by Leo on 8/28/2017.
 */
public class LedStrip extends java.util.Observable {

    // R, G, B
    public static int[] pins = new int[]{0, 4, 6};
    private int r = 0,g = 0,b = 0;
    private int brightness = 100;
    private String lastHex = "00FF00";
    private static LedStrip instance;

    private LedStrip() {
        for (int pin: pins) {
            SoftPwm.softPwmCreate(pin, 0, 255);
        }
        turnOff();
    }

    public static LedStrip getInstance() {
        if (instance == null) {
            instance = new LedStrip();
        }
        return instance;
    }

    public double getBrightness() {
        return brightness;
    }

    /**
     * Sets the brightness of the led strip
     * @param brightness 0-100 double
     */
    public void setBrightness(int brightness) {
        this.brightness = (int) Range.clip(brightness,0, 100);
        SoftPwm.softPwmWrite(pins[0], getR());
        SoftPwm.softPwmWrite(pins[1], getG());
        SoftPwm.softPwmWrite(pins[2], getB());
    }
    // 0-255 for all
    public void setR(int value) {
        r = Math.toIntExact(Math.round((Range.clip(value, 0, 255))));
        setChanged();
        notifyObservers(this);
        SoftPwm.softPwmWrite(pins[0], r);
    }

    public int getR() {
        return Math.toIntExact(Math.round(((double) r) * ((double) brightness/100)));
    }

    public void setG(int value) {
        g = Math.toIntExact(Math.round((Range.clip(value, 0, 255))));
        setChanged();
        notifyObservers(this);
        SoftPwm.softPwmWrite(pins[1], g);
    }

    public int getG() {
        return Math.toIntExact(Math.round(((double) g) * ((double) brightness/100)));
    }

    public void setB(int value) {
        b = Math.toIntExact(Math.round((Range.clip(value, 0, 255))));
        setChanged();
        notifyObservers(this);
        SoftPwm.softPwmWrite(pins[2], b);
    }

    public int getB() {
        return Math.toIntExact(Math.round(((double) b) * ((double) brightness/100)));
    }

    public void turnOff() {
        r = 0;
        g = 0;
        b = 0;
        setChanged();
        notifyObservers(this);
        SoftPwm.softPwmWrite(pins[0], r);
        SoftPwm.softPwmWrite(pins[1], g);
        SoftPwm.softPwmWrite(pins[2], b);
    }

    public String getLastHex() {
        return lastHex;
    }

    public void setRGBfromHex(String colorStr) {
        if (colorStr.substring(0, 1).equals("#") && colorStr.substring(1).length() == 6) {
            lastHex = colorStr.substring(1);
            Color color = new Color(
                    Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                    Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                    Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
            r = color.getRed();
            g = color.getGreen();
            b = color.getBlue();
            setBrightness(brightness);
            setChanged();
            notifyObservers(this);
        }
    }

    public void close() {
        turnOff();
        for (int pin: pins) {
            SoftPwm.softPwmStop(pin);
        }
        instance = null;
    }

}
