package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.soulwing.snmp.*;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.TimeoutException;


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
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();
    }

    static VarbindCollection read(String ip, String community) throws IOException, ExecutionException, InterruptedException {
        SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
        Mib mib = MibFactory.getInstance().newMib();
        loadMib(mib);
        File file = new File("C:\\Users\\Erikc\\Downloads\\NAS.mib");
        mib.load(file);
        target.setAddress(ip);
        target.setCommunity("public");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        SnmpContext context = SnmpFactory.getInstance().newContext(target, mib);
        Future<VarbindCollection> future = executor.submit(new Callable<VarbindCollection>() {
            @Override
            public VarbindCollection call() throws Exception {
                VarbindCollection varbinds = context.get(".1.3.6.1.2.1.1.1.0", ".1.3.6.1.2.1.25.1.1.0", ".1.3.6.1.2.1.25.2.2.0", ".1.3.6.1.2.1.1.6.0", ".1.3.6.1.2.1.1.5.0", ".1.3.6.1.2.1.1.4.0", ".1.3.6.1.2.1.1.1.0").get();
                for (Varbind varbind : varbinds) {
                    if (varbind.getOid().equals(varbind.toString())) {
                        System.out.println(varbind.getOid() + "not found");
                    }
                    System.out.println(varbind.getOid() + " = " + varbind.toString());
                }
                System.out.println(varbinds.asList());
                return varbinds;
            }
        });

        try {
            future.get(2, TimeUnit.SECONDS); //timeout is in 5 seconds
        } catch (TimeoutException e) {
            System.err.println("Your Client is with SNMP not reachable");
            executor.shutdownNow();
            /*try {
                future.cancel(true);
            } catch (CancellationException c) {
                System.out.println("Task cancelled");
            }*/
        }
        VarbindCollection r = future.get();
        return r;
        /*System.out.println("3");
        Callable<VarbindCollection> callable = () -> {
            SnmpContext context = SnmpFactory.getInstance().newContext(target, mib);
            System.out.println("1");
            new Thread(new Runnable() {
                @Override
                public void run() {

                }
            });
            VarbindCollection varbinds = context.get(".1.3.6.1.2.1.1.1.0", ".1.3.6.1.2.1.25.1.1.0", ".1.3.6.1.2.1.25.2.2.0", ".1.3.6.1.2.1.1.6.0", ".1.3.6.1.2.1.1.5.0", ".1.3.6.1.2.1.1.4.0", ".1.3.6.1.2.1.1.1.0").get();
            System.out.println("2");
            System.out.println("asads");
            for (Varbind varbind : varbinds) {
                if (varbind.getOid().equals(varbind.toString())) {
                    System.out.println(varbind.getOid() + "not found");
                }
                System.out.println(varbind.getOid() + " = " + varbind.toString());
            }
            System.out.println(varbinds.asList());
            return varbinds;
        };
        Future<VarbindCollection> result = executor.submit(callable);
        if(!result.isDone()){
            System.out.println("dtes");
            VarbindCollection r = result.get();
            return r;
        }else{
            result.cancel(true);
            System.out.println("fads");
            executor.shutdownNow();
            return null;
        }*/

}

    private static void loadMib(Mib mib) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Erikc\\IdeaProjects\\SNMP\\src\\main\\java\\sample\\mib.txt"));
        String mibString;
        while ((mibString = br.readLine()) != null) {
            try {
                mib.load(mibString);
            } catch (FileNotFoundException e) {
                System.out.println("Module not found: " + e);
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
