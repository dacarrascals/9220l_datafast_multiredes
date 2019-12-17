package com.datafast.pinpad.cmd.LT;

import com.newpos.libpay.utils.ISOUtil;

public class LT_Request {

    private String requestLT;
    private String hash;


    public String getRequestLT() {
        return requestLT;
    }

    public void setRequestLT(String requestLT) {
        this.requestLT = requestLT;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void UnPackData(byte[] aData){

        byte[] tmp = null;
        int offset = 0;

        try
        {
            //hash
            tmp = new byte[32];
            System.arraycopy(aData, offset, tmp, 0, 32);
            offset += 32;
            this.hash = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(tmp)).trim();

        }
        catch(Exception e)
        {
            e.getMessage();
        }

        return;
    }
}
