package sample;

import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;

public class Client {
    public final Hyperlink button;
    private final String ip;
    private final HBox HBox;

    public Client(String ip, String community) {
        this.HBox = new HBox();
        this.ip = ip;
        this.button = new Hyperlink(ip);
        this.button.setStyle("-fx-background-color: green; -fx-text-fill: white");
        this.button.setMinWidth(300);
        this.button.setOnAction(e -> {
            try {
                Controller.getInstance().getInformation(ip, community);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        HBox.setPadding(new Insets(0,0,0,10));
        HBox.getChildren().addAll(button);
    }

    public String getIp() {
        return ip;
    }

    public Hyperlink getTest() {
        return button;
    }

    public HBox getHBox() {
        return HBox;
    }
}
