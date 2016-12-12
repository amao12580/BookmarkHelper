var TLog = {
    /*
     0 -- no log
     1 -- console.log
     2 -- alert
     3 -- innerHTML
     4 -- report
     */
    type: (typeof config != "undefined" && config.log && config.log.type) ? config.log.type : 0,
    /*
     0 -- debug
     1 -- info
     2 -- warn
     3 -- error
     */
    level: (typeof config != "undefined" && config.log && config.log.level) ? config.log.level : 0,
    items: [],
    maxLength: 1000,
    print: function (str, level) {
        if (typeof level == "undefined") {
            level = 0;
        }
        if (level < this.level) {
            return true;
        }
        switch (this.type) {
            case 0:
                break;
            case 1:
                console.log(str);
                break;
            case 2:
                if (typeof TConsole != "undefined") {
                    if (TConsole.isAlert() && 1) { //暂时屏蔽该功能
                        alert(str);
                    }
                }
                break;
            case 3:
                if (!this.logDom) { //to do
                    var div = document.createElement("div");
                    div.style.position = "fixed";
                    div.style.left = "5px";
                    div.style.top = "5px";
                    div.style.width = "600px";
                    div.style.height = "200px";
                    document.body.appendChild(div);
                    this.logDom = div;
                    this.log = "";
                }
                this.log += str;
                this.logDom.innerHTML = this.log;
                break;
            case 4:
                var showType = "debug";
                switch (level) {
                    case 0:
                        showType = "debug";
                        break;
                    case 1:
                        showType = "info";
                        break;
                    case 2:
                        showType = "warning";
                        break;
                    case 3:
                        showType = "error";
                        break;
                    default:
                        showType = "debug";
                        break;
                }
                var info = "[" + showType + "]" + str + "{" + new Date() + "}";
                if(typeof MIG_REPORT != 'undefined') {
                     MIG_REPORT.monitor.log(info);
               }
                if (level == 3) {
                    if(typeof MIG_REPORT != 'undefined') {
                         MIG_REPORT.monitor.reportErr(new Error());
                    }
                }
                break;
            default:
                return false;
                break;
        }
        return true;
    }
}

var TConsole = {
    guidArray: (typeof config != "undefined" && config.log && config.log.guidArray) ? config.log.guidArray : [],
    guid: "unknown",
    qua: "unknown",
    setGuid: function (guid) {
        if (guid) {
            this.guid = guid;
        }
    },
    setQua: function (qua) {
        if (qua) {
            this.qua = qua;
        }
    },
    getGuid: function () {
        return this.guid;
    },
    getQua: function () {
        return this.qua;
    },
    printStr: function (obj) {
        var ret = "";
        if (typeof obj === "object") {
            try {
                ret = JSON.stringify(obj);
            } catch (e) {
                ret = "JSON.stringify error, str = " + str;
            }
        } else {
            ret = obj + "";
        }
        return ret;
    },
    log: function (str, level) {
        TLog.print(str + ", guid = " + this.guid + ", qua = " + this.qua, level);
    },
    isAlert: function () {
        for (var i = 0; i < this.guidArray.length; ++i) {
            if (this.guid == this.guidArray[i]) {
                return true;
            }
        }
        return false;
    }
};

try {
    x5.getBrowserParam(
        function (para) {
            try {
                var paraObj = JSON.parse(para);
            }
            catch (e) {
                TConsole.log("getBrowserParam parse error, para = " + para, 3);
            }
            TConsole.setGuid(paraObj.guid);
            TConsole.setQua(paraObj.qua);
            TConsole.log("getBrowserParam paraObj.guid = " + paraObj.guid + "paraObj.qua = " + paraObj.qua, 1);
        },
        function () {
            TConsole.log("getBrowserParam failed", 3);
        }
    );
} catch (e) {
    TConsole.log("get getBrowserParam error", 3);
}

