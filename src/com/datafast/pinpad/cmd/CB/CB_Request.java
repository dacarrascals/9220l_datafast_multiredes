package com.datafast.pinpad.cmd.CB;

import com.newpos.libpay.utils.ISOUtil;

public class CB_Request {

    private String requestCB;
    private String hash;

    public String getRequestCB() {
        return requestCB;
    }

    public void setRequestCB(String requestCB) {
        this.requestCB = requestCB;
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
