package com.fixme.broker.Models;

public class FIXMessage
{
    private int ID;
    private String beginString = "8=FIX.4.4";
    private int MsgType;
    private int OrderType;
    private String Instrument;
    private int Quantity;
    private int Market;
    private int Price;
    private int checksum;

    public  FIXMessage(){}

    public  FIXMessage(int id, int OrderType, String Instrument, int Quantity, int Price)
    {
        this.setID(id);
        this.setOrderType(OrderType);
        this.setInstrument(Instrument);
        this.setQuantity(Quantity);
        this.setPrice(Price);
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

    public int getMsgType()
    {
        return MsgType;
    }

    public void setMsgType(int msgType)
    {
        MsgType = msgType;
    }

    public int getOrderType()
    {
        return OrderType;
    }

    public void setOrderType(int orderType)
    {
        OrderType = orderType;
    }

    public String getInstrument() {
        return Instrument;
    }

    public void setInstrument(String instrument)
    {
        Instrument = instrument;
    }

    public int getQuantity()
    {
        return Quantity;
    }

    public void setQuantity(int quantity)
    {
        Quantity = quantity;
    }

    public int getMarket()
    {
        return Market;
    }

    public void setMarket(int market)
    {
        Market = market;
    }

    public int getPrice()
    {
        return Price;
    }

    public void setPrice(int price)
    {
        Price = price;
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
}
