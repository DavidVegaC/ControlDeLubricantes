package com.assac.controldelubricantes.DI;

import com.assac.controldelubricantes.Util.Const;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RetrofitModule {
    private static final String BASE_URL = Const.HTTP_SITE;

    @Singleton
    @Provides
    GsonConverterFactory provideGsonConverterFactory(){
        return GsonConverterFactory.create();
    }

    @Singleton
    @Provides
    HttpLoggingInterceptor provideHttpLoggingInterceptor(){
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient(HttpLoggingInterceptor loggingInterceptor){
        return new OkHttpClient.Builder().addInterceptor(loggingInterceptor)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    @Singleton
    @Provides
    Retrofit provideRetrofit(OkHttpClient client, GsonConverterFactory gsonConverterFactory){
        return new Retrofit.Builder()
                .baseUrl(Const.HTTP_SITE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

}
