package sample;

import org.soulwing.snmp.SnmpNotificationEvent;

public class TrapTable {
    private final String oidName;
    private final String type;
    private final String sourceIP;
    private final String valueTrap;

    public TrapTable(SnmpNotificationEvent event, int index) {
        this.oidName = event.getSubject().getVarbinds().get(index).getOid();
        this.type = event.getSubject().getType().toString();
        this.sourceIP = event.getSubject().getPeer().toString();
        this.valueTrap = String.valueOf(event.getSubject().getVarbinds().get(index));
    }

    public String getOidName() {
        return oidName;
    }

    public String getValueTrap() {
        return valueTrap;
    }

    public String getType() {
        return type;
    }

    public String getSourceIP() {
        return sourceIP;
    }
}
