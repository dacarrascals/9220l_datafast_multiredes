package com.newpos.libpay.helper.iso8583;

import android.content.Context;

import com.newpos.libpay.Logger;
import com.newpos.libpay.device.pinpad.PinpadManager;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.global.TMConstants;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

//import static com.datafast.menus.menus.acquirerRow;

/**
 * ISO8583数据描述，解包，组包
 * @author
 */
public class ISO8583 {

	// 第零域是消息类型
	// 第一域是BITMAP
	private boolean isHasMac = false;
	private int fieldNum; // 域的长度
	private FieldAttr[] listFieldAttr; // 域的内容格式描述
	private String[] listFieldData; // 域的数据集合(每个域的数据)

	private String[] listFieldRespData;// 返回数据的域数据
	private String tpdu; // 发送的tpdu
	private String header; // 发送的header
	private String rspHeader; // 返回的header
	private String rspTpdu; //响应的tupu

	private Context mContext ;

	public interface RSPCODE{
		public static final String RSP_00 = "00";
		public static final String RSP_89 = "89";
	}

	private TransLogData LogData = new TransLogData();

	public TransLogData getLogData() {
		return LogData;
	}

	public String getRspHeader() {
		return rspHeader;
	}

	/**
	 * 初始化域的长度、域的内容格式、响应数据和请求数据数组
	 * @param context tpdu header
	 */
	public ISO8583(Context context, String tpdu, String header) {
		this.mContext = context ;
		fieldNum = Integer.parseInt(PAYUtils.lodeConfig(context, TMConstants.ISO8583, "filedNum"));
		int attrLen = fieldNum + 3;
		listFieldAttr = new FieldAttr[attrLen];// 多tpdu header
		SetAttr(context, fieldNum);
		listFieldData = new String[fieldNum + 1];// 0~64 有65个
		listFieldRespData = new String[fieldNum + 1];// 0~64 有65个
		this.tpdu = tpdu;
		this.header = header;
		this.mContext = context ;
	}

	/**
	 * 设置每一个域的属性格式
	 * @param context
	 * @param totalFiled
     */
	private void SetAttr(Context context, int totalFiled) {
		Properties pro = PAYUtils.lodeConfig(context, TMConstants.ISO8583);
		listFieldAttr[0] = getAttr(pro, "tpdu");
		listFieldAttr[1] = getAttr(pro, "header");
		for (int i = 0; i <= totalFiled; i++) {
			listFieldAttr[i + 2] = getAttr(pro, i + "");
		}
	}

	/**
	 * 获取配置文件中域的属性
	 * @param pro 配置文件对象
	 * @param proName 索引
	 * @return 返回域属性对象
	 */
	private FieldAttr getAttr(Properties pro, String proName) {
		String prop = pro.getProperty(proName);
		if (prop == null) {
			return null;
		}
		String[] propGroup = prop.split(",");
		FieldAttr attr = new FieldAttr();
		int data_len = Integer.parseInt(propGroup[1]) ;
		attr.setDataLen(data_len);
		attr.setDataType(Integer.parseInt(propGroup[2]));
		attr.setLenAttr(Integer.parseInt(propGroup[0]));
		attr.setLenType(Integer.parseInt(propGroup[3]));

		return attr;
	}

	/**
	 * 设置域内容
	 * @param fieldNo 域ID
	 * @param data 域内容
	 * @return
	 */
	public int setField(int fieldNo, String data) {
		if (fieldNo > fieldNum) {
			return -1;
		}
		listFieldData[fieldNo] = data;
		return 0;
	}

	/**
	 * 设置响应域内容
	 * @param fieldNo
	 * @param data
     * @return
     */
	private int setRspField(int fieldNo, String data) {
		if (fieldNo > fieldNum) {
			return -1;
		}
		listFieldRespData[fieldNo] = data;
		return 0;
	}

	/**
	 * 取解包完成后的数据
	 * @param fieldNo 域ID
	 * @return
	 */
	public String getfield(int fieldNo) {
		if (fieldNo > fieldNum) {
			return null;
		}
		return listFieldRespData[fieldNo];
	}

