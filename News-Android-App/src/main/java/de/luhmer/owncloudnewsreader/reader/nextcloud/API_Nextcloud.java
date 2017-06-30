package de.luhmer.owncloudnewsreader.reader.nextcloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import de.luhmer.owncloud.accountimporter.helper.AccountImporter;
import de.luhmer.owncloud.accountimporter.helper.NextcloudAPI;
import de.luhmer.owncloud.accountimporter.helper.NextcloudRequest;
import de.luhmer.owncloudnewsreader.SettingsActivity;
import de.luhmer.owncloudnewsreader.database.model.Feed;
import de.luhmer.owncloudnewsreader.database.model.Folder;
import de.luhmer.owncloudnewsreader.database.model.RssItem;
import de.luhmer.owncloudnewsreader.helper.GsonConfig;
import de.luhmer.owncloudnewsreader.model.NextcloudNewsVersion;
import de.luhmer.owncloudnewsreader.model.NextcloudStatus;
import de.luhmer.owncloudnewsreader.model.UserInfo;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * Created by david on 29.06.17.
 */

public class API_Nextcloud {

    private static final String mApiEndpoint = "/index.php/apps/news/api/v1-2/";

    public static Observable<List<Folder>> GetFolders() {
        Type type = new TypeToken<List<Folder>>() {}.getType();

        NextcloudRequest request = new NextcloudRequest.Builder()
                //.setHeader(chain.request().headers().toMultimap())
                .setMethod("GET")
                .setUrl(mApiEndpoint + "folders")
                //.setRequestBody(chain.request().body())
                .build();

        return NextcloudAPI.getInstance().performRequestObservable(type, request);
    }

    public static Observable<List<Feed>> GetFeeds() {
        Type type = new TypeToken<List<Feed>>() {}.getType();

        NextcloudRequest request = new NextcloudRequest.Builder()
                //.setHeader(chain.request().headers().toMultimap())
                .setMethod("GET")
                .setUrl(mApiEndpoint + "feeds")
                //.setRequestBody(chain.request().body())
                .build();

        return NextcloudAPI.getInstance().performRequestObservable(type, request);
    }

    public static List<RssItem> GetRssItems(long batchSize, long offset, int type, long id, boolean getRead, boolean oldestFirst) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("batchSize", String.valueOf(batchSize));
        parameters.put("offset", String.valueOf(offset));
        parameters.put("type", String.valueOf(type));
        parameters.put("id", String.valueOf(id));
        parameters.put("getRead", String.valueOf(getRead));
        parameters.put("oldestFirst", String.valueOf(oldestFirst));


        Type resType = new TypeToken<List<RssItem>>() {}.getType();

        NextcloudRequest request = new NextcloudRequest.Builder()
                //.setHeader(chain.request().headers().toMultimap())
                .setParameter(parameters)
                .setMethod("GET")
                .setUrl(mApiEndpoint + "items")
                //.setRequestBody(chain.request().body())
                .build();

        return NextcloudAPI.getInstance().performRequest(resType, request);
    }

    public static Observable<UserInfo> GetUserInfo() {
        final Type type = UserInfo.class;

        NextcloudRequest request = new NextcloudRequest.Builder()
                //.setHeader(chain.request().headers().toMultimap())
                .setMethod("GET")
                .setUrl(mApiEndpoint + "user")
                //.setRequestBody(chain.request().body())
                .build();

        return NextcloudAPI.getInstance().performRequestObservable(type, request);
    }

    public static Observable<NextcloudStatus> GetStatus() {
        Type type = UserInfo.class;

        NextcloudRequest request = new NextcloudRequest.Builder()
                //.setHeader(chain.request().headers().toMultimap())
                .setMethod("GET")
                .setUrl(mApiEndpoint + "status")
                //.setRequestBody(chain.request().body())
                .build();

        return NextcloudAPI.getInstance().performRequestObservable(type, request);
    }

    public static Observable<NextcloudNewsVersion> GetNextcloudNewsVersion() {
        Type type = NextcloudNewsVersion.class;

        NextcloudRequest request = new NextcloudRequest.Builder()
                //.setHeader(chain.request().headers().toMultimap())
                .setMethod("GET")
                .setUrl(mApiEndpoint + "version")
                //.setRequestBody(chain.request().body())
                .build();

        return NextcloudAPI.getInstance().performRequest(type, request);
    }


    public static Folder createFolder(Map<String, Object> folderMap) {
        String body = GsonConfig.GetGson().toJson(folderMap);
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("POST")
                .setUrl(mApiEndpoint + "folders")
                .setRequestBody(body)
                .build();
        return NextcloudAPI.getInstance().performRequest(Folder.class, request);
    }

    public static Feed createFeed(Map<String, Object> feedMap) {
        String body = GsonConfig.GetGson().toJson(feedMap);
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("POST")
                .setUrl(mApiEndpoint + "feeds")
                .setRequestBody(body)
                .build();
        return NextcloudAPI.getInstance().performRequest(Feed.class, request);
    }

    public static Feed renameFeed(long feedId, Map<String, String> feedTitleMap) {
        String body = GsonConfig.GetGson().toJson(feedTitleMap);
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("PUT")
                .setUrl(mApiEndpoint + "feeds/" + feedId + "/rename")
                .setRequestBody(body)
                .build();
        return NextcloudAPI.getInstance().performRequest(Feed.class, request);
    }

    public static boolean deleteFeed(long feedId) {
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("DELETE")
                .setUrl(mApiEndpoint + "feeds/" + feedId)
                .build();
        return NextcloudAPI.getInstance().performRequest(Void.class, request);
    }




    public static ParcelFileDescriptor UpdatedItems(long lastModified, int type, long id) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("lastModified", String.valueOf(lastModified));
        parameters.put("type", String.valueOf(type));
        parameters.put("id", String.valueOf(id));

        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("GET")
                .setUrl(mApiEndpoint + "items/updated")
                .setParameter(parameters)
                .build();


        return NextcloudAPI.getInstance().performNetworkRequest(request);

    }




    public static boolean markItemsRead(ItemIds items) {
        return markItems("items/read/multiple", items);
    }

    public static boolean markItemsUnread(ItemIds items) {
        return markItems("items/unread/multiple", items);
    }

    public static boolean markItemsStarred(ItemIds items) {
        return markItems("items/star/multiple", items);
    }

    public static boolean markItemsUnstarred(ItemIds items) {
        return markItems("items/unstar/multiple", items);
    }


    public static boolean markItems(String endpoint, ItemIds items) {
        String body = GsonConfig.GetGson().toJson(items);
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("PUT")
                .setUrl(mApiEndpoint + endpoint)
                .setRequestBody(body)
                .build();
        NextcloudAPI.getInstance().performRequest(Void.class, request);
        return true; // TODO check if success!
    }

}
