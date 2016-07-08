package alaindc.memenguage;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by narko on 08/07/16.
 */
public interface FileDownloadService {
    @GET(ServerConsts.RELATIVE_SERVER_DOWNLOADDB_URI)
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Query("id") String id);
}