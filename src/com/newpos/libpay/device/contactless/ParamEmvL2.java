package com.newpos.libpay.device.contactless;

import com.newpos.bypay.EmvL2App;
import com.newpos.bypay.EmvL2Options;

import java.io.Serializable;

public class ParamEmvL2 extends EmvL2App implements Serializable {

    public long otherAmount;
    public ParamEmvL2(){
        l2Options = new EmvL2Options();
        l2Options.Contact = true;
        l2Options.Contactless = true;
        l2Options.EntryPoint = true;
        l2Options.FallbackAid = false;
        l2Options.Magstripe = true;
        l2Options.Signal = false;
        l2Options.Zip  = false;

        TerminalConfigFileName = "TERMINAL";
        ProcessingConfigFileName ="PROCESSING";
        CapkFileName = "CAKEY";
        EntryPointConfigFileName ="ENTRY_POINT";
        RevokFileName="REVOK";

        DetectContact = false;
        DetectContactLess = true;
        DetectMagStripe = false;
        amount  = 0;
        tranType = 0;
    }


}
