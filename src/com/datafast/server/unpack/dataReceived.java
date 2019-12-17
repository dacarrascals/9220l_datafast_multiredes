package com.datafast.server.unpack;

import android.util.Log;

import com.newpos.libpay.utils.ISOUtil;

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
        byte[] cmd = new byte[2];
        byte[] packLen = new byte[2];
        byte[] resP = null ;
        int len, lenTmp;
        int offset = 0;

        try
        {
            //Len
            System.arraycopy(aMsg, 0, packLen, 0, 2);
            len = lenTmp = Integer.parseInt(ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(packLen)),16);
            resP = new byte[len];
            offset+=2;
            //cmd+Data
            while (lenTmp > 0) {
                System.arraycopy(aMsg, offset, resP, 0, lenTmp);
                offset+=lenTmp;
                lenTmp -= offset;
            }

            //cmd
            System.arraycopy(resP, 0, cmd, 0, 2);

            dataRaw = new byte[len-2];
            System.arraycopy(resP, 2, dataRaw, 0, len-2);

            switch (ISOUtil.hex2AsciiStr(ISOUtil.byte2hex(cmd))) {
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
                default:
                    setCmd(NN);
                    break;
            }

        }
        catch(Exception e)
        {
            e.getMessage();
        }
    }

}
