package com.assac.controldelubricantes.DI;

import com.assac.controldelubricantes.Storage.Rest;
import com.assac.controldelubricantes.View.Activity.LoginActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = RetrofitModule.class)
public interface RetrofitComponent {
    void inject(LoginActivity loginActivity);
}
