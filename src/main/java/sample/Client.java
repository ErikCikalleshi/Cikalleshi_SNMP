package sample;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import java.net.MalformedURLException;

public class Client {
    private Button arrow;
    private Button test;
    private Image image;
    private ImageView view;
    private String ip;


    private HBox dump;


    public Client(String ip, String community) throws MalformedURLException {
        this.ip = ip;
        this.dump = new HBox();
        this.test = new Button(ip);
        this.test.setMinWidth(240);
        this.arrow = new Button();
        arrow.setStyle("-fx-background-color: white");
        this.test.setOnAction(e -> {
            try {
                Controller.getInstance().getInformation(ip, community);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        this.dump.setPadding(new Insets(0,0,0,10));
        this.dump.getChildren().addAll(test);
    }

    public String getIp() {
        return ip;
    }

    public HBox getDump() {
        return dump;
    }

    public void setDump(HBox dump) {
        this.dump = dump;
    }
    public Button getTest() {
        return test;
    }

    public void setTest(Button test) {
        this.test = test;
    }
    public Button getArrow() {
        return arrow;
    }

    public void setArrow(Button arrow) {
        this.arrow = arrow;
    }
}
