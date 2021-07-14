package cn.desert.newpos.payui.setting.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.newpos.pay.R;
import com.newpos.libpay.utils.PAYUtils;

public class IPEditText extends LinearLayout {

	private EditText mFirstIP;
	private EditText mSecondIP;
	private EditText mThirdIP;
	private EditText mFourthIP;

	private String mText;
	private String mText1;
	private String mText2;
	private String mText3;
	private String mText4;
	private String tips;

	private SharedPreferences mPreferences;

	public IPEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		/**
		 * 初始化控件
		 */
		View view = LayoutInflater.from(context).inflate(R.layout.app_ipedit, this);
		mFirstIP = (EditText) findViewById(R.id.ip_first);
		mSecondIP = (EditText) findViewById(R.id.ip_second);
		mThirdIP = (EditText) findViewById(R.id.ip_third);
		mFourthIP = (EditText) findViewById(R.id.ip_fourth);
		tips = context.getResources().getString(R.string.ip_invalid);
		mPreferences = context.getSharedPreferences("config_IP",Context.MODE_PRIVATE);
		mFirstIP.setTextSize(20);
		mSecondIP.setTextSize(20);
		mThirdIP.setTextSize(20);
		mFourthIP.setTextSize(20);
		OperatingEditText(context);
	}

	/**
	 * 获得EditText中的内容,当每个Edittext的字符达到三位时,自动跳转到下一个EditText,当用户点击.时,
	 * 下一个EditText获得焦点
	 */
	boolean borrado = false;
	int lenTxt = 0;
	private void OperatingEditText(final Context context) {
		mFirstIP.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				/**
				 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
				 * 用户点击啊.时,下一个EditText获得焦点
				 */
				if (s != null && s.length() > 0) {
					if (s.length() > 1 || s.toString().trim().contains(".")) {
						if (s.toString().trim().contains(".")) {
							mText1 = s.toString().substring(0, s.length() - 1);
							mFirstIP.setText(mText1);
						} else {
							mText1 = s.toString().trim();
						}
						if (Integer.parseInt(mText1) > 255) {
							mFirstIP.setText("");
							return;

						}
						Editor editor = mPreferences.edit();
						editor.putInt("IP_FIRST", mText1.length());
						editor.apply();

						if (s.length() > 2) {
							mSecondIP.setFocusable(true);
							mSecondIP.requestFocus();
						}
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				//TODO  zq
			}
		});

		mFirstIP.setOnKeyListener(new View.OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return false;
				}
				return false;
			}
		});

		mSecondIP.setOnKeyListener(new View.OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return false;
				}
				return false;
			}
		});

		mThirdIP.setOnKeyListener(new View.OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return false;
				}
				return false;
			}
		});

		mFourthIP.setOnKeyListener(new View.OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return false;
				}
				return false;
			}
		});

		mSecondIP.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				/**
				 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
				 * 用户点击啊.时,下一个EditText获得焦点
				 */
				if (s != null && s.length() > 0) {
					if (s.length() > 1 || s.toString().trim().contains(".")) {
						if (s.toString().trim().contains(".")) {
							mText2 = s.toString().substring(0, s.length() - 1);
							mSecondIP.setText(mText2);
						} else {
							mText2 = s.toString().trim();
						}
						if (Integer.parseInt(mText2) > 255) {
							//TODO  zq
							return;
						}
						Editor editor = mPreferences.edit();
						editor.putInt("IP_SECOND", mText2.length());
						editor.apply();

						if (s.length() > 2) {
							mThirdIP.setFocusable(true);
							mThirdIP.requestFocus();
						}
					}
				}

				/**
				 * 当用户需要删除时,此时的EditText为空时,上一个EditText获得焦点
				 */
				if (start == 0 && s != null && s.length() == 0
						&& ! PAYUtils.isNullWithTrim(mFirstIP.getText().toString())
						&& mFirstIP.length() > 1 && borrado) {
					borrado = false;
					mFirstIP.setFocusable(true);
					mFirstIP.requestFocus();
					mFirstIP.setSelection(mPreferences.getInt("IP_FIRST", 0));
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				lenTxt = s.length();
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() < lenTxt) {
					borrado = true;
				}
				if (mSecondIP.getText().length() > 0) {
					if (Integer.parseInt(mSecondIP.getText().toString()) > 255) {
						borrado = false;
						mSecondIP.setText("");
					}
				}
			}
		});

		mThirdIP.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				/**
				 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
				 * 用户点击啊.时,下一个EditText获得焦点
				 */
				if (s != null && s.length() > 0) {
					if (s.length() > 1 || s.toString().trim().contains(".")) {
						if (s.toString().trim().contains(".")) {
							mText3 = s.toString().substring(0, s.length() - 1);
							mThirdIP.setText(mText3);
						} else {
							mText3 = s.toString().trim();
						}

						if (Integer.parseInt(mText3) > 255) {
							//TODO  zq
							return;
						}

						Editor editor = mPreferences.edit();
						editor.putInt("IP_THIRD", mText3.length());
						editor.apply();

						if (s.length() > 2) {
							mFourthIP.setFocusable(true);
							mFourthIP.requestFocus();
						}
					}
				}

				/**
				 * 当用户需要删除时,此时的EditText为空时,上一个EditText获得焦点
				 */
				if (start == 0 && s != null && s.length() == 0
						&& !PAYUtils.isNullWithTrim(mSecondIP.getText().toString())
						&& mSecondIP.length() > 1 && borrado) {
					borrado = false;
					mSecondIP.setFocusable(true);
					mSecondIP.requestFocus();
					mSecondIP.setSelection(mPreferences.getInt("IP_SECOND", 0));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				lenTxt = s.length();
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() < lenTxt) {
					borrado = true;
				}
				if (mThirdIP.getText().length() > 0) {
					if (Integer.parseInt(mThirdIP.getText().toString()) > 255) {
						borrado = false;
						mThirdIP.setText("");
					}
				}
			}
		});

		mFourthIP.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				/**
				 * 获得EditTe输入内容,做判断,如果大于255,提示不合法,当数字为合法的三位数下一个EditText获得焦点,
				 * 用户点击啊.时,下一个EditText获得焦点
				 */
				if (s != null && s.length() > 0) {
					mText4 = s.toString().trim();

					if (Integer.parseInt(mText4) > 255) {
						//TODO  zq
						return;
					}

					Editor editor = mPreferences.edit();
					editor.putInt("IP_FOURTH", mText4.length());
					editor.apply();
				}

				/**
				 * 当用户需要删除时,此时的EditText为空时,上一个EditText获得焦点
				 */
				if (start == 0 && s != null && s.length() == 0
						&& !PAYUtils.isNullWithTrim(mThirdIP.getText().toString())
						&& mThirdIP.length() > 1 && borrado) {
					borrado = false;
					mThirdIP.setFocusable(true);
					mThirdIP.requestFocus();
					mThirdIP.setSelection(mPreferences.getInt("IP_THIRD", 0));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				lenTxt = s.length();
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() < lenTxt) {
					borrado = true;
				}
				if (mFourthIP.getText().length() > 0) {
					if (Integer.parseInt(mFourthIP.getText().toString()) > 255) {
						borrado = false;
						mFourthIP.setText("");
					}
				}
			}
		});
	}

	public String getIPText() {
		if (!PAYUtils.isNullWithTrim(mFirstIP.getText().toString())
				&& !PAYUtils.isNullWithTrim(mSecondIP.getText().toString())
				&& !PAYUtils.isNullWithTrim(mThirdIP.getText().toString())
				&& !PAYUtils.isNullWithTrim(mFourthIP.getText().toString())) {
			return mFirstIP.getText().toString() + "."
					+ mSecondIP.getText().toString() + "."
					+ mThirdIP.getText().toString() + "."
					+ mFourthIP.getText().toString();
		}else {
			return null;
		}
	}

	public void setIPText(String[] ip){
		mFirstIP.setText(ip[0]);
		mSecondIP.setText(ip[1]);
		mThirdIP.setText(ip[2]);
		mFourthIP.setText(ip[3]);
	}

	public void setLiveOrDeath(boolean isLive){
		if(!isLive){
			mFirstIP.setTextColor(Color.GRAY);
			mSecondIP.setTextColor(Color.GRAY);
			mThirdIP.setTextColor(Color.GRAY);
			mFourthIP.setTextColor(Color.GRAY);
		}else {
			mFirstIP.setTextColor(Color.BLACK);
			mSecondIP.setTextColor(Color.BLACK);
			mThirdIP.setTextColor(Color.BLACK);
			mFourthIP.setTextColor(Color.BLACK);
		}
		mFirstIP.setEnabled(isLive);
		mSecondIP.setEnabled(isLive);
		mThirdIP.setEnabled(isLive);
		mFourthIP.setEnabled(isLive);
	}

	public boolean isOk() {
		if (PAYUtils.isNullWithTrim(mFirstIP.getText().toString())
				|| PAYUtils.isNullWithTrim(mSecondIP.getText().toString())
				|| PAYUtils.isNullWithTrim(mThirdIP.getText().toString())
				|| PAYUtils.isNullWithTrim(mFourthIP.getText().toString())) {
			return false;
		}
		return true;
	}
}
