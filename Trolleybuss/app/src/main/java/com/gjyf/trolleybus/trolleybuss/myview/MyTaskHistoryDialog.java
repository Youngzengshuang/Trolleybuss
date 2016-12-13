package com.gjyf.trolleybus.trolleybuss.myview;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;

import com.gjyf.trolleybus.trolleybuss.R;

@SuppressLint("WrongViewCast")
public class MyTaskHistoryDialog extends Dialog {

    protected MyTaskHistoryDialog(Context context) {
        super(context);

    }

    protected MyTaskHistoryDialog(Context context, int theme) {
        super(context, theme);

    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentVIew;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private String mEtMessgae;
        private EditText etMessage;

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

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentVIew = v;
            return this;
        }

        public String getEditContent() {
            return etMessage.getText().toString();
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

        public MyTaskHistoryDialog create() {

            final MyTaskHistoryDialog dialog = new MyTaskHistoryDialog(context, R.style.dialog);
            // View layout = inflater.inflate(R.layout.my_dialog_layout, null);
            dialog.addContentView(contentVIew, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            return dialog;
        }
    }
}