function obj2string(obj) {
    if (typeof obj == "object") {
        var ret = "";
        for (var key in obj) {
            var value = obj[key];
            if (typeof value == "object") {
                value = arguments.callee(value);
            }
            ret += (encodeURIComponent(key) + "=" + encodeURIComponent(value)) + "&";
        }
        ret = ret.substring(0, ret.length - 1);
        return ret;
    } else if (typeof obj == "string") {
        return obj;
    } else if (typeof obj == "number") {
        return obj + "";
    } else if (typeof obj == "boolean") {
        return obj + "";
    } else if (typeof obj == "undefined") {
        return "";
    }
};

/* TConsole.log通过TAjax上报，因此TAjax千万内部别出现log，导致无限递归 */
function TAjax(cfg) {
    this.url = typeof cfg.url != "undefined" ? cfg.url : "";
    this.succ = typeof cfg.succ == "function" ? cfg.succ : function (data) {
    };
    this.fail = typeof cfg.fail == "function" ? cfg.fail : function (status) {
    };
    this.type = typeof cfg.type != "undefined" ? cfg.type : "GET";
    this.data = typeof cfg.data != "undefined" ? cfg.data : "";
    this.time = typeof cfg.time != "undefined" ? cfg.time : 0; //0为无超时时间
    this.timer = -1;
    this.reqId = -1;

    var xhr = new XMLHttpRequest();
    var self = this;
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            clearTimeout(self.timer);
            if (xhr.status == 200 || xhr.status == 304) {
                var rsp = xhr.responseText;
                if(typeof MIG_REPORT != 'undefined') {
                    MIG_REPORT.monitor.reqEnd(self.reqId);
                }
                self.succ(rsp);
            } else {
                if(typeof MIG_REPORT != 'undefined') {
                    MIG_REPORT.monitor.reqErr();
                }
                self.fail(xhr.status);
            }
        }
    }
    if (!this.url) {
        this.fail(-1);
        return null;}
    if
     (self.type == "GET") {
        if (self.data) {
            var index = self.url.indexOf('?');
            if (index != -1) {
                if (typeof self.data == "object") {
                    self.url += ("&" + obj2string(self.data));
                } else {
                    self.url += ("&" + self.data);
                }
            } else {
                if (typeof self.data == "object") {
                    self.url += ("?" + obj2string(self.data));
                } else {
                    self.url += ("?" + self.data);
                }
            }
        }
        xhr.open("GET", self.url, true);
        xhr.send(null);
    } else if (self.type == "POST") {
        if (typeof self.data == "object") {
            var sendData = obj2string(self.data);
        } else {
            var sendData = self.data + "";
        }
        xhr.open("POST", self.url, true);
        xhr.send(sendData);
        this.reqId = MIG_REPORT.monitor.reqStart();
    }
    if (this.time > 0) {
        this.timer = setTimeout(function () {
            xhr.abort();
            if(typeof MIG_REPORT != 'undefined') {
                    MIG_REPORT.monitor.reqFail();
            } 
            self.fail(-2);//timeout
        }, self.time);
    }
};

function inheritPrototype(subType, superType) {
    var prototype = Object.create(superType.prototype);
    prototype.constructor = subType;
    subType.prototype = prototype;
}

function Dom(cfg) {
    var dom = Dom.getDom(cfg);
    if (!dom) {
        return null;
    }
    //Dom对象默认不缓存
    if (dom.id) {
        var ret = Dom.getCache(dom.id);
        if (ret) {
            return ret;
        }
    }
    this.dom = dom;
    this.focus = 0;
    this.isShow = 1;//默认显示状态为1，若该dom初始时为隐藏状态请调用setShowFlag设置为0
    this.onGetFocus = function () {
    };
    this.onLoseFocus = function () {
    };
    this.onShow = function () {
        this.dom.style.display = "block";
        return true;
    };
    this.onHide = function () {
        this.dom.style.display = "none";
        return true;
    };
};

Dom.prototype.hasClass = function (cls) {
    if (!this.dom) {
        return false;
    }
    if (!this.className) {
        this.className = this.dom.className;
    }
    var splitor = " ";
    if (!this.classArray) {
        if (this.className) {
            this.classArray = this.className.split(splitor);
        } else {
            this.classArray = [];
        }
    }
    var ret = false;
    for (var i = 0, l = this.classArray.length; i < l; i++) {
        if (this.classArray[i] == cls) {
            ret = true;
            break;
        }
    }
    return ret;
};

