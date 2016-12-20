package com.cep.service.anomaly.esper2;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.UpdateListener;


public class FltDelayWithMaintEsperRule {

    private EPStatement statement;

    public FltDelayWithMaintEsperRule(EPAdministrator admin) {

        String stmt = "select fd[0].flightNumber as cnt from pattern " +
                "[every ([2] fd=FlightDelayMessage -> [2] m=MaintenanceMessage and not OverrideMessage) " +
                "where timer:within(10000 milliseconds)]";

        statement = admin.createEPL(stmt);
    }

    public void addListener(UpdateListener listener) {
        statement.addListener(listener);
    }
}


