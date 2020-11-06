package com.fixme.router.Entry;

import com.fixme.router.Models.Ports;
import com.fixme.router.Server.PortNumCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;

public class Main
{
    private static ServerSocketChannel socketChannel;
    private static SocketChannel clientChannel;
    private static Selector portSelector;
    private static  Socket socket;
    private static  int[] portNumbers = {Ports.getBrokerPort(), Ports.getMarketPort()};
    private static int randomId;

    public static void main(String[] args) throws IOException
    {
        portSelector = Selector.open();
        for (int portNum : portNumbers)
        {
            socketChannel = ServerSocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.socket().bind(new InetSocketAddress("localhost",portNum));
            socketChannel.register(portSelector, SelectionKey.OP_ACCEPT);
        }

        while (true)
        {
            portSelector.select();
            Iterator<SelectionKey> selectionKeyIterator =  portSelector.selectedKeys().iterator();
            while (selectionKeyIterator.hasNext())
            {
                SelectionKey keys = selectionKeyIterator.next();
                if (keys.isAcceptable())
                {
                    clientChannel = ((ServerSocketChannel) keys.channel()).accept();
                    if (clientChannel != null)
                    {
                        Random rnd = new Random();
                        if (clientChannel.socket().getLocalPort() == 5000)
                        {
                            int number = rnd.nextInt(999999);
                            System.out.println("Id 5000 :"+ number);
                            System.out.println(clientChannel.socket().getLocalPort() + " "  + clientChannel.socket().getRemoteSocketAddress());
                            PortNumCreator portNumCreator = new PortNumCreator(clientChannel.socket().getLocalPort(), clientChannel.socket(), socket, number);
                            Thread brokerThread = new Thread(portNumCreator);
                            brokerThread.start();
                        }
                        else if (clientChannel.socket().getLocalPort() == 5001)
                        {
                            int number = rnd.nextInt(999999);
                            System.out.println("Id 5001 :"+ number);
                            socket = clientChannel.socket();
                            System.out.println(clientChannel.socket().getLocalPort() + " "+ clientChannel.socket().getRemoteSocketAddress());
                            PortNumCreator portNumCreator = new PortNumCreator(clientChannel.socket().getLocalPort(), clientChannel.socket(), socket, number);
                            Thread brokerThread = new Thread(portNumCreator);
                            brokerThread.start();
                        }
                    }
                }
            }
        }
    }

}