Dom.prototype.addClass = function (cls) {
    if (!this.dom) {
        return false;
    }

    if (!this.hasClass(cls)) {
        var splitor = " ";
        this.className += splitor + cls;
        this.classArray.push(cls);
        this.dom.className = this.className;
        return true;
    } else {
        return false;
    }
};

Dom.prototype.removeClass = function (cls) {
    if (!this.dom) {
        return false;
    }
    if (!this.hasClass(cls)) {
        return false;
    }
    var splitor = " ";
    for (var i = 0, l = this.classArray.length; i < l; i++) {
        if (this.classArray[i] == cls) {
            this.classArray.splice(i, 1);
            this.className = this.classArray.join(splitor);
            this.dom.className = this.className;
            return true;
        }
    }
    return false;
};

Dom.prototype.setClass = function (cls) {
    if (!this.dom) {
        return false;
    }
    var splitor = " ";
    this.className = cls;
    this.dom.className = cls;
    if (this.className) {
        this.classArray = cls.split(splitor);
    } else {
        this.classArray = [];
    }
    return true;
};

Dom.prototype.getClass = function () {
    if (!this.dom) {
        return "";
    } else {
        if (!this.className) {
            this.className = this.dom.className;
        }
        return this.className;
    }
};

Dom.prototype.show = function () {
    if (!this.dom) {
        return false;
    }
    if (this.isShow) {
        return true;
    } else {
        this.isShow = 1;
        return this.onShow();
    }
};

Dom.prototype.hide = function () {
    if (!this.dom) {
        return false;
    }
    if (this.isShow) {
        this.isShow = 0;
        return this.onHide();
    } else {
        return true;
    }
};

Dom.prototype.setOnShow = function (fn) {
    this.onShow = fn;
};

Dom.prototype.setOnHide = function (fn) {
    this.onHide = fn;
};

//设置显示状态，默认dom为显示状态，若该dom初始状态为隐藏则需要设置该标志位
Dom.prototype.setShowFlag = function (flag) {
    this.isShow = flag;
};

Dom.prototype.getElementsByClassName = function (cls) {
    if (this.dom) {
        return this.dom.getElementsByClassName(cls);
    } else {
        return [];
    }
};

Dom.prototype.getAttribute = function (key) {
    if (this.dom) {
        return this.dom.getAttribute(key);
    } else {
        return false;
    }
};

Dom.prototype.setAttribute = function (key, value) {
    if (this.dom) {
        return this.dom.setAttribute(key, value);
    } else {
        return false;
    }
};

Dom.prototype.css = function (property, value) {
    if (this.dom) {
        this.dom.style[property] = value;
        return true;
    } else {
        return false;
    }
};

Dom.prototype.getDom = function () {
    return this.dom;
};

Dom.prototype.setInnerHTML = function (html) {
    this.dom.innerHTML = html;
};

Dom.prototype.setOnGetFocus = function (fn) {
    this.onGetFocus = fn;
};

Dom.prototype.setOnLoseFocus = function (fn) {
    this.onLoseFocus = fn;
};

Dom.prototype.getFocus = function (isForce) {
    if (this.focus == 0 || isForce) {
        this.focus = 1;
        this.onGetFocus();
    }
};

Dom.prototype.loseFocus = function (isForce) {
    if (this.focus == 1 || isForce) {
        this.focus = 0;
        this.onLoseFocus();
    }
};

Dom.prototype.getId = function () {
    if (this.dom) {
        return this.dom.id;
    } else {
        return null;
    }
};

Dom.getDom = function (cfg) {
    if (cfg instanceof HTMLElement) {
        var dom = cfg;
    } else if (typeof cfg == "string") {
        var dom = document.getElementById(cfg);
    } else if (cfg && cfg.dom instanceof HTMLElement) {
        var dom = cfg.dom;
    } else if (cfg && typeof cfg.id == "string") {
        var dom = document.getElementById(cfg.id);
    } else {
        var cfgStr = "";
        if (typeof cfg === "object") {
            cfgStr = JSON.stringify(cfg);
        } else {
            cfgStr = cfg + "";
        }
        TConsole.log("get dom error, cfg = " + cfgStr, 2);
        var dom = null;
    }
    return dom;
};

