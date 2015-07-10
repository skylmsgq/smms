<?php if (!defined('BASEPATH')) {
    exit('No direct script access allowed');
}

date_default_timezone_set("Etc/GMT");//这是格林威治标准时间,得到的时间和默认时区是一样的

require APPPATH.'/libraries/Auth_Controller.php';

class Test extends Auth_Controller
{
    public function test1()
    {
        $this->load->model('Test_model', 'user');
        $user_list = $this->user->getAll();
        $user_list_json = json_encode($user_list);
        var_dump($user_list_json);

        $headers = $this->input->request_headers();
        var_dump($headers);
        //var_dump($headers["X-api-key"]);
        $api_key = $this->input->get_request_header('X-api-key', TRUE);
        var_dump($api_key);

        $user_id = $this->input->get_request_header('X-user-id', TRUE);
        var_dump($user_id);

        $time = time();
        var_dump($time);

        echo date('Y-m-d H:i:s', $time);

        echo '0='.$this->uri->segment(0);
        echo '1='.$this->uri->segment(1);
        echo '2='.$this->uri->segment(2);
        echo '3='.$this->uri->segment(3);
        echo '4='.$this->uri->segment(4);
        echo '5='.$this->uri->segment(5);

        var_dump($this->input->server('REQUEST_METHOD'));
    }
}

?>
