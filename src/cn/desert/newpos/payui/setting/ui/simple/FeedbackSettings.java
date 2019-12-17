package cn.desert.newpos.payui.setting.ui.simple;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.newpos.pay.R;
import com.desert.email.EmailStatus;
import com.desert.email.Emailsend;
import com.newpos.libpay.utils.PAYUtils;

import cn.desert.newpos.payui.base.BaseActivity;
import cn.desert.newpos.payui.setting.ui.SettingsFrags;

/**
 * Created by zhouqiang on 2017/11/16.
 * @author zhouqiang
 */
public class FeedbackSettings extends BaseActivity {

    private EditText content ;
    private Button submit ;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_home_advice);
        setNaviTitle(getIntent().getExtras().getString(SettingsFrags.JUMP_KEY));
        setRightVisiblity(View.GONE);
        content = (EditText) findViewById(R.id.feedback_content);
        submit = (Button) findViewById(R.id.feedback_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    private void submit(){
        final String advice = content.getText().toString();
        if(PAYUtils.isNullWithTrim(advice)){
            Toast.makeText(this , getString(R.string.data_null) , Toast.LENGTH_SHORT).show();
            return;
        }
        dialog = new ProgressDialog(this);
        dialog.setTitle(getString(R.string.prompt));
        dialog.setMessage(getString(R.string.sending_content));
        dialog.show();
        final Message message = handler.obtainMessage() ;
        new Thread(){
            @Override
            public void run() {
                message.what = 0 ;
                try {
                    int ret = Emailsend.send(FeedbackSettings.this , advice , "融桌面程序用户的反馈及建议");
                    message.obj = ret ;
                }catch (Exception e){
                    message.obj = EmailStatus.EMAIL_UNKNOWN_EXP ;
                }
                handler.sendMessage(message);
            }
        }.start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(FeedbackSettings.this , getDetails((int)msg.obj) ,
                    Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            super.handleMessage(msg);
        }

        private String getDetails(int status){
            String str ;
            switch (status){
                case EmailStatus.EMAIL_SENT:
                    str = FeedbackSettings.this.getString(R.string.send_success) ;
                    break;
                default:
                    str = FeedbackSettings.this.getString(R.string.send_fail) ;
                    break;
            }
            return str ;
        }
    };
}
