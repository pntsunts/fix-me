package com.fixme.broker.Server;

import com.fixme.broker.Controllers.BrokerController;
import com.fixme.broker.Encoding.DecodeFIXMessages;
import com.fixme.broker.Encoding.EncoderFIXMessages;
import com.fixme.broker.Models.FIXMessage;
import com.fixme.broker.Models.Ports;
import com.fixme.broker.Models.TextColors;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class PortInitiator implements Runnable
{
    private int portNum;
    private static Socket socket;
    private static SocketChannel socketChannel;
    private static SocketAddress socketAddress;
    static BufferedReader reader, serverReader;
    private BrokerController controller;
    private OutputStreamWriter outputStreamWriter;
    private PrintWriter printWriter;
    private EncoderFIXMessages encoderFIXMessages;

    public PortInitiator(int portNum)
    {
        encoderFIXMessages = new EncoderFIXMessages();
        this.portNum = portNum;
        try
        {
            socketChannel = SocketChannel.open();
            socketAddress = new InetSocketAddress(Ports.getHost(), Ports.getBrokerPort());
            socketChannel.connect(socketAddress);
            socket = socketChannel.socket();

            reader = new BufferedReader(new InputStreamReader(System.in));
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            printWriter = new PrintWriter(outputStreamWriter, true);
            controller = new BrokerController(socket, printWriter);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        while(true)
        {
            String input = null;
            try
            {
                input = reader.readLine();
                if (input.equalsIgnoreCase("exit"))
                {
                   System.exit(1);
                }
                else
                {
                    executeRequest(input);
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
        try
        {
            FIXMessage message = controller.populateFIX(controller.splitInput(response));
            if (message != null && message.getOrderType() == 1)
            {
                controller.Purchase(processFIXMessage(message));
            }
            else if (message != null && message.getOrderType() == 2)
            {
                controller.Sell(processFIXMessage(message));
            }
            else
            {
                System.out.println(TextColors.RED + "\nInvalid Input. Follow this format:\n"+ TextColors.RESET + TextColors.GREEN + "|OrderType| |Quantity| |Instrument| |Price|\n" + TextColors.RESET);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String processFIXMessage(FIXMessage message)
    {
        String fix_string = message.getBeginString() + ",53=" + message.getID() + ",35=" + message.getQuantity() + ",48=" + message.getOrderType() +",30="+message.getInstrument()+ ",40="+ message.getPrice();
        encoderFIXMessages.EncodeFIXMessage(fix_string);
        String checkSum = (encoderFIXMessages.getEncoded()).getBytes().length % 256 + "";
        fix_string = (new DecodeFIXMessages(encoderFIXMessages.getEncoded())).getDecoded();
        fix_string = fix_string + ",10=" + checkSum;
        encoderFIXMessages.EncodeFIXMessage(fix_string);

        return encoderFIXMessages.getEncoded();
    }


    public static void closeSocket() throws IOException
    {
        if(socket != null)
        {
            socket.close();
        }
    }
}
