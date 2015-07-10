<!-- 统计区数字字体 -->
<link href='http://www.youziku.com/webfont/NameCSS/29100' rel='stylesheet' type='text/css'/>
<!-- 英文字体 -->
<link href='http://www.youziku.com/webfont/NameCSS/46721' rel='stylesheet' type='text/css'/>
<link rel="stylesheet" type="text/css" href="<?php base_url();?>public/css/farbtastic.css">
<link rel="stylesheet" type="text/css" href="<?php base_url();?>public/css/home.css">
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=gW3cQ4s09L4KYCnxh7VKf26P"></script>
<script type="text/javascript" src="http://api.map.baidu.com/library/MarkerClusterer/1.2/src/MarkerClusterer_min.js"></script>
<script type="text/javascript" src="http://api.map.baidu.com/library/TextIconOverlay/1.2/src/TextIconOverlay_min.js"></script>
<script type="text/javascript" src="<?php base_url();?>public/js/farbtastic.js"></script>
<script type="text/javascript" src="<?php base_url();?>public/js/home.js"></script>
<script type="text/javascript">
var sensor = eval('<?php echo $sensor;?>');
var grid = eval('<?php echo $grid;?>');
var stats = eval('['+'<?php echo $stats;?>'+']');
</script>

<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">更改图例渐变色</h4>
            </div>
            <div class="modal-body">
                <div class="form-item" id="color1"><input type="text" name="color1" class="colorwell" value="#00ffe2" readonly="readonly" style="backgroud-color:#00ffe2;"/></div>
                <div class="form-item" id="color2"><input type="text" name="color2" class="colorwell" value="#003dff" readonly="readonly" style="backgroud-color:#003dff;"/></div>
                <div id="picker"></div>
                <canvas id="gradient-example" widhth="240px" height = "40px"></canvas>
            </div>
            <div class="modal-footer">
                <button id="sure" type="button" class="btn btn-default" data-dismiss="modal">确认</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="futureWork" tabindex="-1" role="dialog" aria-labelledby="futureWorkLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="panel panel-primary" style="margin:0;">
                <div class="panel-heading">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="futureWorkLabel">未来展望</h4>
                </div>
                <div class="modal-body panel-body">
                    <p style="background-color:#CAEEF7;">使用我们的开放API注册您的设备</p>
                    <p style="background-color:#CADAF7;">上传您的设备数据并完成接口配置</p>
                    <p style="background-color:#BEBEFC;">接入您的数据并添加多种传感类型</p>
                    <p style="background-color:#9A98ED;">简单便捷地实现设备和传感器扩展</p>
                </div>
                <!-- <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                </div> -->
            </div>
        </div>
    </div>
</div>

