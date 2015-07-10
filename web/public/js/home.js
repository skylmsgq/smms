$(document).ready(function() {
    setTimeout(function() {
        $("#top img").animate({
            opacity: "1",
            top: "0"
        }, 400);
        setTimeout(function() {
            $("#top h1").animate({
                opacity: "1"
            }, 400);
        }, 300);
    }, 1000);

    $('a').click(function() {
        event.preventDefault();
    });

    // 绘制地图
    var map = new BMap.Map("map"); // 创建地图实例
    map.centerAndZoom(new BMap.Point(121.441764, 31.25287), 12); // 初始化地图，设置中心点坐标和地图级别
    map.enableScrollWheelZoom(); // 允许滚轮缩放 
    map.addControl(new BMap.NavigationControl());
    map.addControl(new BMap.ScaleControl({
        anchor: BMAP_ANCHOR_BOTTOM_RIGHT
    }));
    map.setCurrentCity("上海"); // 设置地图显示的城市 此项是必须设置的
    // var mapStyle = {
    //     style: "grassgreen"
    // };
    map.setMapStyle({
        styleJson: [{
            "featureType": "water",
            "elementType": "all",
            "stylers": {
                "color": "#f2f2f2"
            }
        }, {
            "featureType": "road",
            "elementType": "geometry.fill",
            "stylers": {
                "color": "#ffffff"
            }
        }, {
            "featureType": "road",
            "elementType": "geometry.stroke",
            "stylers": {
                "color": "#bababa"
            }
        }, {
            "featureType": "road",
            "elementType": "labels.text.fill",
            "stylers": {
                "color": "#767676"
            }
        }, {
            "featureType": "road",
            "elementType": "labels.text.stroke",
            "stylers": {
                "color": "#ffffff"
            }
        }, {
            "featureType": "land",
            "elementType": "all",
            "stylers": {
                "color": "#dad3c8"
            }
        }, {
            "featureType": "subway",
            "elementType": "all",
            "stylers": {
                "visibility": "off"
            }
        }, {
            "featureType": "railway",
            "elementType": "all",
            "stylers": {
                "visibility": "off"
            }
        }, {
            "featureType": "local",
            "elementType": "all",
            "stylers": {
                "visibility": "off"
            }
        }, {
            "featureType": "arterial",
            "elementType": "all",
            "stylers": {
                "visibility": "off"
            }
        }]
    });

    // 3公里对应经纬度
    var unit = 0.00449156 * 6;

    // 得到当前所有点，使用点聚类，并确定是否显示
    // 数据源为sensor变量
    var markers = [];
    var icon = new BMap.Icon(img_url + "sensor.png", new BMap.Size(35, 35), {
        imageOffset: new BMap.Size(0, 0), // 设置图片偏移
        anchor: new BMap.Size(16, 30),
        imageSize: new BMap.Size(30, 30)
    });

    for (key in sensor) {
        var marker = new BMap.Marker(new BMap.Point(sensor[key]['longitude'], sensor[key]['latitude']), {
            title: key
        });
        marker.addEventListener("click", function() {
            var thisMarker = this;
            var key = thisMarker.getTitle();
            map.panTo(this.getPosition());
            setTimeout(function() {
                thisMarker.openInfoWindow(new BMap.InfoWindow("<div><p>设备id：" + sensor[key]['deviceId'] + "<p/><p>上传时间：" + sensor[key]['dateTime'] + "<p/><p>湿度：" + sensor[key]['humidity'] + "%<p/><p>大气压：" + sensor[key]['pressure'] + "kPa<p/><p>地理位置：(" + parseFloat(sensor[key]['longitude']).toFixed(4) + "," + parseFloat(sensor[key]['latitude']).toFixed(4) + ")</p><p>设备区域：" + sensor[key]['district'] + "<p/></div>", {
                    enableMessage: false
                }));
            }, 500);

            // 场景管理
            var context = Math.random();
            if (context > 0.5) {
                // 室外
                $("#outdoor img").css("opacity", "1");
                $("#outdoor h3").css("opacity", "1");
                $("#outdoor h1").text(parseFloat(4 + Math.random() * 8).toFixed(1) + "℃").css("opacity", "1");

                $("#indoor img").css("opacity", "0.5");
                $("#indoor h3").css("opacity", "0.5");
                $("#indoor h1").css("opacity", "0");
            } else {
                // 室内
                $("#indoor img").css("opacity", "1");
                $("#indoor h3").css("opacity", "1");
                $("#indoor h1").text(parseFloat(20 + Math.random() * 4).toFixed(1) + "℃").css("opacity", "1");

                $("#outdoor img").css("opacity", "0.5");
                $("#outdoor h3").css("opacity", "0.5");
                $("#outdoor h1").css("opacity", "0");
            }
        });
        marker.setIcon(icon);
        markers.push(marker);
    }

    var markerClusterer = new BMapLib.MarkerClusterer(map, {
        markers: markers
    });
    var sensorTotalNumber = markers.length;

    //个人设备
    var locationShow = true;
    $('#locationShow').click(function() {
        if (!locationShow) {
            markerClusterer.addMarkers(markers);
            locationShow = true;
            $('#locationShow').text('隐藏设备');
        } else {
            markerClusterer.clearMarkers();
            locationShow = false;
            $('#locationShow').text('个人设备');
        }
    });

    // 是否显示上海市区域边界
    var bdary = new BMap.Boundary();
    var ply = new Array();

    bdary.get("上海", function(rs) { //获取行政区域
        // map.clearOverlays(); //清除地图覆盖物       
        var count = rs.boundaries.length; //行政区域的点有多少个
        for (var i = 0; i < count; i++) {
            ply[i] = new BMap.Polygon(rs.boundaries[i], {
                strokeWeight: 2,
                strokeColor: "#D85656",
                fillOpacity: 0.4
            }); //建立多边形覆盖物
            // map.addOverlay(ply[i]); //添加覆盖物
            // map.setViewport(ply.getPath()); //调整视野         
        }
    });

    var rangeShow = false;
    $('#rangeShow').click(function() {
        if (!rangeShow) {
            for (var i = ply.length - 1; i >= 0; i--) {
                map.addOverlay(ply[i]);
            }
            $('#rangeShow').text('取消边界');
            rangeShow = true;
        } else {
            for (var i = ply.length - 1; i >= 0; i--) {
                map.removeOverlay(ply[i]);
            }
            $('#rangeShow').text('上海边界');
            rangeShow = false;
        }
    });
    // $('#rangeShow input').click(function() {
    //     if ($(this).is(":checked")) {
    //         for (var i = ply.length - 1; i >= 0; i--) {
    //             map.addOverlay(ply[i]);
    //         }
    //     } else {
    //         for (var i = ply.length - 1; i >= 0; i--) {
    //             map.removeOverlay(ply[i]);
    //         }
    //     }
    // });

    // 工具栏隐藏
    var hideBtn = false;
    $('#hideBtn').click(function() {
        if (!hideBtn) {
            $('#tool').animate({
                left: '-445px'
            }, 500);
            $('#hideBtn').attr("src", img_url + "rightArrow.png");
            hideBtn = !hideBtn;
        } else {
            $('#tool').animate({
                left: '0px'
            }, 500);
            $('#hideBtn').attr("src", img_url + "leftArrow.png");
            hideBtn = !hideBtn;
        }
    });

    // 绘制网格
    var polygon = [];
    var sensorType = "";
    var sourceType = "mobile";
    var startR = 0,
        startG = 255,
        startB = 226,
        endR = 0,
        endG = 61,
        endB = 255,
        resultR = 0,
        resultG = 0,
        resultB = 0,
        color = "";

    // var colors = ["#52ff00", "#fefd00", "#ffa800", "#ff0f00", "#8400ff", "#888"];

    // 拾色器
    var f = $.farbtastic('#picker');
    var p = $('#picker').css('opacity', 0.25);
    var selected;
    $('.colorwell').each(function() {
        f.linkTo(this);
        $(this).css('opacity', 0.75);
    }).focus(function() {
        if (selected) {
            $(selected).css('opacity', 0.75).removeClass('colorwell-selected');
        }
        f.linkTo(this);
        p.css('opacity', 1);
        $(selected = this).css('opacity', 1).addClass('colorwell-selected');
    });
    var cxt = document.getElementById("gradient-example").getContext("2d");
    var grd = cxt.createLinearGradient(0, 0, 320, 0);
    grd.addColorStop(0, "rgb(" + startR + "," + startG + "," + startB + ")");
    grd.addColorStop(1, "rgb(" + endR + "," + endG + "," + endB + ")");
    cxt.fillStyle = grd;
    cxt.fillRect(0, 0, 320, 40);

    cxt = document.getElementById("gradient").getContext("2d");
    grd = cxt.createLinearGradient(0, 0, 300, 0);
    grd.addColorStop(0, "rgb(" + startR + "," + startG + "," + startB + ")");
    grd.addColorStop(1, "rgb(" + endR + "," + endG + "," + endB + ")");
    cxt.fillStyle = grd;
    cxt.fillRect(0, 0, 300, 40);

    function gridPlot() {
        if (sensorType != "" && sourceType != "") {
            for (var i = 0; i < polygon.length; i++) {
                map.removeOverlay(polygon[i]);
            }
            polygon = [];

            // 获取所需的统计数据
            var max = stats[0][sourceType]["max"][sensorType],
                min = stats[0][sourceType]["min"][sensorType],
                avg = stats[0][sourceType]["avg"][sensorType];

            // 绘制legend
            $('#legend').animate({
                height: "90px"
            }, 600, function() {
                $('#legend p').fadeOut(100, function() {
                    switch (sensorType) {
                        case "temperature":
                            $('#middle').text("温度");
                            min = min + "℃";
                            max = max + "℃";
                            break;

                        case "humidity":
                            $('#middle').text("湿度");
                            min = min + "%";
                            max = max + "%";
                            break;

                        case "pressure":
                            $('#middle').text("气压");
                            min = min + "kPa";
                            max = max + "kPa";
                    }
                    $('#min').text(min);
                    $('#max').text(max);
                    setTimeout(function() {
                        $('#legend p').fadeIn(100);
                    }, 100);
                });
            });

            for (key in grid) {
                var pointArray = [];
                var lat1 = grid[key]["location"]["latitude_1"],
                    lat2 = grid[key]["location"]["latitude_2"],
                    lng1 = grid[key]["location"]["longitude_1"],
                    lng2 = grid[key]["location"]["longitude_2"];

                var cal;
                if (grid[key][sourceType][sensorType] == "暂无实况") {
                    color = "#888";
                } else {
                    cal = (parseFloat(grid[key][sourceType][sensorType]) - (parseFloat(max) + parseFloat(min)) / 2) / ((parseFloat(max) - parseFloat(min)) == 0 ? 99999 : (parseFloat(max) - parseFloat(min)));
                    resultR = (startR + endR) / 2 + (endR - startR) * cal;
                    resultG = (startG + endG) / 2 + (endG - startG) * cal;
                    resultB = (startB + endB) / 2 + (endB - startB) * cal;
                    color = "rgb(" + parseInt(resultR) + "," + parseInt(resultG) + "," + parseInt(resultB) + ")";
                }
                pointArray.push(new BMap.Point(lng1, lat1));
                pointArray.push(new BMap.Point(lng2, lat1));
                pointArray.push(new BMap.Point(lng2, lat2));
                pointArray.push(new BMap.Point(lng1, lat2));
                polygon[key] = new BMap.Polygon(pointArray, {
                    id: key, //多边形ID     
                    strokeColor: color, //线颜色   
                    strokeOpacity: 0.01, //线透明度   
                    strokeWeight: 1, //线宽   
                    fillColor: color, //填充色   
                    fillOpacity: 0.5 //填充透明度   
                });
                map.addOverlay(polygon[key]);
            }
        }
    }

    // legend颜色选取
    $('#sure').click(function() {
        var startColor = $('#color1 input').val();
        var endColor = $('#color2 input').val();
        startR = parseInt(startColor[1], 16) * 16 + parseInt(startColor[2], 16);
        startG = parseInt(startColor[3], 16) * 16 + parseInt(startColor[4], 16);
        startB = parseInt(startColor[5], 16) * 16 + parseInt(startColor[6], 16);
        endR = parseInt(endColor[1], 16) * 16 + parseInt(endColor[2], 16);
        endG = parseInt(endColor[3], 16) * 16 + parseInt(endColor[4], 16);
        endB = parseInt(endColor[5], 16) * 16 + parseInt(endColor[6], 16);

        cxt = document.getElementById("gradient").getContext("2d");
        grd = cxt.createLinearGradient(0, 0, 300, 0);
        grd.addColorStop(0, "rgb(" + startR + "," + startG + "," + startB + ")");
        grd.addColorStop(1, "rgb(" + endR + "," + endG + "," + endB + ")");
        cxt.fillStyle = grd;
        cxt.fillRect(0, 0, 300, 40);

        gridPlot();
    });

    // 工具栏传感器类型选择
    // 并根据工具栏的选择展示不同的传感器值
    $('#sensorType img').click(function() {
        sensorType = $(this).attr('name');
        $('#sensorType img').removeClass("active");
        $(this).addClass("active");
        if (sensorType == 'temperature' || sensorType == 'humidity' || sensorType == 'pressure') {
            gridPlot();
        } else if (sensorType == 'more') {
            $('#futureWork h4').text("如何扩展以支持显示更多传感类型？");
            $('#futureWork').modal();
        }
    });

    // $('#button_type button').click(function(){
    //     $(this).removeClass('active');
    // });

    // 工具栏数据源选择
    // 并根据工具栏的选择展示不同的数据源
    $('#deviceType td').click(function() {
        sourceType = $(this).attr('name');
        if (sourceType == 'mobile' || sourceType == 'station' || sourceType == 'error') {
            gridPlot();
        } else if (sourceType == 'else') {
            $('#futureWork h4').text("如何扩展以支持显示更多传感设备？");
            $('#futureWork').modal();
        }
    });

    // 清除当前网格
    $('#tool #clear').click(function() {
        $('#sensorType button').each(function() {
            $(this).removeClass("active");
        });
        for (var i = 0; i < polygon.length; i++) {
            map.removeOverlay(polygon[i]);
        }
        polygon = [];
        $('#legend p').fadeOut(100, function() {
            $('#legend').animate({
                height: "70px"
            }, 300);
        });
    });

    // 全屏显示
    var fullScreen = false;
    $('#fullScreen').click(function() {
        if (!fullScreen) {
            $('html,body').scrollTop(0).css("overflow", "hidden");
            $('#main').css("position", "absolute");
            $('#main').css("top", "0");
            $('#main').css("left", "0");
            $('#main').width(document.documentElement.clientWidth + "px");
            $('#main').height(document.documentElement.clientHeight + "px");
            $('#map').height(document.documentElement.clientHeight + "px");
            $('#fullScreen').text("退出全屏");
            fullScreen = true;
        } else {
            $('html,body').scrollTop(0).css("overflow", "auto");
            $('#main').css("position", "relative");
            $('#main').width("96%");
            $('#main').height("640px");
            $('#map').height("640px");
            $('#fullScreen').text("全屏显示");
            fullScreen = false;
        }
    });
    window.onresize = function() {
        if (fullScreen) {
            $('#main').width(document.documentElement.clientWidth + "px");
            $('#main').height(document.documentElement.clientHeight + "px");
            $('#map').height(document.documentElement.clientHeight + "px");
        }
    }
});

