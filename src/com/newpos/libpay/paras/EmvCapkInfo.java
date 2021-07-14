package com.newpos.libpay.paras;

import com.pos.device.emv.CAPublicKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EmvCapkInfo implements Serializable {

	public static final String FILENAME = "default_capks.dat";
	private List<CAPublicKey> capkList;

	public EmvCapkInfo() {
		capkList = new ArrayList<CAPublicKey>();
	}

	public List<CAPublicKey> getCapkList() {
		return capkList;
	}

	public void setCapkList(List<CAPublicKey> capkList) {
		this.capkList = capkList;
	}
}
