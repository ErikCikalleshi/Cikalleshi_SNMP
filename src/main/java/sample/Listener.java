package sample;

import org.soulwing.snmp.SnmpFactory;
import org.soulwing.snmp.SnmpListener;

public class Listener {
    /**
     * Listener waits for x seconds for any Traps or Informs
     */
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
                    for (int i = 0; i < snmpNotificationEvent.getSubject().getVarbinds().size(); i++) { //for every Subject add new Item to the TrapTable
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
