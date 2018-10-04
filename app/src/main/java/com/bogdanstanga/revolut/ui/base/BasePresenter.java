package com.bogdanstanga.revolut.ui.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BasePresenter<T extends BasePresenterView> {

    private T mView;
    private CompositeDisposable mCompositeDisposable;

    public BasePresenter() {
        mCompositeDisposable = new CompositeDisposable();
    }

    public void register(T view) {
        if (mView != null) {
            throw new IllegalStateException("View " + mView + " is already attached. Cannot attach " + view);
        }
        mView = view;
    }

    public void unregister() {
        if (mView == null) {
            throw new IllegalStateException("View is already detached");
        }
        mView = null;
        mCompositeDisposable.clear();
    }

    protected void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            mCompositeDisposable.remove(disposable);
            disposable.dispose();
        }
    }

    protected void addToDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

}
