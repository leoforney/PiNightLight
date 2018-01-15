package tk.leoforney.pinightlight;

public class Response {

    public Response(String text) {
        speech = text;
        displayText = text;
    }

    public String speech;
    public String displayText;
    public String source = "pinightlight-webhook";
}
