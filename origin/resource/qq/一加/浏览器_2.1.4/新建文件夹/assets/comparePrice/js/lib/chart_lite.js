/**
 * Created with JetBrains WebStorm.
 * User: user
 * Date: 14-4-27
 * Time: 上午1:12
 * To change this template use File | Settings | File Templates.
 */
'use strict';

function Chart(dom, data, options){
    var canvas, default_opt, ctx;
    var opt_data = data,
        tips_axis = {};

    var zeroBegin = {
        x: 80,
        y: 248
    };

    default_opt = {
        yaxis:{
            lineColor: '#ddd',
            textColor: '#000',
            lineWidth: 1,
            fontSize: 20,
            fontFamily: 'Helvetica',
            min: 20,
            max: 50
        },
        xaxis: {
            lineColor: '#ddd',
            textColor: '#000',
            fontSize: 20,
            lineWidth: 1,
            fontFamily: 'Helvetica',
            labels: []
        },
        stoke: {
            color: '#6dc7ff',
            width: 1
        }


    };
    default_opt = $.extend(true, default_opt, options);
    var num_max = default_opt.yaxis.max,
        num_min = default_opt.yaxis.min,
        xaxisArr = [],
        pxOfMaxToMin = null;

    if(typeof dom === 'string'){
        canvas = document.querySelector(dom);
    } else {
        canvas = dom;
    }

    ctx = canvas.getContext('2d');

    function init(){
        setSize();
        drawYaxis();
        drawXaxis();
        drawLine();
        drawTips();
    }

    //根据福元素宽度和高度设置画布大小
    function setSize(){
        var width = canvas.parentNode.offsetWidth,
            height = 140,
            max_num_length = parseInt(default_opt.yaxis.max).toString().length;

        canvas.width = width * 2;
        canvas.height = height * 2;
        canvas.style.width = width + 'px';
        canvas.style.height = height + 'px';

        //计算文字的宽度
        var data_break = (num_max - num_min) / 4,
            float_length = null;
        ctx.font = default_opt.yaxis.fontSize + 'px ' + default_opt.yaxis.fontFamily;
        zeroBegin.x = ctx.measureText('￥' + Math.round(num_max)).width + 10;
    }

    function drawYaxis(){
        //绘制Y轴左侧的刻度
        var textWidth = zeroBegin.x,
            textHeight = default_opt.yaxis.fontSize / 2 - 3,
            lineStep = 5,
            labelArry = [zeroBegin.y];


        //绘制坐标系
        ctx.strokeStyle = default_opt.yaxis.lineColor;
        ctx.lineWidth = default_opt.yaxis.lineWidth;
        ctx.beginPath();
        ctx.moveTo(zeroBegin.x, 0);
        ctx.lineTo(zeroBegin.x, zeroBegin.y);

        //绘制刻度
        var lineBreak = zeroBegin.y / lineStep;
        for(var i = 1; i < lineStep; i++){
            var new_y = zeroBegin.y - lineBreak * i;
            labelArry.push(new_y);
            ctx.moveTo(zeroBegin.x, new_y);
            ctx.lineTo(canvas.offsetWidth * 2, new_y);
        }
        ctx.closePath();
        ctx.stroke();
        //recode the px of the max to the min
        pxOfMaxToMin = labelArry[0] - labelArry[labelArry.length - 1];

        //绘制刻度标尺
        ctx.font = default_opt.yaxis.fontSize + 'px ' + default_opt.yaxis.fontFamily;
        ctx.fillStyle = default_opt.yaxis.textColor;
        ctx.textAlign = 'end';
        var data_break = Math.ceil((num_max - num_min) / 4);
        labelArry.forEach(function(item, index){
            if(index != 0){
                var txt = '￥' + Math.round(num_min + data_break * index);
                ctx.fillText(txt, zeroBegin.x - 10, item + textHeight);
            }
            if(index == labelArry.length - 1){
                num_max = num_min + data_break * index;
            }
        });
    }

    function drawXaxis(){

        //绘制坐标系
        ctx.strokeStyle = default_opt.xaxis.lineColor;
        ctx.lineWidth = default_opt.xaxis.lineWidth;
        ctx.beginPath();
        ctx.moveTo(zeroBegin.x, zeroBegin.y);
        ctx.lineTo(canvas.offsetWidth * 2, zeroBegin.y);

        //绘制刻度
        var lineBreak = (canvas.offsetWidth * 2 - 50) / 105,
            new_x,
            labels = default_opt.xaxis.labels,
            txt = '';
        ctx.font = default_opt.xaxis.fontSize + 'px ' + default_opt.xaxis.fontFamily;
        ctx.fillStyle = default_opt.xaxis.textColor;
        //ctx.fillText(formatter(1353033300000), zeroBegin.x - 25, zeroBegin.y + 15);
        xaxisArr.push(zeroBegin.x);
        for(var i = 0; i < 90; i++){
            new_x = zeroBegin.x + lineBreak * i;
            xaxisArr.push(new_x);
            if(!(i % 15) || i == 89){
                txt = formatter(labels[i]);
                ctx.moveTo(new_x, 0);
                ctx.lineTo(new_x, zeroBegin.y);
                var fontOffset = ctx.measureText(txt);
                ctx.textAlign = 'center';
                ctx.fillText(txt, new_x, zeroBegin.y + default_opt.xaxis.fontSize + 8);
            }
        }
        xaxisArr.push(zeroBegin.x + lineBreak * i);
        ctx.closePath();
        ctx.stroke();

        //时间戳格式化
        function formatter(timestamp){
            var date = new Date(timestamp);
            return (('0' + (date.getMonth()+1).toString()).substr(-2) + '-' +
                ('0' + date.getDate().toString()).substr(-2));
        }
    }

    function drawLine(){
        var data = opt_data;
        var valueToPx,
            new_y = zeroBegin.y - (data[0] - num_min) * pxOfMaxToMin / (num_max - num_min);
        ctx.strokeStyle = default_opt.stoke.color;
        ctx.lineWidth = 3;
        ctx.beginPath();
        ctx.moveTo(xaxisArr[0], new_y);
        for(var i = 1; i < 91; i++){

            new_y = zeroBegin.y - (data[i-1] - num_min) * pxOfMaxToMin / (num_max - num_min);
            if(!tips_axis.max && data[i-1] == default_opt.data.max){
                tips_axis.max = {};
                tips_axis.max.x = xaxisArr[i];
                tips_axis.max.y = new_y;
            }
            if(!tips_axis.min && data[i-1] == default_opt.data.min){
                tips_axis.min = {};
                tips_axis.min.x = xaxisArr[i];
                tips_axis.min.y = new_y;
            }
            ctx.lineTo(Math.round(xaxisArr[i]), Math.round(new_y));
        }
        ctx.stroke();
    }

    function drawTips(){
        var max_txt = '最高:' + default_opt.data.max,
            min_txt = '最低:' + default_opt.data.min,
            min_line_layout = ctx.measureText(min_txt),
            max_line_layout = ctx.measureText(max_txt);

        ctx.font = '24px ' + default_opt.yaxis.fontFamily;
        ctx.fillStyle = '#e85742';
        if(default_opt.data.max == default_opt.data.min){
            return;
        }
        if(tips_axis.max.x - max_line_layout.width / 2 < zeroBegin.x){
            //是否超出左边界
            ctx.fillText(max_txt, zeroBegin.x + max_line_layout.width / 2 + 10, tips_axis.max.y - 10);
        } else if(max_line_layout.width / 2 + tips_axis.max.x > (canvas.offsetWidth * 2 - 10)){
            //是否超出右边界
            ctx.fillText(max_txt, canvas.offsetWidth*2 - max_line_layout.width / 2 - 10, tips_axis.max.y - 10);
        }else {
            ctx.fillText(max_txt, tips_axis.max.x, tips_axis.max.y - 10);
        }

        ctx.fillStyle = '#32cd32';
        if(min_line_layout.width / 2 + tips_axis.min.x > (canvas.offsetWidth * 2 - 10)){
            ctx.fillText(min_txt, canvas.offsetWidth*2 - min_line_layout.width / 2 - 10, tips_axis.min.y + 28);
        } else if(tips_axis.min.x - min_line_layout.width / 2 < zeroBegin.x){
            ctx.fillText(min_txt, zeroBegin.x + min_line_layout.width / 2 + 10, tips_axis.min.y + 28);
        }else {
            ctx.fillText(min_txt, tips_axis.min.x, tips_axis.min.y + 28);
        }
    }

    init();
}
