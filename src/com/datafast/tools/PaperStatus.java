package com.datafast.tools;

import com.pos.device.printer.Printer;

public class PaperStatus {
    private int ret;
    Printer printer = Printer.getInstance();

    public int getRet() {
        ret = printer.getStatus();
        return Printer.PRINTER_OK;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }
}