	/**
	 * 组包
	 * @return 返回打包完成后的结果
	 */
	public byte[] packetISO8583() {
		byte[] temp = new byte[1024]; // 临时存储数组
		// bitmap
		byte[] bitmap = new byte[16];
		// 缓冲区
		byte[] bb = new byte[2048];// 数据包总数据
		int lenAttr, lenType, dataType, dataMaxLen, headLen;
		int offset = 0;
		int dataLen = 0;
		int appResult = -1;

		// ***********************tpdu处理
		FieldAttr attr = listFieldAttr[0];
		appResult = appendHeader(attr, tpdu, temp);
		if (appResult < 0) {
			return null;
		}
		System.arraycopy(temp, 0, bb, offset, appResult);
		offset += appResult;
		// ***********************header处理
		/*attr = listFieldAttr[1];
		appResult = appendHeader(attr, header, temp);
		if (appResult < 0) {
			return null;
		}
		System.arraycopy(temp, 0, bb, offset, appResult);
		offset += appResult;*/
		// ***********************MsgId处理
		attr = listFieldAttr[2];
		String fieldData = listFieldData[0];
		appResult = appendHeader(attr, fieldData, temp);
		if (appResult < 0) {
			return null;
		}
		System.arraycopy(temp, 0, bb, offset, appResult);
		offset += appResult;

		headLen = offset;// bitmap的偏移量
		attr = listFieldAttr[3]; // bitmap的属性
		if (attr.getDataType() == FieldTypesDefine.FIELDATTR_TYPE_ASCII) {
			offset += fieldNum / 4;// bitmap预留的长度
		}else {
			offset += fieldNum / 8;// bitmap预留的长度
		}

		if (fieldNum == 128){
			bitmap[0] = (byte) 0x80;
		}

		for (int i = 2; i < fieldNum; i++) {
			attr = listFieldAttr[i + 2];
			fieldData = listFieldData[i];

			// 空数据继续下个循环
			if (attr == null || fieldData == null) {
				continue;
			}

			bitmap[(i - 1) / 8] |= 0x80 >> ((i - 1) % 8);
			// 获取属性
			lenAttr = attr.getLenAttr();
			lenType = attr.getLenType();
			dataType = attr.getDataType();
			dataMaxLen = attr.getDataLen();
			// ***********************长度判断
			if (dataType == FieldTypesDefine.FIELDATTR_TYPE_BIN) {
				dataLen = fieldData.length() / 2;
			}else {
				dataLen = fieldData.length();
			}

			if (lenAttr == FieldTypesDefine.FIELDATTR_LEN_TYPE_NO) {
				if (dataLen != dataMaxLen) {
					Logger.debug("len != MaxLen fieldNum:" + i);
					return null;
				}
			} else {
				if (dataLen > dataMaxLen) {
					Logger.debug("len > MaxLen  fieldNum:" + i);
					return null;
				}
				if (lenType == FieldTypesDefine.FIELDATTR_TYPE_N) {
					temp = ISOUtil.int2bcd(dataLen, lenAttr);
					System.arraycopy(temp, 0, bb, offset, lenAttr);
					offset += lenAttr;
				} else if (lenType == FieldTypesDefine.FIELDATTR_TYPE_BIN) {
					// 少用 暂不处理
				} else if (lenType == FieldTypesDefine.FIELDATTR_TYPE_ASCII) {
					// 少用 暂不处理
				}
			}

			if (dataType == FieldTypesDefine.FIELDATTR_TYPE_BIN) {
				temp = ISOUtil.hex2byte(fieldData);
				System.arraycopy(temp, 0, bb, offset, dataLen);
			} else if (dataType == FieldTypesDefine.FIELDATTR_TYPE_ASCII) {
				System.arraycopy(fieldData.getBytes(), 0, bb, offset, dataLen);
			} else if (dataType == FieldTypesDefine.FIELDATTR_TYPE_CN) {
				temp = ISOUtil.str2bcd(fieldData, false, (byte) 0); // 右补
				dataLen = temp.length;
				System.arraycopy(temp, 0, bb, offset, dataLen);
			} else if (dataType == FieldTypesDefine.FIELDATTR_TYPE_N) {
				temp = ISOUtil.str2bcd(fieldData, true, (byte) 0);// 左补
				dataLen = temp.length;
				System.arraycopy(temp, 0, bb, offset, dataLen);
			}
			offset += dataLen;
		}
		if (isHasMac) {
			bitmap[fieldNum / 8 - 1] |= 0x01;
		}
		attr = listFieldAttr[3];
		if (attr.getDataType() == FieldTypesDefine.FIELDATTR_TYPE_ASCII) {
			System.arraycopy(ISOUtil.byte2hex(bitmap).getBytes(), 0, bb,
					headLen, fieldNum / 4);
		} else {
			System.arraycopy(bitmap, 0, bb, headLen, fieldNum / 8);
		}
		if (isHasMac) {
			// ******************************算64域
			byte[] mac ;
			if(TMConfig.getInstance().getStandard() == 2){
				mac = getCITICMAC(listFieldData);
			}else {
				mac = PinpadManager.getInstance().getMac(bb, headLen - 2, offset - headLen + 2);
			}
			// ******************************算mac
			if (mac != null) {
				System.arraycopy(mac, 0, bb, offset, 8);
			}else {
				return null;
			}
			offset += 8;
		}

		// 去掉多余的零
		byte[] packet = new byte[offset];
		System.arraycopy(bb, 0, packet, 0, offset);
		return packet;
	}

