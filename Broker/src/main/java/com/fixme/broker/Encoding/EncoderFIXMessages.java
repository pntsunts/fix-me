package com.fixme.broker.Encoding;

import java.util.Base64;
import java.util.Base64.Encoder;

public class EncoderFIXMessages
{
    private Encoder encoder;
    private static String encoded;
    private static String fix_string;

    public EncoderFIXMessages()
    {
        encoder = Base64.getEncoder();
    }

    public void EncodeFIXMessage(String fix_message)
    {
        fix_string = fix_message;
    }

    public String getEncoded()
    {
        encoded = encoder.encodeToString(fix_string.getBytes());
        return encoded;
    }
}
