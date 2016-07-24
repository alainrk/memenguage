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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import java.io.InputStream;

/**
 * Created by narko on 18/07/16.
 */
public class AsyncImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public AsyncImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap image = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            image = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return image;
    }

    protected void onPostExecute(Bitmap result) {
//        saveToInternalStorage(result);
        bmImage.setImageBitmap(result);
    }

//    private String saveToInternalStorage(Bitmap bitmapImage){
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        // path to /data/data/yourapp/app_data/imageDir
//        File directory = cw.getDir("", Context.MODE_PRIVATE);
//        // Create imageDir
//        File mypath = new File(directory,"profile.jpg");
//
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(mypath);
//            // Use the compress method on the BitMap object to write image to the OutputStream
//            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                fos.close();
//            } catch (IOException ioe) {
//                Log.d("Image Task", "Error in closing file");
//            }
//        }
//        return directory.getAbsolutePath();
//    }
}