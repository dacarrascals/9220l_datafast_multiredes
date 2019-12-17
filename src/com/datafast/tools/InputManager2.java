package com.datafast.tools;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.desert.keyboard.Desertboard;
import com.android.desert.keyboard.DesertboardListener;
import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputListener;
import com.android.desert.keyboard.InputManager;
import com.newpos.libpay.Logger;

import java.text.DecimalFormat;


public class InputManager2 extends InputManager {

    private Context context;
    private InputManager instance;
    private WindowManager mWindowManager;
    private LinearLayout container;
    private Desertboard keyboardView;
    private ImageView alipayView;
    private ImageView wetchatView;
    private ImageView unionpayView;
    private EditText input;
    private String mTitle = InputManager.class.getSimpleName();
    private InputManager.Mode mInputMode;
    private InputManager.Lang mLang;
    private boolean mDisorder;
    private boolean mAddEdit;
    private boolean mAddKeyboard;
    private boolean mAddStyles;
    private boolean useOnce;
    private int mLenData;
    private InputListener mListener;
    private static final String PAY_EN = "Please chose pay style";
    private static final String PAY_CH = "请选择付款方式";
    private static final String NULL_EN = "Input cannot be empty";
    private static final String NULL_CH = "输入不能为空";
    private static final int MAX_LEN_TITLE = 52;//en el diseño base solo se tiene un total de 2 lineas, con un maximo de 26 caracteres por linea

    @SuppressLint("WrongConstant")
    public InputManager2(Context c) {
        super(c);
        this.mInputMode = InputManager.Mode.AMOUNT;
        this.mLang = InputManager.Lang.EN;
        this.mDisorder = false;
        this.mAddEdit = true;
        this.mAddKeyboard = true;
        this.mAddStyles = false;
        this.useOnce = false;
        this.context = c;
        this.instance = this;
        this.mLenData = 8;
        this.mWindowManager = (WindowManager) this.context.getSystemService("window");
    }

