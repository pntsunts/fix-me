package com.fixme.router.Controllers;

import com.fixme.router.Encoding.DecodeFIXMessages;
import com.fixme.router.Encoding.EncoderFIXMessages;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Set;

public class RouterController
{
    private PrintWriter printWriter;
    public static Map<Integer, Socket> brokers;
    private static EncoderFIXMessages encode;
    public RouterController(PrintWriter printWriter, int portNum)
    {
        this.printWriter = printWriter;
        encode = new EncoderFIXMessages();
    }

    public void sendResponse(String response) throws IOException
    {
        printWriter.println(response);
    }

    public void checkIncomingRequest(String response, Socket socket) throws Exception
    {
        if (verifyCheckSum(response))
        {
            if (firstString(response) != null)
            {
                String machine = "Market";
                String[] splitted = firstString(response).split(",");
                String[] value = splitted[1].split("=");

                int uniqueId = Integer.parseInt(value[1]);
                Set<Integer> keys = brokers.keySet();
                for (int i = 0; i < brokers.size(); i++)
                {
                    if (keys.iterator().next() == uniqueId)
                    {
                        machine = "Broker";
                    }
                }

                if(machine.equals("Broker"))
                {
                    sendToClient(response, socket);
                }
                else
                    if(machine.equals("Market"))
                    {
                        Socket clientSocket = null;
                        Set<Integer> elements = brokers.keySet();
                        for (int i = 0; i < brokers.size(); i++)
                        {
                            System.out.println(" : " + elements.iterator().next() + " : " + uniqueId);
                            if ((elements.iterator().next() == uniqueId) || (elements.iterator().next() == Integer.parseInt(splitted[2].split("=")[1])))
                            {
                                clientSocket = brokers.get(Integer.parseInt(splitted[2].split("=")[1]));
                            }
                        }
                        sendToClient(response, clientSocket);
                    }
            }
            else {
                sendResponse("Rejected");
            }
        }
        else
        {
            sendResponse("Rejected");
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

    public void sendToClient(String response, Socket socket) throws IOException
    {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        PrintWriter printWriter = new PrintWriter(outputStreamWriter, true);
        printWriter.println(response);
    }

    public void sendId(String id)
    {
        printWriter.println(id);
    }
}
