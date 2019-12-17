package com.newpos.libpay.trans.manager;

import android.content.Context;

import com.newpos.libpay.Logger;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.global.TMConstants;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.paras.EmvAidInfo;
import com.newpos.libpay.paras.EmvCapkInfo;
import com.newpos.libpay.paras.TerminalAid;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.utils.ISOException;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.emv.CAPublicKey;
import com.pos.device.emv.EMVHandler;
import com.pos.device.emv.IEMVHandler;
import com.pos.device.emv.TerminalAidInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DparaTrans extends Trans implements TransPresenter{

	public DparaTrans(Context ctx , String transEName  , TransInputPara p) {
		super(ctx, transEName);
		para = p ;
		setTraceNoInc(false);
		TransEName = transEName ;
		if(para != null){
			transUI = para.getTransUI();
		}
	}

	@Override
	public ISO8583 getISO8583() {
		return iso8583;
	}

	@Override
	public void start() {
		timeout = 60 * 1000 ;
		transUI.handling(timeout , Tcode.Status.downing_aid);
		if(cfg.isOnline()){
			retVal = DownloadAid();
			if(retVal!=0){
				transUI.showError(timeout , retVal);
			}else {
				transUI.handling(timeout ,Tcode.Status.downing_capk);
				retVal = DownloadCapk();
				if(retVal!=0){
					transUI.showError(timeout , retVal);
				}else {
					loadAIDCAPK2EMVKernel();
					transUI.trannSuccess(timeout , Tcode.Status.downing_succ);
				}
			}
		}else {
			PAYUtils.copyAssetsToData(context , TMConstants.LOCALAID);
			PAYUtils.copyAssetsToData(context , TMConstants.LOCALCAPK);
			loadAIDCAPK2EMVKernel();
			transUI.trannSuccess(timeout , Tcode.Status.downing_succ);
		}
	}

	private List<byte[]> capkQueryList;
	private List<byte[]> aidQueryList;
	private EmvAidInfo emvAidInfo;
	private EmvCapkInfo emvCapkInfo;

	/***
	 * MSGID serFixDate获取 bitmap 组包自动生成41域 从config获取 42域 从config获取 60域 60_1
	 * 交易类型码，和60_3网络管理信息码从setfixdate获取 60_2批次号从config获取 62域
	 */
	private void setFields() {
		iso8583.clearData();
		if (MsgID != null){
			iso8583.setField(0, MsgID);
		}
		iso8583.setField(41, TermID); // 41
		if (MerchID != null){
			iso8583.setField(42, MerchID);// 42
		}
		if (Field60 != null){
			iso8583.setField(60, Field60);// 60
		}
		if (Field62 != null){
			iso8583.setField(62, Field62);// 62
		}
	}

	/**
	 * 查询EMV公钥
	 * QUERY_EMV_CAPK=0820,,,00,372,EMV公钥查询
	 * DOWNLOAD_EMV_CAPK=0800,,,00,370,EMV公钥下载
	 * DOWNLOAD_EMV_CAPK_END=0800,,,00,371,EMV公钥下载结束
	 * QUERY_EMV_PARAM=0820,,,00,382,EMV参数查询
	 * DOWNLOAD_EMV_PARAM=0800,,,00,380,EMV参数下载
	 * DOWNLOAD_EMV_PARAM_END=0800,,,00,381,EMV参数下载结束
	 * @throws ISOException
	 */
	private int queryEMVCapk() throws ISOException {
		int reciveCount = 0;
		int offset = 0;
		int totalLen = 0;
		byte[] b1 = { (byte) 0x9f, 0x06, 0x05 };
		byte[] b2 = { (byte) 0x9f, 0x22, 0x01 };
		byte[] b3 = { (byte) 0xdf, 0x05 };
		byte[] temp = null;
		TransEName = Type.QUERY_EMV_CAPK ;
		setFixedDatas();
		// 设置62域
		while (true) {
			offset = 0;
			Field62 = ISOUtil.byte2hex(
					("1" + ISOUtil.padleft(reciveCount + "", 2, '0'))
							.getBytes());
			setFields();
			retVal = OnLineTrans();
			if (retVal != 0) {
				return retVal;
			}
			// 缓存
			String rspCode = iso8583.getfield(39);
			if (null != rspCode && rspCode.equals("00")) {
				String str62 = iso8583.getfield(62);
				if (null == str62) {
					return Tcode.T_receive_err;
				}
				byte[] field62 = ISOUtil.str2bcd(str62, false);
				totalLen = field62.length;
				if (field62[0] == 0x30) {
					// 已经没有了或者最后
					break;
				}
				offset++;
				while (offset < totalLen) {
					temp = new byte[6];
					if (!ISOUtil.memcmp(b1, 0, field62, offset, 3)) {
						return -1;
					}
					offset += 3;
					System.arraycopy(field62, offset, temp, 0, 5);
					offset += 5;

					if (!ISOUtil.memcmp(b2, 0, field62, offset, 3)) {
						return -1;
					}
					offset += 3;
					System.arraycopy(field62, offset, temp, 5, 1);
					offset++;

					if (!ISOUtil.memcmp(b3, 0, field62, offset, 2)) {
						return -1;
					}
					offset += 2;
					offset += field62[offset] + 1; // 跳过不处理有效期
					capkQueryList.add(temp);
					reciveCount++;
				}
				if (field62[0] != 0x32) {
					// 没有后续包
					break;
				}
			} else {
				return Tcode.T_receive_err;
			}
		}
		return 0;
	}

	/**
	 * 查询EMV参数
	 * @return
	 * @throws ISOException
     */
	private int queryEMVParam() throws ISOException {
		Logger.debug("DparaTrans>>queryEMVParam");
		int reciveCount = 0;
		int offset = 0;
		int totalLen = 0;
		byte[] temp = null;
		int aidLen = 0;
		TransEName = Type.QUERY_EMV_PARAM ;
		setFixedDatas();
		// 设置62域
		while (true) {
			offset = 0;
			Field62 = ISOUtil.byte2hex(
					("1" + ISOUtil.padleft(reciveCount + "",2, '0'))
							.getBytes());// 313130
			setFields();
			retVal = OnLineTrans();
			if (retVal != 0) {
				return retVal;
			}
			// 缓存
			String rspCode = iso8583.getfield(39);
			if (null != rspCode && rspCode.equals("00")) {
				String str62 = iso8583.getfield(62);
				if (null == str62) {
					return Tcode.T_receive_err;
				}
				byte[] field62 = ISOUtil.str2bcd(str62, false);
				totalLen = field62.length;
				if (field62[0] == 0x30) {
					// 已经没有了或者最后
					break;
				}
				offset++;
				while (offset < totalLen) {
					if (!ISOUtil.memcmp(new byte[] { (byte) 0x9f, 0x06 }, 0, field62, offset, 2)) {
						return -1;
					}
					offset += 2;

					aidLen = field62[offset++];
					temp = new byte[aidLen];
					System.arraycopy(field62, offset, temp, 0, aidLen);
					offset += aidLen;
					aidQueryList.add(temp);
					reciveCount++;
				}
				if (field62[0] != 0x32) {
					// 没有后续包
					break;
				}
			} else{
				return Tcode.T_receive_err;
			}
		}
		return 0;
	}

	/**
	 * 下载EMV公钥
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
     */
	private int downEMVCapk() throws IOException {
		TransEName = Type.DOWNLOAD_EMV_CAPK ;
		setFixedDatas();
		byte[] pack_result_rid = null;
		byte[] pack_result_index = null;
		byte[] result = null;
		int packLen = 0;
		int offset = 0;
		int totalLen = 0;
		int Tag = 0;
		int Len = 0;
		CAPublicKey capk = null;
		byte[] temp = null;

		// 设置62域
		for (byte[] item : capkQueryList) {
			// 9F06 05 A000000333 9F22 01 02
			offset = 0;
			pack_result_rid = new byte[8];
			pack_result_index = new byte[4];
			result = new byte[12];
			packLen = PAYUtils.pack_tlv_data(pack_result_rid, 0x9F06, 5, item, 0);
			System.arraycopy(pack_result_rid, 0, result, 0, packLen);
			packLen = PAYUtils.pack_tlv_data(pack_result_index, 0x9F22, 1, item, 5);
			System.arraycopy(pack_result_index, 0, result, pack_result_rid.length, packLen);
			Field62 = ISOUtil.byte2hex(result);
			setFields();
			retVal = OnLineTrans();
			if (retVal != 0) {
				return retVal;
			}
			// 缓存
			String rspCode = iso8583.getfield(39);
			if (null != rspCode && rspCode.equals("00")) {
				String str62 = iso8583.getfield(62);
				if (null == str62) {
					return Tcode.T_receive_err;
				}
				byte[] field62 = ISOUtil.str2bcd(str62, false);
				totalLen = field62.length;
				if (field62[0] == 0x30) {
					// 已经没有了或者最后
					break;
				}
				offset++;
				capk = new CAPublicKey();
				while (offset < totalLen) {
					// get tag
					if ((field62[offset] & 0x1f) == 0x1f) {
						Tag = ISOUtil.byte2int(field62, offset, 2);
						offset += 2;
					} else {
						Tag = ISOUtil.byte2int(new byte[] { field62[offset++] });
					}
					// get Len
					Len = ISOUtil.byte2int(new byte[] { field62[offset++] });
					if ((Len & (byte) 0x80) != 0) {
						int lenL = Len & 3;
						Len = ISOUtil.byte2int(field62, offset, lenL);
						offset += lenL;
					}
					switch (Tag) {
					case 0x9F06: // RID
						temp = new byte[Len];
						System.arraycopy(field62, offset, temp, 0, Len);
						offset += Len;
						capk.setRID(temp);
						break;
					case 0x9F22: // IDX
						temp = new byte[Len];
						System.arraycopy(field62, offset, temp, 0, Len);
						offset += Len;
						capk.setIndex(ISOUtil.byte2int(temp));
						break;
					case 0xDF05: // EXP DATE
						temp = new byte[3];
						if (Len == 4) {
							System.arraycopy(field62, offset + 1, temp, 0, 3);
							offset += Len;
						} else {
							temp = ISOUtil.hex2byte(field62, offset + 2, 3);
							offset += Len;
						}
						capk.setExpirationDate(temp);
						break;
					case 0xDF02: // 公钥模
						temp = new byte[Len];
						System.arraycopy(field62, offset, temp, 0, Len);
						offset += Len;
						capk.setLenOfModulus(Len);
						capk.setModulus(temp);
						break;
					case 0xDF04: // 公钥指数
						temp = new byte[Len];
						System.arraycopy(field62, offset, temp, 0, Len);
						offset += Len;
						capk.setLenOfExponent(Len);
						capk.setExponent(temp);
						break;
					case 0xDF03: // SHA
						temp = new byte[Len];
						System.arraycopy(field62, offset, temp, 0, Len);
						offset += Len;
						if(temp.length > 20){
							try {
								byte[] rid = capk.getRID();
								Logger.debug("gm rid = "+ISOUtil.hexString(rid));
								byte[] index = ISOUtil.int2byte(capk.getIndex());
								Logger.debug("gm index = "+ISOUtil.hexString(index));
								byte[] mod = capk.getModulus();
								Logger.debug("gm mod = "+ISOUtil.hexString(mod));
								byte[] exponent = capk.getExponent();
								Logger.debug("gm exponent = "+ISOUtil.hexString(exponent));
								byte[] gm_in = new byte[rid.length+index.length+mod.length+exponent.length];
								int gmoffset = 0 ;
								System.arraycopy(rid , 0 , gm_in , gmoffset , rid.length);
								gmoffset += rid.length ;
								System.arraycopy(index , 0 , gm_in , gmoffset , index.length);
								gmoffset += index.length ;
								System.arraycopy(mod , 0 , gm_in , gmoffset , mod.length);
								gmoffset += mod.length ;
								System.arraycopy(exponent , 0 , gm_in , gmoffset , exponent.length);
								Logger.debug("gm in = "+ISOUtil.hexString(gm_in));
								temp = MessageDigest.getInstance("SHA-1").digest(gm_in);
								Logger.debug("gm out = "+ISOUtil.hexString(temp));
							} catch (NoSuchAlgorithmException e) {
								Logger.error("Exception" + e.toString());
							}
						}
						Logger.debug("CheckSum SHA = "+ISOUtil.hexString(temp));
						capk.setChecksum(temp);
						capk.setLenOfExponent(capk.getExponent().length);
						break;
					default:
						offset += Len;
						break;
					}
				}
				if (capk.getModulus() == null || capk.getRID() == null
						|| capk.getExponent() == null) {
					continue;
				}
				emvCapkInfo.getCapkList().add(capk);
				if (field62[0] != 0x31) {
					// 没有后续包
					break;
				}
			} else {
				return Tcode.T_receive_err;
			}
		}
		return 0;
	}

	/**
	 * 下载EMV参数
	 * @return
     */
	private int downEMVParam() {
		Logger.debug("DparaTrans>>downEMVParam");
		TransEName = Type.DOWNLOAD_EMV_PARAM ;
		setFixedDatas();
		byte[] pack_result_aid = null;
		int offset = 0;
		int totalLen = 0;
		int Tag = 0;
		int Len = 0;
		TerminalAid aid = null;
		TerminalAidInfo aidInfo = null;
		byte[] temp = null;

		// 设置62域
		for (byte[] item : aidQueryList) {
			// 9F06 07 A000000003 1010 aid
			offset = 0;
			pack_result_aid = new byte[item.length + 3];
			PAYUtils.pack_tlv_data(pack_result_aid, 0x9F06, item.length, item, 0);
			Field62 = ISOUtil.byte2hex(pack_result_aid);
			setFields();
			retVal = OnLineTrans();
			if (retVal != 0) {
				return retVal;
			}
			// 缓存
			String rspCode = iso8583.getfield(39);
			if (null != rspCode && rspCode.equals("00")) {
				String str62 = iso8583.getfield(62);
				if (null == str62) {
					return Tcode.T_receive_err;
				}
				Logger.debug("str62 = "+str62);
				byte[] field62 = ISOUtil.str2bcd(str62, false);
				totalLen = field62.length;
				if (field62[0] == 0x30) {
					// 已经没有了或者最后
					break;
				}
				offset++;
				aid = new TerminalAid();
				aidInfo = new TerminalAidInfo();
				System.out.println("s62filed = "+ISOUtil.hexString(field62));
				while (offset < totalLen) {
					// get tag
					if ((field62[offset] & 0x1f) == 0x1f) {
						Tag = ISOUtil.byte2int(field62, offset, 2);
						offset += 2;
					} else {
						Tag = ISOUtil
								.byte2int(new byte[] { field62[offset++] });
					}
					// get Len
					Len = ISOUtil.byte2int(new byte[] { field62[offset++] });
					if ((Len & (byte) 0x80) != 0) {
						int lenL = Len & 3;
						Len = ISOUtil.byte2int(field62, offset, lenL);
						offset += lenL;
					}

					switch (Tag) {
						case 0x9F06: // AID
							temp = new byte[Len];
							System.arraycopy(field62, offset, temp, 0, Len);
							offset += Len;
							aidInfo.setAId(temp);
							aidInfo.setAIDdLength(Len);
							System.out.println("AID = " + ISOUtil.hexString(temp));
							System.out.println("AID_LEN = " + Len);
							break;
						case 0x9F1B: // 最低限额
							temp = new byte[Len];
							System.arraycopy(field62, offset, temp, 0, Len);
							offset += Len;
							aidInfo.setTerminalFloorLimit(ISOUtil.bcd2int(temp, 0, Len));
							System.out.println("TerminalFloorLimit = " + ISOUtil.bcd2int(temp, 0, Len));
							break;
						case 0x9F08: // 应用版本号(卡片),没用吧??银联POSP送这个是错的，应该改为9F09
						case 0x9F09: // 应用版本号(终端),银联POSP送没有送这个
							temp = new byte[Len];
							System.arraycopy(field62, offset, temp, 0, Len);
							offset += Len;
							aidInfo.setApplicationVersion(temp);
							System.out.println("ApplicationVersion = " + ISOUtil.hexString(temp));
							break;
						case 0xDF01: // ASI,是否允许AID部分匹配
							temp = new byte[Len];
							System.arraycopy(field62, offset, temp, 0, Len);
							offset += Len;
							if (temp[0] == 0) {
								aidInfo.setSupportPartialAIDSelect(true);
							}else {
								aidInfo.setSupportPartialAIDSelect(false);
							}
							System.out.println("是否允许AID部分匹配 = " + (temp[0] == 0 ? true : false));
							break;
						case 0xDF11: // TAC缺省
							temp = new byte[Len];
							System.arraycopy(field62, offset, temp, 0, Len);
							offset += Len;
							aidInfo.setTerminalActionCodeDefault(temp);
							System.out.println("TerminalActionCodeDefault = " + ISOUtil.hexString(temp));
							break;
						case 0xDF12: // TAC联机
							temp = new byte[Len];
							System.arraycopy(field62, offset, temp, 0, Len);
							offset += Len;
							aidInfo.setTerminalActionCodeOnline(temp);
							System.out.println("TerminalActionCodeOnline = " + ISOUtil.hexString(temp));
							break;
						case 0xDF13: // TAC拒绝
							temp = new byte[Len];
							System.arraycopy(field62, offset, temp, 0, Len);
							offset += Len;
							aidInfo.setTerminalActionCodeDenial(temp);
							System.out.println("TerminalActionCodeDenial = " + ISOUtil.hexString(temp));
							break;
						case 0xDF15: // 偏置随机选择阈值
							System.out.println("偏置随机选择阈值 = " + ISOUtil.bcd2int(field62, offset, Len));
							aidInfo.setThresholdValue(ISOUtil.bcd2int(field62, offset, Len));
							offset += Len;
							break;
						case 0xDF16: // 随机选择最大%
							System.out.println("MaximumTargetPercentage = " + ISOUtil.bcd2int(field62, offset, Len));
							aidInfo.setMaximumTargetPercentage(ISOUtil.bcd2int(field62, offset, Len));
							offset += Len;
							break;
						case 0xDF17: // 选择目标%
							System.out.println("TargetPercentage = " + ISOUtil.bcd2int(field62, offset, Len));
							aidInfo.setTargetPercentage(ISOUtil.bcd2int(field62, offset, Len));
							offset += Len;
							break;
						case 0xDF14: // 缺省DDOL
							temp = new byte[Len];
							System.arraycopy(field62, offset, temp, 0, Len);
							offset += Len;
							aidInfo.setLenOfDefaultDDOL(Len);
							aidInfo.setDefaultDDOL(temp);
							System.out.println("LenOfDefaultDDOL = " + Len);
							System.out.println("DefaultDDOL = " + ISOUtil.hexString(temp));
							break;
						case 0xDF19:
							System.out.println("ClFloorLimit = " + ISOUtil.bcd2int(field62, offset, Len));
							aidInfo.setClFloorLimit(ISOUtil.bcd2int(field62, offset, Len));
							offset += Len;
							break;
						case 0xDF20:
							System.out.println("ClReaderMaxTransAmount = " + ISOUtil.bcd2int(field62, offset, Len));
							aidInfo.setClReaderMaxTransAmount(ISOUtil.bcd2int(field62, offset, Len));
							offset += Len;
							break;
						case 0xDF21:
							System.out.println("ClCVMAmount = " + ISOUtil.bcd2int(field62, offset, Len));
							aidInfo.setClCVMAmount(ISOUtil.bcd2int(field62, offset, Len));
							offset += Len;
							break;
						case 0xDF18: // 联机PIN支持能力 1:支持;0不支持
							if (field62[offset++] == 0) {
								aid.setisSurportOnlinePin(false);
								System.out.println(" 联机PIN支持能力 = " + false);
							}else {
								aid.setisSurportOnlinePin(true);
								System.out.println(" 联机PIN支持能力 = " + true);
							}
							break;
						case 0x9F7B:
							System.out.println("lEcMaxTxnAmount = " + ISOUtil.bcd2int(field62, offset, Len));
							aid.setlEcMaxTxnAmount(ISOUtil.bcd2int(field62, offset, Len));
							offset += Len;
							break;
						default:
							offset += Len;
							break;
					}
					if (!(null != aidInfo.getAId() &&
							ISOUtil.memcmp(item, 0, aidInfo.getAId(), 0, item.length))) {
						continue;
					}
				}
				aid.setTerminalAidInfo(aidInfo);
				emvAidInfo.getAidInfoList().add(aid);
				if (field62[0] != 0x31) {
					// 没有后续包
					break;
				}
			} else {
				return Tcode.T_receive_err;
			}
		}
		return 0;
	}

	/**
	 *下载EMV公钥结束
	 * @return
     */
	private int downEMVCapkEnd() {
		TransEName = Type.DOWNLOAD_EMV_CAPK_END ;
		setFixedDatas();
		Field62 = null ;
		setFields();
		retVal = OnLineTrans();
		if (retVal != 0) {
			return retVal;
		}
		// 缓存
		String rspCode = iso8583.getfield(39);
		if (null != rspCode && rspCode.equals("00")) {
			//中信银行
//			String str62 = iso8583.getfield(62);
//			Logger.debug("downEMVCapkEnd str62="+str62);
//			if (null == str62){
//				return -1;
//			}
//			byte[] field62 = ISOUtil.str2bcd(str62, false);
//			if (field62[0] == 0x30) // 已经没有了或者最后
				return 0;
		}
		return Tcode.T_receive_err;
	}

	/**
	 * 下载EMV参数结束
	 * @return
     */
	private int downEMVParamEnd() {
		Logger.debug("DparaTrans>>downEMVParamEnd");
		TransEName = Type.DOWNLOAD_EMV_PARAM_END ;
		setFixedDatas();
		Field62 = null ;
		setFields();
		retVal = OnLineTrans();
		if (retVal != 0) {
			return retVal;
		}
		// 缓存
		String rspCode = iso8583.getfield(39);
		if (null != rspCode && rspCode.equals("00")) {
			//中信银行下载emv参数结束通知不一样(bitmap不同)
//			String str62 = iso8583.getfield(62);
//			if (null == str62){
//				return -1;
//		    }
//			byte[] field62 = ISOUtil.str2bcd(str62, false);
//			if (field62[0] == 0x30) // 已经没有了或者最后
				return 0;
		}
		return Tcode.T_receive_err;
	}

	/**
	 * 下载AID
	 * @return
     */
	public int DownloadAid() {
		Logger.debug("DparaTrans>>DownloadAid");
		aidQueryList = new ArrayList<byte[]>();
		emvAidInfo = new EmvAidInfo();
		try {
			retVal = queryEMVParam();
			if (retVal != 0) {
				return retVal ;
			}
			Logger.debug("query aid ret = " + retVal);
			if (aidQueryList.size() > 0) {
				retVal = downEMVParam();
				if (retVal == 0) {
					if (aidQueryList.size() > 0) {
						for (byte[] item : aidQueryList) {
							Logger.debug("query aid info = "+ISOUtil.byte2hex(item));
						}
					}
					if (emvAidInfo.getAidInfoList().size() > 0) {
						for (TerminalAid item : emvAidInfo.getAidInfoList()) {
							Logger.debug("downloaded aid information = "+ ISOUtil.byte2hex(item.getTerminalAidInfo().getAId()));
						}
					}
					retVal = downEMVParamEnd();
					Logger.debug("download emv parameters ret = " + retVal);
					if (retVal == 0) {
						// save
						try {
							PAYUtils.object2File(emvAidInfo, TMConfig.getRootFilePath() + EmvAidInfo.FILENAME);
							Logger.debug("save emv parameters successfully");
						} catch (FileNotFoundException e) {
							Logger.error("Exception" + e.toString());
							return Tcode.T_unknow_err;
						} catch (IOException e) {
							Logger.error("Exception" + e.toString());
							return Tcode.T_unknow_err;
						}
					} else {
						return retVal;
					}
				} else{
					return retVal ;
				}
			}
		} catch (ISOException e) {
			Logger.error("Exception" + e.toString());
			return Tcode.T_unknow_err ;
		}
		return 0 ;
	}

	/**
	 * 下载公钥
     */
	public int DownloadCapk() {
		capkQueryList = new ArrayList<byte[]>();
		emvCapkInfo = new EmvCapkInfo();
		try {
			retVal = queryEMVCapk();
			if (retVal != 0){
				return retVal ;
			}
			retVal = downEMVCapk();
			if(retVal != 0){
				return retVal ;
			}
			if (retVal == 0) {
				if (capkQueryList.size() > 0) {
					int index = 0 ;
					for (byte[] item : capkQueryList) {
						Logger.debug("query capk result["+index+"]=" + ISOUtil.byte2hex(item));
						index++;
					}
				}
				retVal = downEMVCapkEnd();
				if (retVal == 0) {
					// save
					PAYUtils.object2File(emvCapkInfo, TMConfig.getRootFilePath() + EmvCapkInfo.FILENAME);
					Logger.debug("save capk infomation successfully");
				} else{
					return Tcode.T_unknow_err;
				}
			}
		} catch (ISOException e) {
			Logger.error("Exception" + e.toString());
			return Tcode.T_unknow_err ;
		} catch (FileNotFoundException e) {
			Logger.error("Exception" + e.toString());
			return Tcode.T_unknow_err ;
		} catch (IOException e) {
			Logger.error("Exception" + e.toString());
			return Tcode.T_unknow_err ;
		}
		return 0 ;
	}

	/**
	 * 将下载的CAPK和AID写入到内核中
	 */
	public static void loadAIDCAPK2EMVKernel() {
		IEMVHandler emvHandler = EMVHandler.getInstance();
		String aidFilePath = TMConfig.getRootFilePath() + EmvAidInfo.FILENAME;
		Logger.debug("load aid from path = "+aidFilePath);
		File aidFile = new File(aidFilePath);
		if (!aidFile.exists()) {
			Logger.debug("emv load aid file not found");
		}

		try {
			EmvAidInfo aidInfo = (EmvAidInfo) PAYUtils.file2Object(aidFilePath);
			if (aidInfo != null) {
				for (TerminalAid item : aidInfo.getAidInfoList()) {
					int result = emvHandler.addAidInfo(item.getTerminalAidInfo());
					Logger.debug("load aid 2 kernel with = "+ISOUtil.hexString(item.getTerminalAidInfo().getAId())+",result = "+result);
				}
			}
		} catch (IOException e) {
			Logger.error("Exception" + e.toString());
		} catch (ClassNotFoundException e) {
			Logger.error("Exception" + e.toString());
		}

		String capkFilePath = TMConfig.getRootFilePath()+ EmvCapkInfo.FILENAME;
		Logger.debug("load capk from path = "+capkFilePath);
		File capkFile = new File(capkFilePath);
		if (!capkFile.exists()) {
			Logger.debug("emv load capk file not found");
		}

		try {
			EmvCapkInfo capk = (EmvCapkInfo) PAYUtils.file2Object(capkFilePath);
			if (capk != null) {
				for (CAPublicKey item : capk.getCapkList()) {
					int result = emvHandler.addCAPublicKey(item);
					Logger.debug("load capk 2 kernel with = "+ISOUtil.hexString(item.getRID())+",result = "+result);
				}
			}
		} catch (IOException e) {
			Logger.error("Exception" + e.toString());
		} catch (ClassNotFoundException e) {
			Logger.error("Exception" + e.toString());
		}
	}
}