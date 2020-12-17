package sample;

import org.soulwing.snmp.Varbind;

public class Varbinds {
    private final String name;
    private final String oid;
    private final String value;

    public Varbinds(Varbind varbind) {
        this.name = varbind.getName();
        this.oid = varbind.getOid();
        this.value = varbind.asString();
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
}
