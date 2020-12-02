package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.percederberg.mibble.MibLoader;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.soulwing.snmp.*;

import java.io.File;
import java.lang.String;


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
        mib.load("HOST-RESOURCES-MIB");
        File file = new File("C:\\Users\\Erikc\\Downloads\\NAS.mib");
        mib.load(file);


        SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
        target.setAddress("10.10.30.254");
        target.setCommunity("public");
        /*
            .1.3.6.1.2.1.1.1.0 --> Hardware Information
            .1.3.6.1.2.1.25.1.1.0 --> SystemUpTime
            .1.3.6.1.2.1.25.2.2.0 --> memorySize
            .1.3.6.1.2.1.1.6.0 --> Location
            .1.3.6.1.2.1.1.5.0 --> Name
            .1.3.6.1.4.1.24681.1.2.8.0 --> IfNumber Nas
            .1.3.6.1.4.1.24681.1.2.2.0 --> SystemTotalMem Nas
         */

        SnmpContext context = SnmpFactory.getInstance().newContext(target, mib);
        try {
            String x = ".1.3.6.1.4.1.24681.1.2.1.0";
            VarbindCollection result = context.get(x).get();
            if(x.equals(".1.3.6.1.2.1.25.1.1.0") /*|| x.equals(".1.3.6.1.4.1.24681.1.3.13.0")*/){
                long time = Long.parseLong(String.valueOf(result.get(0)));
                TimeTicks t = new TimeTicks(time);
                System.out.println(t.toString());
            }else{
                //int kb = Integer.parseInt(String.valueOf(result.get(0))) / 1000;
                //System.out.println(kb + "megaByte");
                //OctetString octetString = new OctetString(String.valueOf(result.get(0)));
                System.out.println(result.get(0));
            }

        } finally {
            context.close();
        }

        BorderPane root = new BorderPane();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        //primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
