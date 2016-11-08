package pro.kisscat.www.bookmarkhelper.util.appList;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pro.kisscat.www.bookmarkhelper.R;
import pro.kisscat.www.bookmarkhelper.common.shared.MetaData;
import pro.kisscat.www.bookmarkhelper.converter.support.pojo.App;
import pro.kisscat.www.bookmarkhelper.exception.InitException;
import pro.kisscat.www.bookmarkhelper.util.log.LogHelper;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/10
 * Time:10:47
 */

public class AppListUtil {
    private static Map<String, App> installedAllApp;
    public static String thisAppInfo;

    public static Map<String, App> getInstalledAllApp(Context context) {
        if (installedAllApp == null) {
            init(context);
        }
        return installedAllApp;
    }

    public static void init(Context context) {
        LogHelper.v("AppListUtil init");
        String globalMsg = context.getResources().getString(R.string.notPermissionForReadInstalledAppList);
        if (installedAllApp != null) {
            return;
        }
        installedAllApp = new TreeMap<>();
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            LogHelper.v("context.getPackageManager is null.");
            throw new InitException(globalMsg);
        }
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        if (packages == null || packages.isEmpty()) {
            LogHelper.v("packageManager.getInstalledPackages(0) is null or empty.");
            throw new InitException(globalMsg);
        }
        String mePackageName = context.getPackageName();
        for (PackageInfo packageInfo : packages) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            App app = new App();
            app.setName(applicationInfo.loadLabel(packageManager).toString());
            app.setPackageName(packageInfo.packageName);
            if (mePackageName != null && mePackageName.equals(packageInfo.packageName) && thisAppInfo == null) {
                thisAppInfo = "App name:" + context.getString(R.string.app_name) + ",packageName:" + mePackageName + ",versionName:" + packageInfo.versionName + ",versionCode:" + packageInfo.versionCode;
                LogHelper.v(thisAppInfo);
            }
            app.setVersionName(packageInfo.versionName);
            app.setVersionCode(packageInfo.versionCode);
//            app.setIcon(applicationInfo.loadIcon(packageManager));
            installedAllApp.put(app.getPackageName(), app);
            /**
             Only display the non-system app info
             if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
             appList.add(tmpInfo);//如果非系统应用，则添加至appList
             }
             */
        }
        if (installedAllApp == null) {
            LogHelper.e(MetaData.LOG_E_DEFAULT, "AppListUtil init failure,installedAllApp is null.");
            throw new InitException("AppListUtil init failure.");
        }
    }

    public static boolean isInstalled(Context context, String packageName) {
        return getInstalledAllApp(context).keySet().contains(packageName);
    }

//    public static Drawable getIcon(String packageName) {
//        if (isInstalled(packageName)) {
//            return installedAllApp.get(packageName).getIcon();
//        }
//        return null;
//    }

    public static String getAppName(Context context, String packageName) {
        if (isInstalled(context, packageName)) {
            return getInstalledAllApp(context).get(packageName).getName();
        }
        return "ERROR";
    }

    public static App getAppInfo(Context context, String packageName) {
        if (isInstalled(context, packageName)) {
            return getInstalledAllApp(context).get(packageName);
        }
        return null;
    }
}
