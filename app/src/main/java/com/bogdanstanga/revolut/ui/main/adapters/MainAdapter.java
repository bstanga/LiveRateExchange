package com.bogdanstanga.revolut.ui.main.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bogdanstanga.revolut.R;
import com.bogdanstanga.revolut.model.Currency;
import com.bogdanstanga.revolut.ui.main.holders.CurrencyHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainAdapter extends RecyclerView.Adapter<CurrencyHolder> {

    private CurrencySelectedListener mCurrencySelectedListener;
    private ExchangeValueListener mExchangeValueListener;
    private ArrayList<Currency> mCurrencies;

    public MainAdapter() {
        this.mCurrencies = new ArrayList<>();
        this.setHasStableIds(true);
    }

    public void setInitialData(ArrayList<Currency> currencies) {
        mCurrencies.clear();
        mCurrencies.addAll(currencies);
        this.notifyDataSetChanged();
    }

    public void updateCurrenciesAmount(@Nullable HashMap<String, Double> amountValues, boolean refreshAll) {
        Iterator<Currency> iterator = mCurrencies.iterator();
        // Skip refreshing first row if there is a currency selected
        if (!refreshAll) {
            iterator.next();
        }
        while (iterator.hasNext()) {
            Currency currency = iterator.next();
            double amount = amountValues != null ? amountValues.get(currency.getISO()) : 0;
            currency.setAmount(amount);
        }
        if (refreshAll) {
            this.notifyDataSetChanged();
        } else {
            this.notifyItemRangeChanged(1, mCurrencies.size() - 1);
        }
    }

    public void moveCurrencyOnTop(Currency c) {
        int fromPosition = mCurrencies.indexOf(c);
        int toPosition = 0;
        Currency removedCurrency = mCurrencies.remove(fromPosition);
        mCurrencies.add(toPosition, removedCurrency);
        this.notifyItemMoved(fromPosition, toPosition);
    }


    @Override
    public long getItemId(int position) {
        return mCurrencies.get(position).getISO().hashCode();
    }

    @NonNull
    @Override
    public CurrencyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_currency, viewGroup, false);
        CurrencyHolder currencyHolder = new CurrencyHolder(itemView);

        currencyHolder.onCurrencySelected(currency -> {
            if (mCurrencySelectedListener != null) {
                mCurrencySelectedListener.onCurrencySelected(currency);
            }
        });

        currencyHolder.onExchangeAmountChanged((currency, amount) -> {
            if (mExchangeValueListener != null) {
                mExchangeValueListener.onExchangeValueChanged(currency, amount);
            }
        });

        return currencyHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyHolder currencyHolder, int position) {
        currencyHolder.bind(mCurrencies.get(position));
    }

    @Override
    public int getItemCount() {
        return mCurrencies.size();
    }

    public void onCurrencySelected(MainAdapter.CurrencySelectedListener currencySelectedListener) {
        this.mCurrencySelectedListener = currencySelectedListener;
    }

    public void onExchangeAmountChanged(MainAdapter.ExchangeValueListener exchangeValueListener) {
        this.mExchangeValueListener = exchangeValueListener;
    }

    public interface CurrencySelectedListener {
        void onCurrencySelected(Currency c);
    }

    public interface ExchangeValueListener {
        void onExchangeValueChanged(Currency c, double value);
    }
}
