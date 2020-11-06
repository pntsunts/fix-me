package com.fixme.broker.Entry;

import com.fixme.broker.Models.Ports;
import com.fixme.broker.Server.PortInitiator;

import java.io.IOException;

public class Main
{
    public static void main(String[] args) throws IOException, InterruptedException {
        PortInitiator portInitiator = new PortInitiator(Ports.getBrokerPort());
        Thread brokerThread = new Thread(portInitiator);
        brokerThread.start();
    }
}
