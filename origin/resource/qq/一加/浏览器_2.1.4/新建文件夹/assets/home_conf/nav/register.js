/**
 该js包含所有终端主动调用的方法。
 */
/*
 设置皮肤主题时被调用的接口，
 mode等于
 0为日间模式，
 1为夜间模式，
 2为浅色模式，
 3为深色模式，
 4为节日模式，
 该接口5.4取代setNightTheme和setDayTheme
 */
function setSkinTheme(mode) {
    TConsole.log("setSkinTheme mode  = " + mode, 0);
    if (x5.getPlatform() == "android") {

    } else {
        if (window.localStorage) {
            window.localStorage.setItem('skin', mode);
        }
    }
    if (!isNaN(mode)) {
        mode = parseInt(mode);
    }
    switch (mode) {
        case 1:
            window.cssMng.addCss("psn_night.css", "skinTheme");
            break;
        case 2:
            window.cssMng.addCss("psn_light.css", "skinTheme");
            break;
        case 3:
            window.cssMng.addCss("psn_deep.css", "skinTheme");
            break;
        case 4:
            window.cssMng.addCss("psn_skin.css", "skinTheme");
            break;
        case 0:
        default:
            window.cssMng.removeCss("skinTheme");
            break;
    }
    return true;
}

/*
一加手机特性
*/
function setYiJiaSkinTheme(mode) {
    TConsole.log("setSkinTheme mode  = " + mode, 0);
    if (x5.getPlatform() == "android") {

    } else {
        if (window.localStorage) {
            window.localStorage.setItem('skin', mode);
        }
    }
    if (!isNaN(mode)) {
        mode = parseInt(mode);
    }
    switch (mode) {
        case 1:
            window.cssMng.addCss("psn_night.css", "skinTheme");
            break;
        case 5://TODO
            window.cssMng.addCss("psn_dark.css", "skinTheme");
            break;
        case 0:
        default:
            window.cssMng.removeCss("skinTheme");
            break;
    }
    return true;
}

/*
 ios终端有一处bug，其在调用setSkinTheme之前会调用x5onSkinSwitch。
 由于卡片未实现该函数，因此会抛异常，导致window.onerror捕获并上报，浪费用户流量。
 故实现该函数，避免出现异常。
 */
function x5onSkinSwitch() {

}
/*
 设置夜间模式时被调用的接口，浏览器老版本调用
 */
function setNightTheme() {
    window.cssMng.addCss("psn_night.css", "skinTheme");
    if (window.localStorage) {
        window.localStorage.setItem('theme', 'night');
    }
}

/*
 设置日间模式时被调用的接口，浏览器老版本调用
 */
function setDayTheme() {
    window.cssMng.removeCss("skinTheme");
    if (window.localStorage) {
        window.localStorage.setItem('theme', 'day');
    }
}

/*
 android终端有时候不清楚卡片高度，需要页面将offsetHeight传给终端
 */
function refreshContentHeight() {
    x5.android.onGetOffsetHeight(document.documentElement.offsetHeight);
}

/*
 无图模式切换至有图模式时被调用的接口
 */
function switch2PicStyle() {
    loadAllImg(true);
}

/*
 长按后选择新窗口打开或者后台打开时调用的方法
 */
function openActiveLink() {
    TConsole.log("openActiveLink", 0);
    var activeDom = window.activeDom;
    if (activeDom) {
        var onclick = activeDom.touch.getAttribute("mtt_onclick");
        var self = this;
        if (onclick) {
            window[onclick](activeDom.touch);
        }
    } else {
        TConsole.log("openActiveLink get touchTom failed, dom = " + activeDom, 3);
    }
}

/*
 长按时被调用的接口，目的是给长按对象加上焦点状态
 */
function setActive() {
    TConsole.log("setActive", 0);
    var activeDom = window.activeDom;
    if (activeDom) {
        activeDom.longTouch = 1; //防止touchcancel取消焦点态
        activeDom.focus.addClass("active");
        TConsole.log("setActive, dom = " + activeDom.focus, 0);
    } else {
        TConsole.log("setActive failed, dom = " + activeDom, 3);
    }
};

/*
 离开长按时被调用的接口，目的是给长按对象取消焦点状态
 页面跳转也会调用该接口~~~！！！
 */
