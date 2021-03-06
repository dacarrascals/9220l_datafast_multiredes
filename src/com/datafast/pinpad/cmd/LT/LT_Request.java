package com.datafast.pinpad.cmd.LT;

import com.newpos.libpay.utils.ISOUtil;

public class LT_Request {

    private int countValid;
    private String requestLT;
    private String hash;

    public int getCountValid() {
        return countValid;
    }

    public void setCountValid(int countValid) {
        this.countValid = countValid;
    }

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

    public void UnPackHash(byte[] aData){
        setCorrectHash(aData);
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
            setCorrectHash(aData);

        }
        catch(Exception e)
        {
            e.getMessage();
            setCorrectHash(aData);
        }

        return;
    }

    private void setCorrectHash(byte[] aData){
        try{
            String correctHash = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(aData)).trim();
            correctHash = correctHash.substring(correctHash.length() - 32);
            if (hash == null || !correctHash.equals(hash)){
                hash = correctHash;
                if (hash.length() < 32){
                    hash = ISOUtil.spacepad(hash, 32);
                }
                countValid ++;
            }
        }catch (Exception e){
            e.printStackTrace();
            if (ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(aData)).length() != 32){
                hash = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(aData)).trim();
                if (hash.length() < 32){
                    hash = ISOUtil.spacepad(hash, 32);
                }
                countValid ++;
            }
        }
    }
}
