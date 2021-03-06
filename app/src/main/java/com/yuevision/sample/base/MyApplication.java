package com.yuevision.sample.base;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import com.yuevision.sample.http.HttpUtils;
import com.yuevision.sample.http.aosenhttp.HttpUtils_AOSEN;
import com.yuevision.sample.myconfig.FaceDB;
import com.yuevision.sample.utils.MLog;

/**
 * Created by gqj3375 on 2017/4/28.
 */

public class MyApplication extends Application {
    private final String TAG = this.getClass().toString();
    public FaceDB mFaceDB;
    Uri mImage;

    private static MyApplication MyApplication;

    public static MyApplication getInstance() {
        return MyApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication = this;
        mFaceDB = new FaceDB(this.getExternalCacheDir().getPath());
        mImage = null;

        //初始化网络1
        HttpUtils.getInstance().init(this, MLog.DEBUG);
        //初始化网络2
        HttpUtils_AOSEN.getInstance().init(this, MLog.DEBUG);

        //设置打印,正式打包，设为 false
        MLog.init(true, "SJY");//true
    }

    public void setCaptureImage(Uri uri) {
        mImage = uri;
    }

    public Uri getCaptureImage() {
        return mImage;
    }

    /**
     * 测试text
     */
    public static Bitmap decodeImage(String path) {
        Bitmap res;
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inSampleSize = 1;
            op.inJustDecodeBounds = false;
            //op.inMutable = true;
            res = BitmapFactory.decodeFile(path, op);
            //rotate and scale.
            Matrix matrix = new Matrix();

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }

            Bitmap temp = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
            Log.d("com.arcsoft", "check target Image:" + temp.getWidth() + "X" + temp.getHeight());

            if (!temp.equals(res)) {
                res.recycle();
            }
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