// 根据城市名获取某个区域的经纬度范围
// function getRangeByName(area) {
//     var minLat = 90,
//         maxLat = 0,
//         minLng = 180,
//         maxLng = 0,
//         i = 0,
//         lat = 0,
//         lng = 0;
//     var bdary = new BMap.Boundary();
//     var range = new Array();
//     bdary.get(area, function(rs) { //获取行政区域
//         // map.clearOverlays(); //清除地图覆盖物       
//         var count = rs.boundaries.length; //行政区域的点有多少个
//         for (var j = 0; j < count; j++) {
//             var bd = rs.boundaries[j];
//             while (1) {
//                 i = bd.indexOf(",");
//                 lng = parseFloat(bd.substring(0, i));
//                 bd = bd.substring(i + 1);
//                 i = bd.indexOf(";");
//                 if (i == -1) {
//                     lat = parseFloat(bd);
//                 } else {
//                     lat = parseFloat(bd.substring(0, i));
//                     bd = bd.substring(i + 1);
//                 }
//                 if (lat > maxLat) {
//                     maxLat = lat;
//                 }
//                 if (lat < minLat) {
//                     minLat = lat;
//                 }
//                 if (lng > maxLng) {
//                     maxLng = lng;
//                 }
//                 if (lng < minLng) {
//                     minLng = lng;
//                 }
//                 if (i == -1) break;
//             }
//         }
//         range["minLng"] = minLng;
//         range["maxLng"] = maxLng;
//         range["minLat"] = minLat;
//         range["maxLat"] = maxLat;
//         return range;
//     });
// }

