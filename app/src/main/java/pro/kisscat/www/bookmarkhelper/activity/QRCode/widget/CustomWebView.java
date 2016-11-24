package pro.kisscat.www.bookmarkhelper.activity.QRCode.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;

/**
 * 自定义WebView，长按图片获取图片url
 *
 * @author LinZhang
 */
public class CustomWebView extends WebView implements OnLongClickListener {
    private Context context;
    private LongClickCallBack mCallBack;

    public CustomWebView(Context context, LongClickCallBack mCallBack) {
        super(context);
        this.context = context;
        this.mCallBack = mCallBack;
        initSettings();
    }

    private void initSettings() {
        // 初始化设置
        WebSettings mSettings = this.getSettings();
        mSettings.setJavaScriptEnabled(true);//开启javascript
        mSettings.setDomStorageEnabled(true);//开启DOM
        mSettings.setDefaultTextEncodingName("utf-8");//设置字符编码
        //设置web页面
        mSettings.setAllowFileAccess(true);//设置支持文件流
        mSettings.setSupportZoom(true);// 支持缩放
        mSettings.setBuiltInZoomControls(true);// 支持缩放
        mSettings.setUseWideViewPort(true);// 调整到适合webview大小
        mSettings.setLoadWithOverviewMode(true);// 调整到适合webview大小
        mSettings.setDefaultZoom(ZoomDensity.FAR);// 屏幕自适应网页,如果没有这个，在低分辨率的手机上显示可能会异常
        mSettings.setRenderPriority(RenderPriority.HIGH);
        //提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
        mSettings.setBlockNetworkImage(true);
        mSettings.setAppCacheEnabled(true);//开启缓存机制
        setWebViewClient(new MyWebViewClient());
        setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(v.getContext(), "实验性特性：二维码长按功能处于beta阶段", Toast.LENGTH_LONG).show();
        System.out.println("longClick hit.");
        // 长按事件监听（注意：需要实现LongClickCallBack接口并传入对象）
        final HitTestResult htr = getHitTestResult();//获取所点击的内容
        if (htr.getType() == HitTestResult.IMAGE_TYPE) {//判断被点击的类型为图片
            mCallBack.onLongClickCallBack(htr.getExtra());
        }
        return false;
    }

    private class MyWebViewClient extends WebViewClient {
        /**
         * 加载过程中 拦截加载的地址url
         * url  被拦截的url
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        /**
         * 页面加载过程中，加载资源回调的方法
         */
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        /**
         * 页面加载完成回调的方法
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 关闭图片加载阻塞
            view.getSettings().setBlockNetworkImage(false);

        }

        /**
         * 页面开始加载调用的方法
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            view.loadUrl(MetaData.NETWORKERRORURL);
        }

        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
            CustomWebView.this.requestFocus();
            CustomWebView.this.requestFocusFromTouch();
        }

    }

    /**
     * 长按事件回调接口，传递图片地址
     */
    public interface LongClickCallBack {
        /**
         * 用于传递图片地址
         */
        void onLongClickCallBack(String imgUrl);
    }

}
