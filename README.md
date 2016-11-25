# BookmarkHelper
## 这是什么？
BookmarkHelper（书签助手）是一款Android平台下，浏览器书签转换工具类APP，运行时需要Root权限，目标全面兼容主流Android浏览器相互转换书签数据，暂未考虑支持iOS或桌面端。

使用举例：从[Chrome浏览器](http://www.coolapk.com/apk/com.android.chrome)中，提取用户书签数据，追加合并到[Via浏览器](http://www.coolapk.com/apk/mark.via)，带你轻松脱坑，成功用上最牛逼浏览器！

BookmarkHelper是个人作品，持续维护，永不考虑收费推广，不滥用Root权限，使用完毕可立即卸载，运行时按需连接网络。如果你用的爽，可以[鼓励一下](https://www.kisscat.pro/rewards/wechat-reward-image.png)，我将用于：支付VPS月租，开发更多精彩APP，谢谢您的点滴支持！

<a target="_blank" href="http://ww2.sinaimg.cn/mw690/becd6b85gw1f9oaniwbdig208e0dwu11.gif">使用教程Gif：5.8MB</a>

## 版本计划
### 0.0.X
#### [0.0.1版 Release](https://github.com/amao12580/BookmarkHelper/raw/master/origin/release/0.0.1%E7%89%88/pro.kisscat.www.bookmarkhelper_0.0.1.apk)
支持从[Chrome浏览器](http://www.coolapk.com/apk/com.android.chrome)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。
#### [0.0.2版 Release](https://github.com/amao12580/BookmarkHelper/raw/master/origin/release/0.0.2%E7%89%88/pro.kisscat.www.bookmarkhelper_0.0.2.apk)
1.增加兼容：支持从Flyme5自带浏览器提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

2.当rom不是flyme时，如果rom存在包名为：com.android.browser的浏览器，也可以用Flyme5的规则，一般的内置浏览器都是这个包名。

3.Flyme4.X系列的rom暂未测试，没有设备。如您感兴趣，请与我联系，见下方的邮件地址！

4.Via合并书签修复bug，增强稳定性，现在安装via，但是没有打开过，将不能合并，最少需要您手动打开一次！
#### [0.0.3版 Release](https://github.com/amao12580/BookmarkHelper/raw/master/origin/release/0.0.3%E7%89%88/pro.kisscat.www.bookmarkhelper_0.0.3.apk)
1.增加兼容：支持从[UC浏览器](http://www.coolapk.com/apk/com.UCMobile)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

2.界面大调整，美美哒！

3.部分代码重构，精简掉重合代码段。
#### [0.0.4版 Release](https://github.com/amao12580/BookmarkHelper/raw/master/origin/release/0.0.4%E7%89%88/pro.kisscat.www.bookmarkhelper_0.0.4.apk)
1.增加兼容：支持从[QQ浏览器](http://www.coolapk.com/apk/com.tencent.mtt)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

2.底栏小Tip，关于我、五星好评、捐助
#### [0.0.5版 Release](https://github.com/amao12580/BookmarkHelper/raw/master/origin/release/0.0.5%E7%89%88/pro.kisscat.www.bookmarkhelper_0.0.5.apk)
1.增加兼容：支持从[X浏览器](http://www.coolapk.com/apk/com.mmbox.xbrowser)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

2.Bug 修复：[QQ浏览器](http://www.coolapk.com/apk/com.tencent.mtt)支持读取QQ用户登录的书签数据，微信登录的还在修复中。

3.Bug 修复：[UC浏览器](http://www.coolapk.com/apk/com.UCMobile)支持读取QQ和微信用户登录的书签数据。

4.延迟对Root权限的检查和获取！

5.日志记录进行异步化，不再阻塞主线程

#### [0.0.6版 Release](https://github.com/amao12580/BookmarkHelper/raw/master/origin/release/0.0.6%E7%89%88/pro.kisscat.www.bookmarkhelper_0.0.6.apk)
1.增加兼容：支持从[360浏览器](http://www.coolapk.com/apk/com.qihoo.browser)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

2.Bug 修复：[Chrome浏览器](http://www.coolapk.com/apk/com.android.chrome)现在支持读取完整的书签数据。

3.Bug 修复：[QQ浏览器](http://www.coolapk.com/apk/com.tencent.mtt)支持读取微信用户登录的书签数据。

4.优化：减少主循环中日志打印，避免UI响应超时！

5.背景线程处理书签交换，避免UI响应超时

#### [0.0.7版 Release](https://github.com/amao12580/BookmarkHelper/raw/master/origin/release/0.0.7%E7%89%88/pro.kisscat.www.bookmarkhelper_0.0.7.apk)
1.增加兼容：支持从[搜狗浏览器](http://www.coolapk.com/apk/sogou.mobile.explorer)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

2.Bug 修复：修复部分机型底层shell命令无限阻塞问题

3.Bug 修复：现在所有书签已经完成重复性验证。

4.Bug 修复：[Chrome浏览器](http://www.coolapk.com/apk/com.android.chrome)支持提取嵌套文件夹里的书签数据。

5.Bug 修复：[Chrome浏览器](http://www.coolapk.com/apk/com.android.chrome)支持提取时保留文件夹路径。

6.Bug 修复：[QQ浏览器](http://www.coolapk.com/apk/com.tencent.mtt)支持提取时保留文件夹路径。

7.Bug 修复：[UC浏览器](http://www.coolapk.com/apk/com.UCMobile)支持提取时保留文件夹路径。

8.Bug 修复：[360浏览器](http://www.coolapk.com/apk/com.qihoo.browser)支持提取时保留文件夹路径。

9.Bug 修复：[X浏览器](http://www.coolapk.com/apk/com.mmbox.xbrowser)支持提取时保留文件夹路径。

10.Bug 修复：完善对浏览器应用是否安装的检测。

11.Bug 修复：[Via浏览器](http://www.coolapk.com/apk/mark.via)现在支持最新版本(2.1.1)。

12.一些webview的优化。

================  割   ================

已知问题：

1.[UC浏览器](http://www.coolapk.com/apk/com.UCMobile)无法读取首页的书签数据，找不到存取路径，研究中。

#### [0.0.8版 Release](https://github.com/amao12580/BookmarkHelper/raw/master/origin/release/0.0.8%E7%89%88/pro.kisscat.www.bookmarkhelper_0.0.8.apk)
1.增加兼容：支持从[百度浏览器](http://www.coolapk.com/apk/com.baidu.browser.apps)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

2.代码优化

#### [0.0.9版 Release](https://github.com/amao12580/BookmarkHelper/raw/master/origin/release/0.0.9%E7%89%88/pro.kisscat.www.bookmarkhelper_0.0.9.apk)
1.增加兼容：支持从[欧朋浏览器](http://www.coolapk.com/apk/com.oupeng.mini.android)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

2.代码优化
#### [0.0.10版 Release](https://github.com/amao12580/BookmarkHelper/raw/master/origin/release/0.0.10%E7%89%88/pro.kisscat.www.bookmarkhelper_0.0.10.apk)
1.增加兼容：支持从[猎豹浏览器](http://www.coolapk.com/apk/com.ijinshan.browser_fast)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

2.完善Android6.x及以上的权限检查机制

3.针对三星机型锁定/data/目录，现加入重新挂载机制

4.少许错误描述修复

#### [0.0.11版 Release](https://github.com/amao12580/BookmarkHelper/raw/master/origin/release/0.0.11%E7%89%88/pro.kisscat.www.bookmarkhelper_0.0.11.apk)
1.增加兼容：支持从[FireFox浏览器](http://www.coolapk.com/apk/org.mozilla.firefox)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

2.现在6.0及以上的rom，会在运行时申请权限

3.改善shell命令交互稳定性

4.Bug 修复：部分选项在点击时，不变底色的问题

5.Bug 修复：[UC浏览器](http://www.coolapk.com/apk/com.UCMobile)支持提取时保留文件夹路径。

6.Bug 修复：“文件拷贝失败”问题。

#### [0.0.12版 Release](https://github.com/amao12580/BookmarkHelper/raw/master/origin/release/0.0.12%E7%89%88/pro.kisscat.www.bookmarkhelper_0.0.12.apk)
1.增加兼容：支持从[UC国际版浏览器](http://www.coolapk.com/apk/com.UCMobile.intl)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

2.Bug 修复：针对部分机型，无法获取到：QQ、搜狗、360、UC等浏览器的登录用户书签的问题

3.稳定性：针对部分机型对文件操作有延迟现象，现加入double-check机制

4.Bug 修复：Flyme5自带浏览器支持提取时保留文件夹路径。

5.现在转换任务进行异步化，不再阻塞UI响应，对应的，加入处理进度条。

#### 0.0.13版 Draft
1.增加兼容：支持从[Chrome Canary浏览器](http://www.coolapk.com/apk/com.chrome.canary)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

2.现在捐助页面，支持长按二维码使用微信打开

#### 0.0.14版 Draft
1.增加兼容：支持从[Chrome Beta浏览器](http://www.coolapk.com/apk/com.chrome.beta)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

#### 0.0.15版 Draft
1.增加兼容：支持从[Chrome Dev浏览器](http://www.coolapk.com/apk/com.chrome.dev)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

#### 0.0.16版 Draft
1.增加兼容：支持从[MIUI浏览器](https://play.google.com/store/apps/details?id=com.miui.browser)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

#### 0.0.17版 Draft
1.增加兼容：支持从[Yandex浏览器](http://www.coolapk.com/apk/ru.yandex.searchplugin)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

#### 0.0.18版 Draft
1.增加兼容：支持从[旗鱼浏览器](http://www.coolapk.com/apk/com.ruanmei.qiyubrowser)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

#### 0.0.19版 Draft
1.增加兼容：支持从[H5浏览器](http://www.coolapk.com/apk/org.noear.h5)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

#### 0.0.20版 Draft
1.增加兼容：支持从[星尘浏览器](http://www.coolapk.com/apk/com.chaozhuo.browser_phone)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

#### 0.0.21版 Draft
1.增加兼容：支持从[360极速浏览器](http://www.coolapk.com/apk/com.qihoo.chrome360)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

#### 0.0.22版 Draft
1.增加兼容：支持从一加内置QQ浏览器提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。

#### 0.0.23版 Draft
1.增加兼容：支持从[X浏览器-pro](https://play.google.com/store/apps/details?id=com.mmbox.xbrowser.pro)提取书签，追加到[Via浏览器](http://www.coolapk.com/apk/mark.via)。


---

【FBI Waring！】0.1.X版的开发顺序随时会做出调整，请关注最新README

---
### 0.1.X
#### 0.1.1版 Draft
1.增加兼容：支持从[Via浏览器](http://www.coolapk.com/apk/mark.via)提取书签，追加到[夸克浏览器](http://www.coolapk.com/apk/com.quark.browser)。

#### 0.1.2版 Draft
1.增加兼容：支持从[Via浏览器](http://www.coolapk.com/apk/mark.via)提取书签，追加到[Chrome浏览器](http://www.coolapk.com/apk/com.android.chrome)。

#### 0.1.3版 Draft
1.增加兼容：支持从[Via浏览器](http://www.coolapk.com/apk/mark.via)提取书签，追加到[妮哩萌萌](http://www.coolapk.com/apk/nico.styTool)。

#### 0.1.4版 Draft
1.增加兼容：支持从[Via浏览器](http://www.coolapk.com/apk/mark.via)提取书签，追加到[Mercury浏览器](http://www.coolapk.com/apk/com.ilegendsoft.mercury)。

#### 0.1.5版 Draft
1.增加兼容：支持从[UC浏览器](http://www.coolapk.com/apk/com.UCMobile)提取书签，追加到[QQ浏览器](http://www.coolapk.com/apk/com.tencent.mtt)。

#### 0.1.6版 Draft
1.增加兼容：支持从[QQ浏览器](http://www.coolapk.com/apk/com.tencent.mtt)提取书签，追加到[X浏览器](http://www.coolapk.com/apk/com.mmbox.xbrowser)。

---

## TO-DO List

### Issue

1.读取不到... 红米2 5.1 MIUI8     红米3

没有收到日志，暂停修复

2.能做一个转移书签到夸克浏览器不？

增加兼容

3.识别不出chrome beta 和dev还有chromium啊。。有啥用？

增加兼容

4.有没有adui自带浏览器的书签转换？   给你说，MIUI的浏览器也是定制的QQ浏览器#(勉强)

增加兼容

5.~~魅族mx4，版本号：Flyme 5.1.11.0A 并不能转移成功……点击一下就卡在界面了~~

确认修复

6.~~谷歌浏览器书签栏的书签能转移了，但是书签栏里的文件夹里面的书签仍然不行~~

确认修复

7.可不可以把via书签移到chrome？

增加兼容

8.~~作者大大，反馈一个bug，uc浏览器转via书签中有相当一部分会复制两次，如图~~

确认修复

9.什么时候做个移书签到妮哩萌萌浏览器

增加兼容

10.希望能将别的浏览器书签能导入mercury浏览器

增加兼容

11.不能保存为文件吗

新特性

12.~~[笑哭再见]是我打开方式不对吗？导入QQ的书签，然后原来QQ浏览器和via分好类的文件夹都被被拆开了，现在打开书签满屏的书签，我得整理到什么时候啊！心酸[w累]~~

确认修复

13.可以导UC的首页标签吗？

无法定位UC主页书签的存取路径，暂停修复

14.大兄弟们告诉我，这个能不能把uc的导入QQ？

增加兼容

15.支持yandex

增加兼容

16.支持旗鱼浏览器

增加兼容

17.~~QQ浏览器也有这样的主页书签，也没法同步的吗？[w调皮][w调皮][w调皮]~~

确认QQ主页书签可以读取，不是bug

18.~~打开后安装新浏览器，显示未安装~~

确认修复

19.报via未安装，我的是g play版的，包名与国内版不一致

增加兼容

20.~~新版via，2.1.1，不兼容~~

确认修复

21.那希望大大早日添加华为自带浏览器的切换，应该不是很难，可能就包名不一样吧[w调皮][w调皮][w调皮]

增加兼容

22.@amao12580 何时支持h5

增加兼容

23.via浏览器 文件删除失败！



## UnStable
UnStable是指最新通过基础测试，但未进行大面积设备兼容验证的非稳定版本，不会发布到[Google Play](https://play.google.com/store?hl=zh_CN)或[Cool Market](http://www.coolapk.com)。如果你是狂热爱好者，请点击【[下载链接](https://github.com/amao12580/BookmarkHelper/raw/master/app/build/outputs/apk/app-debug.apk)】，从我的[Github Repo](https://github.com/amao12580/BookmarkHelper)实时获取，不保证会有小瑕疵。



## Bug & 反馈
由于个人精力有限，没有大量设备供测试，如您遇到使用问题，请发送log文件（SD路径：BookmarkHelper/logs/*Log.txt）到Mail：stevenchengmask # gmail.com，我将尽快答复您的疑问！同时也欢迎您的来信建议，我将最短时间内答复您！