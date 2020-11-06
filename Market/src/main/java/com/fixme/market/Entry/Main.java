package com.fixme.market.Entry;

import com.fixme.market.Sever.Db;
import com.fixme.market.Sever.PortInitiator;
import com.fixme.market.Models.Ports;

import java.util.ArrayList;

public class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        Db db = new Db();
        String [] instruments = {"Gold", "Silver", "Shop", "Sasol"};

        for(int i = 0; i < 4; i++)
        {
            db.InsertValues(instruments[i], (i + 1) * (30 * 20 + i), 30 + i * 2);
        }

        ArrayList<String> market = Db.getInstruments();
        for(int i = 0; i < market.size(); i++)
        {
            System.out.println(market.get(i));
        }

        PortInitiator portInitiator = new PortInitiator(Ports.getMarketPort(), db);
        Thread marketThread = new Thread(portInitiator);
        marketThread.start();
    }
}
