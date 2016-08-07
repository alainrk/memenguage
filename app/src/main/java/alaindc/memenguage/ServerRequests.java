/*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*
* This file is part of Memenguage Android app.
* Copyright (C) 2016 Alain Di Chiappari
*/

package alaindc.memenguage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    public static void uploadFile(final String userid, File file, final Context context) {
        // create upload service client
        FileUploadService service = ServiceGenerator.createService(FileUploadService.class);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        RequestBody id =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), userid);

        // finally, execute the request
        Call<ResponseBody> call = service.upload(id, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent commservint = new Intent(Constants.INTENT_COMMSERV_UPDATE);
                commservint.putExtra(Constants.EXTRA_COMMSERV_TYPE, Constants.UPLOAD);
                try {
                    String res = response.body().string().replace("\"","");
                    if (res.equals(userid) && response.isSuccessful()) {
                        commservint.putExtra(Constants.EXTRA_COMMSERV_SUCCESS, true);
                    }
                    else {
                        commservint.putExtra(Constants.EXTRA_COMMSERV_SUCCESS, false);
                    }
                    Log.v("Upload", "success");
                } catch (Exception e) {
                    commservint.putExtra(Constants.EXTRA_COMMSERV_SUCCESS, false);
                }

                LocalBroadcastManager.getInstance(context).sendBroadcast(commservint);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    public static void downloadFile (String userid, final File file, final Context context) {
        FileDownloadService downloadService = ServiceGenerator.createService(FileDownloadService.class);

        Call<ResponseBody> call = downloadService.downloadFileWithDynamicUrlSync(ServerConsts.RELATIVE_SERVER_DOWNLOADDB_URI+userid);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent commservint = new Intent(Constants.INTENT_COMMSERV_UPDATE);
                commservint.putExtra(Constants.EXTRA_COMMSERV_TYPE, Constants.DOWNLOAD);

                if (response.isSuccessful()) {
                    Log.d("DownloadFIle", "server contacted and has file");
                    boolean writtenToDisk = writeResponseBodyToDisk(file, response.body());
                    if (writtenToDisk) {
                        commservint.putExtra(Constants.EXTRA_COMMSERV_SUCCESS, true);
                        Intent updateintent = new Intent(Constants.INTENT_VIEW_UPDATE);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(updateintent);

                    }
                    else {
                        commservint.putExtra(Constants.EXTRA_COMMSERV_SUCCESS, false);
                    }
                } else {
                    commservint.putExtra(Constants.EXTRA_COMMSERV_SUCCESS, false);
                    Log.d("DownloadFIle", "server contact failed");
                }
                LocalBroadcastManager.getInstance(context).sendBroadcast(commservint);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("DownloadFIle", "error");
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

    private static boolean writeResponseBodyToDisk(File file, ResponseBody body) {
        try {

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("WriteFileToDisk", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

//    public static String getMyDeviceId(Context context){
//        try {
//            WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            WifiInfo wifiInfo = wifiMan.getConnectionInfo();
//
//            return Integer.toString(Math.abs(wifiInfo.getMacAddress().hashCode()));
//        } catch (Exception e) {
//            Random rand = new Random(System.currentTimeMillis());
//            return String.valueOf(rand.nextLong()+9999999);
//        }
//
//    }


}
