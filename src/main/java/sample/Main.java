package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.soulwing.snmp.*;

import java.io.*;
import java.net.URL;
import java.util.concurrent.*;
import java.util.concurrent.TimeoutException;


public class Main extends Application {
    static Mib mib = MibFactory.getInstance().newMib();
    static File file;
    static boolean getNext = false;
    private static Stage pStage;

    public static Stage getpStage() {
        return pStage;
    }

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

        loadMib(mib);

        URL url = new File("C:\\Users\\Erikc\\IdeaProjects\\SNMP\\src\\main\\resources\\sample.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        primaryStage.setTitle("SNMP-Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    static VarbindCollection read(String ip, String community, String getMethod) throws ExecutionException, InterruptedException {
        SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
        target.setAddress(ip);
        target.setCommunity(community);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        SnmpContext context = SnmpFactory.getInstance().newContext(target, mib);
        Future<VarbindCollection> future = executor.submit(new Callable<VarbindCollection>() {
            @Override
            public VarbindCollection call() {
                VarbindCollection varbinds = null;
                switch (getMethod) {
                    case "Basic" -> varbinds = context.get(".1.3.6.1.2.1.1.1.0", ".1.3.6.1.2.1.25.1.1.0", ".1.3.6.1.2.1.25.2.2.0", ".1.3.6.1.2.1.1.6.0", ".1.3.6.1.2.1.1.5.0", ".1.3.6.1.2.1.1.4.0", ".1.3.6.1.2.1.1.1.0").get();
                    case "getNext" -> {
                        varbinds = context.getNext(Controller.getInstance().getCommandOID()).get();
                        Controller.getInstance().setCommand(varbinds.get(0).getOid());
                    }
                    case "get" -> {
                        if (Controller.getInstance().getCommandOID().equals(".1.3")) {
                            Controller.getInstance().setCommand(".1.3.6.1.2.1.1.1.0");
                        }
                        varbinds = context.get(Controller.getInstance().getCommandOID()).get();
                    }
                }
                for (Varbind varbind : varbinds) {
                    if (varbind.getOid().equals(varbind.toString())) {
                        System.out.println(varbind.getOid() + "not found");
                    }
                }
                return varbinds;
            }
        });
        try {
            future.get(500, TimeUnit.MILLISECONDS);
        } catch (CancellationException | TimeoutException e) {
            future.cancel(true);
            executor.shutdownNow();
        }
        if (!future.isCancelled()) {
            return future.get();
        } else {
            return null;
        }


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
