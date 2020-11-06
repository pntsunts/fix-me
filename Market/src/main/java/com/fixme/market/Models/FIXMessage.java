package com.fixme.market.Models;

public class FIXMessage
{
    private int ID;
    private String beginString = "8=FIX.4.4";
    private String response;
    private int broker;
    private int checksum;

    public FIXMessage(){}

    public FIXMessage(int broker, int id, String response)
    {
        this.setID(id);
        this.setResponse(response);
        this.setBroker(broker);
    }

    public int getBroker() {
        return broker;
    }

    public void setBroker(int broker) {
        this.broker = broker;
    }

    public String getBeginString()
    {
        return beginString;
    }

    public void setBeginString(String beginString)
    {
        this.beginString = beginString;
    }

    public int getID()
    {
        return ID;
    }

    public void setID(int ID)
    {
        this.ID = ID;
    }

    public int getChecksum()
    {
        return checksum;
    }

    public void setChecksum(int checksum)
    {
        this.checksum = checksum;
    }

    public FIXMessage getMessage()
    {
        return this;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
