package de.luhmer.owncloudnewsreader.di;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.concurrent.atomic.AtomicInteger;

import de.luhmer.owncloud.accountimporter.helper.AccountImporter;
import de.luhmer.owncloud.accountimporter.helper.NextcloudAPI;
import de.luhmer.owncloudnewsreader.SettingsActivity;
import de.luhmer.owncloudnewsreader.reader.OkHttpImageDownloader;
import de.luhmer.owncloudnewsreader.reader.nextcloud.API;
import okhttp3.OkHttpClient;

/**
 * Created by david on 26.05.17.
 */

public class ApiProvider {

    private final SharedPreferences mPrefs;
    private Context context;
    private Gson gson;
    private API mApi;
    private final AtomicInteger refCount = new AtomicInteger(); // Count references for service


    public ApiProvider(SharedPreferences sp, Gson gson, Context context) {
        this.gson = gson;
        this.mPrefs = sp;
        this.context = context;
        initApi();
    }

    public boolean initApi() {
        Account account = AccountImporter.GetCurrentAccount(context);
        return initApi(account);
    }

    public boolean initApi(Account account) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        initImageLoader(mPrefs, client, context);

        mApi = new API(new NextcloudAPI(account, gson));
        acquireBinding();
        return true;
    }

    public void acquireBinding() {
        mApi.getNextcloudAPI().start(context);
        refCount.incrementAndGet();
    }

    public void releaseBinding() {
        if (refCount.get() == 0 || refCount.decrementAndGet() == 0) {
            // release binding
            mApi.getNextcloudAPI().stop(context);
        }
    }



    private void initImageLoader(SharedPreferences mPrefs, OkHttpClient okHttpClient, Context context) {
        int diskCacheSize = Integer.parseInt(mPrefs.getString(SettingsActivity.SP_MAX_CACHE_SIZE,"500"))*1024*1024;
        if(ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().destroy();
        }
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .diskCacheSize(diskCacheSize)
                .memoryCacheSize(10 * 1024 * 1024)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .defaultDisplayImageOptions(imageOptions)
                .imageDownloader(new OkHttpImageDownloader(context, okHttpClient))
                .build();

        ImageLoader.getInstance().init(config);
    }

    public API getAPI() {
        return mApi;
    }
}
