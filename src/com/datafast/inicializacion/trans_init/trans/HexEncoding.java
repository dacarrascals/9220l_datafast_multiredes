package com.datafast.inicializacion.trans_init.trans;

/**
 *
 * @author Elkin Beltrán
 */
public class HexEncoding {


    public HexEncoding()
    {
    }

    public byte[] GetBytes (String s) {
        if (s.length() % 2 == 0) {
            return hex2byte (s.getBytes(), 0, s.length() >> 1);
        } else {
            throw new RuntimeException("Uneven number("+s.length()+") of hex digits passed to hex2byte.");
        }
    }

    public byte[] hex2byte (byte[] b, int offset, int len) {
        byte[] d = new byte[len];
        for (int i=0; i<len*2; i++) {
            int shift = i%2 == 1 ? 0 : 4;
            d[i>>1] |= Character.digit((char) b[offset+i], 16) << shift;
        }
        return d;
    }

     public String hexString(byte[] b) {
        String valueHex = "";
        int hi = 0;
        int lo = 0;
        for (int i=0; i<b.length; i++) {
            hi = ((b[i] >> 4) & 0x0F);
            lo = b[i] & 0x0F;
            valueHex = valueHex + Integer.toHexString(hi).toUpperCase() + Integer.toHexString(lo).toUpperCase();
        }
        return valueHex;
    }

    public String hexString(byte[] b, int offset, int len) {
        String valueHex = "";
        int hi = 0;
        int lo = 0;
        len += offset;
        for (int i=offset; i<len; i++) {
            hi = ((b[i] >> 4) & 0x0F);
            lo = b[i] & 0x0F;
            valueHex = valueHex + Integer.toHexString(hi).toUpperCase() + Integer.toHexString(lo).toUpperCase();
        }
        return valueHex;
    }

    public String hexDump (byte[] b) {
        return hexDump (b, 0, b.length);
    }

    public String hexDump(byte[] b, int offset, int len) {
        String valueHex = "";
        int hi = 0;
        int lo = 0;
        len += offset;
        for (int i=offset; i<len; i++) {
            hi = ((b[i] >> 4) & 0x0F);
            lo = b[i] & 0x0F;
            valueHex = valueHex + Integer.toHexString(hi).toUpperCase() + Integer.toHexString(lo).toUpperCase() + " ";
        }
        return valueHex;
    }

    public String getString(byte[] b, int offset, int len) {
        StringBuffer d = new StringBuffer(len);
        for (int i=offset; i<(offset + len); i++) {
            char c = (char) b[i];
            d.append (c);
        }
        return d.toString();
    }

    public String zeropad(String s, int len){
        return padleft(s, len, '0');
    }

    public String padleft(String s, int len, char c)
    {
        s = s.trim();
        StringBuffer d = new StringBuffer(len);
        int fill = len - s.length();
        while (fill-- > 0)
            d.append (c);
        d.append(s);
        return d.toString();
    }

}
