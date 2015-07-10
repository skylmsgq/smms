<?php
class Home extends CI_Controller{
	public function index(){
		date_default_timezone_set("Asia/Shanghai");

		$sensor = array();
		// $zone = array('0'=>'闵行区','1'=>'宝山区','2'=>'川沙区','3'=>'嘉定区','4'=>'南汇区','5'=>'金山区','6'=>'青浦区','7'=>'松江区','8'=>'奉贤区','9'=>'崇明县','10'=>'陈家镇','11'=>'引水船','12'=>'徐汇区','13'=>'浦东新区');
		// $grid = array();
		// for ($i=0; $i < 200; $i++) { 
		// 	$data = array(
		// 		'deviceId'=>$i,
		// 		'dateTime'=>date('Y-m-d H:i:s',time()),
		// 		'temperature'=>rand(10,30),
		// 		'humidity'=>rand(40,60),
		// 		'pressure'=>rand(99900,100100)/1000,
		// 		'latitude'=>rand(31000000,31400000)/1000000,
		// 		'longitude'=>rand(121200000,121700000)/1000000,
		// 		'district'=>$zone[rand(0,13)]
		// 	);
		// 	$sensor[] = $data;			
		// }

		// $file = 'final.txt';
		// $content = file_get_contents(base_url()."public/".$file);
		// // echo $content;die;

		// $array = explode(";", iconv('gb2312','utf-8',$content));
		// $array = explode(";", $content);
		// for($i = 0; $i < count($array)-1; $i++){
		// 	$record = explode(",", $array[$i]);
		// 	// var_dump($record);
		// 	$sensor[] = array(
		// 		'deviceId'=>$record[0],
		// 		'dateTime'=>date('Y-m-d H:i:s',time()),
		// 		'temperature'=>$record[1],
		// 		'humidity'=>$record[2],
		// 		'pressure'=>$record[3],
		// 		'latitude'=>$record[4],
		// 		'longitude'=>$record[5],
		// 		'district'=>substr($record[6],0,-1)
		// 	);
		// }

		function station_data($district, $cityinfo){
			switch ($district){
				case '闵行区':
				  return $cityinfo[0];
				  break;  
				case '宝山区':
				  return $cityinfo[1];
				  break;
				case '川沙区':
				  return $cityinfo[2];
				  break;
				case '嘉定区':
				  return $cityinfo[3];
				  break;
				case '南汇区':
				  return $cityinfo[4];
				  break;
				case '金山区':
				  return $cityinfo[5];
				  break;
				case '青浦区':
				  return $cityinfo[6];
				  break;
				case '松江区':
				  return $cityinfo[7];
				  break;
				case '奉贤区':
				  return $cityinfo[8];
				  break;
				case '崇明县':
				  return $cityinfo[9];
				  break;
				case '陈家镇':
				  return $cityinfo[10];
				  break;
				case '引水船':
				  return $cityinfo[11];
				  break;
				case '徐汇区':
				  return $cityinfo[12];
				  break;
				case '浦东新区':
				  return $cityinfo[13];
				  break;
				default:
				  return $cityinfo[12];
			}
		}


		function avg_data($data,$type){
			$temperature_sum = 0;
			$humidity_sum = 0;
			$pressure_sum = 0;
			$i = 0;
			$j = 0;
			$m = 0;
			$temperature_max = $data[0]['temperature'];
			$humidity_max = $data[0]['humidity'];
			$pressure_max = $data[0]['pressure'];
			$temperature_min = $data[0]['temperature'];
			$humidity_min = $data[0]['humidity'];
			$pressure_min = $data[0]['pressure'];
			foreach ($data as $key=>$value){
				if (isset($value['temperature']) && (strval($value['temperature']) != '暂无实况' )){
					$temperature_sum = $temperature_sum + $value['temperature'];
					if ($value['temperature'] > $temperature_max){$temperature_max = $value['temperature'];}
					if ($value['temperature'] < $temperature_min){$temperature_min = $value['temperature'];}
					$i++;
				}
				if (isset($value['humidity']) && (strval($value['humidity']) != '暂无实况' )){
					$humidity_sum = $humidity_sum + $value['humidity'];
					if ($value['humidity'] > $humidity_max){$humidity_max = $value['humidity'];}
					if ($value['humidity'] < $humidity_min){$humidity_min = $value['humidity'];}
					$j++;
				}
				if (isset($value['pressure']) && (strval($value['pressure']) != '暂无实况' )){
					$pressure_sum = $pressure_sum + $value['pressure'];
					if ($value['pressure'] > $pressure_max){$pressure_max = $value['pressure'];}
					if ($value['pressure'] < $pressure_min){$pressure_min = $value['pressure'];}
					$m++;
				}
			}
			if($type == 0){
				return array(
					'temperature' => ($i == 0) ? 0:($temperature_sum / $i),
					'humidity' => ($j == 0) ? 0:($humidity_sum / $j),
					'pressure' => ($m == 0) ? 0:($pressure_sum / $m),
					'dateTime' => $data[0]['dateTime']
				);
			} else{
				// echo $i.'      '.$j.'      '.$m.'__________';
				return array(
					'avg' => array(
						'temperature' => ($i == 0) ? 0:($temperature_sum / $i),
						'humidity' => ($j == 0) ? 0:($humidity_sum / $j),
						'pressure' => ($m == 0) ? 0:($pressure_sum / $m)
					),
					'max' => array(
						'temperature' => $temperature_max,
						'humidity' => $humidity_max,
						'pressure' => $pressure_max
					),
					'min' => array(
						'temperature' => $temperature_min,
						'humidity' => $humidity_min,
						'pressure' => $pressure_min
					)
				);
			}
			
		}

		function error_data($grid_mobile,$grid_station){
			if(($grid_mobile['temperature'] != 0) && ($grid_station['temperature'] != '暂无实况')){
				$temperature = $grid_mobile['temperature'] - $grid_station['temperature'];
			} else {
				$temperature = 0;
			}
			if(($grid_mobile['humidity'] != 0) && ($grid_station['humidity'] != '暂无实况')){
				$humidity = $grid_mobile['humidity'] - $grid_station['humidity'];
			} else {
				$humidity = 0;
			}
			if(($grid_mobile['pressure'] != 0) && ($grid_station['pressure'] != '暂无实况')){
				$pressure = $grid_mobile['pressure'] - $grid_station['pressure'];
			} else {
				$pressure = 0;
			}
			return array(
				'temperature' => $temperature,
				'humidity' => $humidity,
				'pressure' => $pressure,
				'dateTime' => $grid_station['dateTime']
			);
		}

		// 得到14个区天气数据
		$sql="select * from cityinfo order by id desc limit 14" ;   
        $cityinfo=$this->db->query($sql)->result_array();

		//得到sensor数据
		// $sql="select device_id as deviceId, time as dateTime, e_temperature as temperature, humidity as humidity, pressure as pressure, latitude as latitude, longitude as longitude, district as district from device_realtime_data" ;   
		$sql="select id as deviceId, t as temperature, h as humidity, p as pressure, lat as latitude, lng as longitude, dist as district from web_demo" ;   
        $sensor=$this->db->query($sql)->result_array();
        foreach ($sensor as $key => $value) {
        	$sensor[$key]['dateTime'] = date('Y-m-d H:i:s',time());
        }

		//计算栅格内sensor数据
		$grid_sensor = array();
		$location_data = array();
		foreach ($sensor as $key=>$value){
			if(isset($value['latitude']) && isset($value['longitude'])){
				$latitude = 31.8733;
				$longitude = 120.862372;
				$latitude_interval = (31.8733-30.6740535)*6/267;//0.0044915599250936
				$longitude_interval = (122.00772-120.862372)*6/(255*cos(31.2*3.1415926/180));//0.0044915607843137
				
				//栅格id
				$id = floor( ( $value['longitude'] - 120.862372 ) / $longitude_interval ) + 255 * floor( ( 31.8733 - $value['latitude'] ) / $latitude_interval );
				
				//计算栅格坐标
				$latitude_1 = $latitude - floor( $id / 255 ) * $latitude_interval;
				$longitude_1 = $longitude + ( $id % 255 ) * $longitude_interval;
				
				$location_data[$id] = array(
					'latitude_1' => $latitude_1,
					'latitude_2' => $latitude_1 - $latitude_interval,
					'longitude_1' => $longitude_1,
					'longitude_2' => $longitude_1 + $longitude_interval,
				); 

				$grid_sensor[$id][] = $value;
			} else{
				unset($sensor[$key]);
			}
		}

		$grid = array();
		$error_data = array();
		foreach ($grid_sensor as $key=>$value){
			$grid_mobile = avg_data($value,0);
			$grid_station = station_data($value[0]['district'],$cityinfo);  
			$grid_error = error_data($grid_mobile,$grid_station);
			$data = array(
				'location' => $location_data[$key],
				'mobile' => $grid_mobile,
				'station' => $grid_station,
				'error' => $grid_error
			);
			$error_data[] = $grid_error;
			$grid[] = $data;
		}

		$stats = array(
			'mobile' => avg_data($sensor,1),
			'station' => avg_data($cityinfo,1),
			'error' => avg_data($error_data,1),
		);

		$this->load->view('header');
		$this->load->view('home/index',array(
			'sensor'=>json_encode($sensor),
			'grid'=>json_encode($grid),
			'stats'=>json_encode($stats)
		));
		$this->load->view('footer');
	}
}
?>