    @SuppressLint("WrongConstant")
    // @Override
    public View getView(boolean flagBtn) {

        this.container = new LinearLayout(this.context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
        this.container.setLayoutParams(lp);
        this.container.setOrientation(1);
        this.container.setBackgroundColor(-1);
        LinearLayout layout = new LinearLayout(this.context);
        lp = new LinearLayout.LayoutParams(-1, 0, 2.0F);
        layout.setLayoutParams(lp);
        layout.setOrientation(1);
        layout.setBackgroundColor(Color.parseColor("#eeeeee"));
        TextView tv = new TextView(this.context);
        if (this.mTitle.length()>MAX_LEN_TITLE)
            tv.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 3.0F));
        else
            tv.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1.4F));
        tv.setTextColor(Color.parseColor("#333333"));
        tv.setTextSize(26.0F);
        tv.setTypeface(null,Typeface.BOLD);
        tv.setText(this.mTitle);
        tv.setGravity(17);
        layout.addView(tv);
        if (this.mAddEdit) {
            this.input = new EditText(this.context);
            if (this.mTitle.length()>MAX_LEN_TITLE)
                lp = new LinearLayout.LayoutParams(-1, 0, 1.5F);
            else
                lp = new LinearLayout.LayoutParams(-1, 0, 1.1F);
            lp.setMarginStart(20);
            lp.setMarginEnd(20);
            this.input.setLayoutParams(lp);
            this.input.setTextSize(30.0F);
            this.input.setTypeface(null,Typeface.BOLD);
            this.input.setTextColor(Color.parseColor("#F54D4F"));
            this.initEditText1(this.input);
            layout.addView(this.input);
        }

        TextView tv1 = new TextView(this.context);
        tv1.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1.0F));
        layout.addView(tv1);
        this.container.addView(layout);
        if (this.mAddKeyboard) {
            this.keyboardView = new Desertboard(this.context);
            this.keyboardView.setVisibility(0);
            this.keyboardView.setListener(new InputManager2.UserInputListener());
            this.keyboardView.setDisorder(this.mDisorder);
            this.keyboardView.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 3.0F));
            this.container.addView(this.keyboardView);
        }

        if (this.mAddStyles) {

            LinearLayout ll = new LinearLayout(this.context);
            if (flagBtn)
                ll.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 0.5F));
            else
                ll.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1.0F));
            ll.setOrientation(0);
            ll.setBackgroundColor(Color.parseColor("#eeeeee"));
            this.alipayView = new ImageView(this.context);
            this.wetchatView = new ImageView(this.context);
            if (flagBtn) {
                this.wetchatView.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 0.5F));
                this.wetchatView.setImageBitmap(Desertboard.getBitmapFromAssets(this.context, "back.png"));
                ll.addView(this.wetchatView);
            } else {
                this.alipayView.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0F));
                this.alipayView.setImageBitmap(Desertboard.getBitmapFromAssets(this.context, "pay_ali.jpg"));
                ll.addView(this.alipayView);
            }
            this.container.addView(ll);
            this.initViewsListeners1();
        }

        return this.container;
    }

    public InputManager setTitle(String title) {
        this.mTitle = title;
        return this.instance;
    }

    public InputManager setTitle(int rid) {
        this.mTitle = this.context.getResources().getString(rid);
        return this.instance;
    }

    public InputManager setLang(InputManager.Lang l) {
        this.mLang = l;
        return this.instance;
    }

    public InputManager setUseOnce(boolean useOnce) {
        this.useOnce = useOnce;
        return this.instance;
    }

    public InputManager addEdit(InputManager.Mode mode) {
        this.mAddEdit = true;
        this.mInputMode = mode;
        return this.instance;
    }

    public InputManager addEdit(InputManager.Mode mode, int lenData) {
        this.mAddEdit = true;
        this.mInputMode = mode;
        this.mLenData = lenData;
        return this.instance;
    }

    public InputManager addKeyboard(boolean disorder) {
        this.mAddKeyboard = true;
        this.mDisorder = disorder;
        return this.instance;
    }

    public InputManager addStyles() {
        this.mAddStyles = true;
        return this.instance;
    }

    public InputManager setListener(InputListener l) {
        this.mListener = l;
        return this.instance;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void release() {
        if (this.container != null) {
            this.container.removeAllViews();
            this.container = null;
        }

    }

    private void initEditText1(final EditText et) {
        if (InputManager.Mode.AMOUNT == this.mInputMode) {
            et.setBackgroundColor(-1);
            BitmapDrawable drawable;
            if (this.mLang == InputManager.Lang.EN) {
                drawable = new BitmapDrawable(Desertboard.getBitmapFromAssets(this.context, "dollor.png"));
            } else {
                drawable = new BitmapDrawable(Desertboard.getBitmapFromAssets(this.context, "rmb.png"));
            }

            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            Display dm = this.mWindowManager.getDefaultDisplay();
            et.setPadding(dm.getWidth() / 2 - 200, 0, 0, 0);
            et.setCompoundDrawables(drawable, (Drawable) null, (Drawable) null, (Drawable) null);
            et.setText("0.00");
            et.setCursorVisible(false);
            et.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    et.requestFocus();
                    return false;
                }
            });
            et.setRawInputType(3);
            et.addTextChangedListener(new InputManager2.EditChangeXsTwoListener(et));
            et.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            et.setSelection(et.getText().length());
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        }

        if (InputManager.Mode.VOUCHER == this.mInputMode || InputManager.Mode.AUTHCODE == this.mInputMode) {
            et.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            et.setBackgroundColor(-1);
            et.setGravity(17);
            et.setInputType(2);
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        }

        if (InputManager.Mode.PASSWORD == this.mInputMode) {
            et.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            et.setBackgroundColor(-1);
            et.setGravity(17);
            et.setInputType(129);
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(this.mLenData)});
        }

        if (InputManager.Mode.DATETIME == this.mInputMode) {
            et.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            et.setBackgroundColor(-1);
            et.setGravity(17);
            et.setInputType(2);
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        }

        if (InputManager.Mode.REFERENCE == this.mInputMode) {
            et.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            et.setBackgroundColor(-1);
            et.setGravity(17);
            et.setInputType(2);
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(this.mLenData)});
        }

    }

    private void initViewsListeners1() {
        this.alipayView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                InputManager2.this.handleInputData1(InputManager.Style.ALIPAY);
            }
        });
        this.wetchatView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                InputInfo inputInfo = new InputInfo();
                inputInfo.setResultFlag(false);
                mListener.callback(inputInfo);
                if (useOnce) {
                    container.removeAllViews();
                }
            }
        });
    }

    @SuppressLint("WrongConstant")
    private void handleInputData1(InputManager.Style style) {
        String in = this.input.getText().toString();
        InputInfo inputInfo = new InputInfo();
        inputInfo.setNextStyle(style);
        if (style == InputManager.Style.COMMONINPUT && this.mAddStyles) {
            if (this.mLang == InputManager.Lang.CH) {
                Toast.makeText(this.context, "请选择付款方式", 0).show();
            } else {
                Toast.makeText(this.context, "Please chose pay style", 0).show();
            }
        } else {
            inputInfo.setResultFlag(true);
            if (InputManager.Mode.AMOUNT == this.mInputMode) {
                in = in.replaceAll("(\\.)?", "");
            }

            if (InputManager.Mode.VOUCHER == this.mInputMode && in.length() < 6) {
                in = padleft(in, 6, '0');
            }

            if (InputManager.Mode.DATETIME == this.mInputMode && in != null && in.length() < 8) {
                if (in.length() == 6) {
                    in = padleft(in, 7, '0');
                }

                if (in != null) {
                    in = padleft(in, 8, '2');
                }

            }

            inputInfo.setResult(in);
            this.mListener.callback(inputInfo);
            if (this.useOnce) {
                this.container.removeAllViews();
            }
        }

    }

    private static void sendKeyCode(final int keyCode) {
        (new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception var2) {
                    Logger.error("Exception" + var2.toString());
                }

            }
        }).start();
    }

    private static String padleft(String s, int len, char c) {
        s = s.trim();
        if (s.length() > len) {
            return null;
        } else {
            StringBuilder d = new StringBuilder(len);
            int var4 = len - s.length();

            while (var4-- > 0) {
                d.append(c);
            }

            d.append(s);
            return d.toString();
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    private static class EditChangeXsTwoListener implements TextWatcher {
        private EditText editText = null;

        public EditChangeXsTwoListener(EditText e) {
            this.editText = e;
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            DecimalFormat dec = new DecimalFormat("0.00");
            if (!s.toString().matches("^((\\d{1})|([1-9]{1}\\d+))(\\.\\d{2})?$")) {
                String userInput = s.toString().replaceAll("[^\\d]", "");
                if (userInput.length() > 0) {
                    Double in = Double.valueOf(Double.parseDouble(userInput));
                    double percen = in.doubleValue() / 100.0D;
                    this.editText.setText(dec.format(percen));
                    this.editText.setSelection(this.editText.getText().length());
                }
            }

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void afterTextChanged(Editable s) {
        }
    }

    final class UserInputListener implements DesertboardListener {
        UserInputListener() {
        }

        public void onVibrate(int ms) {
        }

        public void onChar() {
        }

        public void onInputKey(int key) {
            switch (key) {
                case 0:
                    InputManager2.sendKeyCode(7);
                    break;
                case 1:
                    InputManager2.sendKeyCode(8);
                    break;
                case 2:
                    InputManager2.sendKeyCode(9);
                    break;
                case 3:
                    InputManager2.sendKeyCode(10);
                    break;
                case 4:
                    InputManager2.sendKeyCode(11);
                    break;
                case 5:
                    InputManager2.sendKeyCode(12);
                    break;
                case 6:
                    InputManager2.sendKeyCode(13);
                    break;
                case 7:
                    InputManager2.sendKeyCode(14);
                    break;
                case 8:
                    InputManager2.sendKeyCode(15);
                    break;
                case 9:
                    InputManager2.sendKeyCode(16);
                case 128:
                default:
                    break;
                case 129:
                    if (InputManager2.this.mInputMode == InputManager.Mode.AMOUNT   ||
                        InputManager2.this.mInputMode == InputManager.Mode.PASSWORD ||
                        InputManager2.this.mInputMode == InputManager.Mode.VOUCHER  ||
                        InputManager2.this.mInputMode == InputManager.Mode.REFERENCE) {
                        InputManager2.this.handleInputData1(InputManager.Style.UNIONPAY);
                    } else {
                        InputManager2.this.handleInputData1(InputManager.Style.COMMONINPUT);
                    }
                    break;
                case 130:
                    InputManager2.sendKeyCode(67);
            }

        }

        public void onClr() {
        }

        public void onCannel() {
        }

        public void onEnter(String encpin) {
        }
    }

    public static enum Mode {
        AMOUNT(1),
        PASSWORD(2),
        VOUCHER(3),
        AUTHCODE(4),
        DATETIME(5),
        REFERENCE(6);

        private int val;

        private Mode(int value) {
            this.val = value;
        }

        protected int getVal() {
            return this.val;
        }
    }

    public static enum Lang {
        CH(1),
        EN(2);

        private int val;

        private Lang(int value) {
            this.val = value;
        }

        protected int getVal() {
            return this.val;
        }
    }

    public static enum Style {
        ALIPAY(0),
        WETCHATPAY(1),
        UNIONPAY(2),
        COMMONINPUT(3);

        private int val;

        private Style(int value) {
            this.val = value;
        }

        protected int getVal() {
            return this.val;
        }
    }
}
