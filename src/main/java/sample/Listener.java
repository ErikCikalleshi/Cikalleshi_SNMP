package sample;

import org.soulwing.snmp.SnmpFactory;
import org.soulwing.snmp.SnmpListener;
import org.soulwing.snmp.SnmpNotificationEvent;
import org.soulwing.snmp.SnmpNotificationHandler;

public class Listener {
    private SnmpListener listener;
    private int SNMPPort;

    public Listener(int SNMPPort) {
        this.SNMPPort = SNMPPort;
        this.listener = SnmpFactory.getInstance().newListener(this.SNMPPort, Main.mib);
    }

    public void startListener() {
        try {
            if (listener != null) {
                System.out.println("Listener Started");
                listener.addHandler(snmpNotificationEvent -> {
                    System.out.println("received a notification: " + snmpNotificationEvent);
                    //System.out.println(  snmpNotificationEvent.getSubject().getVarbinds().get(0).getOid());
                    for (int i = 0; i < snmpNotificationEvent.getSubject().getVarbinds().size(); i++) {
                        Controller.getInstance().getTrapTable().getItems().add(new TrapTable(snmpNotificationEvent, i));
                    }

                    return true;
                });
            }
        }catch (Exception e){
            listener.close();
        }


    }

    public void stopListener() {
        if (listener != null) {
            System.out.println("Listener stopped");
            listener.close();
        }
    }

    public SnmpListener getListener() {
        return listener;
    }

    public void setListener(SnmpListener listener) {
        this.listener = listener;
    }

    public int getSNMPPort() {
        return SNMPPort;
    }

    public void setSNMPPort(int SNMPPort) {
        this.SNMPPort = SNMPPort;
    }
}
