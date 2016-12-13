package com.gjyf.trolleybus.trolleybuss.myview;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjyf.trolleybus.trolleybuss.R;

@SuppressLint("WrongViewCast")
public class MyEtIPDialog extends Dialog {

    protected MyEtIPDialog(Context context) {
        super(context);

    }

    protected MyEtIPDialog(Context context, int theme) {
        super(context, theme);

    }

    public static class Builder {
        private Context context;
        private String strhint = "请输入信息";
        private String text;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentVIew;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private String mEtMessgae;
        private EditText et1, et2, et3, et4;

        public Builder(Context context) {
            this.context = context;
        }

        public String getmEtMessgae() {
            return mEtMessgae;
        }

        public Builder setmEtMessgae(String mEtMessgae) {
            this.mEtMessgae = mEtMessgae;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public Builder sethint(String strhint) {
            this.strhint = strhint;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentVIew = v;
            return this;
        }

        public String getEditContent() {
            String e1 = et1.getText().toString().trim();
            String e2 = et2.getText().toString().trim();
            String e3 = et3.getText().toString().trim();
            String e4 = et4.getText().toString().trim();
            String st = "";
            if (!TextUtils.isEmpty(e1) && !TextUtils.isEmpty(e2) && !TextUtils.isEmpty(e3) && !TextUtils.isEmpty(e4)) {
                st = e1 + "." + e2 + "." + e3 + "." + e4;
            }

            return st;
        }


        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int netiveButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(netiveButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String netiveButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = netiveButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public MyEtIPDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final MyEtIPDialog dialog = new MyEtIPDialog(context, R.style.dialog);
            View layout = inflater.inflate(R.layout.my_dialog_ip_layout,
                    null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            ((TextView) layout.findViewById(R.id.tv_my_dialog_et_titile))
                    .setText(title);
            et1 = (EditText) layout.findViewById(R.id.edit1);
            et2 = (EditText) layout.findViewById(R.id.edit2);
            et3 = (EditText) layout.findViewById(R.id.edit3);
            et4 = (EditText) layout.findViewById(R.id.edit4);

            TextWatcher tw = new TextWatcher() {
                //@Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                //@Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                //@Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() == 3) {
                        if (et1.isFocused()) {
                            et1.clearFocus();
                            et2.requestFocus();
                        } else if (et2.isFocused()) {
                            et2.clearFocus();
                            et3.requestFocus();
                        } else if (et3.isFocused()) {
                            et3.clearFocus();
                            et4.requestFocus();
                        }
                    }
                }
            };
            et1.addTextChangedListener(tw);
            et2.addTextChangedListener(tw);
            et3.addTextChangedListener(tw);
            et4.addTextChangedListener(tw);
            if (!TextUtils.isEmpty(text)) {
                text = text.replace(".", ":");
                String[] strings = text.split(":");
                et1.setText(strings[0]);
                et2.setText(strings[1]);
                et3.setText(strings[2]);
                et4.setText(strings[3]);
            }

            if (positiveButtonText != null) {
                Button btnPositive = (Button) layout
                        .findViewById(R.id.btn_mydialog_et_positive);
                btnPositive.setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    btnPositive.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            positiveButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                }
            } else {
                layout.findViewById(R.id.btn_mydialog_et_positive)
                        .setVisibility(View.GONE);
            }

            if (negativeButtonText != null) {
                Button btnNegative = (Button) layout
                        .findViewById(R.id.btn_mydialog_et_negative);
                btnNegative.setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    btnNegative.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            negativeButtonClickListener.onClick(dialog,
                                    DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            } else {
                layout.findViewById(R.id.btn_mydialog_et_negative)
                        .setVisibility(View.GONE);
            }

            if (message != null) {
                TextView tvMessages = (TextView) layout
                        .findViewById(R.id.tv_my_dialog_message);
                tvMessages.setText(message);
            } else if (contentVIew != null) {
                ((LinearLayout) layout.findViewById(R.id.tv_my_dialog_message))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.tv_my_dialog_message))
                        .addView(contentVIew, new LayoutParams(
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT));
            }

            return dialog;
        }
    }
}
