package com.qiscus.rtc.sample.presenter;

import data.UserRepository;

/**
 * Created by rajapulau on 3/13/18.
 */

public class LoginPresenter {
    private View view;
    private UserRepository userRepository;

    public LoginPresenter(View view, UserRepository userRepository) {
        this.view = view;
        this.userRepository = userRepository;
    }

    public void start() {
        userRepository.getCurrentUser(user -> {
            if (user != null) {
                view.showHomePage();
            }
        }, throwable -> view.showErrorMessage(throwable.getMessage()));
    }

    public void login(String name, String email, String password) {
        view.showLoading();
        userRepository.login(name, email, password,
                user -> {
                    view.dismissLoading();
                    view.successLogin();
                },
                throwable -> {
                    view.dismissLoading();
                    view.showErrorMessage(throwable.getMessage());
                });
    }

    public interface View {
        void showHomePage();

        void successLogin();

        void showLoading();

        void dismissLoading();

        void showErrorMessage(String errorMessage);
    }
}