<div id="container">
    <div id="main">
        <div class="wrapper">
            <!-- <div id="intro">
			<div class="input-group">
      			<input type="text" class="form-control">
      			<span class="input-group-btn">
        			<button class="btn btn-default" type="button">Go!</button>
      			</span>
    			</div>
			</div> -->

			<div id="legend">
            	<!-- <img id="gradient" src="<?php base_url();?>public/img/legend.png" data-toggle="modal" data-target="#myModal"> -->
                <canvas id="gradient" widhth="170px" height="30px" data-toggle="modal" data-target="#myModal">您的浏览器不支持画布元素</canvas>
                <p><span id="min" style="float:left;">min</span><span id="middle">sensor</span></span><span id="max" style="float:right;">max</span></p>
            </div>
            <!-- <div id="full_screen">
                <h5>全屏</h5>
                <img src="<?php base_url();?>public/img/fit_to_width.png">
            </div> -->

            <div id="map">
            </div>
            <div id="mask">
                <div class="panel panel-default" id="tool">
                    <div class="panel-heading" id="button_type">
                        <div class="btn-group btn-group-justified">
                            <div class="btn-group">
                                <button type="button" class="btn btn-default" id="rangeShow" style="border-top-left-radius: 0;">上海边界</button>
                            </div>
                            <div class="btn-group">
                                <button type="button" class="btn btn-default" id="locationShow">隐藏设备</button>
                            </div>
                            <div class="btn-group">
                                <button type="button" class="btn btn-default" id="clear">清除图层</button>
                            </div>
                            <div class="btn-group">
                                <button type="button" class="btn btn-default" id="fullScreen" style="border-top-right-radius: 10px;">全屏显示</button>
                            </div>
                        </div>
                    </div>
                    <img id="hideBtn" src="<?php base_url();?>public/img/leftArrow.png">
                    <div class="panel-body">
                        <ul class="nav nav-tabs" role="tablist" id="myTab">
                            <li class="active"><a href="#sensorType" role="tab" data-toggle="tab">显示设定</a></li>
                            <li><a href="#deviceType" role="tab" data-toggle="tab">数据来源</a></li>
                            <li><a href="#context" role="tab" data-toggle="tab">场景管理</a></li>
                            <li><a href="#history" role="tab" data-toggle="tab" style="float:right;">历史统计</a></li>
                        </ul>

                        <hr id="titleLine">

                        <div class="tab-content">
                            <div class="tab-pane active row" id="sensorType">
                                <div class="col-xs-4 col-sm-4 col-md-4">
                                    <div class="icon">
                                        <img src="<?php base_url();?>public/img/icons/temperature.png" data-toggle="tooltip" title="查看温度分布" name="temperature"><br>
                                    </div>
                                    <div class="icon">
                                        <img src="<?php base_url();?>public/img/icons/UV.png" data-toggle="tooltip" title="查看紫外分布" name="UV"><br>
                                    </div>
                                </div>
                                <div class="col-xs-4 col-sm-4 col-md-4">
                                    <div class="icon">
                                        <img src="<?php base_url();?>public/img/icons/humidity.png" data-toggle="tooltip" title="查看湿度分布" name="humidity"><br>
                                    </div>
                                    <div class="icon">
                                        <img src="<?php base_url();?>public/img/icons/rain.png" data-toggle="tooltip" title="查看雨量分布" name="rain"><br>
                                    </div>
                                </div>
                                <div class="col-xs-4 col-sm-4 col-md-4">
                                    <div class="icon">
                                        <img src="<?php base_url();?>public/img/icons/pressure.png" data-toggle="tooltip" title="查看气压分布" name="pressure"><br>
                                    </div>
                                    <div class="icon">
                                        <img src="<?php base_url();?>public/img/icons/more.png" data-toggle="tooltip" title="添加更多类型" name="more"><br>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="deviceType">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th width="24%">数据类型</th>
                                            <th width="19%">设备数量</th>
                                            <th width="19%">上传数据</th>
                                            <th width="19%">下载数据</th>
                                            <th width="19%">成功率</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td name="mobile"><input type="radio" name="deviceType" value="mobile" checked="checked"><span> Meteora</span></td>
                                            <td><div class="progress" style="margin-bottom:-5px;"><div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="70" aria-valuemin="0" aria-valuemax="100" style="width: 70%"></div></div></td>
                                            <td>2.755M</td>
                                            <td>7.657M</td>
                                            <td>97.3%</td>
                                        </tr>
                                        <tr>
                                            <td name="station"><input type="radio" name="deviceType" value="station"><span> 气象站</span></td>
                                            <td><div class="progress" style="margin-bottom:-5px;"><div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="80" aria-valuemin="0" aria-valuemax="100" style="width: 80%"></div></div></td>
                                            <td>3.755M</td>
                                            <td>8.657M</td>
                                            <td>96.5%</td>
                                        </tr>
                                        <tr>
                                            <td name="error"><input type="radio" name="deviceType" value="error"><span> 两者差值</span></td>
                                            <td><div class="progress" style="margin-bottom:-5px;"><div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%"></div></div></td>
                                            <td>0M</td>
                                            <td>0M</td>
                                            <td>100%</td>
                                        </tr>
                                        <tr>
                                            <td name="else"><input type="radio" name="deviceType" value="else"><span> 其他设备</span></td>
                                            <td><div class="progress" style="margin-bottom:-5px;"><div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%"></div></div></td>
                                            <td>0M</td>
                                            <td>0M</td>
                                            <td>100%</td>
                                        </tr>
                                    </tbody>

                                </table>
                            </div>
                            <div class="tab-pane row" id="context">
                                <p>在地图上点击Meteora并查看对应位置室内外温度</p>
                                <div class="col-xs-6 col-sm-6 col-md-6" style="border-right:1px solid #666;" id="indoor">
                                    <img src="<?php base_url();?>public/img/home.png" name="indoor">
                                    <div class="contextInfo">
                                        <h1> 21℃</h1>
                                        <h3> 室内</h3>
                                    </div>
                                </div>
                                <div class="col-xs-6 col-sm-6 col-md-6" style="border-left:1px solid #666;" id="outdoor">
                                    <img src="<?php base_url();?>public/img/bird.png" name="outdoor">
                                    <div class="contextInfo"> 
                                        <h1> 7℃</h1>
                                        <h3> 室外</h3>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="history"></div>
                        </div>
                    </div>
                </div> 
            </div>    
            

