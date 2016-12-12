function lazyLoadImg() {
  x5.isNoPicStyle(function() {
    loadAllImg(0);
  }, function() {
    loadAllImg(1);
  });
}

function loadAllImg(isShow) {
  TConsole.log("loadAllImg isShow = " + isShow, 0);
  var lazyLoadArray = [];
  function bgLoadSucc(ctx) {//backgroundImage方式延迟加载
    ctx.getId().style.backgroundImage = 'url("' + ctx.uri + '")';
    ctx.getId().setAttribute("uri", "");//保证不重复加载
    x5.notifyImgLoadResult(0, loadAllImg.name);
  }
  function imgloadSucc(ctx) {
    ctx.getId().src = ctx.uri;
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
  setTimeout(function() {
    for (var i = 0, l = lazyLoadArray.length; i < l; ++i) {
      lazyLoadArray[i].loadImg();
    }
  }, 0);
}