Dom.cache = {};
Dom.id = 0;
Dom.getCache = function (id) {
    if (this.cache[id]) {
        return this.cache[id];
    } else {
        return null;
    }
};

Dom.setCache = function (obj) {
    var dom = obj.getDom();
    if (dom.id) {
        var id = dom.id;
    } else {
        var id = "mtt_random_" + Dom.id++;
        dom.id = id;
    }

    this.cache[id] = obj;
};

Dom.clearCache = function () {
    this.cache = {};
};

Dom.releaseCache = function (id) {
    if (this.cache[id]) {
        this.cache[id] = null;
        delete cache[id];
        return true;
    } else {
        return false;
    }
};

function TouchDom(cfg) {
    var dom = Dom.getDom(cfg);
    if (dom == null) {
        return null;
    }
    if (dom && dom.id) {
        var ret = TouchDom.getCache(dom.id);
        if (ret) {
            return ret;
        }
    }
    Dom.call(this, cfg);

    this.enableFlag = 1; //是否可用，不可用状态时无法响应touch
    this.longTouchFlag = 0;
    this.startTime = 0;
    this.startX = 0;
    this.startY = 0;
    this.moveFlag = 0;
    this.touchDelayTimer = -1;
    this.clickDelayTimer = -1;
    this.xMoveThreshold = typeof config != "undefined" && config.touch && config.touch.xMoveThreshold ?
        config.touch.xMoveThreshold : 2; //x方向移动阀值，超过阀值才认为是滑动，未超过阀值认为是手抖了~~~
    this.yMoveThreshold = typeof config != "undefined" && config.touch && config.touch.yMoveThreshold ?
        config.touch.yMoveThreshold : 2; //y方向移动阀值
    this.clickThreshold = typeof config != "undefined" && config.touch && config.touch.clickThreshold ?
        config.touch.clickThreshold : 400;
    this.touchDelayTime = typeof config != "undefined" && config.touch && config.touch.touchDelayTime ?
        config.touch.touchDelayTime : 200;
    this.clickDelayTime = typeof config != "undefined" && config.touch && config.touch.clickDelayTime ?
        config.touch.clickDelayTime : 200;
    this.onTouchStart = function (event) {
        //event.preventDefault(); 阻止默认操作，长按就弹不出来了
        return true;
    };
    this.onTouchMove = function (event) {
        return true;
    };
    this.onTouchEnd = function (event) {
        event.preventDefault(); //起始页需要调用终端的接口打开页面，所以需要屏蔽默认跳转
        return true;
    };
    this.onTouchCancel = function (event) {
        return true;
    };
    this.onLongTouch = function () {
        return true;
    };
    this.onClick = function () {
        return true;
    }

    var self = this;
    this.touchStart = function (event) {
        TConsole.log("touchstart, target = " + event.target, 0);
        TouchDom.setLongTouchDom(self);
        self.moveFlag = 0;
        self.startX = event.targetTouches[0].clientX;
        self.startY = event.targetTouches[0].clientY;
        self.startTime = Date.now();
        //加延时的目的是为了移动不出现焦点态
        self.touchDelayTimer = setTimeout(function () {
            self.onGetFocus();
        }, self.touchDelayTimer);
        return self.onTouchStart(event);
    };
    this.touchMove = function (event) {
        TConsole.log("touchmove, target = " + event.target, 0);
        if (self.longTouchFlag) {
            //长按状态move不取消焦点
            return self.onTouchMove(event);
        }
        var deltaX = event.targetTouches[0].clientX - self.startX;
        var deltaY = event.targetTouches[0].clientY - self.startY;
        //超过阀值才认为是滑动，未超过阀值认为是手抖了~~~
        if (Math.abs(deltaX) > self.xMoveThreshold ||
            Math.abs(deltaY) > self.yMoveThreshold) {
            self.moveFlag = 1;
            clearTimeout(self.touchDelayTimer);
            clearTimeout(self.clickDelayTimer);
            self.onLoseFocus();
            return self.onTouchMove(event);
        } else {
            return true;
        }
    };
    this.touchEnd = function (event) {
        TConsole.log("touchend, target = " + event.target, 0);
        if (self.moveFlag) {
            self.moveFlag = 0;
            return self.onTouchEnd(event);
        }
        if (self.longTouchFlag) {
            //长按状态end不取消焦点
            return self.onTouchEnd(event);
        }
        if (Date.now() - self.startTime < self.clickThreshold) {
            clearTimeout(self.touchDelayTimer);
            clearTimeout(self.clickDelayTimer);
            self.onGetFocus();
            self.clickDelayTimer = setTimeout(function () {
                self.onLoseFocus();
                self.onClick();
            }, self.clickDelayTime);
        } else {
            self.onLoseFocus();
        }
        return self.onTouchEnd(event);
    };
    this.touchCancel = function (event) {
        TConsole.log("touchcancel, target = " + event.target, 0);
        if (self.longTouchFlag) {
            //起始页长按的消息顺序是touchstart setActive touchcancel setDeactive，长按时此处不能取消焦点
            return self.onTouchCancel(event);
        } else {
            self.onLoseFocus();
        }
        clearTimeout(self.touchDelayTimer);
        clearTimeout(self.clickDelayTimer);
        return self.onTouchCancel(event);
    };
    this.dom.addEventListener("touchstart", self.touchStart, false);
    this.dom.addEventListener("touchmove", self.touchMove, false);
    this.dom.addEventListener("touchend", self.touchEnd, false);
    this.dom.addEventListener("touchcancel", self.touchCancel, false);
    //TouchDom默认缓存
    TouchDom.setCache(this);
};

