package com.datafast.server.unpack;

import android.util.Log;

import com.newpos.libpay.utils.ISOUtil;

import static com.datafast.pinpad.cmd.defines.CmdDatafast.CB;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CP;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.CT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.LT;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.NN;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PA;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PC;
import static com.datafast.pinpad.cmd.defines.CmdDatafast.PP;

public class dataReceived {

    private String cmd;
    private byte[] dataRaw;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public byte[] getDataRaw() {
        return dataRaw;
    }

    public void setDataRaw(byte[] dataRaw) {
        this.dataRaw = dataRaw;
    }

    public void identifyCommand(byte[] aMsg)
    {
        byte[] packLen;
        int len, correctLen;
        int offset = 0;

        try
        {
            //len
            packLen = new byte[2];
            System.arraycopy(aMsg, offset, packLen, 0, 2);
            offset += 2;
            len = Integer.parseInt(ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(packLen)),16);
            correctLen = ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(aMsg)).trim().length() - 4;

            if (len != correctLen){
                len = correctLen;
            }

            //cmd
            packLen = new byte[2];
            System.arraycopy(aMsg, offset, packLen, 0, 2);
            offset += 2;
            switch (ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(packLen))) {
                case LT:
                    Log.i("CMD" , "LT");
                    setCmd(LT);
                    break;
                case CT:
                    Log.i("CMD" , "CT");
                    setCmd(CT);
                    break;
                case PP:
                    Log.i("CMD" , "PP");
                    setCmd(PP);
                    break;
                case CP:
                    setCmd(CP);
                    break;
                case PC:
                    setCmd(PC);
                    break;
                case PA:
                    setCmd(PA);
                    break;
                case CB:
                    setCmd(CB);
                    break;
                default:
                    setCmd(NN);
                    break;
            }

            dataRaw = new byte[len];
            System.arraycopy(aMsg, offset, dataRaw, 0, len);

        }
        catch(Exception e)
        {
            e.getMessage();
        }
    }

}
