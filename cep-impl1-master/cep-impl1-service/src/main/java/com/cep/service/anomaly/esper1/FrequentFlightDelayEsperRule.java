package com.cep.service.anomaly.esper1;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.UpdateListener;


public class FrequentFlightDelayEsperRule {

    private EPStatement statement;

    public FrequentFlightDelayEsperRule(EPAdministrator admin, long delayWindow, int anomalyCount) {

//        String stmt = "select count(*) as cnt from FlightDelayMessage.win:time_batch(" +
//                delayWindow +
//                " milliseconds) having count(*) >= " +
//                anomalyCount;

        String stmt = "select count(*) as cnt from FlightDelayMessage.win:time_batch(3000 milliseconds) having count(*) >= 6";

        statement = admin.createEPL(stmt);
    }

    public void addListener(UpdateListener listener) {
        statement.addListener(listener);
    }
}


