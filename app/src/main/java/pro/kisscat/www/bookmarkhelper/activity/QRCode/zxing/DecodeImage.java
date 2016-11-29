package pro.kisscat.www.bookmarkhelper.activity.QRCode.zxing;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.Hashtable;
import java.util.Vector;

import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

public class DecodeImage {
    public static Result handleQRCodeFormBitmap(Bitmap bitmap) {
        RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        Hashtable<DecodeHintType, Object> hints = new Hashtable<>(2);
        Vector<BarcodeFormat> decodeFormats = new Vector<>();
        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        multiFormatReader.setHints(hints);
        Result result = null;
        try {
            result = multiFormatReader.decodeWithState(binaryBitmap);
        } catch (NotFoundException e) {
            LogHelper.e(e);
            e.printStackTrace();
        }
        return result;
    }
}
