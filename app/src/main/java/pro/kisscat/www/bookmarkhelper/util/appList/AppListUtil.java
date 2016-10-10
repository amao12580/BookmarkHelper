package pro.kisscat.www.bookmarkhelper.util.appList;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pro.kisscat.www.bookmarkhelper.pojo.App;

/**
 * Created with Android Studio.
 * Project:BookmarkHelper
 * User:ChengLiang
 * Mail:stevenchengmask@gmail.com
 * Date:2016/10/10
 * Time:10:47
 */

public class AppListUtil {
    private static List<App> installedAllApp;
    private static Set<String> installedAllAppPackageName;

    public static List<App> getInstalledAll(Context context) {
        if (installedAllApp != null) {
            return installedAllApp;
        }
        installedAllAppPackageName = new TreeSet<>();
        List<App> appList = new ArrayList<>(); //用来存储获取的应用信息数据
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            App app = new App();
            app.setName(applicationInfo.loadLabel(packageManager).toString());
            app.setPackageName(packageInfo.packageName);
            installedAllAppPackageName.add(app.getPackageName());
            app.setVersionName(packageInfo.versionName);
            app.setVersionCode(packageInfo.versionCode);
            app.setIcon(applicationInfo.loadIcon(packageManager));
            /**
             Only display the non-system app info
             if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
             appList.add(tmpInfo);//如果非系统应用，则添加至appList
             }
             */
            appList.add(app);
        }
        installedAllApp = appList;
        return installedAllApp;
    }

    public static boolean isInstalled(String packageName) {
        return installedAllAppPackageName.contains(packageName);
    }
}
