package com.bogdanstanga.revolut.ui.main.holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bogdanstanga.revolut.R;
import com.bogdanstanga.revolut.model.Currency;
import com.bogdanstanga.revolut.ui.main.adapters.MainAdapter;
import com.bogdanstanga.revolut.ui.views.CurrencyEditText;
import com.bogdanstanga.revolut.util.KeyboardHelper;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CurrencyHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.currency_iso_code)
    TextView mCurrencyISOCode;
    @BindView(R.id.currency_name)
    TextView mCurrencyName;
    @BindView(R.id.currency_exchange_value)
    CurrencyEditText mCurrencyExchange;
    @BindView(R.id.currency_flag)
    ImageView mCountryFlag;

    private MainAdapter.CurrencySelectedListener mCurrencySelectedListener;
    private MainAdapter.ExchangeValueListener mExchangeValueListener;

    public CurrencyHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(view -> {
            mCurrencyExchange.requestFocus();
            KeyboardHelper.show(mCurrencyExchange);

            if (mCurrencySelectedListener != null) {
                Currency currency = (Currency) view.getTag();
                mCurrencySelectedListener.onCurrencySelected(currency);
            }
        });

        mCurrencyExchange.setOnCurrencyInputListener(new CurrencyEditText.CurrencyInputListener() {
            @Override
            public void onAmountChanged(CurrencyEditText view, double amount) {
                if (view.getTag() != null && mExchangeValueListener != null) {
                    Currency currency = (Currency) view.getTag();
                    currency.setAmount(amount);
                    mExchangeValueListener.onExchangeValueChanged(currency, amount);
                }
            }

            @Override
            public void onFocusChanged(CurrencyEditText view, boolean hasFocus) {
                if (view.getTag() != null && mCurrencySelectedListener != null && hasFocus) {
                    Currency currency = (Currency) view.getTag();
                    mCurrencySelectedListener.onCurrencySelected(currency);
                }
            }
        });
    }

    public void bind(Currency currency) {
        itemView.setTag(currency);
        mCurrencyExchange.setTag(currency);

        mCurrencyISOCode.setText(currency.getISO());
        mCurrencyName.setText(currency.getName());
        mCountryFlag.setImageBitmap(null);
        Glide.with(itemView)
                .load(currency.getCountryFlagUrl())
                .into(mCountryFlag);

        mCurrencyExchange.setAmount(currency.getAmount());
    }

    public void onCurrencySelected(MainAdapter.CurrencySelectedListener currencySelectedListener) {
        this.mCurrencySelectedListener = currencySelectedListener;
    }

    public void onExchangeAmountChanged(MainAdapter.ExchangeValueListener exchangeValueListener) {
        this.mExchangeValueListener = exchangeValueListener;
    }

}
