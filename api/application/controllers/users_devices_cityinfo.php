<?php defined('BASEPATH') OR exit('No direct script access allowed');

// This can be removed if you use __autoload() in config.php OR use Modular Extensions
require APPPATH.'/libraries/Auth_Controller.php';

class users_devices_cityinfo extends Auth_Controller
{
    function __construct()
    {
        parent::__construct();
        $this->load->model('users_devices_cityinfo_model');
    }

    function index_get($user_id='')
    {   
        if($user_id!==''){
            $content=$this->input->get();
            if(!$content){
                $content['order'] = 'desc';
            }
            //$this->response("%".$content."%", 200);

            $result=$this->users_devices_cityinfo_model->getdatas($content);
            if($result){
                $this->response($result, 200);
            }
            else{
                $this->response(array('error' => 'no cityinfo data'), 404);
            }
        }
        else{
            $this->response(array('error' => 'lack user_id'), 404);
        }
    }
}
