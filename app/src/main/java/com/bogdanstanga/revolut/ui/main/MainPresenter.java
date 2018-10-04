package com.bogdanstanga.revolut.ui.main;

import com.bogdanstanga.revolut.ui.base.BasePresenter;
import com.bogdanstanga.revolut.model.Currency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter extends BasePresenter<MainPresenterView> {

    private static final String DEFAULT_CURRENCY = "EUR";
    private final Scheduler mSingleThreadScheduler = Schedulers.from(Executors.newSingleThreadExecutor());

    private Scheduler mUIScheduler;
    private Scheduler mIOScheduler;
    private MainRepository mRepository;
    private MainPresenterView mView;
    private Currency mSelectedCurrency;
    private Disposable mPeriodCheckDisposable;
    private double mMultiplier = 1d;

    public MainPresenter(Scheduler uiScheduler, Scheduler ioScheduler, MainRepository mainRepository) {
        this.mUIScheduler = uiScheduler;
        this.mIOScheduler = ioScheduler;
        this.mRepository = mainRepository;
    }

    @Override
    public void register(MainPresenterView view) {
        super.register(view);
        mView = view;

        addToDisposable(mRepository.getCurrencyList(DEFAULT_CURRENCY).subscribeOn(mIOScheduler)
                .observeOn(mUIScheduler)
                .subscribe(currencies -> {
                    mView.showLoading(false);
                    setInitialCurrencyList(currencies);
                    startPeriodCheck();
                }, throwable -> {
                    throwable.printStackTrace();
                    view.showLoading(false);
                    view.showLoadingError();
                }));

        addToDisposable(view.onCurrencySelected()
                .subscribe(currency -> {
                    if (mSelectedCurrency == null || !mSelectedCurrency.equals(currency)) {
                        mSelectedCurrency = currency;
                        mView.selectCurrency(currency);
                        mMultiplier = currency.getAmount();
                        startPeriodCheck();
                    }
                }));

        addToDisposable(view.onAmountChanged()
                .doOnNext(pair -> mMultiplier = pair.second)
                .switchMap(pair -> mRepository.getRatesUpdate(pair.first.getISO()).subscribeOn(mIOScheduler))
                .map(exchangeRates -> {
                    // Calculate actual value based on the current exchange rate
                    for (Map.Entry<String, Double> entry : exchangeRates.entrySet()) {
                        entry.setValue(entry.getValue() * mMultiplier);
                    }
                    return exchangeRates;
                })
                .doOnError(throwable -> {
                    mMultiplier = 1;
                    mView.clearCurrenciesAmount(false);
                })
                .retry()
                .observeOn(mUIScheduler)
                .subscribe(
                        currencies -> mView.updateCurrenciesAmount(currencies, false)
                ));
    }

    private void startPeriodCheck() {
        dispose(mPeriodCheckDisposable);

        String selectedCurrencyISO = mSelectedCurrency == null ? DEFAULT_CURRENCY : mSelectedCurrency.getISO();
        Disposable disposable = Observable
                .interval(1, TimeUnit.SECONDS, mSingleThreadScheduler)
                .flatMap(ignore -> mRepository.getRatesUpdate(selectedCurrencyISO).subscribeOn(mIOScheduler))
                .map(exchangeRates -> {
                    // Calculate actual value based on the current exchange rate
                    for (Map.Entry<String, Double> entry : exchangeRates.entrySet()) {
                        entry.setValue(entry.getValue() * mMultiplier);
                    }
                    return exchangeRates;
                })
                .observeOn(mUIScheduler)
                .doOnError(throwable -> {
                    mMultiplier = 1;
                    //If there are no selected currencies refresh all of them
                    boolean refreshAll = mSelectedCurrency == null;
                    mView.clearCurrenciesAmount(refreshAll);
                })
                .retry()
                .subscribe(currencies -> {
                    //If there are no selected currencies refresh all of them
                    boolean refreshAll = mSelectedCurrency == null;
                    mView.updateCurrenciesAmount(currencies, refreshAll);
                });

        addToDisposable(disposable);
        mPeriodCheckDisposable = disposable;
    }

    private void setInitialCurrencyList(ArrayList<Currency> currencies) {
        Collections.sort(currencies, (c1, c2) -> c1.getISO().compareTo(c2.getISO()));
        mView.setInitialData(currencies);
    }


}