inheritPrototype(TouchDom, Dom);

TouchDom.prototype.setOnTouchStart = function (fn) {
    var self = this;
    this.dom.removeEventListener(self.touchStart);
    this.onTouchStart = fn;
    this.dom.addEventListener("touchstart", self.touchStart, false);
};

TouchDom.prototype.setOnTouchMove = function (fn) {
    var self = this;
    this.dom.removeEventListener(self.touchMove);
    this.onTouchMove = fn;
    this.dom.addEventListener("touchmove", self.touchMove, false);
};

TouchDom.prototype.setOnTouchEnd = function (fn) {
    var self = this;
    this.dom.removeEventListener(self.touchEnd);
    this.onTouchEnd = fn;
    this.dom.addEventListener("touchend", self.touchEnd, false);
};

TouchDom.prototype.setOnTouchCancenl = function (fn) {
    var self = this;
    this.dom.removeEventListener(self.touchCancel);
    this.onTouchCancel = fn;
    this.dom.addEventListener("touchcancel", self.touchCancel, false);
};

TouchDom.prototype.setOnClick = function (fn) {
    this.onClick = fn;
};

TouchDom.prototype.setLongTouchFlag = function (flag) {
    this.longTouchFlag = flag;
};

TouchDom.prototype.setOnLongTouch = function (fn) {
    this.onLongTouch = fn;
};

TouchDom.prototype.handleLongTouch = function () {
    return this.onLongTouch();
};

TouchDom.prototype.disable = function () {
    if (this.enableFlag) {
        this.enableFlag = 0;
        var self = this;
        this.dom.removeEventListener("touchstart", self.touchStart, false);
        this.dom.removeEventListener("touchmove", self.touchMove, false);
        this.dom.removeEventListener("touchend", self.touchEnd, false);
        this.dom.removeEventListener("touchcancel", self.touchCancel, false);
        return true;
    } else {
        return false;
    }
};

TouchDom.prototype.enable = function () {
    if (this.enableFlag == 0) {
        this.enableFlag = 1;
        var self = this;
        this.dom.addEventListener("touchstart", self.touchStart, false);
        this.dom.addEventListener("touchmove", self.touchMove, false);
        this.dom.addEventListener("touchend", self.touchEnd, false);
        this.dom.addEventListener("touchcancel", self.touchCancel, false);
        return true;
    } else {
        return false;
    }
};

