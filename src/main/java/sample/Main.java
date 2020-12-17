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
    @Override
    public void start(Stage primaryStage) throws Exception {
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
        URL url = new File("C:\\Users\\Erikc\\IdeaProjects\\SNMP\\src\\main\\resources\\sample.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1000, 1200));
        primaryStage.show();
    }

    static VarbindCollection read(String ip, String community) throws IOException {
        SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
        Mib mib = MibFactory.getInstance().newMib();
        loadMib(mib);
        File file = new File("C:\\Users\\Erikc\\Downloads\\NAS.mib");
        mib.load(file);
        target.setAddress(ip);
        target.setCommunity(community);
        SnmpContext context = SnmpFactory.getInstance().newContext(target, mib);
        VarbindCollection varbinds = context.get(".1.3.6.1.2.1.1.1.0", ".1.3.6.1.2.1.25.1.1.0", ".1.3.6.1.2.1.25.2.2.0", ".1.3.6.1.2.1.1.6.0", ".1.3.6.1.2.1.1.5.0", ".1.3.6.1.2.1.1.4.0", ".1.3.6.1.2.1.1.1.0").get();
        for (Varbind varbind : varbinds) {
            if(varbind.getOid().equals(varbind.toString())) {
                System.out.println(varbind.getOid() + "not found");
            }
            System.out.println(varbind.getOid() + " = " + varbind.toString());
        }
        //System.out.println(varbinds.asList());
        return varbinds;
    }

    private static void loadMib(Mib mib) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Erikc\\IdeaProjects\\SNMP\\src\\main\\java\\sample\\mib.txt"));
        String mibString;
        while ((mibString = br.readLine()) != null){
            try {
                mib.load(mibString);
            }catch (FileNotFoundException e){
                System.out.println("Module not found: " + e);
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
