;(
    function(){
        var ua = navigator.userAgent;
        var isAndroid = /android/ig.test(ua) && /mqq/ig.test(ua);
        if(typeof mtt_navi !== "undefined")
        {
            isAndroid = true;
        }
        var isIos = /iphone|ipod/ig.test(ua) && /mqq/ig.test(ua);

        function getUA()
        {
            if(isAndroid)
            {
                var info = x5.android.getBrowserParam();
                if(info)
                {
                    info = eval('(' + info + ')');
                    var qua = info.qua + "";
                    qua = qua.match(/([0-9\.]+)/ig, '');
                    if(qua && qua.length > 0)
                        return qua[0];
                }
            }
            try
            {
                var ua = navigator.userAgent;
                var reg = /MQQBrowser\/(\d{2})/;
                var regRemoveDot = /\./g;
                ua =  ua.replace(regRemoveDot,'');
                var res = reg.exec(ua);
                if(res && res.length > 1)
                {
                    return res[1];
                }
                return undefined;
            }
            catch(e)
            {
                return undefined;
            }
        }

        var x5 = {
            commandQueue:[],
            commandQueueFlushing:false,
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

        x5.exec = function (successCallback, errorCallback, service, action, options) {

            var callbackId = null;
            var command = {
                className:service,
                methodName:action,
                options:{},
                arguments:[]
            };

            if (successCallback || errorCallback) {
                callbackId = service + x5.callbackId++;
                x5.callbacks[callbackId] = {
                    success:successCallback,
                    fail:errorCallback
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
        x5.callbackSuccess = function (callbackId, args) {
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

        x5.ios = x5.ios || {};

        // option的格式如{flag：0}
        x5.ios.notifyImgLoadResult = function(flag, callback)
        {
            function err()
            {

            }
            x5.exec(callback, err, "app", "notifyImgLoadResult", [{"flag": flag}]);
        };
		
		x5.ios.openNaviUrl = function (options, succCallback, errCallback) {
            x5.exec(succCallback, errCallback, "app", "openNaviUrl", [options]);
        };
		
		x5.ios.isSupportJSOpenUrl = function (succCallback, errCallback) {
            x5.exec(succCallback, errCallback, "app", "isSupportJSOpenUrl", []);
        };

        // android相关的接口
        x5.android = x5.android || {};

        // 图片加载接口
        x5.android.notifyImgLoadResult = function(flag, callback)
        {
            var ret = -1;
            if (typeof mtt_navi !== "undefined")
            {
                try
                {
                    mtt_navi.notifyImgLoadResult(flag, callback + "");
                    ret = 0;
                }
                catch(e)
                {
                    ret = -1;
                }
            }
            else
            {
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
        x5.android.openNaviUrl = function(options, suc, err){
            if (typeof mtt_navi !== "undefined")
            {
                try
                {
                    if(mtt_navi.openNaviUrl)
                    {
                        mtt_navi.openNaviUrl(JSON.stringify(options), suc + "", err + "");
                    }
                }
                catch(e)
                {
                    err && err();
                }
            }
            else
            {
                err && err();
            }
        };
		
		 /**
         * 是否支持打开URL
         */
        x5.android.isSupportJSOpenUrl = function(){
            var ret = false;
            if (typeof mtt_navi !== "undefined")
            {
                try
                {
                    if(mtt_navi.isSupportJSOpenUrl)
                    {
                        ret = mtt_navi.isSupportJSOpenUrl();
                    }
                }
                catch(e)
                {
                    ret = false;
                }
            }
            return ret;
        };

        // 旧的版本采用T5Kit,新的版本采用x5(协议)
        window.T5Kit = {};
        for(var i in x5)
        {
            T5Kit[i] = x5[i];
        }

        /**
         * 图片加载
         * @param flag 0: 标识加载成功， 1：标识加载失败
         * @param callback 供浏览器调起的接口
         */
        x5.notifyImgLoadResult = function(flag, callback){
            if(!isIos && !isAndroid)
            {
                return -1;
            }
            else
            {
                if(isIos){
                    x5.ios.notifyImgLoadResult(flag, callback);
                    return 0;
                }
                else
                {
                    return x5.android.notifyImgLoadResult(flag, callback);
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
            if(!isAndroid && !isIos) // 不是qq浏览器
            {
                err && err();
            }
            else
            {
                if(isAndroid)
                {
                    x5.android.openNaviUrl(options, suc, err);
                }
                else
                {
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
            if(!isAndroid && !isIos) // 不是qq浏览器
            {
                err && err();
            }
            else
            {
                if(isAndroid)
                {
                    var ret = x5.android.isSupportJSOpenUrl();
                    if(ret)
                    {
                        suc && suc();
                    }
                    else
                    {
                        err && err();
                    }
                }
                else
                {
                    x5.ios.isSupportJSOpenUrl(suc, err);
                }
            }
        };

        x5.getQQBrowerVer = getUA;

        window.x5 = x5;
    }
)();
var MTT_PARAMS="{'qua':'APQB50_GA/500650&X5MTT_3/025006&AP&508013&77226&9326&V3'}";
var MOVE_THRESHOLD = 5;
var OVER_TIME=0;
var LONG_TAP=500;
var ACTIVE_DELAY=100;
var actTimer;
var cur = null; 
var actObj = null;
var actType = 0; // 0 -- color 1 -- mask 2 -- navbar
var loadError = false;
var loadnum = 0;
var start_time = 0;
var time_delay = 400;
var isOpenUrl = false;
var isMove = false;
var delayTimer;
var imgFirstTimer;
var imgNextTimer;
var isSuportOpenUrl = false;
var navbarIndex = 0;

var nightStyle=null; 
function addStyle(stylePath) {
    if(nightStyle==null){
                var container = document.getElementsByTagName("head")[0];
                nightStyle = document.createElement("link");
                nightStyle.rel = "stylesheet";
                nightStyle.type = "text/css";
                nightStyle.media = "screen";
                nightStyle.href = stylePath;
                container.appendChild(nightStyle);
    }
 }
 function getStyle(stylePath){
    var links = document.getElementsByTagName("link");
    for(var i=0;i<links.length;i++){
      if(links[i].href == stylePath){
          return links[i];
      }
    }
    return null;
 }
function removeStyle(stylePath){
    if(nightStyle!=null){
      nightStyle.parentNode.removeChild(nightStyle);
      nightStyle=null;
    }
}            

function setNightTheme(){
    addStyle("night.css") ;
    window.sessionStorage.setItem('theme','night');
}

function setDayTheme(){
    removeStyle("night.css") ;
    window.sessionStorage.setItem('theme','day');
}

function restoreTheme(){
    if(window.sessionStorage.getItem('theme') == "night"){
      setNightTheme();
    } else if(typeof(mtt_navi) != "undefined" && mtt_navi.isNightSkin()){
      setNightTheme();
    }
}
function loadImage(img,url){
    var pNode=img.parentNode;
    function handleFail() {
        // kill previous error handlers
        this.onload = this.onabort = this.onerror = function() {};
		loadError = true;
    }
    //img.parentNode.removeChild(img);
    img.onerror = img.onabort = handleFail;
    img.onload = function() {
        //pNode.appendChild (img);
    }
    img.src = url;
    
    return(img);
}

function removeClass(c,className){
    var str = c.getAttribute("class");
    if(!str)str="";
    var splitor = " ";
    var strs = str.split(splitor);
    str="";
    for(var i=0;i<strs.length;i++){
        if(strs[i] !=className){
            str +=strs[i] +splitor;
        }
    }
    if(str.length >0)str = str.substring(0,str.length -splitor.length);
    str = str.trim();
    if(str =="") c.removeAttribute("class");
    else c.setAttribute("class",str);
}

function addClass(c,className){
    var str = c.getAttribute("class");
    if(!str)str="";
    var splitor = " ";
    var strs = str.split(splitor);
    var found=false;
    for(var i=0;i<strs.length;i++){
        if(strs[i] ==className){
            found=true;
            break;
        }
    }
    if(!found)str=str+" "+className;
    str = str.trim();
    c.setAttribute("class",str);
}

function makeupPmsJumpURLForInterface() {
	  var as = document.getElementsByClassName("302_Jump");
  for(var i=0; i<as.length; i++){
    var a = as[i];
    if(a.getAttribute("p_id")){
      a.href = "http://p.mb.qq.com/h?id="+a.getAttribute("p_id")+"&type="+a.getAttribute("p_type")+"&b="+a.getAttribute("p_b")+"&d="+a.getAttribute("p_d")+"&u="+encodeURIComponent(a.href)+"&f="+a.getAttribute("p_f");
    }
  }
}

function isSupportJSOpenUrlSuc()
{
    isSuportOpenUrl = true;
    for (var list = document.querySelectorAll("A"), index = 0; index < list.length; ++index) {
			list[index].addEventListener("click", function(c) {
				c.preventDefault()
			}, !1);
	  }
}

function isSupportJSOpenUrlErr()
{
    isSuportOpenUrl = false;
    for (var list = document.querySelectorAll("IMG"), index = 0; index < list.length; ++index) {
		var aObj = null;
		if (list[index].parentNode && (list[index].parentNode.tagName=="SPAN"))
		{
			if (list[index].parentNode.parentNode)
			{
				aObj = list[index].parentNode.parentNode;
			}
		} 
		else
		{
			if (list[index].parentNode)
			{
				aObj = list[index].parentNode;
			}
		}
		if (aObj)
		{
			aObj.addEventListener("click", function(c) {
				c.preventDefault()
			}, !1);
		}
	  }
	  
	  for (var list = document.querySelectorAll(".book-title"), index = 0; index < list.length; ++index) {
		if (list[index].parentNode && list[index].parentNode.parentNode)
		{
			var aObj = list[index].parentNode.parentNode;
			aObj.addEventListener("click", function(c) {
				c.preventDefault();
			}, !1);
		}
	  }
}
/*******************************终端调用函数begin********************************************************************************/
/**************激活点击态，在长按按钮时调用********************/
function setActive(obj){
    if(cur!=null)
	{
        if(cur.tagName=="A")
		{
			if (cur.parentNode)
			{
				addClass(cur.parentNode, "active");
			}
		}
		else if(cur.tagName=="IMG")
		{
			if (cur.parentNode.getAttribute("class")=="img-wrap")
			{
				addClass(cur.parentNode.parentNode, "active");
				if(cur.parentNode.getElementsByClassName('active-mask').length != 0)
				{
				    cur.parentNode.getElementsByClassName('active-mask')[0].style.display = 'block';
				}
			}
			//news pic
			else
			{
			    addClass(cur.parentNode, "active");
				if(cur.parentNode.getElementsByClassName('active-mask').length != 0)
				{
				    cur.parentNode.getElementsByClassName('active-mask')[0].style.display = 'block';
				}
			}
		} 
		else if (cur.tagName=="SPAN" && (cur.getAttribute("class")=="title" || cur.getAttribute("class")=="reply-num"))
		{
			if (cur.parentNode && cur.parentNode.parentNode)
			{
				addClass(cur.parentNode.parentNode, "active");
			}
		}	
    }
}

/**************去激活点击态，在长按按钮放开后调用********************/
function setDeactive(obj){
    if(cur!=null)
	{
        if(cur.tagName=="A")
		{
			if (cur.parentNode)
			{
				removeClass(cur.parentNode, "active");
			}
			cur = null;
		}
		else if(cur.tagName=="IMG")
		{
            if (cur.parentNode.getAttribute("class")=="img-wrap")
			{
				removeClass(cur.parentNode.parentNode, "active");
			    if(cur.parentNode.getElementsByClassName('active-mask').length != 0)
				{
				    cur.parentNode.getElementsByClassName('active-mask')[0].style.display = 'none';
				}
			}
		    //news pic
			else
			{
			    removeClass(cur.parentNode, "active");
				if(cur.parentNode.getElementsByClassName('active-mask').length != 0)
				{
				    cur.parentNode.getElementsByClassName('active-mask')[0].style.display = 'none';
				}
			}
            cur = null;
		}
		
		else if (cur.tagName=="SPAN" && (cur.getAttribute("class")=="title" || cur.getAttribute("class")=="reply-num"))
		{
			if (cur.parentNode && cur.parentNode.parentNode)
			{
				removeClass(cur.parentNode.parentNode, "active");
			}
			cur = null;
		}	
    }
}

window.addEventListener("DOMContentLoaded", event_init, !1);

function event_init() 
{
    restoreTheme();
    document.body.style.display = 'block';
}

function init(){
  makeupPmsJumpURLForInterface();
  //restoreTheme();
  var t=document.body;//document.getElementById("content");
  //stop img defult event
  isSuportOpenUrl = false;
  x5.isSupportJSOpenUrl(isSupportJSOpenUrlSuc, isSupportJSOpenUrlErr);

  t.addEventListener("touchstart", function(e) {
          isMove = false;
          start_time = Date.now();
          if (actTimer != null)
		  {
		      clearTimeout(actTimer);
			  actTimer=null;
		  }
		  if (actObj)
		  {
		      if (actType == 0)
			  {
					removeClass(actObj, "active");
			  }
			  else if (actType == 1) 
			  {
					var mask = actObj.getElementsByClassName('active-mask');
					if (mask[0]){
						mask[0].style.display = "none";
					}
			  }
			  else if (actType == 2) {
					removeClass(actObj, "current");
			  }	
			  actObj = null;
			  cur = null;
		  }
          t.setAttribute("data-startx", e.touches[0].clientX);
          t.setAttribute("data-starty", e.touches[0].clientY);
          var o = e.srcElement ? e.srcElement : e.target;
          if (o.tagName == "A")
		  {
			  if (o.parentNode) {
					if (o.parentNode.tagName == "LI" || o.parentNode.tagName == "DIV") {
						actObj = o.parentNode;
						actType = 0;
						cur = o; 
					} else if ( o.parentNode.tagName == "H2") {
						actObj = o;
						actType = 0;
						cur = o; 
					}
			   }
          } 
		  else if (o.tagName=="IMG") // 1
		  {
		      if (o.parentNode.parentNode)
			  {
				if (o.parentNode.parentNode.tagName == "DIV") {
					actObj = o.parentNode.parentNode;
					actType = 1;
					cur = o;
				} else if (o.parentNode.parentNode.parentNode && o.parentNode.parentNode.parentNode.tagName == "LI") {
					  actObj = o.parentNode.parentNode.parentNode;
					  actType = 1;
					  cur=o;
				} 
			  }
          }
		  else if (o.tagName == "LI") {
				actObj = o;
				actType = 2;
				cur = o;
		  }
		  else if (o.tagName == "SPAN") {
				if (o.parentNode.parentNode && o.parentNode.parentNode.tagName == "DIV") {
					actObj = o.parentNode.parentNode;
				} else if (o.parentNode.parentNode.parentNode && o.parentNode.parentNode.parentNode.tagName == "LI") {
					actObj = o.parentNode.parentNode.parentNode;
				} else if (o.parentNode.parentNode.parentNode.parentNode && o.parentNode.parentNode.parentNode.parentNode.tagName == "LI") {
					actObj = o.parentNode.parentNode.parentNode.parentNode;	
				}
				actType = 1;
				cur = o;
		  }
		  
          if (actObj && cur) {
			  clearTimeout(actTimer);
              actTimer = setTimeout(function(){         
					   if (actType == 0) // 1
					   {
							addClass(actObj, "active"); // 2
					   }
					   else if (actType == 1) {
							var mask = actObj.getElementsByClassName('active-mask');
							if (mask[0]){
								mask[0].style.display = "block";
							} else {
								var div = document.createElement("div");
								div.setAttribute("class", "active-mask");
								actObj.appendChild(div);
								div.style.display = "block";
							}
					   }
					   else if (actType == 2) {				
							var navbarArray = document.getElementsByClassName("navbar-item");
							if (navbarArray && navbarArray.length != 0) {
								for (var i = 0, navlength = navbarArray.length; i < navlength; ++i) {
									if (o.isEqualNode(navbarArray[i])) {
										var lastNavbarIndex = navbarIndex;
										addClass(navbarArray[i], "current");
										navbarIndex = i;
									} else {
										removeClass(navbarArray[i], "current");
									}
								}
							}
							if (lastNavbarIndex != navbarIndex) {
								var catalog = document.getElementById("video-items");
								var catalogChilds = catalog.childNodes;
								for (var i = 0, count = 0, cataLength = catalogChilds.length; i < cataLength; ++i) {
									var tempChild = catalogChilds[i];
									if (tempChild.nodeType == 1) {
										if (count == lastNavbarIndex) {
											removeClass(tempChild, "current");
										} else if (count == navbarIndex) {
											addClass(tempChild, "current");
										}
										++count;
									}
								}
							}
					   }
					   if (actTimer != null)
					   {
						  clearTimeout(actTimer);
						  actTimer=null;
					   }
                     },ACTIVE_DELAY);
          }
      });
      t.addEventListener("touchmove", function(e){
                var startx = parseInt(t.getAttribute("data-startx", 10));
                var starty = parseInt(t.getAttribute("data-starty", 10));
                var deltax = e.touches[0].clientX - startx;
                var deltay = e.touches[0].clientY - starty;

                if (Math.abs(deltax) > MOVE_THRESHOLD 
                    || Math.abs(deltay) > MOVE_THRESHOLD) {
                    if (actTimer != null) {
						clearTimeout(actTimer); 
						actTimer = null;
					}
					isMove = true;
                } 

      });
      t.addEventListener("touchcancel", function(e){
            try {
				if (actTimer != null) {
					clearTimeout(actTimer);
					actTimer = null;
				}
				if (actObj)
				{
					  if(actType == 0)
					  {
							removeClass(actObj, "active"); // -2
					  }
					  else if (actType == 1) 
					  {
							var mask = actObj.getElementsByClassName('active-mask');
							if (mask[0]){
								mask[0].style.display = "none";
							}
					  }
					  else if (actType == 2) {
							removeClass(actObj, "current");
					  }	
					  actObj = null;
					  cur = null;
				}
            } catch(e){
                //document.getElementById("msg").innerHTML=e.message;
            }
      });
      t.addEventListener("touchend", function(e){
          try {
				isOpenUrl = false;
				var delay = Date.now() - start_time;
				if (delay < time_delay)
				{
					isOpenUrl = true;    
				}
				var o = e.srcElement ? e.srcElement : e.target;
				actObj = null;
				 if (o.tagName == "A")
				  {
					  if (o.parentNode) {
							if (o.parentNode.tagName == "LI" || o.parentNode.tagName == "DIV") {
								actObj = o.parentNode;
								actType = 0;
								cur = o; 
							} else if ( o.parentNode.tagName == "H2") {
								actObj = o;
								actType = 0;
								cur = o; 
							}
					   }
				  } 
				  else if (o.tagName=="IMG") // 1
				  {
					  if (o.parentNode.parentNode)
					  {
						if (o.parentNode.parentNode.tagName == "DIV") {
							actObj = o.parentNode.parentNode;
							actType = 1;
							cur = o;
						} else if (o.parentNode.parentNode.parentNode && o.parentNode.parentNode.parentNode.tagName == "LI") {
							  actObj = o.parentNode.parentNode.parentNode;
							  actType = 1;
							  cur=o;
						} 
					  }
				  }
				  else if (o.tagName == "LI") {
						actObj = o;
						actType = 2;
						cur = o;
				  }
				  else if (o.tagName == "SPAN") {
						if (o.parentNode.parentNode && o.parentNode.parentNode.tagName == "DIV") {
							actObj = o.parentNode.parentNode;
						} else if (o.parentNode.parentNode.parentNode && o.parentNode.parentNode.parentNode.tagName == "LI") {
							actObj = o.parentNode.parentNode.parentNode;
						} else if (o.parentNode.parentNode.parentNode.parentNode && o.parentNode.parentNode.parentNode.parentNode.tagName == "LI") {
							actObj = o.parentNode.parentNode.parentNode.parentNode;	
						}
						actType = 1;
						cur = o;
				  }
				if (actObj) {
					if (typeof(mtt_navi) == "undefined")
					{

		//document.getElementById("msg").innerHTML="没定义mtt_navi";
					}
					else if(!mtt_navi.isPopupMenuShowing())
					{

		//document.getElementById("msg").innerHTML="没显示长按菜单";
					}
					else 
					{
		//document.getElementById("msg").innerHTML="长按菜单显示中";
						isOpenUrl = false;
					}
				}	
			
				if (actTimer != null)
				{
					clearTimeout(actTimer); 
					actTimer = null;
					if (actObj)
					{
						clearTimeout(delayTimer);
						delayTimer = setTimeout(function() {
							if (actObj)
							{
								  if (actType == 0)
								  {
										removeClass(actObj, "active"); // -2
								  }
								  else if (actType == 1) 
								  {
										var mask = actObj.getElementsByClassName('active-mask');
										if (mask[0]){
											mask[0].style.display = "none";
										}
								  }
								  else if (actType == 2) {
										//removeClass(actObj, "current");
								  }	
								  actObj = null;
							}
							var aEle = null;
							if (cur.tagName == "IMG")
							{
							   if (cur.parentNode && cur.parentNode.parentNode && cur.parentNode.parentNode.tagName == "A")
							   {
									aEle = cur.parentNode.parentNode;
							   }
							}
						    else if (cur.tagName == "A")
						    {
								aEle = cur;
						    } else if (cur.tagName == "LI") {
								var children = cur.getElementsByTagName("A");
								if (children) {
									aEle = children[0];
								}
							} else if (cur.tagName == "SPAN") {
								if (cur.parentNode && cur.parentNode.parentNode) {
									if (cur.parentNode.parentNode.tagName == "A") {
										eEle = cur.parentNode.parentNode;
									} else if(cur.parentNode.parentNode.parentNode && cur.parentNode.parentNode.parentNode.tagName == "A") {
										eEle = cur.parentNode.parentNode.parentNode;
									}
								}
							}
							
							if (aEle) {
							   if(isOpenUrl && !isMove)
							   {
							       if (isSuportOpenUrl)
								   {
								       var options = {};
									   options.url = aEle.href;
									   options.rpt = aEle.getAttribute("rpt");
								       x5.openNaviUrl(options,success,error);
								   }
								   else
								   {
								       window.location.href = aEle.href;
								   }
								   isOpenUrl = false;
							   }
							}
						}, 100);
					}
				}
				else 
				{
					if (actObj)
					{
						  if (actType == 0)
						  {
								removeClass(actObj, "active"); // -2
						  }
						  else if (actType == 1) 
						  {
								var mask = actObj.getElementsByClassName('active-mask');
								if (mask[0]){
									mask[0].style.display = "none";
								}
						  }
						  else if (actType == 2) {
								//removeClass(actObj, "current");
						  }	
						  actObj = null;
					}
					var aEle = null;
					if (cur.tagName=="IMG")
					{
					   if (cur.parentNode && cur.parentNode.parentNode && cur.parentNode.parentNode.tagName == "A")
					   {
							aEle = cur.parentNode.parentNode;
					   }
					}
					else if (cur.tagName=="A")
					{
						aEle = cur;
					} else if (o.tagName == "LI") {
						var children = o.getElementsByTagName("A");
						if (children) {
							aEle = children[0];
						}
					} else if (cur.tagName == "SPAN") {
						if (cur.parentNode && cur.parentNode.parentNode) {
							if (cur.parentNode.parentNode.tagName == "A") {
								aEle = cur.parentNode.parentNode;
							} else if(cur.parentNode.parentNode.parentNode && cur.parentNode.parentNode.parentNode.tagName == "A") {
								aEle = cur.parentNode.parentNode.parentNode;
							}
						}
					}
					
					if (aEle) {
					   if(isOpenUrl && !isMove)
					   {
						   if (isSuportOpenUrl)
						   {
							   var options = {};
							   options.url = aEle.href;
							   options.rpt = aEle.getAttribute("rpt");
							   x5.openNaviUrl(options,success,error);
						   }
						   else
						   {
							   window.location.href = aEle.href;
						   }
						   isOpenUrl = false;
					   }
					}
				}
          } catch(e){
                //document.getElementById("msg").innerHTML=e.message;
          }
      });
	  var nav = document.getElementsByClassName("title-nav video-items-navbar");
	  if (nav && nav[0]) {
		var liArray = nav[0].getElementsByTagName("li");
		if (liArray && liArray[0]) {
			cur = liArray[0];
			actObj = cur;
			actType = 2;
		}
	  }
      imgFirstTimer = setTimeout(function(){
		loadError = false;
		loadnum = 0;
		var imgs = document.getElementsByTagName("IMG");
		for(var i=0;i< imgs.length;i++){
		   if(imgs[i].getAttribute("lazyLoad")=="1"){
			   //imgs[i].src = imgs[i].getAttribute("url");
			   loadImage(imgs[i],imgs[i].getAttribute("url"));
		   }
		}
		setImgTimeOut();
		if(imgFirstTimer!=null){
		   clearTimeout(imgFirstTimer);
		   imgFirstTimer=null;
		}
	 }, 0);
	 setTimeout(showOtherSection, 25);
}

function reLoadImg(){
    if(imgFirstTimer!=null){
		clearTimeout(imgFirstTimer);
		imgFirstTimer=null;
	}
    imgFirstTimer = setTimeout(function(){
		loadError = false;
		loadnum = 0;
		var imgs = document.getElementsByTagName("IMG");
		for(var i=0;i< imgs.length;i++){
		   if(imgs[i].getAttribute("lazyLoad")=="1"){
			   loadImage(imgs[i],imgs[i].getAttribute("url"));
		   }
		}
		
		if(imgFirstTimer!=null){
			clearTimeout(imgFirstTimer);
			imgFirstTimer=null;
	    }
	}, 0);

	setImgTimeOut();
}

function setImgTimeOut(){
    if(imgNextTimer!=null){
		clearTimeout(imgNextTimer);
		imgNextTimer=null;
	}
    imgNextTimer = setTimeout(function(){
		if(loadError){
			loadnum = loadnum + 1;
			if(loadnum <= 2)
			{
				var imgs = document.getElementsByTagName("IMG");
				for(var i=0;i< imgs.length;i++){
				   if(imgs[i].getAttribute("lazyLoad")=="1"){
					   loadImage(imgs[i],imgs[i].getAttribute("url"));
				   }
				}
				
				setImgTimeOut();
			}
			else{
				loadnum = 0; 
				x5.notifyImgLoadResult(1, reLoadImg.name);
				if(imgNextTimer!=null){
					clearTimeout(imgNextTimer);
					imgNextTimer=null;
				}
			}
			
			loadError = false;
		}
		else {
			loadError = false;
			loadnum = 0;
		    x5.notifyImgLoadResult(0, reLoadImg.name);
			if(imgNextTimer!=null){
				clearTimeout(imgNextTimer);
				imgNextTimer=null;
	        }
		}
	 }, 3000);

}

function success(){

}


function error(){

}

function showOtherSection(){
    var nodes = document.getElementsByTagName("section");
    if(nodes)
    {
        for(var i = 0; i < nodes.length; i++)
        {
            var node = nodes[i];
            removeClass(node, "not-inscreen");
        }
    }
}

function refreshContentHeight()
{
     if (typeof mtt_navi !== "undefined")
    {

	mtt_navi.onGetOffsetHeight(document.documentElement.offsetHeight);
    }
}

function openActiveLink() {
	isOpenUrl = true;
	clearTimeout(actTimer); 
	if (cur)
	{
		clearTimeout(delayTimer);
		delayTimer = setTimeout(function() {
			var aEle = null;
			if (cur.tagName == "IMG")
			{
			   if (cur.parentNode && cur.parentNode.parentNode && cur.parentNode.parentNode.tagName == "A")
			   {
					aEle = cur.parentNode.parentNode;
			   }
			} else if (cur.tagName == "A")
			{
				aEle = cur;
			} else if (cur.tagName == "LI") {
				var children = cur.getElementsByTagName("A");
				if (children) {
					aEle = children[0];
				}
			} else if (cur.tagName == "SPAN") {
				if (cur.parentNode && cur.parentNode.parentNode) {
					if (cur.parentNode.parentNode.tagName == "A") {
						eEle = cur.parentNode.parentNode;
					} else if(cur.parentNode.parentNode.parentNode && cur.parentNode.parentNode.parentNode.tagName == "A") {
						eEle = cur.parentNode.parentNode.parentNode;
					}
				}
			}
			
			if (aEle) {
			   if(isOpenUrl && !isMove)
			   {
				   if (isSuportOpenUrl)
				   {
					   var options = {};
					   options.url = aEle.href;
					   options.rpt = aEle.getAttribute("rpt");
					   x5.openNaviUrl(options,success,error);
				   }
				   else
				   {
					   window.location.href = aEle.href;
				   }
				   isOpenUrl = false;
			   }
			}
		}, 100);
	}
}