TouchDom.cache = {};
TouchDom.id = 0;
TouchDom.getCache = function (id) {
    if (this.cache[id]) {
        return this.cache[id];
    } else {
        return null;
    }
};

TouchDom.setCache = function (obj) {
    var dom = obj.getDom();
    if (dom.id) {
        var id = dom.id;
    } else {
        var id = "mtt_random_" + TouchDom.id++;
        dom.id = id;
    }

    this.cache[id] = obj;
};

TouchDom.clearCache = function () {
    this.cache = {};
};

TouchDom.releaseCache = function (id) {
    if (this.cache[id]) {
        this.cache[id] = null;
        delete cache[id];
        return true;
    } else {
        return false;
    }
};

TouchDom.setLongTouchDom = function (obj) {
    this.longTouchDom = obj;
};

TouchDom.getLongTouchDom = function () {
    return this.longTouchDom;
};

function bindTouchEvent() {
    var focusArray = document.getElementsByClassName("mtt_focus");
    var focus = null;
    var touchArray = [];
    for (var i = 0, l = focusArray.length; i < l; ++i) {
        focus = new Dom(focusArray[i]);
        touchArray = focus.getElementsByClassName("mtt_touch");
        for (var j = 0, jLen = touchArray.length; j < jLen; ++j) {
            if (touchArray[j].getAttribute("mtt_is_stop_propagation")) {
                continue;
            }
            bindDom(touchArray[j], focus);
        }
        if (focus.hasClass("mtt_touch")) {
            bindDom(focus.getDom(), focus);
        }
        ;
    }
};

function bindDom(dom, focus) {
    var touchDom = new TouchDom(dom);
    touchDom.setOnGetFocus(function () {
        focus.addClass("active");
    });
    touchDom.setOnLoseFocus(function () {
        focus.removeClass("active");
    });

    var isStopPropagation = dom.getAttribute("mtt_is_stop_propagation");
    if (isStopPropagation) {
        touchDom.setOnTouchStart(function (event) {
            event.stopPropagation();
            return false;
        });

        touchDom.setOnTouchEnd(function (event) {
            event.preventDefault();
            event.stopPropagation();
            return false;
        });
    }
    ;

    var onclick = dom.getAttribute("mtt_onclick");
    if (onclick) {
        touchDom.setOnClick(function () {
            window[onclick](touchDom);
        });
        touchDom.setOnLongTouch(function () {
            window[onclick](touchDom);
        });
    }
    ;

    return touchDom;
};

function nativeOpenUrl(url, rpt) {
    TConsole.log("nativeOpenUrl url = " + url + ", rpt = " + rpt, 0);
    if (!rpt) {
        rpt = "";
    }
    var options = {
        url: url,
        rpt: rpt
    };
    x5.addRPTStatistics({
        url: url,
        rpt: rpt + ";action=0"
    }, function () {
    }, function () {
    });
    try {
        x5.openNaviUrl(options, "", function () {
            TConsole.log("x5.openNaviUrl fail", 2);
            if (url) {
                window.location.href = url;
            }
            return false;
        });
    } catch (e) {
        TConsole.log("x5.openNaviUrl fail, msg = " + e.message, 2);
        if (url) {
            window.location.href = url;
        }
        return false;
    }
};

function CssMng() {
    this.style = {};
    var linkArray = document.head.getElementsByTagName("link");
    for (var i = 0, l = linkArray.length; i < l; ++i) {
        var link = linkArray[i];
        this.style[link.href] = link;
    }
};

CssMng.prototype.addCss = function (url, name) {
    if (typeof name == "undefined") {
        var name = url + "";
    }
    if (!this.style[name]) {
        var css = document.createElement("link");
        css.rel = "stylesheet";
        css.type = "text/css";
        css.media = "screen";
        css.href = url;
        document.head.appendChild(css);
        this.style[name] = css;
    } else {
        this.style[name].href = url;
    }
};

CssMng.prototype.removeCss = function (name) {
    if (this.style[name]) {
        this.style[name].href = "";
    }
};

