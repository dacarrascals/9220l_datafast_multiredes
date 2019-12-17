package cn.desert.newpos.payui.setting.ui.classical;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.desert.email.EmailStatus;
import com.desert.email.Emailsend;
import com.newpos.libpay.Logger;
import com.newpos.libpay.utils.PAYUtils;

/**
 * Created by zhouqiang on 2017/7/5.
 * @author zhouqiang
 */
public class AboutDesert implements View.OnClickListener{

    private Activity mActivity = null ;
    private RelativeLayout rLayout = null ;
    private EditText content = null ;

    public AboutDesert(Activity a , RelativeLayout l , String title){
        this.mActivity = a ;
        this.rLayout = l ;
        rLayout.removeAllViews();
        rLayout.inflate(mActivity , R.layout.setting_frag_aboutdesert , rLayout);
        rLayout.findViewById(R.id.setting_save).setOnClickListener(this);
        ((TextView)rLayout.findViewById(R.id.setting_title_tv)).setText(title);
        ((TextView)rLayout.findViewById(R.id.setting_save)).
                setText(mActivity.getString(R.string.send_email));
        content = (EditText) rLayout.findViewById(R.id.about_us_content);
    }

    private ProgressDialog dialog = null ;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(mActivity , getDetails((int)msg.obj) ,
                    Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            super.handleMessage(msg);
        }

        private String getDetails(int status){
            String str ;
            switch (status){
                case EmailStatus.EMAIL_SENT:
                    str = mActivity.getString(R.string.send_success) ;
                    break;
                default:
                    str = mActivity.getString(R.string.send_fail) ;
                    break;
            }
            return str ;
        }
    };

    @Override
    public void onClick(View view) {
        if(R.id.setting_save == view.getId()){
            final String email = content.getText().toString();
            if(PAYUtils.isNullWithTrim(email)){
                Toast.makeText(mActivity ,
                        mActivity.getString(R.string.data_null) ,
                        Toast.LENGTH_SHORT).show();
            }else {
                dialog = new ProgressDialog(mActivity);
                dialog.setTitle("提示");
                dialog.setMessage("正在发送反馈和建议...");
                dialog.show();
                final Message message = handler.obtainMessage() ;
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            message.what = 0 ;
                            int ret = Emailsend.send(mActivity , email , "融桌面程序用户的反馈及建议");
                            Logger.debug("Emailsend.send="+ret);
                            message.obj = ret ;
                        }catch (Exception e){
                            Logger.debug("Emailsend.send.fail");
                            message.obj = EmailStatus.EMAIL_UNKNOWN_EXP ;
                        }
                        handler.sendMessage(message);
                    }
                }.start();
            }
        }
    }
}
