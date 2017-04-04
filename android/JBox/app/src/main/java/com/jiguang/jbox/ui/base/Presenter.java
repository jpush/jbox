package com.jiguang.jbox.ui.base;


public interface Presenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();
}
