package de.luhmer.owncloudnewsreader.reader.nextcloud;

import java.util.List;
import java.util.Map;

import de.luhmer.owncloudnewsreader.database.model.Feed;
import de.luhmer.owncloudnewsreader.database.model.Folder;
import de.luhmer.owncloudnewsreader.database.model.RssItem;
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
 * Created by david on 22.05.17.
 */


public interface API {


    @GET("items/updated")
    @Streaming
    Observable<ResponseBody> updatedItems(
            @Query("lastModified") long lastModified,
            @Query("type") int type,
            @Query("id") long id
    );



}
