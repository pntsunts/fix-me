package com.fixme.market.Sever;

import com.fixme.market.Controllers.MarketController;
import com.fixme.market.Encoding.EncoderFIXMessages;
import com.fixme.market.Models.Ports;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class PortInitiator implements Runnable
{
    private int portNum;
    private static OutputStreamWriter outputStreamWriter;
    private static PrintWriter printWriter;
    private static Socket socket;
    private SocketChannel socketChannel;
    private SocketAddress socketAddress;
    private BufferedReader reader;
    private MarketController controller;
    private EncoderFIXMessages encoderFIXMessages;
    private Db db;

    public PortInitiator(int portNum, Db db)
    {
        encoderFIXMessages = new EncoderFIXMessages();
        this.portNum = portNum;
        this.db = db;
        try
        {
            socketChannel = SocketChannel.open();
            socketAddress = new InetSocketAddress(Ports.getHost(), Ports.getMarketPort());
            socketChannel.connect(socketAddress);
            socket = socketChannel.socket();

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            printWriter = new PrintWriter(outputStreamWriter, true);
            controller = new MarketController(socket, printWriter, this.db);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        String input = null;
        while(true)
        {
            try
            {
                input = reader.readLine();
                executeRequest(input);
                if (input.equalsIgnoreCase("exit"))
                {
                    System.exit(0);
                }
            }
            catch (Exception ex)
            {
                System.err.println(ex.getMessage());
            }
        }
    }

    public void executeRequest(String response)
    {
        if (!response.isEmpty())
        {

            if (controller.verifyCheckSum(response))
            {
                Map<Integer, String> keys = new Hashtable<Integer, String>();
                String[] splitted = controller.firstString(response).split(",");
                for (int i = 0; i < splitted.length; i++) {
                    String[] value = splitted[i].split("=");
                    keys.put(Integer.parseInt(value[0]), value[1]);
                }

                try {
                    if (keys.get(48).equalsIgnoreCase("1")) {
                        System.out.println("\n************ BUYING REQUEST ************");

                        controller.ProcessBuy(Integer.parseInt(keys.get(53)), keys.get(30), Integer.parseInt(keys.get(35)), Integer.parseInt(keys.get(40)));
                        ArrayList<String> market = Db.getInstruments();
                        for(int i = 0; i < market.size(); i++)
                        {
                            System.out.println(market.get(i));
                        }
                    } else if (keys.get(48).equalsIgnoreCase("2")) {
                        System.out.println("\n************ SELLING REQUEST ************");

                        controller.ProcessSell(Integer.parseInt(keys.get(53)), keys.get(30), Integer.parseInt(keys.get(35)), Integer.parseInt(keys.get(40)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void closeSocket() throws IOException
    {
        if (socket != null)
        {
            socket.close();
        }
    }
}
