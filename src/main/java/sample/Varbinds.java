package sample;

import org.soulwing.snmp.Varbind;

public class Varbinds {
    private final String name;
    private final String oid;
    private final String value;
    private final String ip;

    public Varbinds(Varbind varbind, String ip) {
        this.name = varbind.getName();
        this.oid = varbind.getOid();
        this.value = varbind.asString();
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getOID() {
        return oid;
    }

    public String getIP() {
        return ip;
    }
}
