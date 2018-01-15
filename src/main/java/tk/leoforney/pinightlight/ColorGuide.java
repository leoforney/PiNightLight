package tk.leoforney.pinightlight;

import java.util.HashMap;

public class ColorGuide {

    private HashMap<String, String> colorMap;

    public ColorGuide() {
        colorMap = new HashMap<>();
        colorMap.put("black", "#000000");
        colorMap.put("silver", "#C0C0C0");
        colorMap.put("gray", "#808080");
        colorMap.put("white", "#FFFFFF");
        colorMap.put("maroon", "#800000");
        colorMap.put("red", "#FF0000");
        colorMap.put("purple", "#800080");
        colorMap.put("fuchsia", "#FF00FF");
        colorMap.put("green", "#4CAF50");
        colorMap.put("lime", "#00FF00");
        colorMap.put("olive", "#808000");
        colorMap.put("yellow", "#FFFF00");
        colorMap.put("navy", "#000080");
        colorMap.put("blue", "#0000FF");
        colorMap.put("teal", "#009688");
        colorMap.put("aqua", "#00FFFF");
        colorMap.put("orange", "#FF9800");
    }

    public String getColor(String name) {
        boolean exists = colorMap.containsKey(name);
        if (exists) {
            return colorMap.get(name);
        } else {
            return "";
        }
    }

}
