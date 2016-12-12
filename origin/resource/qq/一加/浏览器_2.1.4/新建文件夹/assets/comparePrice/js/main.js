/**
 * user: wheatowu
 * date: 14-4-4
 * time: 下午4:28
 * description:
 * param:
 */

(function () {
    var chart,
        isLoading = false,
        list_index = null,
        isInit = false;
        trend_wrap = document.querySelector('#J_trend_chart');

    //图表初始化
    function chartInit(price_list, shopping_trend){

        if(!price_list.length){
            document.querySelector('.mod-current-trend').style.display = 'none';
            //document.querySelector('#container').style.display = 'none';
            //document.querySelector('#J_current_info').style.display = 'none';
            return;
        }
        price_list = price_list.split(';').slice(0, 90);
        price_list.forEach(function(item, index){
            if(price_list[index] == -1){
                price_list[index] = price_list[index - 1];
            } else {
                price_list[index] = parseFloat(item);

            }
        });
        price_list = price_list.reverse();
        var max_price,
            min_price,
            real_max_price,
            real_min_price,
            price_step,
            last_day = null,
            days_price = [],
            month_day = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
        //获取时间列表
        last_day = shopping_trend.shoppingTrendLastday.split('-').slice(1);
        last_day[0] = parseInt(last_day[0]);
        last_day[1] = parseInt(last_day[1]);

        for(var i = 0, len = 90; i < len; i++){
            var day = new Date();
            (function(index){
                if(last_day[1] - index > 0){
                    day.setDate(last_day[1] - index);
                    day.setMonth(last_day[0] - 1);
                } else {
                    day.setMonth(last_day[0] - 2);
                    day.setDate(last_day[1] - index + month_day[day.getMonth()]);
                }
                //if(i % 7 === 0){
                days_price.unshift(Date.parse(day));
                //}
            })(i);
        }

        min_price = real_min_price = parseFloat(shopping_trend.shoppingTrendLowerPrice);            //parseFloat(shopping_trend.ShoppingTrendLowerPrice) - 100;
        max_price = real_max_price = parseFloat(shopping_trend.shoppingTrendHighestPrice);
        price_step = Math.ceil((max_price - min_price) / 2);
        if(!price_step){
            min_price = 0;
            max_price = max_price * 2;
        } else {
            min_price = min_price - price_step;
            max_price += price_step;
        }

        if(shopping_trend.shoppingTrendCurrentPrice){
            var current_data_dom = document.querySelector('#J_current_info'),
                current_data_name = document.querySelector('#J_current_god_name'),
                current_data_item = current_data_dom.querySelectorAll('li')[0],
                now_price = shopping_trend.shoppingTrendCurrentPrice,
                now_name = shopping_trend.shoppingTrendSiteName,
                now_god_name = shopping_trend.shoppingTrendTitle.replace('|$|', "'");
            current_data_item.innerHTML = current_data_item.innerHTML.replace('{mall}', now_name).replace('{price}',
                '￥' + now_price);
            current_data_name.innerHTML = now_god_name;
            current_data_dom.style.display = 'block';

            function clickHandler(){
                console.log('click');
                var item_comments = current_data_item.querySelector('.comments');
                if(trend_wrap.className.indexOf('show') >= 0){
                    trend_wrap.className = trend_wrap.className.replace('show', 'hide');
                    item_comments.className = item_comments.className.replace('show', 'hide');

                } else {
                    console.log('a');
                    trend_wrap.className = trend_wrap.className.replace('hide', 'show');
                    item_comments.className = item_comments.className.replace('hide', 'show');
                    window.showbar && window.showbar.reportHistoryClick();
                }
            }
            if(!isInit){
                current_data_item.addEventListener('click', clickHandler);
                isInit = true;
            }
        }

        var options = {
            xaxis: {
                textColor: '#a29fa2',
                lineColor: '#ededed',
                labels: days_price
            },
            yaxis: {
                textColor: '#e85742',
                lineColor: '#ededed',
                min: min_price,
                max: max_price
            },
            stoke: {
                color: '#6dc7ff'
            },
            data:{
                max: formatDecimal(real_max_price),
                min: formatDecimal(real_min_price)
            }
        };
        var chart = new Chart('#myDraw', price_list, options);
    }
    //处理数据
    function formatNum(num){
        var int_num = Math.floor(num / 10000);
        if(int_num){
            int_num = Math.round(num / 10000);
            return int_num + '万';
        } else {
            return num;
        }
    }
    function formatDecimal(num){
        var decimal = num.toString().split('.');
        if(parseInt(decimal[1]) > 0){
            return num;
        } else {
            return parseInt(decimal);
        }

    }
        //第一次数据加载
    function firstLoading(list_data, suggest_list){
        var begin = 0,
            end = null;

        end = list_data.length;
        loadData(list_data, suggest_list, begin, end);
        document.querySelector('#J_load_more').style.display = 'block';

        //

    }
    //加载数据
    function loadData(list_data, suggest_list, begin, end){
        isLoading = true;
        var current_info = document.querySelector('#J_current_info .comments'),
          trend_chart = document.querySelector('#J_trend_chart');
        var li_templ = '<li data-href="{url}"><div class="mall">{mall}</div><div class="price">{price}</div>' +
            '<div class="comments">{comments}</div></li>',
          suggest_tpl = '<li data-href="{url}"><figure><div style="background-image: url({pic});"></div>' +
            '</figure><div class="detail"><div class="god-name">{name}</div>' +
            '<div class="price">￥{price}</div></div></li>';
        var list_html = '',
          suggest_html = '';

        if(!list_data || list_data.length == 0){
            document.querySelector('.mod-all-price').style.display = 'none';

        } else {
          trend_chart.className = trend_chart.className.replace('show', 'hide');
        }

        //其他商城价格有的时候， 推荐商品不显示
        if(list_data && list_data.length > 0){
          suggest_list = [];
        }
        if(!suggest_list || suggest_list.length == 0){
          document.querySelector('#J_suggest_list').parentNode.style.display = 'none';
        }

        //其他商城价格排序
        list_data.sort(function(a, b){
          return a.productPrice - b.productPrice;
        });
        for(var i = begin; i < end; i++){
            //comment typeof num
            var comment_or_salse = '';
            if(list_data[i].productCommentNum != 'undefined' && list_data[i].productCommentNum != -1){
              comment_or_salse = formatNum(list_data[i].productCommentNum) + '条评论';
            } else if(list_data[i].productSaleNum != 'undefined' && list_data[i].productSaleNum != -1) {
              comment_or_salse = formatNum(list_data[i].productSaleNum) + '件月销';
            }
            list_html += li_templ.replace('{url}', "qb://compareprice/openurl/moreotherprice/?url~" +
              list_data[i].productUrl).replace('{mall}',list_data[i].productName).replace('{price}',
                '￥'+list_data[i].productPrice).replace('{comments}', comment_or_salse);
        }

        /* suggest list */
        suggest_list.forEach(function(item){
          suggest_html += suggest_tpl.replace('{url}', "qb://compareprice/openurl/moresimilarity/?url~" +
            item.shoppingSuggestUrl).replace('{pic}',
            item.shoppingSuggestOriginalImage).replace('{name}', item.shoppingSuggestTitle).replace('{mall}',
            'xxxx').replace('{price}', item.shoppingSuggestPrice);
        });
        list_index = end;

        document.querySelector('#J_price_list').innerHTML += list_html;
        document.querySelector('#J_suggest_list').innerHTML = suggest_html;
      isLoading = false;
    }


    window.loadPage = function(jsonData){
        var $active_item = null;
        if(!jsonData){
            jsonData = '{}';
        }
        jsonData = JSON.parse(jsonData);
        try{
            var list_data = jsonData.MTTShoppingPriceTiled ? jsonData.MTTShoppingPriceTiled.productInfoList : jsonData.shoppingPriceTiled.productInfoList,
              month_prices = jsonData.shoppingTrend.shoppingTrendMonth,
              shoping_trend = jsonData.shoppingTrend,
              suggest_data = jsonData.shoppingSuggest.shoppingSuggestInfos;
        } catch (e){
            list_data = '';
            shoping_trend = '';
            document.querySelector('.mod-all-price').style.display = 'none';
            document.querySelector('.mod-current-trend').style.display = 'none';
            document.querySelector('#container').style.display = 'none';
            document.querySelector('#J_current_info').style.display = 'none';
            return ;
        }


        firstLoading(list_data, suggest_data);
        chartInit(month_prices, shoping_trend);
        document.body.style.visibility = 'visible';

        $('#J_price_list, #J_suggest_list').on('touchstart', 'li', function(e){
            var touch = event.touches[0],
              dom = touch.target;
            while(dom.nodeName != 'LI'){
              dom = dom.parentNode;
            }
            var $item = $(dom);

            $item.addClass('active');
            $active_item = $item;
        });
        window.addEventListener('touchend', function(e){
            $active_item && $active_item.removeClass('active');
        });
        window.addEventListener('touchmove', function(e){
            $active_item && $active_item.removeClass('active');
        });
        $('#J_price_list li, #J_suggest_list li').on('click', function(){
          window.location.href = $(this).attr('data-href');
        });
        $(window).on('resize', function(){
            chartInit(month_prices, shoping_trend);
        });
    };

})();

