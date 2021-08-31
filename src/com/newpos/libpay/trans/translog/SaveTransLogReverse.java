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
import static com.datafast.definesDATAFAST.DefinesDATAFAST.FILE_NAME_REVERSE_SAVE;
import static com.datafast.menus.menus.idAcquirer;

/**
 * 交易日志管理类
 * @author
 */

public class SaveTransLogReverse implements Serializable {
    private static String TranLogPath = "translog.dat";

    private List<TransLogData> transLogData = new ArrayList<TransLogData>();
    private static SaveTransLogReverse saveTransLogReverse;
    private static String idAcqTmp;

    private SaveTransLogReverse() {
    }

    public static SaveTransLogReverse getInstance() {
        if (saveTransLogReverse == null) {
            String filepath = TMConfig.getRootFilePath() + TranLogPath;
            try {
                saveTransLogReverse = ((SaveTransLogReverse) PAYUtils.file2Object(filepath));
            } catch (FileNotFoundException e) {
                saveTransLogReverse = null;
            } catch (IOException e) {
                saveTransLogReverse = null;
            } catch (ClassNotFoundException e) {
                saveTransLogReverse = null;
            }if (saveTransLogReverse == null) {
                saveTransLogReverse = new SaveTransLogReverse();
            }
        }
        return saveTransLogReverse;
    }

    public static SaveTransLogReverse getInstance(String acquirer_id) {

        if (idAcqTmp != null){
            if (!idAcqTmp.equals(acquirer_id))
                saveTransLogReverse = null;
        }

        if (saveTransLogReverse == null) {
            idAcqTmp = acquirer_id;
            String filepath = TMConfig.getRootFilePath() + acquirer_id + TranLogPath;
            try {
                saveTransLogReverse = ((SaveTransLogReverse) PAYUtils.file2Object(filepath));
            } catch (FileNotFoundException e) {
                saveTransLogReverse = null;
            } catch (IOException e) {
                saveTransLogReverse = null;
            } catch (ClassNotFoundException e) {
                saveTransLogReverse = null;
            }if (saveTransLogReverse == null) {
                saveTransLogReverse = new SaveTransLogReverse();
            }
        }
        return saveTransLogReverse;
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
        }
    }

    public boolean saveLog(TransLogData data, String acquirer_id) {
        transLogData.add(data);
        Logger.debug("transLogData size " + transLogData.size());
        try {
            PAYUtils.object2File(saveTransLogReverse, TMConfig.getRootFilePath()+ acquirer_id + TranLogPath);
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
            PAYUtils.object2File(saveTransLogReverse, TMConfig.getRootFilePath()+ acquirer_id + TranLogPath);
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
            if (transLogData.size() == 0) {
                SaveTransLogReverse.getInstance(idAcquirer + FILE_NAME_REVERSE_SAVE).clearAll(idAcquirer + FILE_NAME_REVERSE_SAVE);
            }
            Logger.debug("Debug point deleteTransLog " + transLogData.toString());
            return true;
        }
        return false;
    }
}