//highcharts
$(function() {
    $('#history').highcharts({
        chart: {
            type: 'spline',
            backgroundColor: 'rgba(250,250,250,0.6)'
        },
        title: {
            text: '2014年每月平均温度'
        },
        xAxis: {
            categories: ['一月', '二月', '三月', '四月', '五月', '六月',
                '七月', '八月', '九月', '十月', '十一月', '十二月'
            ]
        },
        yAxis: {
            title: {
                text: '温度'
            },
            labels: {
                formatter: function() {
                    return this.value + '°'
                }
            }
        },
        tooltip: {
            crosshairs: true,
            shared: true
        },
        plotOptions: {
            spline: {
                marker: {
                    radius: 4,
                    lineColor: '#666666',
                    lineWidth: 1
                }
            }
        },
        series: [{
            name: '闵行',
            marker: {
                symbol: 'square'
            },
            data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, {
                y: 26.5,
                marker: {
                    symbol: 'url(' + img_url + '/sun.png)'
                }
            }, 23.3, 18.3, 13.9, 9.6]

        }, {
            name: '徐汇',
            marker: {
                symbol: 'diamond'
            },
            data: [{
                y: 3.9,
                marker: {
                    symbol: 'url(' + img_url + '/snow.png)'
                }
            }, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
        }]
    });
});