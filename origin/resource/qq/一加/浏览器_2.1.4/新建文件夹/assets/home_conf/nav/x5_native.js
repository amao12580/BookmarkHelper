!function(a){var b,c,d,e;try{a["__loadStart"]=+new Date,b="mig_report_loading",c={set:function(b,c){a.localStorage&&a.localStorage.setItem(b,c)},get:function(b){return a.localStorage?a.localStorage.getItem(b):null}},d=c.get(b),d&&"end"!==d&&(a["__migLoadingError"]=1),c.set(b,"start"),e=[["DOMContentLoaded","__loadDOMEnd"],["load","__loadWindowEnd","WindowLoaded"]],e.forEach(function(d){a.addEventListener(d[0],function(){a[d[1]]=+new Date,c.set(b,d[2]||d[0])})})}catch(f){console.log(f.message)}}(window);

window["MIG_REPORT"]=function(global){var CommonLib,MigMetis,WebMonitor;return global["MIG_REPORT"]?global["MIG_REPORT"]:(CommonLib=function(){var a={isOBJ:function(a,b){return Object.prototype.toString.call(a).toLowerCase()===("[object "+(b||"Object")+"]").toLowerCase()},param:function(b){var d,c=[];for(d in b)(a.isOBJ(b[d],"string")||a.isOBJ(b[d],"number"))&&c.push(d+"="+encodeURIComponent(b[d]));return c.join("&")},extend:function(a){a=a||{};var b,c,e,d=arguments;for(b=1,c=d.length;c>b;b++)for(e in d[b])d[b].hasOwnProperty(e)&&(a[e]=d[b][e]);return a},guid:function(){function a(){return Math.floor(65536*(1+Math.random())).toString(16).substring(1)}return a()+a()+"-"+a()+"-"+a()+"-"+a()+"-"+a()+a()+a()},encrypt:function(a){try{return global.btoa(global.encodeURIComponent(a))}catch(b){return console.log(b),""}},ajax:function(b,c){function d(a,b){return void 0!==a[b]}try{c=c||{};var e=new global.XMLHttpRequest,f=null;c["method"]=c["method"]||"GET",c["params"]&&(b+="?"+a.param(c["params"])),e.onreadystatechange=function(){4===e.readyState&&f&&clearTimeout(f)},e.open(c["method"],b,!0),d(e,"withCredentials")&&(e.withCredentials=c["witchCred"]||!1),/post/i.test(c["method"])?e.send(a.encrypt(c["data"])):e.send(null),d(e,"onabort")&&(f=setTimeout(function(){e.abort(),e=null},2e4))}catch(g){}}};return a}(),MigMetis=function(a){function b(b){var c={};return c.param=CommonLib.param,c.extend=CommonLib.extend,c.guid=CommonLib.guid,c.isOBJ=CommonLib.isOBJ,c.getCookie=function(c,d){var e,f;return e=d?d.document.cookie:(b||a).document.cookie,f=e.match(new RegExp("(^| )"+c+"=([^;]*)(;|$)")),null!==f?f[2]:""},c.setCookie=function(c,d,e,f,g){e=e||30;var j,h=new Date,i=h;i.setTime(h.getTime()+1e3*60*60*24*e),j=c+"="+d+"; expires="+i.toUTCString()+(f?"; domain="+f:""),g?g.document.cookie=j:(b||a).document.cookie=j},c}function c(c){var d=this;return d.$=b(),d.options=d.$.extend({appId:"",qq:"",qua:"",guid:"",metisHost:"http://webstat.html5.qq.com/metis"},c||{}),d._metisInfo="",d._speedObj={},d._timeout=-1,d._cookieWin=a,a.location.host.indexOf(".qq.com")>-1&&(document.domain="qq.com",a.location.host.indexOf(".html5.qq.com")<0&&(d._cookieWin=document.createElement("iframe"),d._cookieWin.style.display="none",d._cookieWin.src=d.options["metisHost"]+"/x5_proxy.html",document.body.appendChild(d._cookieWin),d._cookieWin=d._cookieWin["contentWindow"])),d["getMetisInfo"]=function(a){var b=this,c="";try{(a||!b._metisInfo)&&(b._metisInfo=b.$.getCookie("qb_metis4web",b._cookieWin),b._metisInfo&&(clearTimeout(b._timeout),b._timeout=setTimeout(function(){b._metisInfo=""},b._metisInfo.split("_")[2]-Date.now()))),c=b._metisInfo}catch(d){console.log(d)}return c},d["setCookie"]=function(a,b,c,d){try{return this.$.setCookie(a,b,c,d,this._cookieWin),!0}catch(e){console.log(e)}return!1},d["getCookie"]=function(a){var b="";try{b=this.$.getCookie(a,this._cookieWin)}catch(c){console.log(c)}return b},d["_log"]=function(b,c,d,e,f,g){try{var i,h=this;if(h._metisInfo=h["getMetisInfo"](),h._metisInfo||g)return i={biz:h.options["appId"],url:a.location.href,seq:h._metisInfo,qq:h.options["qq"],qua:h.options["qua"],guid:h.options["guid"],level:b,msg:(h.$.isOBJ(c)?JSON.stringify(c):c)||""},d&&(i["error"]=d),e&&(i["time"]=e),f&&(i["key"]=f),CommonLib.ajax(h.options["metisHost"]+"/api/log",{params:i,withCred:!0}),!0}catch(j){console.log(j)}return!1},d._getMsgErr=function(a){return d.$.isOBJ(a)?a.error:void 0},["info","debug","warn","error"].forEach(function(a){d[a]=function(a){return function(b){d["_log"](a,b,d._getMsgErr(b))}}(a)}),d["start"]=function(a){return a?(this._speedObj[a]=Date.now(),this._speedObj[a]):!1},d["end"]=function(a,b){var c=this._speedObj[a];return c?(delete this._speedObj[a],this["_log"]("info",b,d._getMsgErr(b),Date.now()-c,a)):!1},d}return c}(global),WebMonitor=function(){var exports,keyMap={LOAD_TIME:["mig_load_time"],REQ_AJAX_TIME:["mig_req_ajax_time"],LOAD_JS_ERR:["mig_load_js_error",701],LOAD_CSS_ERR:["mig_load_css_error",702],LOAD_IMG_ERR:["mig_load_img_error",703],REQ_AJAX_FAIL:["mig_req_ajax_fail",704],LOAD_PAGE_ERR:["mig_load_page_error",705],RUN_JS_ERR:["mig_run_js_error",801],REQ_AJAX_ERR:["mig_req_ajax_error",802],OTHER_ERR:["mig_other_error",899]},getErrorCode=function(a){return keyMap.hasOwnProperty(a)&&keyMap[a][1]?keyMap[a][1]:keyMap.OTHER_ERR[1]},_config={ignore:[],delay:5,errDetail:!1,perPercentage:10,appId:"",appName:"",metis:null,globalCapture:!0,autoReportLoadTime:!0},_isOBJ=CommonLib.isOBJ,afterWindLoaded=function(a,b){global["__loadWindowEnd"]?a(b):global.addEventListener("load",function(){global["__loadWindowEnd"]=getTimestamp(),a(b)})},throttle=function(a){return a=a||_config["perPercentage"],100*Math.random()<=a},setConfig=function(a){if(_isOBJ(a)){for(var b in a)_config[b]=a[b];a.hasOwnProperty("appId")&&!a.hasOwnProperty("appName")&&(_config["appName"]=a["appId"].split(".")[0]),_config["globalCapture"]&&errorStat.globalCapture(),_config["autoReportLoadTime"]&&throttle()&&afterWindLoaded(function(){var d,a=parseInt(global["__loadStart"]),b=parseInt(global["__loadDOMEnd"]),c=parseInt(global["__loadWindowEnd"]);a&&b&&c&&(d=Math.max(b-a,c-a),reportToServer.performance({loadTime:d+"ms"}))})}},getTimestamp=function(){return(new Date).getTime()},reportToServer=function(){var a="http://jsreport.html5.qq.com",b="POST",c=function(b){return[a,_config["appName"],b].join("/")},d=function(a,b){return b=b||[],_isOBJ(a,"Array")?b=b.concat(a):b.push(a),b},e=function(a,c,e){if(_config["appId"]&&_config["appName"]){var f={id:_config["appId"]};f[c]=d(e),f[c]["length"]&&CommonLib.ajax(a,{method:b,data:JSON.stringify(f)})}};return{performance:function(a){e(c("performance"),"items",a)},error:function(a){e(c("error"),"errors",a)},log:function(a){e(c("log"),"log",a)}}}(),storage=function(){var db=global.localStorage?global.localStorage:void 0,remove=function(a){db&&db.removeItem(a)},get=function(a){return db?db.getItem(a)||"":""},set=function(a,b){db&&db.setItem(a,b)},getavr=function(key){var _set,_len,_sum,v=get(key),r=0;return v&&(_set=v.split(","),_len=_set.length,_sum=eval(_set.map(function(a){return parseInt(a)}).join("+")),r=Math.ceil(_sum/_len)),r},push=function(a,b){var c=get(a);c?set(a,c+","+b):set(a,b)},sum=function(a,b){var c=get(a);c?set(a,parseInt(c)+parseInt(b)):set(a,b)};return{get:get,set:set,sum:sum,push:push,remove:remove,getavr:getavr}}(),renderStat=function(){var a=!1,b=0,c=0,d=0,e=0,f="mig_report_loading",g=function(a){!a&&(a={});var b=a["min_height"]||250,c=a["height"]||document.documentElement.getBoundingClientRect()["height"];b>=c&&(storage.sum(keyMap.LOAD_PAGE_ERR[0],1),_config["errDetail"]&&reportToServer.log("[Empty_Page_Error]dom height"))},h=function(h){global["__loadStart"]&&(b=parseInt(global["__loadStart"])),global["__loadDOMEnd"]&&(c=parseInt(global["__loadDOMEnd"])),global["__loadWindowEnd"]&&(d=parseInt(global["__loadWindowEnd"])),global["__migLoadingError"]&&(storage.sum(keyMap.LOAD_PAGE_ERR[0],1),_config["errDetail"]&&reportToServer.log("[Empty_Page_Error]render status")),storage.set(f,"end"),_isOBJ(h)&&g(h),a=!0,e=getTimestamp(),throttle()&&setTimeout(function(){var a=0;a=c&&d&&e?Math.max(c-b,d-b,e-b):99999,a&&3e4>=a&&reportToServer.performance({loadTime:a+"ms"}),_config["metis"]&&_config["metis"]["_log"]("info",{__loadStart:b,__loadDOMEnd:c,__loadWindowEnd:d,__loadEnd:e},0,a,"renderEnd")},500)};return{renderEnd:function(b){!a&&!_config["autoReportLoadTime"]&&afterWindLoaded(h,b)},checkEmptyPage:g}}(),reqStat=function(){var a=[],b=function(){var b=Math.floor(1e5*Math.random());return a[b]=getTimestamp(),b},c=function(b){var c=a[b]||0,d=getTimestamp()-c;d>=1&&3e4>=d&&storage.push(keyMap.REQ_AJAX_TIME[0],d),delete a[b]},d=function(){storage.sum(keyMap.REQ_AJAX_ERR[0],1)},e=function(){storage.sum(keyMap.REQ_AJAX_FAIL[0],1)};return{reqStart:b,reqEnd:c,reqErr:d,reqFail:e}}(),loadStat=function(){return{loadErrJs:function(){storage.sum(keyMap.LOAD_JS_ERR[0],1)},loadErrCss:function(){storage.sum(keyMap.LOAD_CSS_ERR[0],1)},loadErrImg:function(){storage.sum(keyMap.LOAD_IMG_ERR[0],1)}}}(),errorStat=function(){var a=[],b=function(a){var b=a.stack?a.stack.replace(/\n/gi,"").split(/\bat\b/).slice(0,5).join("@").replace(/\?[^:]+/gi,""):"",c=a.toString();return b.indexOf(c)<0&&(b=c+"@"+b),b},c=!1,d=function(){if(!c){c=!0;var d=global.onerror;global.onerror=function(c,e,f,g,h){var i=c;h&&(i=b(h)),e&&(i+="@@url:"+e),f&&(i+="@@line:"+f),g&&(i+="@@col:"+g),a.push(i),storage.sum(keyMap.RUN_JS_ERR[0],1),d&&d.apply(global,arguments)}}},e=function(c){var d=b(c);a.push(d),storage.sum(keyMap.RUN_JS_ERR[0],1)};return{reportErr:e,getErrList:function(){return a},clearErrList:function(){a=[]},globalCapture:d}}();return global.setInterval(function(){var b,c,d,e,f,g,h,i,j,k,l,m,a=[];for(b in keyMap)"LOAD_TIME"!==b&&(c=0,c="REQ_AJAX_TIME"===b?storage.getavr(keyMap[b][0]):storage.get(keyMap[b][0]),parseInt(c)>0&&("REQ_AJAX_TIME"===b?throttle()&&reportToServer.performance({loadAjax:c+"ms"}):(d=getErrorCode(b),a.push({errorCode:d,cnt:c}))),storage.remove(keyMap[b][0]));if(reportToServer.error(a),_config["errDetail"]){for(e=errorStat.getErrList(),f="mig_monitor_"+location.host.replace(/\./g,"_"),g=[],h=0;h<e.length&&g.length<5;h++){for(i=e[h],j=!1,k=0;k<_config["ignore"].length;k++)if(l=_config["ignore"][k],_isOBJ(l,"RegExp")&&l.test(i)){j=!0;break}j||(m=[f,i].join(";"),g.push(m))}g.length&&reportToServer.log(g)}errorStat.clearErrList()},1e3*_config["delay"]),exports={setConfig:setConfig,reportErr:errorStat.reportErr,reqStart:reqStat.reqStart,reqEnd:reqStat.reqEnd,reqErr:reqStat.reqErr,reqFail:reqStat.reqFail,loadErrJs:loadStat.loadErrJs,loadErrCss:loadStat.loadErrCss,loadErrImg:loadStat.loadErrImg,renderEnd:renderStat.renderEnd,checkEmptyPage:renderStat.checkEmptyPage,log:reportToServer.log}}(),{setConfig:function(a){this["metis"]=new MigMetis(a),a["metis"]=this.metis,WebMonitor["setConfig"](a),this["monitor"]=WebMonitor}})}(window);

