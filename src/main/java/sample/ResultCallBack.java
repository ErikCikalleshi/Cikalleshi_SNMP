package sample;

import org.soulwing.snmp.SnmpCallback;
import org.soulwing.snmp.SnmpEvent;
import org.soulwing.snmp.SnmpException;
import org.soulwing.snmp.VarbindCollection;

public class ResultCallBack implements SnmpCallback<VarbindCollection> {
    @Override
    public void onSnmpResponse(SnmpEvent<VarbindCollection> snmpEvent) {
        try {
            VarbindCollection result = snmpEvent.getResponse().get();
            System.out.format("%s uptime %s\n",
                    result.get("sysName"),
                    result.get("sysUpTime"));
        }
        catch (SnmpException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            snmpEvent.getContext().close();
        }

    }
}
