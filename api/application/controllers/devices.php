<?php defined('BASEPATH') OR exit('No direct script access allowed');

// This can be removed if you use __autoload() in config.php OR use Modular Extensions
require APPPATH.'/libraries/Auth_Controller.php';

class devices extends Auth_Controller
{
    function __construct()
    {
        parent::__construct();
        $this->load->model('devices_model');

    }

    function index_get($device_id='')
    {
        if($device_id){ //通过device_id来获取
            $result=$this->devices_model->get_by_deviceid($device_id);
            if($result)
                $this->response($result, 200);
            else{
                $this->response(array('error' => 'no such device_id'), 404);
            }
        }
        else{
            $this->response(array('error' => 'invalid url'), 404);
        }
    }

    function index_post($device_id='')
    {
        if($device_id){  //通过device_id来更新
            $data=json_decode(file_get_contents('php://input'),true);
            if($data!=null){
                if(array_key_exists('device_name',$data)){
                    $data['device_id']=$device_id;
                    $result=$this->devices_model->get_by_deviceid($device_id);
                    if($result){
                        if($this->devices_model->update($data)){
                            $this->response($data, 200);
                        }
                        else{
                            $this->response(array('error' => 'update error'), 404);
                        }                       
                    }
                    else
                        $this->response(array('error' => 'no such device_id'),404);
                }
                else{
                    $this->response(array('error' => 'invalid body'), 404);
                }
            }
            else{
                $this->response(array('error' => 'body is null'), 404);
            }
        }
        else{//新增设备
            $data=json_decode(file_get_contents('php://input'),true);
            if($data!=null){
                if(array_key_exists('device_id',$data) && array_key_exists('device_name',$data)){
                    $tmpdata=$data;
                    $data['active_time']=date("Y-m-d G:i:s");
                    $data['owner_num']=0;
                    $data['user_num']=0;
                    $data['authorization_code']=md5(implode('',$tmpdata));
                    $data['enabled']=1;
                    if($this->devices_model->insert($data)){
                        $this->response($data, 200);
                    }
                    else{
                        $this->response(array('error' => 'insert error'), 404);
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
    }

    function index_delete($device_id='')
    {
        if($device_id){   //通过device_id来删除设备
            if($this->devices_model->get_by_deviceid($device_id)){
                if($this->devices_model->del_by_deviceid($device_id)){
                    $result['status']='ok';
                    $this->response($result, 200);                    
                }
                else{
                    $this->response(array('error' => 'delete error'), 404);
                }
            }
            else{
                $this->response(array('error' => 'no such device_id'), 404);
            }
        }
        else{
            $this->response(array('error' => 'invalid url'), 404);
        }
    }
}
