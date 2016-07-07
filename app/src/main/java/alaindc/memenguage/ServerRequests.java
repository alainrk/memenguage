package alaindc.memenguage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by narko on 07/07/16.
 */
public class ServerRequests {

    public static void uploadFile(File file, final Context context) {
        // create upload service client
        FileUploadService service = ServiceGenerator.createService(FileUploadService.class);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // add another part within the multipart request
        String idString = getMyDeviceId(context);
        RequestBody id =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), idString);

        // finally, execute the request
        Call<ResponseBody> call = service.upload(id, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                try {
                    String res = response.body().string().replace("\"","");
                    if (res.equals(getMyDeviceId(context)))
                        Toast.makeText(context, "Database uploaded!", Toast.LENGTH_LONG).show();
                    Log.v("Upload", "success");
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    public static class ServiceGenerator {

        private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        private static Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(ServerConsts.BASENAME_SERVER_REQUEST_URI)
                        .addConverterFactory(GsonConverterFactory.create());

        public static <S> S createService(Class<S> serviceClass) {
            Retrofit retrofit = builder.client(httpClient.build()).build();
            return retrofit.create(serviceClass);
        }
    }

    public static String getMyDeviceId(Context context){
        try {
            WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMan.getConnectionInfo();

            return Integer.toString(Math.abs(wifiInfo.getMacAddress().hashCode()));
        } catch (Exception e) {
            Random rand = new Random(System.currentTimeMillis());
            return String.valueOf(rand.nextLong()+9999999);
        }

    }


}
