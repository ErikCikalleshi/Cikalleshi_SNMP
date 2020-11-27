package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.soulwing.snmp.*;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Mib mib = MibFactory.getInstance().newMib();
        mib.load("SNMPv2-SMI");
        mib.load("SNMPv2-TC");
        mib.load("SNMPv2-CONF");
        mib.load("SNMP-FRAMEWORK-MIB");
        mib.load("SNMP-TARGET-MIB");
        mib.load("SNMP-NOTIFICATION-MIB");
        mib.load("IANA-ITU-ALARM-TC-MIB");
        mib.load("ITU-ALARM-TC-MIB");
        mib.load("INET-ADDRESS-MIB");
        mib.load("RFC1155-SMI");
        mib.load("RFC1158-MIB");
        mib.load("RFC-1212");
        mib.load("RFC1213-MIB");
        mib.load("RMON-MIB");
        mib.load("RFC1271-MIB");
        mib.load("TOKEN-RING-RMON-MIB");
        mib.load("RMON2-MIB");
        mib.load("ALARM-MIB");


        SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
        target.setAddress("192.168.1.41");
        target.setCommunity("public");

        SnmpContext context = SnmpFactory.getInstance().newContext(target, mib);
        try {
            VarbindCollection result = context.get(".1.3.6.1.2.1.1.1.0").get();
            System.out.println(result.get(0));
        } finally {
            context.close();
        }

        BorderPane root = new BorderPane();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
