(function(){
    var toolbar = null;

    function renderPage(data){
      /* A   价格信息
         B   价格趋势
         C   相关推荐
       */
      var A = false,
        B = false,
        C = false,
        status = 0,
        openurl = 'qb://compareprice/morepage',
        r_args = ['otherprice', '', '', ''];

      var tpls = ['发现其他商城报价:<span>￥{price}</span>', '提供商品历史价格参考', '暂无比价，找到{length}家相似商品', '暂无比价，我们正在努力完善...'],
        box = document.querySelector('#J_box_inner');

      //获取数据
      var JsonData = data,
        suggest_data = null,
        trend_data = null,
        price_data = null;

      if(!JsonData || JsonData == '{}'){
        window.showbar && window.showbar.showToolBar();
        box.innerHTML = tpls[3];
        box.style.background = 'none';
        return false;
      }


      JsonData = JSON.parse(JsonData);
      suggest_data = JsonData.shoppingSuggest;
      trend_data = JsonData.shoppingTrend;
      price_data = JsonData.shoppingPriceTiled;

      if(suggest_data && suggest_data.shoppingSuggestInfos && suggest_data.shoppingSuggestInfos.length > 0){
        C = true;
      }
      if(price_data && price_data.productInfoList && price_data.productInfoList.length > 0){
        A = true;
      }
      if(trend_data && trend_data.shoppingTrendMonth){
        B = true;
      }

      if(A){
        status = 0;
      } else if(B && C){
        status = 1;
      } else if(C){
        status = 2;
      } else {
        status = 3;
      }
      box.removeAttribute('style');
      box.style.background = '';
      box.parentNode.removeAttribute('data-status');
      switch (status){
        case 0:
          var price;
          price_data.productInfoList.sort(function(a, b){
            return a.productPrice - b.productPrice;
          });
          price = price_data.productInfoList[0].productPrice;
          box.innerHTML = tpls[status].replace('{price}', price);
          break;
        case 1:
          box.innerHTML = tpls[status];
          break;
        case 2:
          var length = suggest_data.shoppingSuggestInfos.length;
          box.innerHTML = tpls[status].replace('{length}', length);
          break;
        case 3:
          box.innerHTML = tpls[status];
          box.style.background = 'none';
          box.parentNode.setAttribute('data-status', 3);
          break;
      }
      window.showbar && window.showbar.showToolBar();

      if(status != 3){
        document.querySelector('.mod-bar-box').addEventListener('click', function(){
          if(document.querySelector('.mod-bar-box').getAttribute('data-status') == 3){
            return false;
          }
          window.location.href = openurl + '/' + r_args[status];
        });
      }
    }



    window.loadPage = function(data){
        renderPage(data);
    };

})();
