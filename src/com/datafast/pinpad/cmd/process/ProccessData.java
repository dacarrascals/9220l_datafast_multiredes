package com.datafast.pinpad.cmd.process;

import com.newpos.libpay.utils.ISOUtil;

public class ProccessData {

    private StringBuilder data;

    public ProccessData() {
        data = new StringBuilder();
    }

    public void setData(String value) {
        data.append(value);
    }

    public byte[] getByteData(boolean addHash) {

        byte[] temp1 = new byte[12000];
        byte[] newData = null;
        byte[] len = new byte[2];
        byte[] temp2 = null;
        byte[] packet = null;
        int dataLen = -1;

        temp2 = data.toString().getBytes();
        dataLen = temp2.length;

        System.arraycopy(temp2, 0, temp1, 0, dataLen);
        packet = new byte[dataLen];
        System.arraycopy(temp1, 0, packet, 0, dataLen);


        if (!addHash) {
            int lenTmp = dataLen - 32;
            newData = new byte[dataLen + 4];
            len[0] = (byte) (lenTmp >> 8);
            len[1] = (byte) lenTmp;
        } else {
            newData = new byte[packet.length + 4];
            len[0] = (byte) (packet.length >> 8);
            len[1] = (byte) packet.length;
        }

        System.arraycopy(ISOUtil.hexString1(len).getBytes(), 0, newData, 0, 4);
        System.arraycopy(packet, 0, newData, 4, packet.length);

        return newData;
    }
}