<!--             <div id="tool">
            	<div id="hideBtn">
                </div>
                <form>
                	<div id="sensorType">
                        <ul>
                            <li data-toggle="tooltip" data-placement="top" title="查看温度值分布" name="temperature">
                                <img src="<?php base_url();?>public/img/temperature.png">
                                <span>温度</span>
                            </li>
                            <li data-toggle="tooltip" data-placement="top" title="查看湿度值分布" name="humidity">
                                <img src="<?php base_url();?>public/img/humidity.png">
                                <span>湿度</span>
                            </li>
                            <li data-toggle="tooltip" data-placement="top" title="查看气压值分布" name="pressure">
                                <img src="<?php base_url();?>public/img/pressure.png">
                                <span>气压</span>
                            </li>
                            <p id="clear">清除</p>
                        </ul>

                    </div>

                    <div id="deviceType">
                        <h5>数据源</h5>
                        <ul>
                            <li name="mobile">
                                <input type="radio" name="deviceType" value="mobile" checked="checked">
                                <span>Meteora</span>
                            </li>
                            <li name="station">
                                <input type="radio" name="deviceType" value="station">
                                <span>气象数据</span>
                            </li>
                            <li name="error">
                                <input type="radio" name="deviceType" value="error">
                                <span>两者误差</span>
                            </li>
                        </ul>
                    </div>

                    <div id="setting">
                        <h5>显示设定</h5>
                    	<ul>
                    		<li id="locationShow">
                                <input type="checkbox" name="location" checked="checked">
                                <span>Meteora</span>
                            </li>
                    		<li id="rangeShow">
                                <input type="checkbox" name="range">
                                <span>上海边界</span>
                            </li>
                            
                    	</ul>
                    </div>                    
                </form>
            </div> -->
        </div>
    </div>

    <br>

    <div id="stats">
        <div class="wrapper">
            <div id="statsData">
                <div class='default'>
                    <h3 style="color:#3F93CF;"><?=rand(200,300);?></h3>
                    <h5>个Meteora分布</h5>
                </div>
                <div class='default'>
                    <h3 style="color:#CF763F;"><?=rand(40000,50000);?></h3>
                    <h5>条数据记录</h5>
                </div>
                <div class='default'>
                    <h3 style="color:#953FCF;"><?=rand(1000,2000);?></h3>
                    <h5>次网页浏览</h5>
                </div>
                <div>
                    <h3 style="color:#CF3F76;"><?=rand(80,120);?></h3>
                    <h5>次App下载</h5>
                </div>
            </div>

            <div id="download">
                <div id="android">
                    Android App<span style="font-size:28px;margin-left:5px;">下载</span>
                </div>
                <div id="iphone">
                    iPhone App<span style="font-size:28px;margin-left:5px;">下载</span>
                </div>
            </div>
        </div>
    </div>

</div>