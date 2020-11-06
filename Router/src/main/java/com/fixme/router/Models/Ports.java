package com.fixme.router.Models;

public class Ports
{
    private static int mPortNum = 5001;
    private static  int bPortNum = 5000;
    private static  String host = "localhost";

    public static int  getMarketPort()
    {
        return mPortNum;
    }

    public static int getBrokerPort()
    {
        return bPortNum;
    }

    public static String getHost()
    {
        return host;
    }
}