	private byte[] getCITICMAC(String[] packages){
		Logger.debug("==getCITICMAC==");
		//41 42   // 32 44  // 61
		int[] result_fileds = {0, 3, 4, 11, 12, 13, 25, 32, 38, 39,41,42,44,61} ;
		String temp = "" ;
		for (int i = 0 ; i < result_fileds.length ; i++){
			if(packages[result_fileds[i]]!=null){
				String str ;
				if(result_fileds[i] == 41 || result_fileds[i] == 42){
					byte[] bcd = ISOUtil.str2bcd(packages[result_fileds[i]] , false);
					int l = bcd.length*2 ;
					if(result_fileds[i] == 42){
						l -= 1 ;
					}
					str = ISOUtil.bcd2str(bcd , 0 , l , false);
				}else if(result_fileds[i] == 32 || result_fileds[i] == 44){
					int ll = packages[result_fileds[i]].length() ;
					if(ll < 10){
						str = "0"+ll+packages[result_fileds[i]];
					}else {
						str = ll + packages[result_fileds[i]];
					}
				}else if(result_fileds[i] == 61){
					int lll = packages[result_fileds[i]].length() ;
					if( lll < 16){
						str = packages[result_fileds[i]];
					}else {
						str = packages[result_fileds[i]].substring(0 , 16);
					}
				}else {
					str = packages[result_fileds[i]];
				}
				temp += str + " ";
			}
		}
		temp = temp.trim();
		Logger.debug("temp="+temp);
		String ascii = BCD2ASC(temp.getBytes()) ;
		Logger.debug("ascii = "+ascii);

		byte[] mac_in =  ISOUtil.hex2byte(ascii);
		Logger.debug("mac_in = "+ISOUtil.hexString(mac_in));
		Logger.debug("mac_in len = "+mac_in.length);
		byte[] mac = PinpadManager.getInstance().getMac(mac_in , 0 , mac_in.length);
		if(mac != null){
			Logger.debug("mac = "+ISOUtil.hexString(mac));
		}
		String bcd2ascii = BCD2ASC(ISOUtil.hexString(mac).getBytes()).substring(0 , 16);
		mac = ISOUtil.hex2byte(bcd2ascii);
		Logger.debug("mac ascii= "+ISOUtil.hexString(mac));

		return mac;
	}