MIG_REPORT.setConfig({
    appId: 'navigationpage.card',
    delay: 60,
    errDetail: false,
    perPercentage: 1,
    globalCapture: false,
    ignore: []
});

var config = {
	cardId : 'unknown',
	log : {
		url : 'http://jsreport.html5.qq.com/navigationpage/log',
		guidArray : [
		],
		type : 4,
		level : 2
	}
};


;(function() {
    var ua = navigator.userAgent;
    var platform = "android";
    var USE_QB_BRIDGE_VERSION = 5.9;
    var useQBbridge = false;
    var isAndroid = /android/ig.test(ua) && /mqq/ig.test(ua);
    if (typeof mtt_navi !== "undefined") {
        isAndroid = true;
    }
    var isIos = /iphone|ipod/ig.test(ua) && /mqq/ig.test(ua);
    if (isIos) {
        platform = "ios";
    }
    var x5 = {
        commandQueue : [],
        commandQueueFlushing : false,
        resources: {
            base: !0
        }
    };

    x5.callbackId = 0;
    x5.callbacks = {};
    x5.callbackStatus = {
        NO_RESULT:0,
        OK:1,
        CLASS_NOT_FOUND_EXCEPTION:2,
        ILLEGAL_ACCESS_EXCEPTION:3,
        INSTANTIATION_EXCEPTION:4,
        MALFORMED_URL_EXCEPTION:5,
        IO_EXCEPTION:6,
        INVALID_ACTION:7,
        JSON_EXCEPTION:8,
        ERROR:9
    };

    x5.createBridge = function () {
        var bridge = document.createElement("iframe");
        bridge.setAttribute("style", "display:none;");
        bridge.setAttribute("height", "0px");
        bridge.setAttribute("width", "0px");
        bridge.setAttribute("frameborder", "0");
        document.documentElement.appendChild(bridge);
        return bridge;
    };

    x5.exec = function (suc, err, service, action, options) {
        var callbackId = null;
        var command = {
            className : service,
            methodName : action,
            options : {},
            arguments : []
        };

        if (suc || err) {
            callbackId = service + x5.callbackId++;
            x5.callbacks[callbackId] = {
                success : suc,
                fail : err
            };
        }

        if (callbackId != null) {
            command.arguments.push(callbackId);
        }

        for (var i = 0; i < options.length; ++i) {
            var arg = options[i];
            if (arg == undefined || arg == null) {
                continue;
            } else if (typeof(arg) == 'object') {
                command.options = arg;
            } else {
                command.arguments.push(arg);
            }
        }

        x5.commandQueue.push(JSON.stringify(command));
        if (x5.commandQueue.length >= 1 && !x5.commandQueueFlushing) {
            if (!x5.bridge) {
                x5.bridge = x5.createBridge();
            }
            x5.bridge.src = "mtt:" + service + ":" + action;
        }
    };

    // 浏览器调用接口
    x5.getAndClearQueuedCommands = function () {
        var json = JSON.stringify(x5.commandQueue);
        x5.commandQueue = [];
        return json;
    };

    // 浏览器执行成功的回调函数
    x5.callbackSuccess = function(callbackId, args) {
        if (x5.callbacks[callbackId]) {
            if (args.status === x5.callbackStatus.OK) {
                try {
                    if (x5.callbacks[callbackId].success) {
                        x5.callbacks[callbackId].success(args.message);
                    }
                } catch (e) {
                    console.log("Error in success callback: " + callbackId + " = " + e);
                }
            }
            if (!args.keepCallback) {
                delete x5.callbacks[callbackId];
            }
        }
    };

    // 浏览器执行失败的回调函数
    x5.callbackError = function (callbackId, args) {
        if (x5.callbacks[callbackId]) {
            try {
                if (x5.callbacks[callbackId].fail) {
                    x5.callbacks[callbackId].fail(args.message);
                }
            } catch (e) {
                console.log("Error in error callback: " + callbackId + " = " + e);
            }
            if (!args.keepCallback) {
                delete x5.callbacks[callbackId];
            }
        }
    };

    //以下为ios终端接口实现
    x5.ios = x5.ios || {};

    // option的格式如{flag：0}
    x5.ios.notifyImgLoadResult = function(flag, callback) {
        x5.exec(callback, null, "app", "notifyImgLoadResult", [{"flag": flag}]);
    };

    x5.ios.addNaviCard = function (options, succ, err) {
        x5.exec(succ, err, "app", "addNaviCard", [options]);
    };

    x5.ios.deleteNaviCard = function (options, succ, err) {
        x5.exec(succ, err, "app", "deleteNaviCard", [options]);
    };

    x5.ios.isNaviCardExist = function (options, succ, err) {
        x5.exec(succ, err, "app", "isNaviCardExist", [options]);
    };

    x5.ios.openCardPool = function (options, succ, err) {
        x5.exec(succ, err, "app", "openCardPool", [options]);
    };

    x5.ios.openNaviUrl = function (options, succ, err) {
        x5.exec(succ, err, "app", "openNaviUrl", [options]);
    };

    x5.ios.isSupportJSOpenUrl = function (succ, err) {
        x5.exec(succ, err, "app", "isSupportJSOpenUrl", []);
    };

    x5.ios.isNoPicStyle = function (succ, err) {
        x5.exec(succ, err, "app", "isNoPicStyle", []);
    };

    x5.ios.getHistory = function(options, succ, err) {
        x5.exec(succ, err, "video", "getHistory", [options]);
    };
    x5.ios.playEpisode = function(options, succ, err) {
        x5.exec(succ, err, "video", "playEpisode", [options]);
    };

    x5.ios.getNovelHistory = function(options, succ, err) {
        x5.exec(succ, err, "app", "getNovelHistory", [options]);
    };

    x5.ios.isNaviCardShowHistory= function (options, succ, err) {
        x5.exec(succ, err, "app", "isNaviCardShowHistory", [options]);
    };
    x5.ios.getSwitchStatus= function (options, succ, err) {
        x5.exec(succ, err, "app", "getSwitchStatus", [options]);
    };
    x5.ios.setNaviCardLocalStorage = function(options, succ, err) {
        x5.exec(succ, err, "app", "setNaviCardLocalStorage", [options]);
    };

    x5.ios.getNaviCardLocalStorage = function(options, succ, err) {
        x5.exec(succ, err, "app", "getNaviCardLocalStorage", [options]);
    };

    x5.ios.notifyNaviCardRelayout = function(succ, err) {
        x5.exec(succ, err, "app", "notifyNaviCardRelayout", []);
    };
    x5.ios.reportStatDatas = function(options, succ, err) {
        x5.exec(succ, err, "app", "reportStatDatas", [options]);
    };
    x5.ios.getBrowserParam = function (succ, err) {
        x5.exec(succ, err, "app", "getBrowserParam", []);
    };
    x5.ios.getGeoLocation = function (succ, err) {
        x5.exec(succ, err, "app", "getGeoLocation", []);
    };
    x5.ios.replaceNaviCard = function (options, succ, err) {
        x5.exec(succ, err, "app", "replaceNaviCard", [options]);
    };
    x5.ios.addRPTStatistics = function (options, succ, err) {
        x5.exec(succ, err, "app", "addRPTStatistics", [options]);
    };
    x5.ios.openSearchUrl = function (options, succ, err) {
        x5.exec(succ, err, "app", "openSearchUrl", [options]);
    };
	x5.ios.openTypeUrl = function(succ, err, options){
		x5.exec(succ, err, "app", "openTypeUrl", [options]);
	};
    
    window.T5Kit = {};
    for (var i in x5) {
        T5Kit[i] = x5[i];
    }
    // android相关的接口，5.7以上使用qb_bridge
    x5.android = x5.android || {};

    x5.android.addNaviCard = function(options, suc, err) {
        if (useQBbridge) {
            try {
                qb_bridge.exec(suc, err, "navicard", "addNaviCard", options);
            } catch(e) {
                err && err();
            }
        }else if (typeof mtt !== "undefined") {
            try {
                if (mtt.addNaviCard) {
                    mtt.addNaviCard(JSON.stringify(options), suc + "", err + "");
                }
            } catch(e) {
                err && err();
            }
        } else {
            err && err();
        }
    };

    x5.android.deleteNaviCard = function(options, suc, err) {
        if (useQBbridge) {
            try {
                qb_bridge.exec(suc, err, "navicard", "deleteNaviCard", options);
            } catch(e) {
                err && err();
            }
        }else if (typeof mtt !== "undefined") {
            try {
                if (mtt.deleteNaviCard) {
                    mtt.deleteNaviCard(JSON.stringify(options), suc + "", err + "");
                }
            } catch(e) {
                err && err();
            }
        } else {
            err && err();
        }
    };

    x5.android.isNaviCardExist = function(options, suc, err) {
        if (useQBbridge) {
            try {
                qb_bridge.exec(function(opt) {
                    suc(opt.result);
                }, err, "navicard", "isNaviCardExist", options);
            } catch(e) {
                err && err();
            }
        }else if (typeof mtt !== "undefined") {
            try {
                if (mtt.isNaviCardExist) {
                    mtt.isNaviCardExist(JSON.stringify(options), suc + "", err + "");
                }
            } catch(e) {
                err && err();
            }
        } else {
            err && err();
        }
    };

    x5.android.openCardPool = function(options, suc, err) {
        if (useQBbridge) {
            try {
                qb_bridge.exec(suc, err, "navicard", "openCardPool", options);
            } catch(e) {
                err && err();
            }
        } else if (typeof mtt !== "undefined") {
            try
            {
                if (mtt.openCardPool) {
                    mtt.openCardPool(JSON.stringify(options), suc + "", err + "");
                }
            } catch(e) {
                err && err();
            }
        } else {
            err && err();
        }
    };
    // 图片加载接口
    x5.android.notifyImgLoadResult = function(flag, callback) {
        var ret = -1;
        if (useQBbridge) {
            try {
                qb_bridge.exec(suc, err, "navicard", "notifyImgLoadResult", "");
                ret = 0
            } catch(e) {
                ret = -1;
            }
        } else if (typeof mtt_navi !== "undefined") {
            try {
                mtt_navi.notifyImgLoadResult(flag, callback + "");
                ret = 0;
            } catch(e) {
                ret = -1;
            }
        } else {
            ret =  -1;
        }
        return ret;
    };

    /*
     options
     {
     url: url信息，
     rpt: rpt信息
     }
     */
    x5.android.openNaviUrl = function(options, suc, err) {
        if (useQBbridge) {
            try {
                qb_bridge.exec(suc, err, "navicard", "openNaviUrl", options);
            } catch(e) {
                err && err();
            }
        } else if (typeof mtt_navi !== "undefined") {
            try {
                if (mtt_navi.openNaviUrl) {
                    mtt_navi.openNaviUrl(JSON.stringify(options), suc + "", err + "");
                }
            } catch(e) {
                err && err();
            }
        } else {
            err && err();
        }
    };

    /**
     * 是否支持打开URL
     */
    x5.android.isSupportJSOpenUrl = function() {
        var ret = false;
        if (useQBbridge) {
            try {
                ret = qb_bridge.exec(null, null, "navicard", "isSupportJSOpenUrl", "");
                ret = ret == "1" ? true : false; //5.7返回字符串
            } catch(e) {
                ret = false;
            }
        } else if (typeof mtt_navi !== "undefined") {
            try {
                if (mtt_navi.isSupportJSOpenUrl) {
                    ret = mtt_navi.isSupportJSOpenUrl();
                }
            } catch(e) {
                ret = false;
            }
        }
        return ret;
    };

    x5.android.isNoPicStyle = function() {
        var ret = false;
        if (useQBbridge) {
            try {
                ret = qb_bridge.exec(null, null, "navicard", "isNoPicStyle", "");
            } catch(e) {
                ret = false;
            }
        } else if (typeof mtt_navi !== "undefined") {
            try {
                if (mtt_navi.isNoPicStyle) {
                    ret = mtt_navi.isNoPicStyle();
                }
            } catch(e) {
                ret = false;
            }
        }
        return ret;
    };
    //终端还未完成5.7新接口
    x5.android.playEpisode = function(options, suc, err) {
        if (useQBbridge) {
            try {
                qb_bridge.exec(null, null, "navicard", "getVideoHistory", options);
            } catch(e) {
                err && err();
            }
        } else if (x5mtt) {
            if (x5mtt.playEpisode) {
                var result = x5mtt.playEpisode(options);
                suc && suc(result);
            }
        } else{
            err && err();
        }
    };

    x5.android.getHistory = function(options, succ, fail) {
        if (useQBbridge) {
            try {
                var result = qb_bridge.exec(null, null, "navicard", "getVideoHistory", options);
                succ && succ(result);
            } catch(e) {
                fail && fail();
            }
        } else if (x5mtt) {
            if (x5mtt.getHistory) {
                var result = x5mtt.getHistory(options);
                succ && succ(result);
            }
        } else{
            fail && fail();
        }
    };

    x5.android.getNovelHistory = function(options, succ, fail) {
        if (useQBbridge) {
            try {
                var result = qb_bridge.exec(null, null, "navicard", "getNovelHistory", options);
                succ && succ(result);
            } catch(e) {
               fail && fail();
            }
        } else if (x5mtt) {
            if (x5mtt.getNovelHistory) {
                var result = x5mtt.getNovelHistory(options);
                succ && succ(result);
            }
        } else{
            fail && fail();
        }
    };

    x5.android.setNaviCardLocalStorage = function(options, suc, err) {
        if (useQBbridge) {
            try {
                qb_bridge.exec(suc, err, "navicard", "setNaviCardLocalStorage", options);
            } catch(e) {
                err && err();
            }
        } else if (typeof mtt_navi !== "undefined") {
            try {
                if (mtt_navi.setNaviCardLocalStorage) {
                    mtt_navi.setNaviCardLocalStorage(JSON.stringify(options), suc + "", err + "");
                }
            } catch(e) {
                err && err();
            }
        } else {
            err && err();
        }
    };
    x5.android.reportStatDatas = function(options, suc, err){
        if (useQBbridge) {
            try {
                qb_bridge.exec(suc, err, "navicard", "reportStatDatas", options);
            } catch(e) {
                err && err();
            }
        } else if (typeof mtt_navi !== "undefined") {
            try {
                if(mtt_navi.reportStatDatas) {
                    mtt_navi.reportStatDatas(JSON.stringify(options), suc + "", err + "");
                }
            } catch(e) {
                err && err();
            }
        } else {
            err && err();
        }
    };
    x5.android.getNaviCardLocalStorage = function(options, suc, err){
        if (useQBbridge) {
            try {
                var result = qb_bridge.exec(null, null, "navicard", "getNaviCardLocalStorageSync", options);
                if (typeof result == "string") {
                    result = JSON.parse(result);
                }
                if (result) {
                    suc && suc(result.result[0]);
                } else {
                    suc && suc(result);
                }
                
            } catch(e) {
                err && err();
            }
        } else if (typeof mtt_navi !== "undefined") {
            try {
                if (mtt_navi.getNaviCardLocalStorage) {
                    var result = mtt_navi.getNaviCardLocalStorage(JSON.stringify(options));
                    if(typeof result == "string"){
                        result = JSON.parse(result);
                    }
                    suc && suc(result);
                }
            } catch(e) {
                err && err();
            }
        } else {
            err && err();
        }
    };

    x5.android.notifyNaviCardRelayout = function(){
        if (useQBbridge) {
            try {
                qb_bridge.exec(null, null, "navicard", "notifyNaviCardRelayout", "");
            } catch(e) {
               
            }
        } else if (typeof mtt_navi !== "undefined") {
            try {
                if (mtt_navi.notifyNaviCardRelayout) {
                    mtt_navi.notifyNaviCardRelayout();
                }
            } catch(e) {
            }
        }
    };

    x5.android.isNaviCardShowHistory = function(options, suc, err){
        if (useQBbridge) {
            try {
                qb_bridge.exec(suc, err, "navicard", "isNaviCardShowHistory", options);
            } catch(e) {
                err && err();
            }
        } else if (typeof mtt_navi !== "undefined") {
            try {
                if (mtt_navi.isNaviCardShowHistory) {
                    mtt_navi.isNaviCardShowHistory(JSON.stringify(options), suc + "", err + "");
                }
            } catch(e) {
                err && err();
            }
        } else {
            err && err();
        }
    };
    //5.7以后才有同步异步只说，5.7以上默认为同步加载，也可调用异步接口getSwitchStatusASync
    x5.android.getSwitchStatusSync = function(options, suc, err) {
        if (useQBbridge) {
            try {
                var result = qb_bridge.exec(null, null, "navicard", "getSwitchStatusSync", options);
                if (typeof result == "string") {
                    result = JSON.parse(result);
                }
                suc && suc(result);
            } catch(e) {
                err && err();
            }
        } else if (typeof mtt_navi !== "undefined") {
            try {
                if (mtt_navi.getSwitchStatus) {
                    var result = mtt_navi.getSwitchStatus(JSON.stringify(options));
                    if (typeof result == "string") {
                        result = JSON.parse(result);
                    }
                    suc && suc(result);
                }
            } catch(e) {
                err && err();
            }
        } else {
            err && err();
        }
    };
    //只在5.7以上有效，5.7以下异步有bug，故没有使用
    x5.android.getSwitchStatusASync = function(options, suc, err){
        if (useQBbridge) {
            try {
                qb_bridge.exec(suc, err, "navicard", "getSwitchStatusASync", options);
            } catch(e) {
                err && err();
            }
        }else {
            err && err();
        }
    };
    //终端还未完成5.7新接口
    x5.android.getGeoLocation = function(suc, err) {
        if (useQBbridge) {
            try {
                qb_bridge.exec(suc, err, "device", "geolocation", "");
            } catch(e) {
                err && err();
            }
        } else if (typeof x5mtt !== "undefined") {
            try {
                if (x5mtt.getGeolocation) {
                    var options = JSON.stringify({
                        cbSuccess : suc.name + "",
                        cbError : err.name + ""
                    });
                    x5mtt.getGeolocation(options);
                }
            } catch(e) {
                err && err();
            }
        } else {
            err && err();
        }
    };

    x5.android.replaceNaviCard = function(options, suc, err) {
        if (useQBbridge) {
            try {
                qb_bridge.exec(suc, err, "navicard", "replaceNaviCard", options);
            } catch(e) {
                err && err();
            }
        } else if (typeof mtt_navi !== "undefined") {
            try {
                if (mtt_navi.replaceNaviCard) {
                    mtt_navi.replaceNaviCard(JSON.stringify(options), suc + "", err + "");
                }
            } catch(e) {
                err && err();
            }
        } else {
            err && err();
        }
    };

    x5.android.addRPTStatistics = function(options, suc, err) {
        if (useQBbridge) {
            try {
                qb_bridge.exec(suc, err, "navicard", "addRPTStatistics", options);
            } catch(e) {
                err && err();
            }
        } else if (typeof mtt_navi !== "undefined") {
            try {
                if (mtt_navi.addRPTStatistics) {
                    mtt_navi.addRPTStatistics(JSON.stringify(options), suc + "", err + "");
                }
            } catch(e) {
                err && err();
            }
        } else {
            err && err();
        }
    };
    x5.android.openSearchUrl = function(options, suc, err) {
        if (useQBbridge) {
            try {
                qb_bridge.exec(suc, err, "navicard", "openSearchUrl", options);
            } catch(e) {
                err && err();
            }
        } else if (typeof mtt_navi !== "undefined") {
            try {
                if (mtt_navi.openSearchUrl) {
                    mtt_navi.openSearchUrl(JSON.stringify(options), suc + "", err + "");
                }
            } catch(e) {
                err && err();
            }
        } else {
            err && err();
        }
    };
    
    x5.android.getSkinTheme = function() {
        var ret = -1;
        if (useQBbridge) {
            try {
                ret = qb_bridge.exec(null, null, "navicard", "getSkinTheme", "");
                ret = parseInt(ret);
            } catch(e) {
                
            }
        } else if (typeof mtt_navi !== "undefined") {
            if (mtt_navi.getSkinTheme) {
                ret = mtt_navi.getSkinTheme();
            }
        }
        return ret;
    };
    
    x5.android.getYiJiaSkinTheme = function() {
        var ret = -1;
        if (qbVersion >= 5.7 && typeof qb_bridge !== "undefined") {
            try {
                ret = qb_bridge.exec(null, null, "navicard", "getYiJiaSkinTheme", "");
                ret = parseInt(ret);
            } catch(e) {
                
            }
        } else if (typeof mtt_navi !== "undefined") {
            if (mtt_navi.getYiJiaSkinTheme) {
                ret = mtt_navi.getYiJiaSkinTheme();
            }
        }
        return ret;
    };

    x5.android.isNightSkin = function() {
        var ret = false;
        if (useQBbridge) {
            try {
                ret = qb_bridge.exec(null, null, "navicard", "isNightSkin", "");
                ret = ret == "1" ? true : false;
            } catch(e) {
            }
        } else if (typeof mtt_navi !== "undefined") {
            if (mtt_navi.isNightSkin) {
                ret = mtt_navi.isNightSkin();
            }
        }
        return ret;
    };
    
    x5.android.onGetOffsetHeight = function(height) {
        var ret = false;
        if (useQBbridge) {
            try {
                ret = qb_bridge.exec(null, null, "navicard", "onGetOffsetHeight", {"height" : height});
            } catch(e) {
            }
        } else if (typeof mtt_navi !== "undefined") {
            if (mtt_navi.onGetOffsetHeight) {
                ret = mtt_navi.onGetOffsetHeight(height);
            }
        }
        return ret;
    };
	
	x5.android.openTypeUrl = function(suc, err, options){
		if (useQBbridge) {
            try {
                qb_bridge.exec(suc, err, "navicard", "openTypeUrl", options);
            } catch(e) {
                err && err();
            }
        } else if (typeof mtt_navi !== "undefined") {
            try {
                if (mtt_navi.openTypeUrl) {
                    mtt_navi.openTypeUrl(JSON.stringify(options), suc + "", err + "");
                }
            } catch(e) {
                err && err();
            }
        } else {
            err && err();
        }
	};
    
    x5.addNaviCard = function(options, suc, err) {
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                x5.android.addNaviCard(options, suc, err);
            } else {
                x5.ios.addNaviCard(options, suc, err);
            }
        }
    };

    x5.deleteNaviCard = function(options, suc, err) {
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                x5.android.deleteNaviCard(options, suc, err);
            } else {
                x5.ios.deleteNaviCard(options, suc, err);
            }
        }
    };

    x5.isNaviCardExist = function(options, suc, err) {
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                x5.android.isNaviCardExist(options, suc, err);
            } else {
                x5.ios.isNaviCardExist(options, suc, err);
            }
        }
    };

    x5.openCardPool = function(options, succ, err) {
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                x5.android.openCardPool(options, succ, err);
            } else if (isIos) {
                x5.ios.openCardPool(options, succ, err);
            }
        }
    };
    /**
     * 图片加载
     * @param flag 0: 标识加载成功， 1：标识加载失败
     * @param callback 供浏览器调起的接口
     */
    x5.notifyImgLoadResult = function(flag, callback) {
        if (!isIos && !isAndroid) {
            return -1;
        } else {
            if (isAndroid) {
                return x5.android.notifyImgLoadResult(flag, callback);
            } else if (isIos) {
                x5.ios.notifyImgLoadResult(flag, callback);
                return 0;
            }
        }
    };

    /**
     *
     * @param options {
     *  url:
     *  rpt:
     * }
     * @param suc
     * @param err
     */
    x5.openNaviUrl = function(options, suc, err){
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                x5.android.openNaviUrl(options, suc, err);
            } else if (isIos) {
                x5.ios.openNaviUrl(options, suc, err);
            }
        }
    };

    /**
     * 是否支持打开URL
     * @param suc 支持
     * @param err 不支持
     */
    x5.isSupportJSOpenUrl = function(suc, err){
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                var ret = x5.android.isSupportJSOpenUrl();
                if (ret) {
                    suc && suc();
                } else {
                    err && err();
                }
            } else {
                x5.ios.isSupportJSOpenUrl(suc, err);
            }
        }
    };

    x5.isNoPicStyle = function(suc, err) {
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                var ret = x5.android.isNoPicStyle();
                ret = ret == "1" ? true : false; //5.7只能返回字符串，不能返回数值型或者布尔型
                if (ret) { 
                    suc && suc();
                } else {
                    err && err();
                }
            } else {
                x5.ios.isNoPicStyle(suc, err);
            }
        }
    };

    x5.getHistory = function(paramString, succ, fail) {
        if (!isIos && ! isAndroid) {
            succ && succ(JSON.stringify({
                history: [],
                total: - 1
            }));
        } else {
            if (isAndroid) {
                x5.android.getHistory(paramString, succ, fail);
            } else if (isIos) {
                x5.ios.getHistory(paramString, succ, fail);
            }
        }
    };

    x5.playEpisode = function(paramString, succ, err) {
        if (!isIos && ! isAndroid) {
            return;
        } else {
            if (isAndroid) {
                x5.android.playEpisode(paramString, succ, err);
            } else {
                x5.ios.playEpisode(paramString, succ, err);
            }
        }
    };

    x5.isNaviCardShowHistory = function(options, suc, err){
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                x5.android.isNaviCardShowHistory(options, suc, err);
            } else {
                x5.ios.isNaviCardShowHistory(options, suc, err);
            }
        }
    };
    x5.getSwitchStatus = function(options, suc, err){
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                x5.android.getSwitchStatusSync(options, suc, err);
            } else {
                x5.ios.getSwitchStatus(options, suc, err);
            }
        }
    };
    x5.getNovelHistory = function(options, succ, fail) {
        if (!isIos && ! isAndroid) {
            succ && succ(JSON.stringify({
                history: [],
                total: - 1
            }));
        } else {
            if (isAndroid) {
                x5.android.getNovelHistory(options, succ, fail);
            } else {
                x5.ios.getNovelHistory(options, succ, fail);
            }
        }
    };

    x5.setNaviCardLocalStorage = function(options, suc, err) {
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                x5.android.setNaviCardLocalStorage(options, suc, err);
            } else {
                x5.ios.setNaviCardLocalStorage(options, suc, err);
            }
        }
    };

    x5.getNaviCardLocalStorage = function(options, suc, err){
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                x5.android.getNaviCardLocalStorage(options, suc, err);
            } else {
                x5.ios.getNaviCardLocalStorage(options, suc, err);
            }
        }
    };

    x5.getBrowserParam = function(succ, err){
        if (!isIos && !isAndroid) {
            err && err();
        }
        else{
            if (isAndroid) {
                var para = mtt.getBrowserParam();
                if (para && para != "") {
                    succ && succ(para);
                } else{
                    err && err();
                }
            } else{
                x5.ios.getBrowserParam(succ, err);
            }
        }
    };

    x5.notifyNaviCardRelayout = function() {
        if (!isAndroid && !isIos) {
        } else {
            if (isAndroid) {
                x5.android.notifyNaviCardRelayout();
            } else {
                x5.ios.notifyNaviCardRelayout();
            }
        }
    };

    x5.reportStatDatas = function(options, suc, err) {
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                x5.android.reportStatDatas(options, suc, err);
            } else {
                x5.ios.reportStatDatas(options, suc, err);
            }
        }
    };

    x5.getGeoLocation = function(suc, err) {
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                x5.android.getGeoLocation(suc, err);
            } else {
                x5.ios.getGeoLocation(suc, err);
            }
        }
    };

    x5.replaceNaviCard = function(options, suc, err) {
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                x5.android.replaceNaviCard(options, suc, err);
            } else {
                x5.ios.replaceNaviCard(options, suc, err);
            }
        }
    };
    x5.addRPTStatistics = function(options, suc, err) {
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                x5.android.addRPTStatistics(options, suc, err);
            } else {
                x5.ios.addRPTStatistics(options, suc, err);
            }
        }
    };
    x5.openSearchUrl = function(options, suc, err) {
        if (!isAndroid && !isIos) {
            err && err();
        } else {
            if (isAndroid) {
                x5.android.openSearchUrl(options, suc, err);
            } else {
                x5.ios.openSearchUrl(options, suc, err);
            }
        }
    };
	// 点击按钮通知终端更新图标
	x5.openTypeUrl = function(suc, err, options) {
        if (!isAndroid && !isIos) {
            err && err();
        } else {
			if(qbVersion >= 6.1){
				if (isAndroid) {
					x5.android.openTypeUrl(suc, err, options);
				} else {
					x5.ios.openTypeUrl(suc, err, options);
				}
			}
        }
    };
        
    //以下为Android5.7以上接口用到，原有的mtt有闭包bug，5.7以后使用qb_bridge实现。
    var qbVersion = -1;
    if (isAndroid) {
        var ver = ua.match(/mqqbrowser\/([0-9\.]+)/i);
        if (ver && ver.length) {
            qbVersion = ver[1];
        }
        if (qbVersion >= USE_QB_BRIDGE_VERSION) {//5.x
            if (window.qb_bridge == undefined) {
               window.qb_bridge = {
                    nativeExec : function(service, action, callbackId, argsJson) {
                        return prompt(argsJson, 'mtt:[' + [service, action, callbackId] + ']');
                    }
                }; 
            }
            var qbCallbackId = 0;
            var callbacks = {};

            qb_bridge.exec = function(success, fail, service, action, args) {
                var callbackId = service + qbCallbackId++;
                var argsJson = args == null ? "" 
                    : (typeof args == "object" ? JSON.stringify(args) : args + "");

                if (success || fail) {
                    callbacks[callbackId] = {success: success, fail: fail};
                }
                var exception = null;
                var ret = null;
                try {
                    ret = qb_bridge.nativeExec(service, action, callbackId, argsJson);
                } catch(e) {
                    exception = e;
                }
                
                return ret;
            };

            qb_bridge.callbackFromNative = function(callbackId, args) {
                var callback = callbacks[callbackId];
                var argsJson = JSON.parse(args);
                if (callback) {
                    if (argsJson.succ) {
                        callback.success && callback.success(argsJson.msg);
                    } else {
                        callback.fail && callback.fail(argsJson.msg);
                    }

                    if (!argsJson.keep) {
                        delete callbacks[callbackId];
                    }
                }
            };
        }
    } else if (!isIos) {// pc
        window.qb_bridge = {
            nativeExec : function(service, action, callbackId, argsJson) {
                return null;
            }
        };  
    }
   
    x5.getQBVersion = function() {
        return qbVersion;
    };
    
    x5.getPlatform = function() {
        return platform;
    }
    
    x5.isUseQBbridge = function() {
        return useQBbridge;
    }
    
    if (isAndroid) {
        if (qbVersion >= USE_QB_BRIDGE_VERSION) {
            var ret = qb_bridge.exec(null, null, "qb", "appVersion", null);
            if (ret) {
                qbVersion = (ret.match(/^\d\.\d/))[0];
            }
        }
        if (qbVersion >= USE_QB_BRIDGE_VERSION && typeof qb_bridge !== "undefined") {
            useQBbridge = true;
        }
    } else if (isIos) {
        x5.ios.getBrowserParam(function(info) {
            if (info) {
                try {
                    info = JSON.parse(info);
                    var qua = info.qua + "";
                    qua = qua.match(/([0-9\.]+)/ig, '');
                    if (qua && qua.length > 0) {
                        if (qua[0] && qua[0].length == 2) {
                            qbVersion = qua[0][0] + "." + qua[0][1];
                        };
                        
                    }
                } catch(e) {
                    
                }
            }
        }, function() {
        });
    }   
    window.x5 = x5;
})();