CssMng.prototype.hasCss = function (url, name) {
    if (typeof name == "undefined") {
        var name = url + "";
    }
    if (this.style[name]) {
        return true;
    } else {
        return false;
    }
};

function LazyLoad() {
    this.id = null;
    this.type = 0; // 0 -- backgroundImage; 1 -- img
    this.state = 0; // 0 -- not loaded; 1 -- loaded
    this.uri = "";
    this.img = null;
    this.cnt = 0;
    this.loadSuccCB = function (ctx) {
    };
    this.loadFailCB = function (ctx) {
    };
    this.loadInterval = 3000;
};

LazyLoad.prototype.loadImg = function () {
    if (!this.img) {
        this.createImg();
    }
    if (!this.uri) {
        this.state = 1;
        this.loadSuccCB(this);
        return true;
    }
    if (this.state == 0) {

        this.img.src = this.uri;
        var self = this;
        clearTimeout(this.timer);
        self.timer = setTimeout(function () {
            self.loadImg();
        }, self.loadInterval);
    }
    return true;
};

LazyLoad.prototype.createImg = function () {
    if (!this.img) {
        this.img = document.createElement("img");
        var self = this;
        this.img.onload = function () {
            clearTimeout(self.timer);
            self.state = 1;
            self.loadSuccCB(self);
            self.img = null;
        };
        this.img.onabort = function () {

            ++self.cnt;
            self.loadFailCB(self);
        };
        this.img.onerror = function () {
            ++self.cnt;
            self.loadFailCB(self);
        };
        this.img.style.display = "none";
    }
    return true;
};

LazyLoad.prototype.getState = function () {
    return this.state;
};

LazyLoad.prototype.setState = function (state) {
    this.state = state;
};

LazyLoad.prototype.setSuccCB = function (fn) {
    this.loadSuccCB = fn;
};

LazyLoad.prototype.setFailCB = function (fn) {
    this.loadFailCB = fn;
};

LazyLoad.prototype.setUri = function (uri) {
    this.uri = uri;
};

LazyLoad.prototype.setImg = function (img) {
    var orginalCB = {
        onload: null,
        onabort: null,
        onerror: null
    }
    if (img.onload) {
        orginalCB.onload = img.onload;
    }
    if (img.onabort) {
        orginalCB.onabort = img.onabort;
    }
    if (img.onerror) {
        orginalCB.onerror = img.onerror;
    }
    this.img = img;
    var self = this;
    self.cnt = 0;
    this.img.onload = function () {
        clearTimeout(self.timer);
        self.state = 1;
        self.loadSuccCB(self);
        self.img = null;
        if (orginalCB.onload) {
            orginalCB.onload();
        }
    };
    this.img.onabort = function () {
        ++self.cnt;
        self.loadFailCB(self);
        if (orginalCB.onabort) {
            orginalCB.onabort();
        }
    };
    this.img.onerror = function () {
        ++self.cnt;
        self.loadFailCB(self);
        if (orginalCB.onerror) {
            orginalCB.onerror();
        }
    };
};

LazyLoad.prototype.getLoadCnt = function () {
    return this.cnt;
};

LazyLoad.prototype.setId = function (id) {
    this.id = id;
};

LazyLoad.prototype.getId = function () {
    return this.id;
};

LazyLoad.prototype.abort = function () {
    clearTimeout(this.timer);
    this.cnt = 0;
};

/*以下是和x5相关的封装*/
function X5Cache(key) {
    this.key = key;
    this.updateTime = -1;
    this.value = null;
    this.isValid = false;
};

X5Cache.prototype.getUpdateTime = function () {
    return this.updateTime;
};

X5Cache.prototype.getValue = function () {
    return this.value;
};

X5Cache.prototype.invalid = function () {
    this.updateTime = -1;
    this.value = null;
    this.isValid = false;
};

X5Cache.prototype.isValidCache = function () {
    return this.isValid;
};

