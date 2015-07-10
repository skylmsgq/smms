<?php defined('BASEPATH') OR exit('No direct script access allowed');

// This can be removed if you use __autoload() in config.php OR use Modular Extensions
require APPPATH.'/libraries/Auth_Controller.php';

class users_devices_data extends Auth_Controller
{
    function __construct()
    {
        parent::__construct();
        $this->load->model('users_devices_data_model');
        $this->load->model('users_devices_realtimedata_model');
    }

    function index_get($user_id='',$device_id='')
    {
        if($user_id!=='' && $device_id!==''){//得到设备device_id的具体数据
            $content=$this->input->get();
            //if($content){
                // if(array_key_exists('stime',$content)&&array_key_exists('etime',$content)
                //     &&array_key_exists('wlongt',$content)&&array_key_exists('elongt',$content)
                //     &&array_key_exists('nlatit',$content)&&array_key_exists('slatit',$content))
            //{
            $tmpresult=$this->users_devices_data_model->get_devlist_by_userid($user_id);
            $tmp=array('device_id' => $device_id);
            if(in_array($tmp,$tmpresult) ){//设备列表里有才能得到数据
                $content['device_id']=$device_id;
                $result=$this->users_devices_data_model->getdatas($content);
                $this->response($result, 200);
            }
            else{
                $this->response(array('error' => 'the device is not under the list of this user'), 404);
            }
            //}
                // else{
                //     $this->response(array('error' => 'invalid parameter'), 404);
                // }
            //}
            // else{
            //     $this->response(array('error' => 'lack query parameter'), 404);
            // }
        }
        else{
            $this->response(array('error' => 'invalid url'), 404);
        }
    }

    function index_post($user_id='',$device_id='')
    {
        if($user_id!=='' && $device_id!==''){//以device_id这个设备，上传具体数据
            $tmpresult=$this->users_devices_data_model->get_devlist_by_userid($user_id);
            $tmp=array('device_id' => $device_id);
            if(in_array($tmp,$tmpresult) ){//设备列表里有才能上传数据
                $data=json_decode(file_get_contents('php://input'),true);
                if($data!=null){
                    if(array_key_exists('device_data',$data)){
                        $content=$data['device_data'];
                        $result['device_data_inserted']=count($content);
                        if($result['device_data_inserted']!=0){//判断device_data是否为空
                            foreach($content as $row){
                                $row['device_id']=$device_id;
                                $finalcontent[]=$row;
                            }
                            if($this->users_devices_data_model->insert_batch($finalcontent)){
                                //如果插入成功，把最后一条数据插入到realtimedata表
                                if(count($finalcontent)>=1){
                                    $tmp_content = $finalcontent[count($finalcontent)-1];
                                    if(!empty($tmp_content['latitude'])&&!empty($tmp_content['longitude'])&&$tmp_content['latitude']!=''&&$tmp_content['longitude']!=''){
                                        $url_request = 'http://api.map.baidu.com/geocoder?location='.floatval($tmp_content['latitude']).','.floatval($tmp_content['longitude']).'&output=json&key=28bcdd84fae25699606ffad27f8da77b';
                                        $s = file_get_contents($url_request);
                                        $sensor_location = json_decode($s,'ture');
                                        $sensor_district = $sensor_location['result']['addressComponent']['district'];
                                    }else{
                                        $sensor_district = '';
                                    }

                                    $tmp_content['district'] = $sensor_district;
                                    $finalcontent_realtime = $tmp_content;
                                    $this->users_devices_realtimedata_model->update($finalcontent_realtime);

                                }
                                $this->response($result, 200);                               
                            }
                            else{
                                $this->response(array('error' => 'insert error'), 404);
                            }                           
                        }
                        else{
                            $this->response(array('error' => 'the device_data is null'), 404);
                        }
                    }
                    else{
                        $this->response(array('error' => 'invalid body'), 404);
                    }
                }
                else{
                    $this->response(array('error' => 'body is null'), 404);
                }
            }
            else{
                $this->response(array('error' => 'the device is not under the list of this user'), 404);
            }
        }
        else{
            $this->response(array('error' => 'invalid url'), 404);
        }
    }

}
