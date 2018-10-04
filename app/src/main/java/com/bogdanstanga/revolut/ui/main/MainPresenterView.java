package com.bogdanstanga.revolut.ui.main;

import android.util.Pair;

import com.bogdanstanga.revolut.ui.base.BasePresenterView;
import com.bogdanstanga.revolut.model.Currency;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;

public interface MainPresenterView extends BasePresenterView {

    void showLoading(boolean isRefreshing);

    void setInitialData(ArrayList<Currency> currencies);

    void selectCurrency(Currency currency);

    void updateCurrenciesAmount(HashMap<String, Double> amountValues, boolean refreshAll);

    void clearCurrenciesAmount(boolean refreshAll);

    void showLoadingError();

    Observable<Currency> onCurrencySelected();

    Observable<Pair<Currency, Double>> onAmountChanged();
}
