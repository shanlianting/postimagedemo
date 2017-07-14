package com.example.andy.imageuploader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zzti.fengyongge.imagepicker.PhotoSelectorActivity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_pyq)
public class PyqActivity extends AppCompatActivity {

    @ViewInject(R.id.edit)
    private EditText mEditText;
    @ViewInject(R.id.gridview)
    private GridView mGridView;
    private Myadapter adapter;

    private List<String> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        adapter=new Myadapter();
        mGridView.setAdapter(adapter);
    }

    @Event(R.id.add)
    private void add(View v){
        Intent intent = new Intent(this, PhotoSelectorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("limit", 9);//number是选择图片的数量
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null) {
                    List<String> paths = (List<String>) data.getExtras().getSerializable("photos");
                    //path是选择拍照或者图片的地址数组
                    list.clear();
                    list.addAll(paths);
                    adapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Event(R.id.send)
    private void send(View v){
        //http://169.254.251.53:8080/1511k/servlet/UploadServlet

        RequestParams params=new RequestParams("http://169.254.251.53:8080/1511k/servlet/UploadServlet");
        String text = mEditText.getText().toString();
        params.addQueryStringParameter("info",text);

        for (String imagePath: list){
            params.addBodyParameter("file",new File(imagePath));
        }
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson=new Gson();
                Bean bean = gson.fromJson(result,Bean.class);
                Log.e("onSuccess", "onSuccess: "+result);
                mGridView.setNumColumns(1);
                list.clear();
                list.addAll(bean.getList());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(PyqActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    class Myadapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView image=new ImageView(PyqActivity.this);
            x.image().bind(image,list.get(position));
            return image;
        }
    }
}
