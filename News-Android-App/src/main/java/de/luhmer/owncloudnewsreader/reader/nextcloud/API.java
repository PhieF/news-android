package de.luhmer.owncloudnewsreader.reader.nextcloud;

import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.luhmer.owncloud.accountimporter.helper.NextcloudAPI;
import de.luhmer.owncloud.accountimporter.helper.NextcloudRequest;
import de.luhmer.owncloudnewsreader.database.model.Feed;
import de.luhmer.owncloudnewsreader.database.model.Folder;
import de.luhmer.owncloudnewsreader.database.model.RssItem;
import de.luhmer.owncloudnewsreader.helper.GsonConfig;
import de.luhmer.owncloudnewsreader.model.NextcloudNewsVersion;
import de.luhmer.owncloudnewsreader.model.NextcloudStatus;
import de.luhmer.owncloudnewsreader.model.UserInfo;
import io.reactivex.Observable;

/**
 * Created by david on 29.06.17.
 */

public class API {

    private static final String mApiEndpoint = "/index.php/apps/news/api/v1-2/";
    private NextcloudAPI nextcloudAPI;

    public API(NextcloudAPI nextcloudAPI) {
        this.nextcloudAPI = nextcloudAPI;
    }

    public NextcloudAPI getNextcloudAPI() {
        return nextcloudAPI;
    }


    public Observable<List<Folder>> getFolders() {
        Type type = new TypeToken<List<Folder>>() {}.getType();
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("GET")
                .setUrl(mApiEndpoint + "folders")
                .build();

        return nextcloudAPI.performRequestObservable(type, request);
    }

    public Observable<List<Feed>> getFeeds() {
        Type type = new TypeToken<List<Feed>>() {}.getType();
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("GET")
                .setUrl(mApiEndpoint + "feeds")
                .build();

        return nextcloudAPI.performRequestObservable(type, request);
    }

    public List<RssItem> getRssItems(long batchSize, long offset, int type, long id, boolean getRead, boolean oldestFirst) throws IOException, RemoteException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("batchSize", String.valueOf(batchSize));
        parameters.put("offset", String.valueOf(offset));
        parameters.put("type", String.valueOf(type));
        parameters.put("id", String.valueOf(id));
        parameters.put("getRead", String.valueOf(getRead));
        parameters.put("oldestFirst", String.valueOf(oldestFirst));

        Type resType = new TypeToken<List<RssItem>>() {}.getType();
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setParameter(parameters)
                .setMethod("GET")
                .setUrl(mApiEndpoint + "items")
                .build();

        return nextcloudAPI.performRequest(resType, request);
    }

    public Observable<UserInfo> getUserInfo() {
        final Type type = UserInfo.class;
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("GET")
                .setUrl(mApiEndpoint + "user")
                .build();

        return nextcloudAPI.performRequestObservable(type, request);
    }

    public Observable<NextcloudStatus> getStatus() {
        Type type = UserInfo.class;
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("GET")
                .setUrl(mApiEndpoint + "status")
                .build();

        return nextcloudAPI.performRequestObservable(type, request);
    }

    public Observable<NextcloudNewsVersion> getNextcloudNewsVersion() {
        Type type = NextcloudNewsVersion.class;
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("GET")
                .setUrl(mApiEndpoint + "version")
                .build();

        return nextcloudAPI.performRequestObservable(type, request);
    }


    public Folder createFolder(Map<String, Object> folderMap) throws IOException, RemoteException {
        String body = GsonConfig.GetGson().toJson(folderMap);
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("POST")
                .setUrl(mApiEndpoint + "folders")
                .setRequestBody(body)
                .build();
        return nextcloudAPI.performRequest(Folder.class, request);
    }

    public Observable<List<Feed>> createFeed(Map<String, Object> feedMap) {
        Type feedListType = new TypeToken<List<Feed>>() {}.getType();
        String body = GsonConfig.GetGson().toJson(feedMap);
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("POST")
                .setUrl(mApiEndpoint + "feeds")
                .setRequestBody(body)
                .build();
        return nextcloudAPI.performRequestObservable(feedListType, request);
    }

    public Feed renameFeed(long feedId, Map<String, String> feedTitleMap) throws IOException, RemoteException {
        String body = GsonConfig.GetGson().toJson(feedTitleMap);
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("PUT")
                .setUrl(mApiEndpoint + "feeds/" + feedId + "/rename")
                .setRequestBody(body)
                .build();
        return nextcloudAPI.performRequest(Feed.class, request);
    }

    public boolean deleteFeed(long feedId) throws IOException, RemoteException {
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("DELETE")
                .setUrl(mApiEndpoint + "feeds/" + feedId)
                .build();
        return nextcloudAPI.performRequest(Void.class, request);
    }




    public ParcelFileDescriptor updatedItems(long lastModified, int type, long id) throws IOException, RemoteException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("lastModified", String.valueOf(lastModified));
        parameters.put("type", String.valueOf(type));
        parameters.put("id", String.valueOf(id));

        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("GET")
                .setUrl(mApiEndpoint + "items/updated")
                .setParameter(parameters)
                .build();
        return nextcloudAPI.performNetworkRequest(request);
    }



    // https://github.com/owncloud/news/wiki/Items-1.2#mark-multiple-items-as-read
    public boolean markItemsRead(ItemIds items) throws IOException, RemoteException {
        String body = GsonConfig.GetGson().toJson(items);
        return markItems("items/read/multiple", body);
    }

    // https://github.com/owncloud/news/wiki/Items-1.2#mark-multiple-items-as-unread
    public boolean markItemsUnread(ItemIds items) throws IOException, RemoteException {
        String body = GsonConfig.GetGson().toJson(items);
        return markItems("items/unread/multiple", body);
    }

    // https://github.com/owncloud/news/wiki/Items-1.2#mark-multiple-items-as-starred
    public boolean markItemsStarred(ItemMap items) throws IOException, RemoteException {
        String body = GsonConfig.GetGson().toJson(items);
        return markItems("items/star/multiple", body);
    }

    // https://github.com/owncloud/news/wiki/Items-1.2#mark-multiple-items-as-unstarred
    public boolean markItemsUnstarred(ItemMap items) throws IOException, RemoteException {
        String body = GsonConfig.GetGson().toJson(items);
        return markItems("items/unstar/multiple", body);
    }

    public boolean markItems(String endpoint, String body) throws IOException, RemoteException {
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("PUT")
                .setUrl(mApiEndpoint + endpoint)
                .setRequestBody(body)
                .build();
        nextcloudAPI.performRequest(Void.class, request);
        return true; // TODO check if success!
    }

}
