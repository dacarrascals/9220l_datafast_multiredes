package cn.desert.newpos.payui.transrecord;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.newpos.pay.R;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.PAYUtils;

import java.util.Locale;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;

import static cn.desert.newpos.payui.UIUtils.labelHTML;
import static com.newpos.libpay.trans.Trans.Type.ELECTRONIC;
import static com.newpos.libpay.trans.Trans.Type.ELECTRONIC_DEFERRED;

public class HistorylogAdapter extends ListAdapter<TransLogData> {

	private TMConfig config;
	private OnItemReprintClick click ;

	public HistorylogAdapter(Activity context , OnItemReprintClick l) {
		super(context);
		config = TMConfig.getInstance();
		click = l ;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHold viewHold = null;
		TransLogData item = null;
		if (mList.size() > 0) {
			item = mList.get(position);
		}
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_history_item, null);
			viewHold = new ViewHold();
			viewHold.tv_pan = (TextView) convertView.findViewById(R.id.tv_pan);
			viewHold.tv_voucherno = (TextView) convertView.findViewById(R.id.tv_voucherno);
			viewHold.tv_authno = (TextView) convertView.findViewById(R.id.tv_authno);
			viewHold.tv_amount = (TextView) convertView.findViewById(R.id.tv_amount);
			viewHold.tv_tip = (TextView) convertView.findViewById(R.id.tv_tip);
			viewHold.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
			viewHold.tv_batchno = (TextView) convertView.findViewById(R.id.tv_batchno);
			viewHold.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
			viewHold.tv_right_top = (TextView) convertView.findViewById(R.id.status_flag);
			viewHold.reprint = (Button) convertView.findViewById(R.id.re_print);
			convertView.setTag(viewHold);
		} else {
			viewHold = (ViewHold) convertView.getTag();
		}

		if (item != null) {
			String pan = item.getPan() ;
			if (!PAYUtils.isNullWithTrim(pan)) {
				String temp ;
				if(item.isScan()){
					temp = labelHTML(UIUtils.getStringByInt(mContext, R.string.pay_code), pan);
				}else {
					temp = labelHTML(UIUtils.getStringByInt(mContext, R.string.card_num), pan);
				}
				viewHold.tv_pan.setText(Html.fromHtml(temp));
			}

			String auth = item.getAuthCode() ;
			if (!PAYUtils.isNullWithTrim(auth)) {
				viewHold.tv_authno.setText(Html.fromHtml(labelHTML(UIUtils.getStringByInt(mContext, R.string.auth_code), auth)));
			}

			final String traceno = item.getTraceNo() ;
			if (!PAYUtils.isNullWithTrim(traceno)) {
				viewHold.tv_voucherno.setText(Html.fromHtml(labelHTML(UIUtils.getStringByInt(mContext, R.string.voucher_num), traceno)));
			}

			String amount = item.getAmount().toString() ;
			if (!PAYUtils.isNullWithTrim(amount)) {
				/*if (item.getTypeCoin().equals(LOCAL))
					viewHold.tv_amount.setText(Html.fromHtml(labelHTML(UIUtils.getStringByInt(mContext, R.string.amount), " DOLAR. " + PAYUtils.TwoWei(amount))));
				else
					viewHold.tv_amount.setText(Html.fromHtml(labelHTML(UIUtils.getStringByInt(mContext, R.string.amount), " DOLAR " + PAYUtils.TwoWei(amount))));*/
				viewHold.tv_amount.setText(Html.fromHtml(labelHTML(UIUtils.getStringByInt(mContext, R.string.amount), " $ " + PAYUtils.TwoWei(amount))));
			}

			viewHold.tv_tip.setVisibility(View.GONE);
			/*String tip = String.valueOf(item.getTipAmout());
			if (!PAYUtils.isNullWithTrim(tip)) {
				if (item.getTypeCoin().equals(LOCAL))
					viewHold.tv_tip.setText(Html.fromHtml(labelHTML(UIUtils.getStringByInt(mContext, R.string.tip), " DOLAR. " + PAYUtils.TwoWei(tip))));
				else
					viewHold.tv_tip.setText(Html.fromHtml(labelHTML(UIUtils.getStringByInt(mContext, R.string.tip), " DOLAR " + PAYUtils.TwoWei(tip))));
			}else {
				viewHold.tv_tip.setVisibility(View.GONE);
			}*/

			String en = item.getEName() ;
			if(!PAYUtils.isNullWithTrim(en)) {
				if(Locale.getDefault().getLanguage().equals("zh")){
					viewHold.tv_status.setText(Html.fromHtml(labelHTML(UIUtils.getStringByInt(mContext, R.string.trans_type), MasterControl.en2ch(en))));
				}else {
					if (en.equals(ELECTRONIC_DEFERRED))//Validacion para visualizacion grafica de PE
						viewHold.tv_status.setText(Html.fromHtml(labelHTML(UIUtils.getStringByInt(mContext, R.string.trans_type), ELECTRONIC.replace("_", " "))));
					else
						viewHold.tv_status.setText(Html.fromHtml(labelHTML(UIUtils.getStringByInt(mContext, R.string.trans_type), en.replace("_", " "))));
				}
			}

			if(item.getIsVoided()) {
				viewHold.tv_right_top.setVisibility(View.VISIBLE);
				viewHold.tv_right_top.setText(UIUtils.getStringByInt(mContext , R.string.is_revocation));
			}else if(item.isPreComp()){
				viewHold.tv_right_top.setVisibility(View.VISIBLE);
				viewHold.tv_right_top.setText(UIUtils.getStringByInt(mContext , R.string.is_completed));
			}else {
				viewHold.tv_right_top.setVisibility(View.GONE);
			}

			viewHold.tv_date.setText(Html.fromHtml(labelHTML(UIUtils.getStringByInt(mContext, R.string.trans_date), PAYUtils.printStr(item.getLocalDate(), item.getLocalTime()))));

			String bacth = item.getBatchNo();//acquirerRow.getSb_curr_batch_no();//config.getBatchNo() ;
			if (!PAYUtils.isNullWithTrim(bacth)) {
				viewHold.tv_batchno.setText(Html.fromHtml(labelHTML(UIUtils.getStringByInt(mContext, R.string.batch_num), bacth)));
			}

			viewHold.reprint.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(click!=null){
						click.OnItemClick(traceno);
					}
				}
			});

			convertView.setTag(R.id.tag_item_history_trans, item);
		}
		return convertView;
	}

	final class ViewHold {
		TextView tv_pan;
		TextView tv_voucherno;
		TextView tv_authno;
		TextView tv_amount;
		TextView tv_tip;
		TextView tv_date;
		TextView tv_batchno;
		TextView tv_status;
		TextView tv_right_top;
		Button reprint ;
	}

	public interface OnItemReprintClick{
		void OnItemClick(String traceNO);
	}
}
