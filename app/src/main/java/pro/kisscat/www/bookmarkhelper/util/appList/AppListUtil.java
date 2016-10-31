package pro.kisscat.www.bookmarkhelper.util.appList;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pro.kisscat.www.bookmarkhelper.pojo.App;
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

    public static void init(Context context) {
        LogHelper.v("AppListUtil init");
        if (installedAllApp != null) {
            return;
        }
        installedAllApp = new TreeMap<>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            App app = new App();
            app.setName(applicationInfo.loadLabel(packageManager).toString());
            app.setPackageName(packageInfo.packageName);
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
    }

    public static boolean isInstalled(String packageName) {
        return installedAllApp.keySet().contains(packageName);
    }

//    public static Drawable getIcon(String packageName) {
//        if (isInstalled(packageName)) {
//            return installedAllApp.get(packageName).getIcon();
//        }
//        return null;
//    }

    public static String getAppName(String packageName) {
        if (isInstalled(packageName)) {
            return installedAllApp.get(packageName).getName();
        }
        return "ERROR";
    }
}