function setDeactive() {
    TConsole.log("setDeactive", 0);
    var activeDom = window.activeDom;
    if (activeDom) {
        activeDom.longTouch = 0;
        clearTimeout(activeDom.focusTimer);
        clearTimeout(activeDom.clickTimer);
        activeDom.focus.removeClass("active");
        TConsole.log("setDeactive, dom = " + activeDom.focus, 0);
        self.activeDom = null;
    } else {
        TConsole.log("setDeactive failed, dom = " + activeDom, 3);
    }
};

function lazyLoadImg() {
    x5.isNoPicStyle(function () {
        loadAllImg(0);
    }, function () {
        loadAllImg(1);
    });
};

function loadAllImg(isShow) {
    TConsole.log("loadAllImg isShow = " + isShow, 0);
    var lazyLoadArray = [];

    function bgLoadSucc(ctx) {//backgroundImage方式延迟加载
        ctx.getId().style.backgroundImage = 'url("' + ctx.uri + '")';
        ctx.getId().setAttribute("uri", "");//保证不重复加载
        x5.notifyImgLoadResult(0, loadAllImg.name);
    }

    function imgloadSucc(ctx) {
        ctx.getId().setAttribute("uri", "");
        x5.notifyImgLoadResult(0, loadAllImg.name);
    }

    function loadFail(ctx) {
        if (ctx.getLoadCnt() >= 3) {
            ctx.abort();
            x5.notifyImgLoadResult(1, loadAllImg.name); //由终端在网络良好的时候调用loadAllImg
        }
    }

    var divs = document.getElementsByTagName("DIV");
    for (var i = 0, l = divs.length; i < l; i++) { //针对backgroundImg
        var uri = divs[i].getAttribute("uri");
        if (uri) {
            var obj = new LazyLoad();
            obj.setId(divs[i]);
            if (isShow) {
                obj.setUri(uri);
                obj.setSuccCB(bgLoadSucc);
                obj.setFailCB(loadFail);
            } else {
                obj.setUri("");
            }
            lazyLoadArray.push(obj);
        }
    }
    var imgs = document.getElementsByTagName("IMG");
    for (var i = 0, l = imgs.length; i < l; i++) { //针对backgroundImg
        var uri = imgs[i].getAttribute("uri");
        if (uri) {
            var obj = new LazyLoad();
            obj.setId(imgs[i]);
            obj.setImg(imgs[i]);
            if (isShow) {
                obj.setUri(uri);
                obj.setSuccCB(imgloadSucc);
                obj.setFailCB(loadFail);
            } else {
                obj.setUri("");
            }
            lazyLoadArray.push(obj);
        }
    }
    setTimeout(function () {
        for (var i = 0, l = lazyLoadArray.length; i < l; ++i) {
            lazyLoadArray[i].loadImg();
        }
    }, 0);
};

function imgClick(dom) {
    var dom = dom.getDom().parentNode;
    var url = dom.getAttribute("href");
    var rpt = dom.getAttribute("rpt");
    nativeOpenUrl(url, rpt);
}

function aClick(dom) {
    var url = dom.getAttribute("href");
    var rpt = dom.getAttribute("rpt");
    nativeOpenUrl(url, rpt);
}

function imgFigureClick(dom) {
    var dom = dom.getDom().parentNode.parentNode;
    var url = dom.getAttribute("href");
    var rpt = dom.getAttribute("rpt");
    nativeOpenUrl(url, rpt);
}

function selfClick(dom) {
    var url = dom.getAttribute("href");
    var rpt = dom.getAttribute("rpt");
    nativeOpenUrl(url, rpt);
}


function touchStartHandler(event) {
    TConsole.log("touchstart", 0);
    if (this.activeDom && this.activeDom.longTouch) {
        return false;
    }
    var focus = searchFocus(event.target);
    if (focus) {
        if (this.activeDom) {
            clearTimeout(this.activeDom.focusTimer);
            clearTimeout(this.activeDom.clickTimer);
            this.activeDom.focus.removeClass("active");
            this.activeDom = null;
        }
        //避免滚屏时焦点闪烁
        var focusTimer = setTimeout(function () {
            focus.addClass("active");
        }, 200);
        var touch = null;
        if (focus.hasClass("mtt_touch")) {
            touch = focus;
        } else {
            touch = searchTouch(event.target);
        }
        this.activeDom = {
            target: event.target, //源dom
            touch: touch,
            focus: focus, //焦点所对应dom
            startTime: Date.now(),
            focusTimer: focusTimer,
            clickTimer: -1
        }
    }
};

function touchMoveHandler(event) {
    TConsole.log("touchmove", 0);
    if (this.activeDom && !this.activeDom.longTouch) {
        clearTimeout(this.activeDom.focusTimer);
        clearTimeout(this.activeDom.clickTimer);
        this.activeDom.focus.removeClass("active");
        this.activeDom = null;
    }
};

