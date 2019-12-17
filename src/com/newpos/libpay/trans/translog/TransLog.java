package com.newpos.libpay.trans.translog;

import com.newpos.libpay.Logger;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.PAYUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.datafast.menus.MenuAction.callBackSeatle;

/**
 * 交易日志管理类
 * @author
 */

public class TransLog implements Serializable {
	private static String TranLogPath = "translog.dat";
	private static String ScriptPath = "script.dat";
	private static String ReversalPath = "reversal.dat";
	private static String ReversalCashPath = "reversalCash.dat";

	private List<TransLogData> transLogData = new ArrayList<TransLogData>();
	private static TransLog tranLog;
	private static String idAcqTmp;

	private TransLog() {
	}

	public static TransLog getInstance() {
		if (tranLog == null) {
			String filepath = TMConfig.getRootFilePath() + TranLogPath;
			try {
				tranLog = ((TransLog) PAYUtils.file2Object(filepath));
			} catch (FileNotFoundException e) {
				tranLog = null;
			} catch (IOException e) {
				tranLog = null;
			} catch (ClassNotFoundException e) {
				tranLog = null;
			}if (tranLog == null) {
				tranLog = new TransLog();
			}
		}
		return tranLog;
	}

	public static TransLog getInstance(String acquirer_id) {

		if (idAcqTmp != null){
			if (!idAcqTmp.equals(acquirer_id))
				tranLog = null;
		}

		if (tranLog == null) {
			idAcqTmp = acquirer_id;
			String filepath = TMConfig.getRootFilePath() + acquirer_id + TranLogPath;
			try {
				tranLog = ((TransLog) PAYUtils.file2Object(filepath));
			} catch (FileNotFoundException e) {
				tranLog = null;
			} catch (IOException e) {
				tranLog = null;
			} catch (ClassNotFoundException e) {
				tranLog = null;
			}if (tranLog == null) {
				tranLog = new TransLog();
			}
		}
		return tranLog;
	}

	public List<TransLogData> getData() {
		return transLogData;
	}

	public int getSize() {
		return transLogData.size();
	}

	public TransLogData get(int position) {
		if (!(position > getSize())) {
			return transLogData.get(position);
		}
		return null;
	}

