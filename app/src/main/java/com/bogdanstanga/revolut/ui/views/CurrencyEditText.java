package com.bogdanstanga.revolut.ui.views;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import java.text.DecimalFormat;

public class CurrencyEditText extends AppCompatEditText {

    private static final int MAX_AMOUNT = 999999;
    private String mPreviousText = "";
    private CurrencyInputListener mCurrencyInputListener;
    private DecimalFormat mDecimalFormat = new DecimalFormat("#,##0.##");
    private TextWatcher mTextWatcher;

    public CurrencyEditText(Context context) {
        super(context);
        init();
    }

    public CurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CurrencyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setOnFocusChangeListener((view, focus) -> {
            this.setSelection(this.getText().length());
            if (focus) {
                this.addTextChangedListener(getTextWatcher());
            } else {
                this.removeTextChangedListener(getTextWatcher());
            }
            if (mCurrencyInputListener != null) {
                mCurrencyInputListener.onFocusChanged(this, focus);
            }
        });
    }

    private void setFormattedText(String text) {
        this.removeTextChangedListener(getTextWatcher());
        this.setText(text, BufferType.EDITABLE);
        this.addTextChangedListener(getTextWatcher());
    }

    public void setAmount(double amount) {
        String amountText = amount == 0 ? "" : mDecimalFormat.format(amount);
        setFormattedText(amountText);
    }

    private double getAmount(String text) {
        try {
            double amount = Double.parseDouble(text.replaceAll(",", ""));
            // Round to 2 decimals
            return ((int) (amount * 100d)) / 100d;
        } catch (Exception e) {
            return 0;
        }
    }

    private String formatAmountText(String text) {
        if (text.isEmpty()) {
            return "";
        }
        if (text.charAt(0) == '.') {
            if (text.length() == 1) {
                return "";
            }
            text = text.substring(1);
        }
        // Keep only 2 decimals
        String[] split = text.split("\\.");
        if (split.length == 2) {
            String decimals = split[1];
            decimals = decimals.length() > 2 ? decimals.substring(0, 2) : decimals;
            text = split[0] + "." + decimals;
        }
        String endingDecimal = text.endsWith(".") ? "." : "";
        Double value = Double.parseDouble(text.replaceAll(",", ""));
        return mDecimalFormat.format(value) + endingDecimal;
    }

    private TextWatcher getTextWatcher() {
        if (mTextWatcher == null) {
            mTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    mPreviousText = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String text = s.toString();
                    if (text.equals(mPreviousText)) {
                        return;
                    }

                    int selectionPositionFromEnd = text.length() - getSelectionStart();

                    double amount = getAmount(text);
                    if (amount > MAX_AMOUNT) {
                        setFormattedText(mPreviousText);
                        setSelection(Math.max(0, mPreviousText.length() - selectionPositionFromEnd));
                        return;
                    }

                    String formattedText = formatAmountText(text);
                    CurrencyEditText.this.setFormattedText(formattedText);
                    CurrencyEditText.this.setSelection(Math.max(0, formattedText.length() - selectionPositionFromEnd));

                    if (mCurrencyInputListener != null) {
                        mCurrencyInputListener.onAmountChanged(CurrencyEditText.this, amount);
                    }
                }
            };
        }
        return mTextWatcher;
    }

    public void setOnCurrencyInputListener(CurrencyInputListener currencyInputListener) {
        this.mCurrencyInputListener = currencyInputListener;
    }

    public interface CurrencyInputListener {
        void onAmountChanged(CurrencyEditText view, double amount);

        void onFocusChanged(CurrencyEditText view, boolean hasFocus);
    }

}
