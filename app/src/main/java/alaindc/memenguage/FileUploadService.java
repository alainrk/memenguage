package alaindc.memenguage;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by narko on 07/07/16.
 */
public interface FileUploadService {
    @Multipart
    @POST(ServerConsts.RELATIVE_SERVER_UPLOADDB_URI)
    Call<ResponseBody> upload(@Part("id") RequestBody id,
                              @Part MultipartBody.Part file);
}