function touchEndHandler(event) {
    TConsole.log("touchend", 0);
    event.preventDefault();
    if (this.activeDom && !this.activeDom.longTouch) {
        var focus = this.activeDom.focus;
        if( focus.hasClass('active') ) {
            setTimeout(function(){
                focus.removeClass('active');
            }, 200);
        }
        else {
            clearTimeout(this.activeDom.focusTimer);
            focus.addClass("active");
            setTimeout(function () {
                focus.removeClass('active');
            }, 100);
        }

        if (Date.now() - this.activeDom.startTime < 400) {
            var onclick = null;
            if (this.activeDom.touch) {
                onclick = this.activeDom.touch.getAttribute("mtt_onclick");
            }
            var self = this;
            if (onclick) {
                //保证先执行focus.removeClass("active");清除焦点再跳转，避免页面返回后焦点依然存在
                clearTimeout(this.activeDom.clickTimer);//连续点击情况下取消上一次点击操作
                this.activeDom.clickTimer = setTimeout(function () {
                    window[onclick](self.activeDom.touch);
                    self.activeDom.touch = null;
                }, 200);
            }
        } else {
            this.activeDom.touch = null;
        }
    }
};

function touchCancelHandler(event) {
    TConsole.log("touchcancel", 0);
    if (this.activeDom && !this.activeDom.longTouch) {
        clearTimeout(this.activeDom.focusTimer);
        clearTimeout(this.activeDom.clickTimer);
        this.activeDom.focus.removeClass("active");
        this.activeDom = null;
    }
};

//向上查找焦点对象
function searchFocus(target) {
    if (target == document) {
        return null;
    }
    var touch = searchTouch(event.target);
    if (touch == null) {
        return null;
    }
    do {
        var target = new Dom(target);
        if (target.hasClass("mtt_focus")) {
            return target;
        } else {
            target = target.getDom().parentNode;
        }
    } while (target != document);
    return null;
};

//向上查找touch对象
function searchTouch(target) {
    if (target == document) {
        return null;
    }

    do {
        var target = new Dom(target);
        if (target.hasClass("mtt_touch")) {
            return target;
        } else {
            target = target.getDom().parentNode;
        }
    } while (target != document);
    return null;
};

function refreshCards() {

};

function onPagePause() {

};

function onPageResume() {
    new TAjax(window.exposure);
};

window.addEventListener("DOMContentLoaded", function () {
    if (document.body.name) {
        window.config.cardId = document.body.name;
    } else if (document.body.id) {
        window.config.cardId = document.body.id;
        window.exposure = {
            url: "http://card.3g.qq.com/report",//TODO 需要统计组修改统计 config.report.url || "",
            succ: function (data) {
            },
            fail: function (status) {
            },
            type: "POST",
            data: JSON.stringify({
                sName: "exposure",
                vItems: [
                    {
                        iType: 1,
                        sKey: "info",
                        iValue: 1,
                        sLog: document.body.id
                    }
                ]
            })
        };
    }
    window.cssMng = new CssMng();
    if (x5.getPlatform() == "android") { //android
        if (x5.getQBVersion() >= 5.4) { //新版本
            setSkinTheme(x5.android.getSkinTheme())
        } else {//老版本
            if(x5.android.getYiJiaSkinTheme() != -1) {//一加手机
                setYiJiaSkinTheme(x5.android.getYiJiaSkinTheme());
            } else if(x5.android.isNightSkin()) {
                setNightTheme();
            }
        }
    } else { // ios 注：有些无mtt_navi分支的android机型也会走到该分支
        if (window.localStorage) {
            var mode = window.localStorage.getItem('skin');
            if (mode) {//新版本
                setSkinTheme(mode);
            } else {//老版本 
                mode = window.localStorage.getItem('theme');
                if (mode == 'night') {
                    setNightTheme();
                }
            }
        }
    }

}, true);

window.addEventListener("touchstart", touchStartHandler, true);
window.addEventListener("touchmove", touchMoveHandler, true);
window.addEventListener("touchend", touchEndHandler, true);
window.addEventListener("touchcancel", touchCancelHandler, true);
window.addEventListener("click", function (event) {
    event.preventDefault();
}, true);

window.addEventListener("load", function () {
    if(typeof MIG_REPORT != 'undefined') {
       MIG_REPORT.monitor.renderEnd(); 
    }
    lazyLoadImg();
}, true);