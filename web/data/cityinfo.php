<?php
//@author:skylmsgq
//初始化
date_default_timezone_set("Asia/Shanghai");
$cityinfo = array(
	array('code' => '101020200','city' => '闵行'),
	array('code' => '101020300','city' => '宝山'),
	array('code' => '101020400','city' => '川沙'),
	array('code' => '101020500','city' => '嘉定'),
	array('code' => '101020600','city' => '南汇'),
	array('code' => '101020700','city' => '金山'),
	array('code' => '101020800','city' => '青浦'),
	array('code' => '101020900','city' => '松江'),
	array('code' => '101021000','city' => '奉贤'),
	array('code' => '101021100','city' => '崇明'),
	array('code' => '101021101','city' => '陈家镇'),
	array('code' => '101021102','city' => '引水船'),
	array('code' => '101021200','city' => '徐家汇'),
	array('code' => '101021300','city' => '浦东')
);

//连接数据库
$con = mysql_connect('202.121.178.239:3306', 'root', 'smmsmysql');
if (!$con){
 	die('Could not connect: ' . mysql_error());
}
mysql_select_db("smms", $con);

//清空数据库
// $delete_sql = "DELETE FROM cityinfo";
// mysql_query($delete_sql);

//中国天气网api接口
$url = 'http://www.weather.com.cn/data/sk/';

foreach ($cityinfo as $key => $value) {
	//获取实时天气数据
	$url_request = $url.$cityinfo[$key]['code'].'.html';
	$s = file_get_contents($url_request);
	$weatherinfo = json_decode($s,true);

	$cityinfo[$key]['temperature'] = $weatherinfo['weatherinfo']['temp']; 
	$cityinfo[$key]['station_WD'] = $weatherinfo['weatherinfo']['WD']; //风向
	$cityinfo[$key]['station_WS'] = $weatherinfo['weatherinfo']['WS']; //风力
	$cityinfo[$key]['humidity'] = ($weatherinfo['weatherinfo']['SD'] == '暂无实况') ? $weatherinfo['weatherinfo']['SD'] : substr($weatherinfo['weatherinfo']['SD'],0,-1);
	$cityinfo[$key]['pressure'] = "101.3";
	$cityinfo[$key]['dateTime'] = date('Y-m-d H:i:s',time()); 

	//插入数据库
	$sql = "INSERT INTO cityinfo (code, city, temperature, station_WD, station_WS, humidity, pressure, dateTime) VALUES ('".$cityinfo[$key]['code']."','".$cityinfo[$key]['city']."','".$cityinfo[$key]['temperature']."','".$cityinfo[$key]['station_WD']."','".$cityinfo[$key]['station_WS']."','".$cityinfo[$key]['humidity']."','".$cityinfo[$key]['pressure']."','".$cityinfo[$key]['dateTime']."')";
	mysql_query($sql);
}

mysql_close($con);

?>