	public final static char[] BToA = "0123456789abcdef".toCharArray() ;
	public static String BCD2ASC(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			int h = ((bytes[i] & 0xf0) >>> 4);
			int l = (bytes[i] & 0x0f);
			temp.append(BToA[h]).append( BToA[l]);
		}
		return temp.toString() ;
	}

	/**
	 * 解包
	 * @param data 数据包
	 * @return 异常返回-1 解包完成后的数据保存在listfieldRespData中
	 */
	public int unPacketISO8583(byte[] data) {
		Logger.debug("==ISO8583->unPacketISO8583");
		byte[] MacBlock = null;
		byte[] bitmap = new byte[16];
		int offset = 0;
		int lenAttr, lenType, dataMaxLen;
		int dataLen = 0;
		FieldAttr attr = new FieldAttr();
		int headLen = 0;
		// ******************************tpdu处理
		attr = listFieldAttr[0];
		offset += appRsp(-2, attr, data, offset, attr.getDataLen());
		// ******************************header处理
		/*attr = listFieldAttr[1];
		offset += appRsp(-1, attr, data, offset, attr.getDataLen());
		headLen = offset;*/
		// ******************************msgid处理
		attr = listFieldAttr[2];
		offset += appRsp(0, attr, data, offset, attr.getDataLen());
		// ******************************header处理
		if (listFieldAttr[3].getDataLen() != 8) {
			bitmap = new byte[16];
		}else {
			bitmap = new byte[8];
		}
		if (listFieldAttr[3].getDataType() == FieldTypesDefine.FIELDATTR_TYPE_ASCII) {
			// System.arraycopy(data, offset, bitmap, 0, bitmap.length);
			String strMap = new String(data, offset, bitmap.length);
			bitmap = ISOUtil.str2bcd(strMap, true);
			setRspField(1, strMap);
			offset += bitmap.length * 2;
		} else {
			System.arraycopy(data, offset, bitmap, 0, bitmap.length);
			setRspField(1, ISOUtil.byte2hex(bitmap));
			offset += bitmap.length;
		}

		for (int i = 2; i <= fieldNum; i++) {
			if (offset > data.length) {
				return Tcode.T_unknow_err;
			}
			if (offset == data.length) {
				break;
			}
			if ((0xff & bitmap[(i - 1) / 8] & (0x80 >> ((i - 1) % 8))) == (byte) 0 ) {
				continue;
			}
			attr = listFieldAttr[i + 2];

			//解决中信报文非法问题
			if(attr == null){
				continue;//空属性进行下次循环
			}

			dataMaxLen = attr.getDataLen();
			// dataType = attr.getDataType();
			lenAttr = attr.getLenAttr();
			lenType = attr.getLenType();

			// ******************************处理长度
			if (lenAttr == FieldTypesDefine.FIELDATTR_LEN_TYPE_NO) {
				dataLen = dataMaxLen;
			} else {
				if (lenType == FieldTypesDefine.FIELDATTR_TYPE_N) {
					dataLen = ISOUtil.bcd2int(data, offset, lenAttr);
					offset += lenAttr;
				} else if (lenType == FieldTypesDefine.FIELDATTR_TYPE_ASCII) {
					dataLen = Integer
							.parseInt(new String(data, offset, lenAttr));
					offset += lenAttr;
				} else {
					dataLen = ISOUtil.byte2int(data, offset, lenAttr);
					offset += lenAttr;
				}
			}
			// ******************************处理数据
			if (i == 64) {
				if(TMConfig.getInstance().getStandard() == 2){
					MacBlock = getCITICMAC(listFieldRespData);
				}else {
					MacBlock = PinpadManager.getInstance().getMac(data, headLen, offset - headLen);
				}
			}
			if(data.length < offset || data.length < offset+dataLen){
				break;//报文异常，退出
			}else {
				int ret = appRsp(i, attr, data, offset, dataLen);
				if(ret == -1){
					break;
				}else {
					offset += ret ;
				}
			}
		}

		String recMac = listFieldRespData[64];
		if (null != recMac) {
			byte[] recBMac = recMac.getBytes();
			for (int i = 0; i < 4; i++) {
				if (MacBlock != null && MacBlock[i] != recBMac[i]) {
					return Tcode.T_package_mac_err;
				}
			}
		}

		int sendMsgId = PAYUtils.Object2Int(listFieldData[0]);
		int reciveMsgId = PAYUtils.Object2Int(listFieldRespData[0]);
		String sendProCode = listFieldData[3];
		String reciveProCode = listFieldRespData[3];
		String sendTrackNo = listFieldData[11];
		String reciveTrackNo = listFieldRespData[11];
		String sendTermID = listFieldData[41];
		String reciveTermID = listFieldRespData[41];
		String sendMerchID = listFieldData[42];
		String reciveMerchID = listFieldRespData[42];

		if (reciveMsgId - sendMsgId != 10) {
			return Tcode.T_package_illegal;
		}
		/*if (reciveTrackNo != null && !sendTrackNo.equals(reciveTrackNo)) {
			return Tcode.T_package_illegal;
		}*/
		/*if (reciveProCode != null && !sendProCode.equals(reciveProCode)) {
			return Tcode.T_package_illegal;
		}
		if (!sendTermID.equals(reciveTermID)) {
			return Tcode.T_package_illegal;
		}*/
		/*if (!sendMerchID.equals(reciveMerchID)) {
			return Tcode.T_package_illegal;
		}*/

		return 0;
	}

	/**
	 * 签到62域上送BCD,结算上送ASCII
	 * @param type
     */
	public void set62AttrDataType(int type){
		listFieldAttr[64].setDataType(type);
	}

	/**
	 * 清除数据
	 */
	public void clearData() {
		listFieldData = null;
		listFieldAttr = null;
		fieldNum = Integer.parseInt(PAYUtils.lodeConfig(mContext, TMConstants.ISO8583, "filedNum"));
		int attrLen = fieldNum + 3;
		listFieldAttr = new FieldAttr[attrLen];// 多tpdu header
		SetAttr(mContext, fieldNum);
		listFieldData = new String[fieldNum + 1];// 0~64 有65个
		listFieldRespData = new String[fieldNum + 1];// 0~64 有65个
	}

	public boolean isHasMac() {
		return isHasMac;
	}

	public void setHasMac(boolean isHasMac) {
		this.isHasMac = isHasMac;
	}

	/***
	 * @param attr 域属性
	 * @param content 域内容
	 * @param bb 暂存区
	 * @return
	 */
	private int appendHeader(FieldAttr attr, String content, byte[] bb) {
		byte[] temp = null;
		int dataLen = -1;
		if (attr.getDataType() != FieldTypesDefine.FIELDATTR_TYPE_ASCII) {
			if (attr.getDataLen() != content.length()) {
				return -1;
			}
			temp = ISOUtil.str2bcd(content, false, (byte) 0);
			dataLen = temp.length;
			System.arraycopy(temp, 0, bb, 0, dataLen);
		} else {
			if (attr.getDataLen() != content.length()) {
				return -1;
			}
			dataLen = content.length();
			System.arraycopy(content.getBytes(), 0, bb, 0, dataLen);
		}
		return dataLen;
	}

	private int appRsp(int FieldNo, FieldAttr attr, byte[] data, int offset, int dataLen) {
		int rspOffset = 0;
		String temp = null;
		if (attr.getDataType() == FieldTypesDefine.FIELDATTR_TYPE_ASCII) {
			if (FieldNo == 63) {
				byte[] a = new byte[dataLen];
				System.arraycopy(data, offset, a, 0, dataLen);
				try {
					temp = new String(a, "gbk");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					Logger.error("Exception" + e.toString());
				}
			} else {
				temp = new String(data, offset, dataLen);
			}
			rspOffset = dataLen;
		} else if (attr.getDataType() == FieldTypesDefine.FIELDATTR_TYPE_BIN) {
			temp = ISOUtil.byte2hex(data, offset, dataLen);
			rspOffset = dataLen;
		} else {
			rspOffset = (dataLen + 1) / 2;
			//add
			if(data.length<=offset || data.length < offset+rspOffset){
				return -1 ;
			}else {
				temp = ISOUtil.byte2hex(data, offset, rspOffset);
			}
		}
		if (FieldNo == -1) {
			rspHeader = temp;
		}else if (FieldNo == -2) {
			rspTpdu = temp;
		}else {
			setRspField(FieldNo, temp);
		}
		return rspOffset;
	}
}