X5Cache.prototype.get = function (succ, fail) {
    if (!this.key) {
        TConsole.log("get cache failed, key = " + this.key, 2);
        this.invalid();
        fail();
        return false;
    }

    var options = {};
    options.keys = [this.key];
    var self = this;
    try {
        x5.getNaviCardLocalStorage(options, function (data) {
            if (typeof data === "object") {
                TConsole.log("get cache, obj data = " + JSON.stringify(data), 0);
            } else {
                TConsole.log("get cache, str data = " + data + "", 2);
                self.invalid();
                fail();
                return false;
            }
            var cacheStr = data[self.key];
            if (cacheStr) {
                try {
                    var cache = JSON.parse(cacheStr);
                } catch (e) {
                    TConsole.log("parse cache error, cacheStr = " + cacheStr, 3);
                    self.invalid();
                    fail();
                    return false;
                }
            } else {
                TConsole.log("get cache, key = " + self.key + ", cacheStr = " + cacheStr, 1);
                self.invalid();
                fail();
                return false;
            }
            if (cache.cache == null) {
                TConsole.log("get cache succ, but cache = null", 1);
                self.invalid();
                fail();
                return false;
            } else {
                self.updateTime = cache.updateTime;
                self.value = cache.cache;
                self.isValid = true;
                succ(cache.cache);
                return true;
            }
        }, function () {
            TConsole.log("getNaviCardLocalStorage failed", 2);
            self.invalid();
            fail();
        });
    } catch (e) {
        TConsole.log("get cache error, key = " + this.key + ", msg = " + e.message, 3);
        self.invalid();
        fail();
        return false;
    }
    return true;
};

//由于android的bug，需要借助全局域规避闭包访问不到的问题，
//因此使用该函数时请一定等一个set的回调执行后再调用另外一个set,避免两个set使用的全局变量相互影响
//5.7的qb_bridge解决了闭包bug，因此做分支处理
X5Cache.prototype.set = function (data, succ, fail) {
    //android x5库有作用域的bug，成功回调和失败回调函数只能访问全局变量，不能访问闭包
    if (!this.key) {
        TConsole.log("set cache failed, key = " + this.key, 2);
        this.invalid();
        fail();
        return false;
    }
    var time = Date.now();
    var obj = {
        cache: data,
        updateTime: time
    }

    this.updateTime = time;
    this.value = data;
    this.isValid = true;
    try {
        var cacheStr = JSON.stringify(obj);
    } catch (e) {
        TConsole.log("stringify cache error" + ", msg = " + e.message, 3);
        this.invalid();
        fail();
        return false;
    }
    var options = {};
    options[this.key] = cacheStr;
    TConsole.log("save cache, options = " + JSON.stringify(options), 0);
    this.succ = succ;
    this.fail = fail;
    var self = this;
    if (x5.getQBVersion() < 5.7) {
        window['__X5Cache'] = this;
    }
    //android x5库有作用域的bug,只能借助全局域  
    try {
        x5.setNaviCardLocalStorage(options, function () {
            if (x5.getQBVersion() < 5.7) {
                self = window['__X5Cache'];
            }
            self.succ();
        }, function () {
            if (x5.getQBVersion() < 5.7) {
                self = window['__X5Cache'];
            }
            TConsole.log("save cache failed, cache = " + JSON.stringify(self.value), 3);
            self.invalid();
            self.fail();
        });
    } catch (e) {
        TConsole.log("save cache error, cache = " + JSON.stringify(options) + ", msg = " + e.message, 3);
        this.invalid();
        fail();
        return false;
    }

    return true;
};

X5Cache.prototype.clear = function () {
    var self = this;
    this.set(null, function () {
        TConsole.log("clear cache succ, key = " + self.key);
    }, function () {
        TConsole.log("clear cache failed, key = " + self.key);
    });
};

function Security() {
    if (Security.singleton) {
        return Security.singleton;
    }

    Security.singleton = this;
};

Security.prototype.filterInnerHTML = function (str) {
    str += "";
    return str.replace(/[&'"<>\/\\\-\x00-\x09\x0b-\x0c\x1f\x80-\xff]/g, function (r) {
        return "&#" + r.charCodeAt(0) + ";";
    }).replace(/\r\n/g, "<BR>").replace(/\n/g, "<BR>").replace(/\r/g, "<BR>").replace(/ /g, "&nbsp;");
};

Security.singleton = null;//保证单例
