package com.fixme.broker.Controllers;

import com.fixme.broker.Encoding.DecodeFIXMessages;
import com.fixme.broker.Models.FIXMessage;
import com.fixme.broker.Models.TextColors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BrokerController
{
    private PrintWriter printWriter;
    private Socket socket;
    private BufferedReader bufferedReader;
    public static String uniqueID = null;

    public BrokerController(Socket socket, PrintWriter printWriter) throws IOException
    {
        this.socket = socket;
        this.printWriter = printWriter;
        serverResponse();
    }

    public void Purchase(String encoded) throws IOException {
        printWriter.println(encoded);
        serverResponse();
    }

    public void Sell(String encoded) throws IOException {
        printWriter.println(encoded);
        serverResponse();
    }

    public void serverResponse() throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response = bufferedReader.readLine();
        if (response.contains("ID:"))
        {
            uniqueID = response.substring(response.indexOf(":") + 1);
        }
        else
        {
            String[] splitted = firstString(response).split(",");
            String[] value = splitted[3].split("=");
            if (value[1].equalsIgnoreCase("Accepted"))
                System.out.println(TextColors.RED + "\nResponse: "+value[1] + "\n" + TextColors.RESET);
            else
                if (value[1].equalsIgnoreCase("Rejected"))
                    System.out.println( TextColors.RED + "\nResponse: "+value[1] + "\n" + TextColors.RESET);
        }
    }

    public String[] splitInput(String input_request)
    {
        String [] input = null;

        if (!input_request.isEmpty())
            input = input_request.split(" ");

        return input;
    }

    public FIXMessage populateFIX(String[] input)
    {
        FIXMessage message = null;
        if (input != null && input.length != 0 && input.length == 4)
        {
            try
            {
                Integer.parseInt(input[1]);
            }
            catch (NumberFormatException ex)
            {
                System.err.println(ex.getMessage());
                return null;
            }

            if (input[0].equalsIgnoreCase("BUY"))
            {
                message = new FIXMessage(Integer.parseInt(uniqueID), 1, input[2], Integer.parseInt(input[1]), Integer.parseInt(input[3]));
            }
            else
                if (input[0].equalsIgnoreCase("SELL"))
                {
                    message = new FIXMessage(Integer.parseInt(uniqueID), 2, input[2], Integer.parseInt(input[1]),Integer.parseInt(input[3]));
                }
        }

        return message != null ? message.getMessage() : null;
    }

    public String firstString(String request)
    {
        request= (new DecodeFIXMessages(request)).getDecoded();
        String firstSegment = request.substring(0, request.lastIndexOf(","));
        return firstSegment;
    }

}
