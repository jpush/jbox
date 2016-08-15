package com.jiguang.jbox.account;

import com.jiguang.jbox.data.Developer;
import com.jiguang.jbox.BasePresenter;
import com.jiguang.jbox.BaseView;

import java.util.List;

public class DeveloperContract {

    public interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadDevelopers(boolean forceUpdate);

        void addNewDeveloper();

        void deleteDeveloper(String devKey);
    }

    public interface View extends BaseView<Presenter> {

        void showDevelopers(List<Developer> developers);

        void setLoadingIndicator(boolean active);

        void showAddDeveloper();

        void showLoadingError();

        void showSuccessfullySavedMessage();

        boolean isActive();
    }

}
