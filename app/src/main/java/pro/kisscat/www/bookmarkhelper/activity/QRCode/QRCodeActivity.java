package pro.kisscat.www.bookmarkhelper.activity.QRCode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.activity.QRCode.widget.CustomDialog;
import pro.kisscat.www.bookmarkhelper.activity.QRCode.widget.CustomWebView;
import pro.kisscat.www.bookmarkhelper.activity.QRCode.zxing.DecodeImage;
import pro.kisscat.www.bookmarkhelper.util.Path;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

public class QRCodeActivity extends AppCompatActivity implements CustomWebView.LongClickCallBack {
    private Context context;
    private CustomWebView mCustomWebView;
    private CustomWebView mCustomWebView2;
    private CustomDialog mCustomDialog;
    private ArrayAdapter<String> adapter;
    private boolean isQR;//判断是否为二维码
    private Result result;//二维码解析结果
    private String url;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogHelper.v("QRCodeActivity start.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        context = getApplicationContext();
        Bundle bundle = getIntent().getBundleExtra("bundle");
        String mTitle = bundle.getString("title");
        int mLogo = bundle.getInt("logo");
        LogHelper.v("title:" + mTitle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.qrcode_webview_toolbar);
        toolbar.setLogo(ContextCompat.getDrawable(this, mLogo));
        toolbar.setTitle("  " + mTitle);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(true);
        }
        initWebViews();
    }

    /**
     * 换用imageview ？？
     */
    private void initWebViews() {
        mCustomWebView = new CustomWebView(this, this);
        //被tiny压缩的二维码图片，已经丢掉了关键信息，没法解码
        mCustomWebView.loadUrl(context.getResources().getString(R.string.donateWeixinURL));//加载页面
        mCustomWebView.setFocusable(true);
        mCustomWebView.setFocusableInTouchMode(true);

//        mCustomWebView2 = new CustomWebView(this, this);
//        mCustomWebView2.loadUrl(context.getResources().getString(R.string.donateAlipayURL));//加载页面
//        mCustomWebView2.setFocusable(true);
//        mCustomWebView2.setFocusableInTouchMode(true);
//        RelativeLayout relativeLayout2 = (RelativeLayout) findViewById(R.id.qrcode_webview_alipay_layout);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.qrcode_layout);
        linearLayout.addView(mCustomWebView);
//        linearLayout.addView(mCustomWebView2);
    }

    @Override
    public void onLongClickCallBack(final String imgUrl) {
        url = imgUrl;
        // 获取到图片地址后做相应的处理
        MyAsyncTask mTask = new MyAsyncTask();
        mTask.execute(imgUrl);
        showDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    /**
     * 判断是否为二维码
     * param url 图片地址
     * return
     */
    private boolean decodeImage(String sUrl) {
        Bitmap bitmap = getBitmap(sUrl);
        result = DecodeImage.handleQRCodeFormBitmap(bitmap);
        isQR = result != null;
        return isQR;
    }

    public class MyAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isQR) {
                handler.sendEmptyMessage(0);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            decodeImage(params[0]);
            return null;
        }
    }

    private static final String QRCodeFileName = "打赏书签助手10元";

    /**
     * 根据地址获取网络图片 sUrl 图片地址
     */
    public Bitmap getBitmap(String sUrl) {
        String dirPath = Path.SDCARD_ROOTPATH + Path.SDCARD_APP_ROOTPATH + Path.FILE_SPLIT;
        File dir = new File(dirPath);
        dir.mkdirs();
        String filePath = dirPath + QRCodeFileName + ".jpg";
        HttpURLConnection conn = null;
        Bitmap bitmap = null;
        try {
            URL url = new URL(sUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        if (bitmap != null) {
            saveMyBitmap(bitmap, filePath);//先把bitmap生成jpg图片
            return bitmap;
        }
        return null;
    }

    /**
     * 显示Dialog
     * param v
     */
    private void showDialog() {
        initAdapter();
        mCustomDialog = new CustomDialog(this) {
            @Override
            public void initViews() {
                // 初始CustomDialog化控件
                ListView mListView = (ListView) findViewById(R.id.lv_dialog);
                mListView.setAdapter(adapter);
                mListView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // 点击事件
                        switch (position) {
                            case 0:
                                sendToFriends();//把图片发送给好友
                                closeDialog();
                                break;
                            case 1:
                                saveImageToGallery(QRCodeActivity.this);
                                Toast.makeText(QRCodeActivity.this, "已保存到相册.", Toast.LENGTH_LONG).show();
                                closeDialog();
                                break;
                            case 2:
                                goIntent(view.getContext());
                                closeDialog();
                                break;
                        }

                    }
                });
            }
        };
        mCustomDialog.show();
    }

    /**
     * 初始化数据
     */
    private void initAdapter() {
        adapter = new ArrayAdapter<>(this, R.layout.qrcode_item_dialog);
        adapter.add("发送给朋友");
        adapter.add("保存到手机");
    }

    /**
     * 是二维码时，才添加为识别二维码
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (isQR) {
                    adapter.add("识别二维码");
                }
                adapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * 发送给好友
     */
    private void sendToFriends() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri imageUri = Uri.parse("file://" + file.getAbsolutePath());
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享11111111");
        intent.putExtra(Intent.EXTRA_TITLE, "Title22222222222222");
        intent.putExtra(Intent.EXTRA_TEXT, "text333333333333333333333333");
        startActivity(Intent.createChooser(intent, getTitle()));
    }

    /**
     * bitmap 保存为jpg 图片
     *
     * @param mBitmap  图片源
     * @param filePath 图片保存路径以及文件名
     */
    public void saveMyBitmap(Bitmap mBitmap, String filePath) {
        file = new File(filePath);
        FileOutputStream fOut = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fOut = new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        if (fOut != null) {
            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 先保存到本地再广播到图库
     */
    public void saveImageToGallery(Context context) {
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), QRCodeFileName, null);
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * https://ydmmocoo.github.io/2016/06/30/Android%E8%B0%83%E7%94%A8%E5%BE%AE%E4%BF%A1%E6%89%AB%E4%B8%80%E6%89%AB%E5%92%8C%E6%94%AF%E4%BB%98%E5%AE%9D%E6%89%AB%E4%B8%80%E6%89%AB/
     */
    public void goIntent(Context context) {
        try {
            //利用Intent打开微信
//            Uri uri = Uri.parse("weixin://" + result.toString());
//            Uri uri = Uri.parse("weixin://w.url.cn/s/CMNZfQf");
            Uri uri = Uri.parse("weixin://");
            LogHelper.v("goIntent.uri:" + uri.toString());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            Toast.makeText(context, "打开扫一扫，选择相册中的二维码图片。", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            LogHelper.e(e);
            //若无法正常跳转，在此进行错误处理
            Toast.makeText(context, "无法跳转到微信，请检查您是否安装了微信！", Toast.LENGTH_SHORT).show();
        }
    }
}
