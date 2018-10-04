package com.bogdanstanga.revolut.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bogdanstanga.revolut.R;
import com.bogdanstanga.revolut.model.Currency;
import com.bogdanstanga.revolut.ui.main.adapters.MainAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements MainPresenterView {

    @BindView(R.id.recycler_view)
    RecyclerView mRecycler;
    @BindView(R.id.loading_view)
    ProgressBar mLoadingView;

    private MainAdapter mAdapter = new MainAdapter();
    private LinearLayoutManager mLayoutManager;
    private MainPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mAdapter);

        try {
            // Prevents recyclerView to blink when calling notifyItemRangeChanged
            RecyclerView.ItemAnimator itemAnimator = mRecycler.getItemAnimator();
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        } catch (NullPointerException e) {
            //ignore
        }

        mPresenter = new MainPresenter(AndroidSchedulers.mainThread(), Schedulers.io(), new MainRepository());
        mPresenter.register(this);
    }

    @Override
    public void showLoading(boolean isLoading) {
        mLoadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setInitialData(ArrayList<Currency> currencies) {
        mAdapter.setInitialData(currencies);
        mRecycler.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateCurrenciesAmount(HashMap<String, Double> amountValues, boolean refreshAll) {
        mAdapter.updateCurrenciesAmount(amountValues, refreshAll);
    }

    @Override
    public void clearCurrenciesAmount(boolean refreshAll) {
        mAdapter.updateCurrenciesAmount(null, refreshAll);
    }

    @Override
    public void selectCurrency(Currency currency) {
        mAdapter.moveCurrencyOnTop(currency);
        mLayoutManager.scrollToPositionWithOffset(0, 0);
    }

    @Override
    public void showLoadingError() {
        Toast.makeText(this, R.string.loading_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Observable<Currency> onCurrencySelected() {
        return Observable.create(emitter -> mAdapter.onCurrencySelected(emitter::onNext));
    }

    @Override
    public Observable<Pair<Currency, Double>> onAmountChanged() {
        return Observable.create(emitter -> mAdapter.onExchangeAmountChanged((selectedCurrency, amount) -> emitter.onNext(new Pair<>(selectedCurrency, amount))));
    }

    @Override
    protected void onDestroy() {
        mPresenter.unregister();
        super.onDestroy();
    }
}
