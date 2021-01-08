package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.soulwing.snmp.*;
import java.io.*;
import java.net.URL;


public class Main extends Application {
    static Mib mib = MibFactory.getInstance().newMib();
    static File file;
    private static Stage pStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        pStage = primaryStage;
        /*
            .1.3.6.1.2.1.1.1.0 --> Hardware Information
            .1.3.6.1.2.1.25.1.1.0 --> SystemUpTime
            .1.3.6.1.2.1.25.2.2.0 --> memorySize
            .1.3.6.1.2.1.1.6.0 --> Location
            .1.3.6.1.2.1.1.5.0 --> sysName
            .1.3.6.1.4.1.24681.1.2.8.0 --> IfNumber Nas
            .1.3.6.1.4.1.24681.1.2.2.0 --> SystemTotalMem Nas
            .1.3.6.1.2.1.1.4.0 --> sysContact
            .1.3.6.1.2.1.1.1.0 --> sysDescr
            .1.3.6.1.2.1.1.1.0
         */
        SNMPScanner.initialize();
        URL url = new File("C:\\Users\\Erikc\\IdeaProjects\\SNMP\\src\\main\\resources\\sample.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("src/main/java/sample/styles.css");
        primaryStage.setTitle("SNMP-Tool");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public static Stage getpStage() {
        return pStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
