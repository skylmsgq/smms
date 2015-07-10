<?php defined('BASEPATH') OR exit('No direct script access allowed');

// This can be removed if you use __autoload() in config.php OR use Modular Extensions
require APPPATH.'/libraries/Auth_Controller.php';

class users extends Auth_Controller
{
    function __construct()
    {
        parent::__construct();
        //users_model,载入之后，可用使用$this->users_model来操作
        $this->load->model('users_model');
    }

    function index_get($user_id='')
    {
        if($user_id){ //获得指定user_id的信息
            $result=$this->users_model->get_by_userid($user_id);
            if($result)
                $this->response($result, 200);
            else{
                $this->response(array('error' => 'no such user_id'), 404);
            }
        }
        else{
            $content=$this->input->get();
            $count=count($content);
            if($content==0){//获得所有的用户，默认返回10条
                $limit=10;
                $query_name='';
                $result = $this->users_model->getall($limit,$query_name);
                if($result){
                    $finalresult['users']=$result;
                    $this->response($finalresult, 200);
                }
                else
                    $this->response(array('error' => 'users could not be found'), 404);
            }
            else if($count>=1){//可指定Limit参数
                $limit=10;
                $query_name='';
                if(array_key_exists('limit',$content)){$limit=$content['limit'];}
                if(array_key_exists('query_name',$content)){$query_name=$content['query_name'];}
                $result = $this->users_model->getall($limit,$query_name);
                if($result)
                    $this->response($result, 200);
                else
                    $this->response(array('error' => 'users could not be found'), 404);
            }
            else{
                $this->response(array('error' => 'invalid url'), 404);
            }
        }
    }

    function index_post($user_id='')
    {
        if($user_id){ //通过user_id来更新用户
            $data=json_decode(file_get_contents('php://input'),true);
            if($data!=null){
                if(array_key_exists('username',$data)||array_key_exists('password',$data)||array_key_exists('email',$data)){
                    $data['user_id']=$user_id;
                    $result=$this->users_model->get_by_userid($user_id);
                    if($result){
                        if($this->users_model->update($data)){
                            $this->response($data, 200);
                        }
                        else{
                            $this->response(array('error' => 'update error'), 404);
                        }
                    }
                    else
                        $this->response(array('error' => 'no such user_id'),404);
                }
                else{
                    $this->response(array('error' => 'invalid body'), 404);
                }
            }
            else{
                $this->response(array('error' => 'body is null'), 404);
            }
        }
        else{//新增用户或者登陆已有的用户去拿token
            $data=json_decode(file_get_contents('php://input'),true);
            if($data!=null){
                if(array_key_exists('action',$data)&&$data['action']=='register'&&
                    array_key_exists('username',$data)&&
                    array_key_exists('password',$data)&&array_key_exists('email',$data)){
                    $data1['user_id']=md5(implode('',$data));
                    $tmpdata=$data1;
                    $data1['reg_time']=date("Y-m-d G:i:s");
                    $data1['token']=md5(implode('',$tmpdata));
                    $data1['username']=$data['username'];
                    $data1['password']=$data['password'];
                    $data1['email']=$data['email'];
                    $data1['device_owner_num']=0;
                    $data1['device_user_num']=0;
                    $data1['enabled']=1;
                    $data1['role']='member';
                    if($this->users_model->insert($data1)){
                        $inserted=$this->users_model->get_by_userid($data1['user_id']);
                        $this->response($inserted, 200);
                    }
                    else{
                        $this->response(array('error' => 'register error'), 404);
                    }
                }else if (array_key_exists('action',$data)&&$data['action']=='login'&&
                    array_key_exists('username',$data)&&array_key_exists('password',$data)){
                    $found=$this->users_model->get_by_username_password($data['username'],$data['password']);
                    if($found){
                        $this->response($found, 200);
                    }
                    else{
                        $this->response(array('error' => 'login error'), 404);
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

    function index_delete($user_id='')
    {
        if($user_id){  //通过user_id来删除用户
            if($this->users_model->get_by_userid($user_id)){
                if($this->users_model->del_by_userid($user_id)){
                    $result['status']='ok';
                    $this->response($result, 200);                   
                }
                else{
                    $this->response(array('error' => 'delete error'), 404);
                }
            }
            else{
                $this->response(array('error' => 'no such user'), 404);
            }
        }
        else{
            $this->response(array('error' => 'invalid url'), 404);
        }
    }
}
