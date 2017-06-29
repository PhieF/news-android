package de.luhmer.owncloudnewsreader;

import android.app.Application;

import java.util.concurrent.atomic.AtomicInteger;

import de.luhmer.owncloud.accountimporter.helper.AccountImporter;
import de.luhmer.owncloud.accountimporter.helper.NextcloudAPI;
import de.luhmer.owncloudnewsreader.di.ApiModule;
import de.luhmer.owncloudnewsreader.di.AppComponent;
import de.luhmer.owncloudnewsreader.di.DaggerAppComponent;
import de.luhmer.owncloudnewsreader.helper.GsonConfig;

public class NewsReaderApplication extends Application {

    private final AtomicInteger refCount = new AtomicInteger();

    @Override
    public void onCreate() {
        super.onCreate();

        NextcloudAPI.getInstance().setGson(GsonConfig.GetGson());

        initDaggerAppComponent();
    }

    public void acquireBinding() {
        NextcloudAPI.getInstance().start(this);
        refCount.incrementAndGet();
    }

    public void releaseBinding() {
        if (refCount.get() == 0 || refCount.decrementAndGet() == 0) {
            // release binding
            NextcloudAPI.getInstance().stop();
        }
    }

    public void initDaggerAppComponent() {
        // Dagger%COMPONENT_NAME%

        mAppComponent = DaggerAppComponent.builder()
                .apiModule(new ApiModule(this))
                .build();

        // If a Dagger 2 component does not have any constructor arguments for any of its modules,
        // then we can use .create() as a shortcut instead:
        //mAppComponent = DaggerAppComponent.create();
    }

    private AppComponent mAppComponent;

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
