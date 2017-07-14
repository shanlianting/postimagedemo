package com.example.andy.imageuploader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zzti.fengyongge.imagepicker.PhotoSelectorActivity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @ViewInject(R.id.user_photo)
    private ImageView userPhoto;
    ImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        options = new ImageOptions.Builder()
                .setUseMemCache(false)
                .setCircular(true)
                .build();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.send_pyq:
                intent = new Intent(this, PyqActivity.class);
                startActivity(intent);
                break;
            case R.id.user_photo:
                intent = new Intent(MainActivity.this, PhotoSelectorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("limit", 1);//number是选择图片的数量
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null) {
                    List<String> paths = (List<String>) data.getExtras().getSerializable("photos");//path是选择拍照或者图片的地址数组
                    //处理代码
                    if (paths != null && paths.size() > 0) {
                        String imagePath = paths.get(0);
                        File imageFile = change(imagePath);
                        upload(imageFile);
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private File change(String imagePath) {

        int pos = imagePath.lastIndexOf("/");
        String imageName = imagePath.substring(pos + 1, imagePath.length());

        //100*100
        File newFilePath = new File(getExternalCacheDir(), imageName);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        while (width / inSampleSize > 100 || height / inSampleSize > 100) {
            inSampleSize *= 2;
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

        try {
            FileOutputStream fos = new FileOutputStream(newFilePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fos);
            fos.flush();
            fos.close();

            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newFilePath;
    }

    private void upload(File imageFile) {
        RequestParams params = new RequestParams("http://169.254.122.97:8080/1511k/servlet/UploadServlet");
        params.setMultipart(true);
        params.addBodyParameter("upload", imageFile);
        params.addQueryStringParameter("username", "张三");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("onSuccess", "onSuccess: " + result);
                Bean bean = new Gson().fromJson(result, Bean.class);
                x.image().bind(userPhoto, bean.getList().get(0), options);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
