package cn.desert.newpos.payui.setting.ui.classical;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.desert.poplist.PopupListView;
import com.android.desert.poplist.PopupView;
import com.android.newpos.pay.R;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.PAYUtils;

import java.util.ArrayList;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.base.PayApplication;

/**
 * Created by zhouqiang on 2017/3/13.
 * @author zhouqiang
 */
public class TransparaFrags extends Activity{

    private PopupListView popupListView ;
    private ArrayList<PopupView> popupViews = new ArrayList<>();
    private Context mContext ;
    private PopupView metchant ;
    private PopupView trans ;
    private PopupView master ;
    private PopupView password ;
    private PopupView carduse ;
    private PopupView scanner ;
    private TMConfig config ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_frag_trans);
        PayApplication.getInstance().addActivity(this);
        config = TMConfig.getInstance() ;
        mContext = TransparaFrags.this ;
        popupListView = (PopupListView) findViewById(R.id.setting_trans_poplist);
        loadViews();
        initPopupViews();
    }

    @Override
    public void onBackPressed() {
        if (popupListView.isItemZoomIn()) {
            popupListView.zoomOut();
        } else {
            super.onBackPressed();
        }
    }

    private void initPopupViews() {
        popupViews.add(metchant);
        popupViews.add(trans);
        popupViews.add(master);
        popupViews.add(password);
        popupViews.add(carduse);
        popupViews.add(scanner);
        popupListView.init(null);
        popupListView.setItemViews(popupViews);
    }

    public void setting_return(View v){
        if (popupListView.isItemZoomIn()) {
            popupListView.zoomOut();
        }
    }

    private void loadViews(){
       metchant = new PopupView(mContext , R.layout.setting_frag_trans_poplist_head){
            @Override
            public void setViewsElements(View view) {
                TextView textView = (TextView) view.findViewById(R.id.setting_trans_poplist_tv);
                textView.setText("商户信息");
            }

            @Override
            public View setExtendView(View view) {
                View extendView;
                if (view == null) {
                    extendView = LayoutInflater.from(mContext).
                            inflate(R.layout.setting_frag_trans_merchant, null);
                    readMerchantInfo(extendView);
                } else {
                    extendView = view;
                }
                return extendView ;
            }
        };

       trans =  new PopupView(mContext, R.layout.setting_frag_trans_poplist_head) {
            @Override
            public void setViewsElements(View view) {
                TextView textView = (TextView) view.findViewById(R.id.setting_trans_poplist_tv);
                textView.setText("交易参数");
            }

            @Override
            public View setExtendView(View view) {
                View extendView;
                if (view == null) {
                    extendView = LayoutInflater.from(mContext).
                            inflate(R.layout.setting_frag_trans_sys, null);
                    readTransInfo(extendView);
                } else {
                    extendView = view;
                }
                return extendView;
            }
        };

       master =  new PopupView(mContext, R.layout.setting_frag_trans_poplist_head) {
            @Override
            public void setViewsElements(View view) {
                TextView textView = (TextView) view.findViewById(R.id.setting_trans_poplist_tv);
                textView.setText("主管密码修改");
            }

            @Override
            public View setExtendView(View view) {
                View extendView;
                if (view == null) {
                    extendView = LayoutInflater.from(mContext).
                            inflate(R.layout.setting_frag_trans_master, null);
                    readMasterInfo(extendView);
                } else {
                    extendView = view;
                }
                return extendView;
            }
        };

       password =  new PopupView(mContext, R.layout.setting_frag_trans_poplist_head) {
            @Override
            public void setViewsElements(View view) {
                TextView textView = (TextView) view.findViewById(R.id.setting_trans_poplist_tv);
                textView.setText("交易输密参数");
            }

            @Override
            public View setExtendView(View view) {
                View extendView;
                if (view == null) {
                    extendView = LayoutInflater.from(mContext).
                            inflate(R.layout.setting_frag_trans_password, null);
                    readPasswordSwitch(extendView);
                } else {
                    extendView = view;
                }
                return extendView;
            }
        };

      carduse =  new PopupView(mContext, R.layout.setting_frag_trans_poplist_head) {
            @Override
            public void setViewsElements(View view) {
                TextView textView = (TextView) view.findViewById(R.id.setting_trans_poplist_tv);
                textView.setText("交易用卡参数");
            }

            @Override
            public View setExtendView(View view) {
                View extendView;
                if (view == null) {
                    extendView = LayoutInflater.from(mContext).
                            inflate(R.layout.setting_frag_trans_carduse, null);
                    readCarduseSwitch(extendView);
                } else {
                    extendView = view;
                }
                return extendView;
            }
        };

        scanner =  new PopupView(mContext, R.layout.setting_frag_trans_poplist_head) {
            @Override
            public void setViewsElements(View view) {
                TextView textView = (TextView) view.findViewById(R.id.setting_trans_poplist_tv);
                textView.setText("扫码参数");
            }

            @Override
            public View setExtendView(View view) {
                View extendView;
                if (view == null) {
                    extendView = LayoutInflater.from(mContext).
                            inflate(R.layout.setting_frag_trans_scanner, null);
                    scannerSwitch(extendView);
                } else {
                    extendView = view;
                }
                return extendView;
            }
        };
    }

    private void readMerchantInfo(View v){
        final EditText merid = (EditText) v.findViewById(R.id.setting_merchant_merid);
        final EditText mername = (EditText) v.findViewById(R.id.setting_merchant_mername);
        final EditText terid = (EditText) v.findViewById(R.id.setting_merchant_terid);
        merid.setText(config.getMerchID());
        mername.setText(config.getMerchName());
        terid.setText(config.getTermID());
        v.findViewById(R.id.setting_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mid = merid.getText().toString();
                String mname = mername.getText().toString();
                String tid = terid.getText().toString();
                if(PAYUtils.isNullWithTrim(mid)|| PAYUtils.isNullWithTrim(mname)||PAYUtils.isNullWithTrim(tid)){
                    UIUtils.toast(TransparaFrags.this , false , R.string.data_null);
                }else {
                    config.setMerchID(mid)
                           .setMerchName(mname)
                            .setTermID(tid)
                            .save();
                    UIUtils.toast(TransparaFrags.this , true , R.string.save_success);
                }
            }
        });
    }

    private void readTransInfo(View v){
        final EditText traceEdit = (EditText) v.findViewById(R.id.setting_trans_trace_edit);
        final Spinner printSp = (Spinner) v.findViewById(R.id.setting_trans_print_spinner);
        final EditText tpduEdit = (EditText) v.findViewById(R.id.setting_trans_tupu_edit);
        final Spinner exitSp = (Spinner) v.findViewById(R.id.setting_trans_exit_time);
        final EditText batchEdit = (EditText) v.findViewById(R.id.setting_trans_batch_edit);
        final EditText firmEdit = (EditText) v.findViewById(R.id.setting_trans_firm_edit);
        final Spinner trackSp = (Spinner) v.findViewById(R.id.setting_trans_track_encrypt);
        final Spinner waitSP = (Spinner) v.findViewById(R.id.setting_trans_wait_user_time);
        final EditText reversal = (EditText) v.findViewById(R.id.setting_trans_reversal_count);
        setAdaper(trackSp , R.array.onoff);
        trackSp.setSelection(config.isTrackEncrypt()?0:1);

        setAdaper(printSp , R.array.print_num);
        printSp.setSelection(config.getPrinterTickNumber()-1 , true);

        setAdaper(exitSp , R.array.exit_time);
        exitSp.setSelection((config.getWaitUserTime() / 5) - 1 , true);

        setAdaper(waitSP , R.array.wait_time);
        waitSP.setSelection((config.getWaitUserTime()/30)-1 , true);

        traceEdit.setText(config.getTraceNo());
        tpduEdit.setText(config.getTpdu());
        batchEdit.setText(config.getBatchNo());
        firmEdit.setText(config.getFirmCode());
        reversal.setText(String.valueOf(config.getReversalCount()));

        v.findViewById(R.id.setting_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trace = traceEdit.getText().toString();
                String tpdu = tpduEdit.getText().toString();
                String batch = batchEdit.getText().toString();
                String firm = firmEdit.getText().toString();
                String rever = reversal.getText().toString();
                if(PAYUtils.isNullWithTrim(trace)||PAYUtils.isNullWithTrim(tpdu)||PAYUtils.isNullWithTrim(batch)||
                        PAYUtils.isNullWithTrim(firm)||PAYUtils.isNullWithTrim(rever)){
                    UIUtils.toast(TransparaFrags.this , false , R.string.data_null);
                }else {
                    config.setTraceNo(Integer.parseInt(trace))
                            .setBatchNo(Integer.parseInt(batch))
                            .setTpdu(tpdu)
                            .setFirmCode(firm)
                            .setReversalCount(Integer.parseInt(rever))
                            .setPrinterTickNumber(printSp.getSelectedItemPosition()+1)
                            //.setExitTime((exitSp.getSelectedItemPosition()+launcher_classical_en)*5)
                            .setWaitUserTime((waitSP.getSelectedItemPosition()+1)*30)
                            .setTrackEncrypt(trackSp.getSelectedItemPosition()==0?true:false)
                            .save();
                    UIUtils.toast(TransparaFrags.this , true , R.string.save_success);
                }
            }
        });
    }

    private void readMasterInfo(View v){
        final EditText oldEdot = (EditText) v.findViewById(R.id.setting_master_oldpwd_edit);
        final EditText newEdit = (EditText) v.findViewById(R.id.setting_master_newpwd_edit);
        v.findViewById(R.id.setting_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String old = oldEdot.getText().toString();
                String news = newEdit.getText().toString();
                if(old.equals(TMConfig.getInstance().getMasterPass())){
                    if(!PAYUtils.isNullWithTrim(news)){
                        config.setMasterPass(news).save();
                        UIUtils.toast(TransparaFrags.this , true , R.string.save_success);
                    }else {
                        UIUtils.toast(TransparaFrags.this , false , R.string.data_null);
                    }
                }else {
                    UIUtils.toast(TransparaFrags.this , false ,R.string.original_pass_err);
                }
            }
        });
    }

    private void readPasswordSwitch(View v){
        final Spinner spinner = (Spinner) v.findViewById(R.id.setting_trans_input_pass_revocation);
        setAdaper(spinner , R.array.onoff);
        spinner.setSelection(TMConfig.getInstance().getRevocationPassSwitch()?0:1);
        v.findViewById(R.id.setting_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                config.setRevocationPassWSwitch(spinner.getSelectedItemPosition()==0?true:false).save();
                UIUtils.toast(TransparaFrags.this , true , R.string.save_success);
            }
        });
    }

    private void readCarduseSwitch(View v){
        final Spinner spinner = (Spinner) v.findViewById(R.id.setting_trans_input_pass_revocation);
        setAdaper(spinner , R.array.onoff);
        spinner.setSelection(TMConfig.getInstance().getRevocationCardSwitch()?0:1);
        v.findViewById(R.id.setting_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                config.setRevocationCardSwitch(spinner.getSelectedItemPosition()==0?true:false).save();
                UIUtils.toast(TransparaFrags.this , true , R.string.save_success);
            }
        });
    }

    private void scannerSwitch(View v){
        final Spinner beep = (Spinner) v.findViewById(R.id.setting_trans_scanner_beep);
        setAdaper(beep , R.array.onoff);
        beep.setSelection(config.isScanBeeper()?0:1);
        final Spinner torch = (Spinner) v.findViewById(R.id.setting_trans_scanner_torchon);
        setAdaper(torch , R.array.onoff);
        torch.setSelection(config.isScanTorchOn()?0:1);
        final Spinner back = (Spinner) v.findViewById(R.id.setting_trans_scanner_front_behind);
        setAdaper(back , R.array.scanner_back);
        back.setSelection(config.isScanBack()?0:1);
        v.findViewById(R.id.setting_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                config.setScanBack(back.getSelectedItemPosition()==0?true:false)
                        .setScanTorchOn(torch.getSelectedItemPosition()==0?true:false)
                        .setScanBeeper(beep.getSelectedItemPosition()==0?true:false)
                        .save();
                UIUtils.toast(TransparaFrags.this , true , R.string.save_success);
            }
        });
    }

    private void setAdaper(Spinner spinner , int arrayID){
        String[] array = getResources().getStringArray(arrayID);
        ArrayAdapter adapter = new ArrayAdapter(this , android.R.layout.simple_spinner_dropdown_item , array);
        spinner.setAdapter(adapter);
    }
}
