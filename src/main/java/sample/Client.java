package sample;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Client {
    private Button arrow;
    private Button test;
    private Image image;
    private ImageView view;



    private HBox dump;
    public Client(String ip, String community) throws MalformedURLException {
        this.dump = new HBox();
        this.test = new Button(ip);
        this.test.setMinWidth(240);
        this.arrow = new Button();
        arrow.setStyle("-fx-background-color: white");
        //this.test.setMaxWidth(240);

        URL url = new File("C:\\Users\\Erikc\\IdeaProjects\\SNMP\\src\\main\\java\\sample\\arrow.jpg").toURI().toURL();
        image = new Image(String.valueOf(url));
        view = new ImageView(image);
        view.setFitWidth(20);
        view.setFitHeight(10);
        arrow.setGraphic(view);
        this.test.setOnAction(e -> {
            try {
                Controller.getInstance().load(ip, community);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        this.dump.setPadding(new Insets(0,0,0,10));
        this.dump.getChildren().addAll(test);
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
