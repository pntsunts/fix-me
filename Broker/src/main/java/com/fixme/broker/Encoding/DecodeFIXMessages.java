package com.fixme.broker.Encoding;

import java.util.Base64;
import java.util.Base64.Decoder;

public class DecodeFIXMessages
{
    private Decoder decoder ;
    private static String decoded;
    private static  String encoded_fix_string;
    private static  byte[] bytes;

    public DecodeFIXMessages(String encoded_fix_message)
    {
        decoder = Base64.getUrlDecoder();
        encoded_fix_string = encoded_fix_message;
    }

    public String getDecoded()
    {
        bytes = decoder.decode(encoded_fix_string);
        decoded = new String(bytes);
        return  decoded;
    }
}
