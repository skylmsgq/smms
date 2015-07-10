<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Meteora－属于你的个人移动智能气象站</title>
    <meta name="description" content="">
    
    <script type="text/javascript" src="<?php base_url();?>public/js/jquery.min.js"></script>
    <script type="text/javascript" src="<?php base_url();?>public/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<?php base_url();?>public/js/highcharts.js"></script>
    <script type="text/javascript" src="<?php base_url();?>public/js/exporting.js"></script>
    <script type="text/javascript">
        var base_url = "<?php echo base_url();?>";
        var img_url = base_url + "public/img/";
        $(function(){
            $("[data-toggle='tooltip']").tooltip();
        });
    </script>

    <link rel="stylesheet" type="text/css" href="<?php base_url();?>public/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="<?php base_url();?>public/css/common.css">
    <!-- 标题字体 -->
    <!-- <link href='http://www.youziku.com/webfont/CSS/c368dbbd86cbb4e13cf3e2a04141a88d' rel='stylesheet' type='text/css'/> -->
    <!-- <link href='http://www.youziku.com/webfont/CSS/0d390bd5ca5270bdf8e80c635429505d' rel='stylesheet' type='text/css'/> -->
    <!-- <link href='http://www.youziku.com/webfont/CSS/2f0c1fb94fa2e23eb587ab4f76efd467' rel='stylesheet' type='text/css'/> -->
    <link href='http://www.youziku.com/webfont/CSS/7b505be3a01c76e5ba846f1a39482ca5' rel='stylesheet' type='text/css'/>
</head>

<body>
    <nav class="navbar navbar-default" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="">Meteora</a>
            </div>

            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav">
                    <li><a href="">首页</a></li>
                    <li><a href="">Android App</a></li>
                    <li><a href="">iPhone App</a></li>
                    <li><a href="">关于我们</a></li>  
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="">登录</a></li>
                    <li><a href="">注册</a></li>

            </div>
        </div>
    </nav>

    <div id="top">
        <div class="wrapper">
            <img src="<?php base_url();?>public/img/logo.png">

            <h1>晴雨，一个你与天的预言故事</h1>
            <!-- Split button -->
            <!-- <div class="btn-group">
                <button type="button" class="btn btn-primary">Facebook</button>
                <button type="button" class="btn btn-success">Twitter</button>
                <button type="button" class="btn btn-danger">Weibo</button>
                <button type="button" class="btn btn-warning" id='wechat'>Wechat</button>
            </div> -->
        </div>
    </div>