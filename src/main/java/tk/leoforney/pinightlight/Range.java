package tk.leoforney.pinightlight;

/**
 * Created by Leo on 9/2/2017.
 */
public class Range {
    public static double clip(double value, double min, double max) {
        double curr = value;
        if (curr < min) {
            curr = min;
        }
        if (curr > max) {
            curr = max;
        }
        return curr;
    }
}
