package pro.kisscat.www.bookmarkhelper.util.sign;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import pro.kisscat.www.bookmarkhelper.BuildConfig;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/12/14
 * Time:17:39
 */

public class SignUtil {
    public static boolean isAppCanUse = false;

    public static boolean check(Context context) {
        getSingInfo(context);
        LogHelper.write();
        return isAppCanUse;
    }

    private static void getSingInfo(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            parseSignature(sign.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseSignature(byte[] signature) throws CertificateException, NoSuchAlgorithmException {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
        String pubKey = cert.getPublicKey().toString();
        String algorithm = cert.getPublicKey().getAlgorithm();
        String signNumber = cert.getSerialNumber().toString();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] encodeByte = cert.getEncoded();
        byte[] b = md5.digest(encodeByte);
        //key即为应用签名
        String signMD5key = byte2HexFormatted(b);
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        byte[] b1 = sha1.digest(encodeByte);
        String signSHA1key = byte2HexFormatted(b1);
        MessageDigest sha256 = MessageDigest.getInstance("SHA256");
        byte[] b2 = sha256.digest(encodeByte);
        String signSHA256key = byte2HexFormatted(b2);
        LogHelper.v("signName:" + cert.getSigAlgName());
        LogHelper.v("pubKey:" + pubKey);
        LogHelper.v("algorithm:" + algorithm);
        LogHelper.v("signNumber:" + signNumber);
        LogHelper.v("subjectDN:" + cert.getSubjectDN().toString());
        LogHelper.v("signMD5key:" + signMD5key);
        LogHelper.v("signSHA1key:" + signSHA1key);
        LogHelper.v("signSHA256key:" + signSHA256key);
        isAppCanUse = BuildConfig.DEBUG || (equalsMD5(signMD5key) && equalsSHA1(signSHA1key) && equalsSHA256(signSHA256key));
    }

    /**
     * 将获取到得编码进行16进制转换
     */
    private static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1)
                h = "0" + h;
            if (l > 2)
                h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1))
                str.append(':');
        }
        return str.toString();
    }

    private static boolean equalsMD5(String input) {
        String expect = "A8:1D:C6:0A:5B:03:AE:FE:BA:40:05:7D:BE:AE:F5:78";
        return !(input == null || !input.equals(expect));
    }

    private static boolean equalsSHA1(String input) {
        String expect = "29:97:1C:5E:8B:67:B8:A0:74:AC:FA:4D:5F:B5:EB:07:8B:82:F6:77";
        return !(input == null || !input.equals(expect));
    }

    private static boolean equalsSHA256(String input) {
        String expect = "BB:5C:A4:24:CD:43:EE:4A:0F:7B:F2:0D:F7:22:B0:45:28:FA:E6:D4:70:0C:52:38:60:A5:AE:EF:2F:8A:95:59";
        return !(input == null || !input.equals(expect));
    }
}