	/**
	 * 清除交易记录的二进制文件
	 */
	public void clearAll() {
		transLogData.clear();
		String FullName = TMConfig.getRootFilePath() + TranLogPath;
		File file = new File(FullName);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 清除交易记录的二进制文件
	 */
	public void clearAll(String acquirer_id) {
		transLogData.clear();
		String FullName = TMConfig.getRootFilePath() + acquirer_id + TranLogPath;
		File file = new File(FullName);
		if (file.exists()) {
			file.delete();
			if (callBackSeatle != null)
				callBackSeatle.getRspSeatleReport(0);
		}
	}

	/**
	 * 获取上一条交易记录
	 */
	public TransLogData getLastTransLog() {
		if (getSize() >= 1) {
			return transLogData.get(getSize() - 1);
		}
		return null;
	}

	/**
	 * 保存交易记录
	 * @return
	 */
	public boolean saveLog(TransLogData data) {
		transLogData.add(data);
		Logger.debug("transLogData size " + transLogData.size());
		try {
			PAYUtils.object2File(tranLog, TMConfig.getRootFilePath()+ TranLogPath);
		} catch (FileNotFoundException e) {
			Logger.debug("save translog file not found");
			return false;
		} catch (IOException e) {
			Logger.debug("save translog IOException");
			return false;
		}
		return true;
	}

	public boolean saveLog(TransLogData data, String acquirer_id) {
		transLogData.add(data);
		Logger.debug("transLogData size " + transLogData.size());
		try {
			PAYUtils.object2File(tranLog, TMConfig.getRootFilePath()+ acquirer_id + TranLogPath);
		} catch (FileNotFoundException e) {
			Logger.debug("save translog file not found");
			return false;
		} catch (IOException e) {
			Logger.debug("save translog IOException");
			return false;
		}
		return true;
	}

	public boolean saveLog(String acquirer_id) {
		try {
			PAYUtils.object2File(tranLog, TMConfig.getRootFilePath()+ acquirer_id + TranLogPath);
		} catch (FileNotFoundException e) {
			Logger.debug("save translog file not found");
			return false;
		} catch (IOException e) {
			Logger.debug("save translog IOException");
			return false;
		}
		return true;
	}
	/**
	 * 更新交易记录
	 * @param logIndex 交易记录索引
	 * @param newData 更新后的数据
	 * @return 更新结果
	 */
	public boolean updateTransLog(int logIndex, TransLogData newData) {
		if (getSize() > 0) {
			transLogData.set(transLogData.indexOf(transLogData.get(logIndex)), newData);
			return true;
		}
		return false;
	}

	/**
	 * 获取当前交易的索引号
	 * @param data
	 * @return
     */
	public int getCurrentIndex(TransLogData data){
		int current = -1 ;
		for (int i = 0 ; i < transLogData.size() ; i++){
			if(transLogData.get(i).getTraceNo().equals(data.getTraceNo())){
				current = i ;
			}
		}
		return current ;
	}

	/**
	 * Borra transacción anulada
	 * @param logIndex
	 * @return
	 */
	public boolean deleteTransLog(int logIndex) {
		if (getSize() > 0) {
			transLogData.remove(logIndex);
			Logger.debug("Debug point deleteTransLog " + transLogData.toString());
			return true;
		}
		return false;
	}
	/**
	 * 根据索引获取交易记录
	 * @param logIndex 交易记录索引
	 * @return 交易对象
	 */
	public TransLogData searchTransLogByIndex(int logIndex) {
		if (getSize() > 0 && getSize() - 1 >= logIndex) {
			return transLogData.get(logIndex);
		}
		return null;
	}

	/**
	 * 根据流水号获取交易记录
	 * @param TraceNo 交易流水号
	 * @return 交易记录
	 */
	public TransLogData searchTransLogByTraceNo(String TraceNo) {
		if (getSize() > 0) {
			for (int i = 0; i < getSize(); i++) {
				if (!PAYUtils.isNullWithTrim(transLogData.get(i).getTraceNo())) {
					if (transLogData.get(i).getTraceNo().equals("" + TraceNo)) {
						return transLogData.get(i);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 根据授权码及日期获取交易记录
	 * @param auth date
	 * @return 交易记录
	 */
	public TransLogData searchTransLogByAUTHDATE(String auth , String date) {
		if (getSize() > 0) {
			for (int i = 0; i < getSize(); i++) {
				TransLogData data = transLogData.get(i);
				if (!PAYUtils.isNullWithTrim(data.getAuthCode()) &&
						!PAYUtils.isNullWithTrim(data.getLocalDate())) {
					if (data.getAuthCode().equals("" + auth) &&
							data.getLocalDate().equals("" + date)) {
						return data;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 根据参考号及日期获取交易记录
	 * @param refer date
	 * @return 交易记录
	 */
	public TransLogData searchTransLogByREFERDATE(String refer , String date) {
		if (getSize() > 0) {
			for (int i = 0; i < getSize(); i++) {
				TransLogData data = transLogData.get(i);
				if (!PAYUtils.isNullWithTrim(data.getRRN()) &&
						!PAYUtils.isNullWithTrim(data.getLocalDate())) {
					if (data.getRRN().equals("" + refer) &&
							data.getLocalDate().equals("" + date)) {
						return data;
					}
				}
			}
		}
		return null;
	}


	/**
	 * 保存脚本结果
	 * @return
	 */
	public static boolean saveScriptResult(TransLogData data) {
		try {
			PAYUtils.object2File(data, TMConfig.getRootFilePath()+ ScriptPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Logger.error("Exception" + e.toString());
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.error("Exception" + e.toString());
			return false;
		}
		return true;
	}

	/**
	 * 保存冲正信息
	 * @return
	 */
	public static boolean saveReversal(TransLogData data , boolean isreversoCaja ) {
		try {
			if (isreversoCaja){
				PAYUtils.object2File(data, TMConfig.getRootFilePath()+ ReversalCashPath);
			}else {
				PAYUtils.object2File(data, TMConfig.getRootFilePath()+ ReversalPath);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Logger.error("Exception" + e.toString());
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.error("Exception" + e.toString());
			return false;
		}
		return true;
	}

	/**
	 * 获取冲正信息
	 * @return
	 */
	public static TransLogData getReversal(boolean isReverseCash) {
		try {
			if (isReverseCash){
				return (TransLogData) PAYUtils.file2Object(TMConfig.getRootFilePath() + ReversalCashPath);
			}else {
				return (TransLogData) PAYUtils.file2Object(TMConfig.getRootFilePath() + ReversalPath);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Logger.error("Exception" + e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.error("Exception" + e.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			Logger.error("Exception" + e.toString());
		}
		return null;
	}

	/**
	 * 获取脚本信息
	 * @return
	 */
	public static TransLogData getScriptResult() {
		try {
			return (TransLogData) PAYUtils.file2Object(TMConfig.getRootFilePath()+ ScriptPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Logger.error("Exception" + e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.error("Exception" + e.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			Logger.error("Exception" + e.toString());
		}
		return null;
	}

	/**
	 * 清除冲正
	 * @return
	 */
	public static boolean clearReveral(boolean isReverseCash) {
		File file;
		if (isReverseCash){
			file = new File(TMConfig.getRootFilePath() + ReversalCashPath);
		}else{
			file = new File(TMConfig.getRootFilePath() + ReversalPath);
		}

		if (file.exists() && file.isFile()) {
			file.delete();
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 清除脚本执行结果
	 * @return
	 */
	public static boolean clearScriptResult() {
		File file = new File(TMConfig.getRootFilePath()+ ScriptPath);
		if (file.exists() && file.isFile()) {
			file.delete();
			return false;
		} else {
			return true;
		}
	}
}