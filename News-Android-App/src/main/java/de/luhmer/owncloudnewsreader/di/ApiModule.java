package de.luhmer.owncloudnewsreader.di;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.luhmer.owncloudnewsreader.helper.GsonConfig;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by david on 22.05.17.
 */

@Module
public class ApiModule {

    private Application mApplication;

    public ApiModule(Application application) {
        this.mApplication = application;
    }

    // Dagger will only look for methods annotated with @Provides
    @Provides
    @Singleton
    // Application reference must come from AppModule.class
    SharedPreferences providesSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mApplication);
    }

    /*
    @Provides
    @Singleton
    NextcloudAPI providexNextcloudAPI() {
        return new NextcloudAPI("");
    }*/

    /*
    @Provides
    @Singleton
    Cache provideOkHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(application.getCacheDir(), cacheSize);
        return cache;
    }*/

    @Provides
    @Singleton
    Gson provideGson() {
        return GsonConfig.GetGson();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache) {
        OkHttpClient client = new OkHttpClient();
        //client.setCache(cache);
        return client;
    }

    @Provides
    @Singleton
    ApiProvider provideAPI(SharedPreferences sp, Gson gson) {
        return new ApiProvider(sp, gson, mApplication);
    }

}
