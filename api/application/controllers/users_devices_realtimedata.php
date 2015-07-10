<?php defined('BASEPATH') OR exit('No direct script access allowed');

// This can be removed if you use __autoload() in config.php OR use Modular Extensions
require APPPATH.'/libraries/Auth_Controller.php';

class users_devices_realtimedata extends Auth_Controller
{
    function __construct()
    {
        parent::__construct();
        $this->load->model('users_devices_realtimedata_model');
    }

    function index_get($user_id='')
    {
        if($user_id!==''){
            $content=$this->input->get();
            if(!$content){
                $content['order'] = 'desc';
            }

            $result=$this->users_devices_realtimedata_model->getdatas($content);
            if($result){
                $this->response($result, 200);
            }
            else{
                $this->response(array('error' => 'no realtime data'), 404);
            }
        }
        else{
            $this->response(array('error' => 'lack user_id'), 404);
        }
    }
}
