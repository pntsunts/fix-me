package com.fixme.market.Controllers;

import com.fixme.market.Encoding.DecodeFIXMessages;
import com.fixme.market.Encoding.EncoderFIXMessages;
import com.fixme.market.Models.FIXMessage;
import com.fixme.market.Sever.Db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MarketController
{
    private static Socket socket;
    private static PrintWriter printWriter;
    private static BufferedReader bufferedReader;
    private static EncoderFIXMessages encode;
    public static String uniqueID = null;
    private Db db;

    public MarketController (Socket sockets, PrintWriter writer, Db db) throws IOException {
        socket = sockets;
        printWriter = writer;
        this.db = db;
        encode = new EncoderFIXMessages();
        serverResponse();
    }

    public void ProcessBuy(int brokerId, String instrType_, int quantity_, int price_) throws IOException
    {
        if (this.db.buyInstrument(instrType_, quantity_, price_)){
            printWriter.println(processFIXMessage(populateFIX("Accepted", brokerId)));
        }
        else {
            printWriter.println(processFIXMessage(populateFIX("Rejected", brokerId)));
        }
    }

    public void ProcessSell(int brokerId, String instrType_, int quantity_, int price_) throws IOException
    {
        if(this.db.sellInstrument(instrType_, quantity_, price_))
        {
            printWriter.println(processFIXMessage(populateFIX("Accepted", brokerId)));
        }
        else
        {
            printWriter.println(processFIXMessage(populateFIX("Rejected", brokerId)));
        }
    }

    public void serverResponse() throws IOException
    {
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response = bufferedReader.readLine();
        if (response.contains("ID:"))
        {
            uniqueID = response.substring(response.indexOf(":") + 1);
        }
    }

    public boolean verifyCheckSum(String request)
    {
        request= (new DecodeFIXMessages(request)).getDecoded();
        String last = request.substring(request.lastIndexOf(",") + 1);
        String firstSegment = request.substring(0, request.lastIndexOf(","));
        String check = last.substring(last.lastIndexOf("=") + 1);

        encode.EncodeFIXMessage(firstSegment);
        String encoded = encode.getEncoded();

        if ((encoded.getBytes().length % 256) == Integer.parseInt(check))
        {
            return true;
        }

        return false;
    }

    public String firstString(String request)
    {
        request= (new DecodeFIXMessages(request)).getDecoded();
        String firstSegment = request.substring(0, request.lastIndexOf(","));
        return firstSegment;
    }

    public String processFIXMessage(FIXMessage message)
    {
        String fix_string = message.getBeginString() + ",53=" + message.getID() + ",70=" + message.getBroker() + ",50=" + message.getResponse();
        encode.EncodeFIXMessage(fix_string);
        String checkSum = (encode.getEncoded()).getBytes().length % 256 + "";
        fix_string = (new DecodeFIXMessages(encode.getEncoded())).getDecoded();
        fix_string = fix_string + ",10=" + checkSum;
        encode.EncodeFIXMessage(fix_string);

        return encode.getEncoded();
    }

    public FIXMessage populateFIX(String response, int brokerId)
    {
        FIXMessage message = null;
        message = new FIXMessage(brokerId, Integer.parseInt(uniqueID), response);

        return message != null ? message.getMessage() : null;
    }
}
