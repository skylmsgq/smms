<?php defined('BASEPATH') OR exit('No direct script access allowed');

// This can be removed if you use __autoload() in config.php OR use Modular Extensions
require APPPATH.'/libraries/Auth_Controller.php';

class users_devices extends Auth_Controller
{
    function __construct()
    {
        parent::__construct();
        $this->load->model('users_devices_model');
    }

    function index_get($user_id='',$device_id='')
    {
        if($user_id!=='' && !$device_id){//返回user_id用户下面的设备列表
            $content=$this->input->get();
            $count=count($content);
            $limit=10;
            if($content!=0){
                if(array_key_exists('limit',$content)){$limit=$content['limit'];}
                else
                    $this->response(array('error' => 'invalid parameter'), 404);
            }
            $result = $this->users_devices_model->get_devlist_by_userid_limit($user_id,$limit);
            if($result)
                $this->response($result, 200);
            else
                $this->response(array('error' => 'no such user_id'), 404);
        }
        else if ($user_id!=='' && $device_id!==''){//返回device_id设备的具体信息
            $tmpresult=$this->users_devices_model->get_devlist_by_userid($user_id);
            $tmp=array('device_id' => $device_id);
            if(in_array($tmp,$tmpresult) ){//设备列表里有才能获得信息
                $result=$this->users_devices_model->get_device($device_id);
                $this->response($result, 200);
            }
            else{
                $this->response(array('error' => 'the device is not under the list of this user'), 404);
            }
        }
        else{
            $this->response(array('error' => 'invalid url'), 404);
        }
    }

    function index_post($user_id='',$device_id='')
    {
        if($user_id!=='' && !$device_id){//绑定device_owner
            $data=json_decode(file_get_contents('php://input'),true);
            if($data!=null){
                if(array_key_exists('device_id',$data)){
                    $result=$this->users_devices_model->get_owner_num($data['device_id']);
                    if(!array_key_exists('owner_num',$result)){
                        $this->response(array('error' => 'the device_id is wrong'), 404);
                    }
                    else if(array_key_exists('owner_num',$result)&&$result['owner_num']==0){
                        $data['user_id']=$user_id;
                        $data['bind_time']=date("Y-m-d G:i:s");
                        $data['role']='device_owner';
                        if($this->users_devices_model->insert($data)&&
                            $this->users_devices_model->set_owner_num_to_one($data['device_id'])){
                            $this->response($data, 200);
                        }
                        else{
                            $this->response(array('error' => 'bound error'), 404);
                        }
                    }
                    else if(array_key_exists('owner_num',$result)&&$result['owner_num']==1){
                        $this->response(array('error' => 'the device is already bounded'), 404);
                    }
                    else{
                        $this->response(array('error' => 'wrong'), 404);
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
        else if($user_id!=='' && $device_id!==''){//绑定device_user
            $content=json_decode(file_get_contents('php://input'),true);
            if($content!=null){
                if(array_key_exists('authorization_code',$content)){//有 authorization_code
                    $result=$this->users_devices_model->get_auth_code_by_deviceid($device_id);
                    if(!array_key_exists('authorization_code',$result)){
                        $this->response(array('error' => 'the device_id is wrong'), 404);
                    }
                    else if($content['authorization_code']==$result['authorization_code']){//authorization_code 匹配
                        $data['device_id']=$device_id;
                        $data['user_id']=$user_id;
                        $data['bind_time']=date("Y-m-d G:i:s");
                        $data['role']='device_user';
                        if($this->users_devices_model->insert($data)&&
                            $this->users_devices_model->add_one_to_user_num($device_id)){
                            $this->response($data, 200);
                        }
                        else{
                            $this->response(array('error' => 'binding error'), 404);
                        }
                    }
                    else{
                        $this->response(array('error' => 'invalid authorization_code'), 404);
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
            $this->response(array('error' => 'invalid url'), 404);
        }
    }

    function index_delete($user_id='',$device_id='')
    {
        if($user_id!=='' && $device_id!==''){//只解绑user关系
            $tmpresult=$this->users_devices_model->get_userdevlist_by_userid($user_id);
            $tmp=array('device_id' => $device_id);
            if(in_array($tmp,$tmpresult) ){//user的设备列表里有才能解绑定
                if($this->users_devices_model->del_by_userid_deviceid($user_id,$device_id)&&
                    $this->users_devices_model->minus_one_from_user_num($device_id)){
                    $result['status']='ok';
                    $this->response($result, 200);                    
                }
                else{
                    $this->response(array('error' => 'unbinding error'), 404);
                }
            }
            else{
                $this->response(array('error' => 'the user has no device_user relationship with the device'), 404);
            }
        }
        else{
            $this->response(array('error' => 'invalid url'), 404);
        }
    }
}
