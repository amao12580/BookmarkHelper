package pro.kisscat.www.bookmarkhelper.activity.QRCode.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import pro.kisscat.www.bookmarkhelper.R;

/**
 * 弹出图片操作框
 */
public abstract class CustomDialog extends Dialog {

    private Context context;

    /**
     * 构造器
     *
     * @param context 上下文
     */
    protected CustomDialog(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
        setContentView(R.layout.qrcode_custom_dialog);
        createDialog();
    }

    /**
     * 设置dialog
     */
    private void createDialog() {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        initViews();
        if (!(context instanceof Activity)) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
    }

    public abstract void initViews();

    public CustomDialog closeDialog() {
        this.dismiss();
        return this;
    }
}
