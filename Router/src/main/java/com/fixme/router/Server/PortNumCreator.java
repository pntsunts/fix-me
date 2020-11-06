package com.fixme.router.Server;

import com.fixme.router.Controllers.RouterController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Map;

public class PortNumCreator implements Runnable {
    private int portNum;
    private ServerSocket socket;
    private ServerSocketChannel socketChannel;
    private SocketChannel clientChannel;
    private static Socket client, marketSocket;
    private BufferedReader reader;
    private OutputStreamWriter outputStreamWriter;
    private PrintWriter printWriter;
    private RouterController controller;
    private static ServerSocketChannel socket_Channel;
    private static SocketChannel client_Channel;
    private int uniqueID;
    private static Map<Integer, Socket> brokers = new Hashtable<Integer, Socket>();
    public PortNumCreator(int portNum, Socket socket, Socket marketSocket, int uniqueID)
    {
        this.portNum = portNum;
        this.client = socket;
        this.marketSocket = marketSocket;
        this.uniqueID = uniqueID;
        if (portNum == 5000)
        {
            brokers.put(uniqueID, socket);
            RouterController.brokers = brokers;
        }
        try
        {
            outputStreamWriter = new OutputStreamWriter(client.getOutputStream());
            printWriter = new PrintWriter(outputStreamWriter, true);
            controller = new RouterController(printWriter, portNum);
            controller.sendId("ID:" + uniqueID);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {

        try
        {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        while(true)
        {
            String line = null;
            try
            {
                line = reader.readLine();
                redirectRequest(line);
            }
            catch (Exception ex)
            {
                System.err.println(ex.getMessage());
            }
        }
    }

    public void redirectRequest(String req) throws Exception
    {
        System.out.println(req);
        controller.checkIncomingRequest(req, marketSocket);
    }


    public void closeSocket()
    {
        if (client != null)
        {
            try
            {
                client.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
