package sample;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class Client extends HBox {
    private Button test;

    public Client(String ip, String community) {
        this.test = new Button(ip);
        this.test.setOnAction(e -> {
            try {
                Controller.getInstance().load(ip, community);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        this.getChildren().addAll(this.test);
    }

    public Button getTest() {
        return test;
    }

    public void setTest(Button test) {
        this.test = test;
    }

}
