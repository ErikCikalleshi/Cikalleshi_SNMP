package sample;

import org.soulwing.snmp.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.TimeoutException;

public class SNMPScanner {

    public static void initialize() throws IOException {
        SNMPScanner.loadMib(Main.mib);
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

    public static VarbindCollection read(String ip, String community, String getMethod) throws ExecutionException, InterruptedException {
        SimpleSnmpV2cTarget target = new SimpleSnmpV2cTarget();
        target.setAddress(ip);
        target.setCommunity(community);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        SnmpContext context = SnmpFactory.getInstance().newContext(target, Main.mib);
        Future<VarbindCollection> future = executor.submit(() -> {
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
}
