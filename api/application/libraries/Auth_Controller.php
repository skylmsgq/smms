<?php defined('BASEPATH') or exit('No direct script access allowed');

/**
 * CodeIgniter Authentication Controller
 *
 *
 * @package         CodeIgniter
 * @subpackage      Libraries
 * @category        Libraries
 * @author          Bin Zhang
 * @license
 * @link
 * @version
 */

/*
    含义              解释
200 OK              确认GET、PUT和DELETE操作成功
201 Created         确认POST操作成功
304 Not Modified    用于条件GET访问，告诉客户端资源没有被修改
400 Bad Request     通常用于POST或者PUT请求，表明请求的内容是非法的
401 Unauthorized    需要授权
403 Forbidden       没有访问权限
404 Not Found       服务器上没有资源
405 Method Not Allowed  请求方法不能被用于请求相应的资源
409 Conflict        访问和当前状态存在冲突
*/

// This can be removed if you use __autoload() in config.php OR use Modular Extensions
require APPPATH.'/libraries/REST_Controller.php';

class Auth_Controller extends REST_Controller {
    /**
     * Constructor function
     * @author Bin Zhang
     * @todo
     */
    public function __construct() {

        parent::__construct();

        $request_method = $this->input->server('REQUEST_METHOD');
        $segment_1 = $this->uri->segment(1);
        $segment_2 = $this->uri->segment(2);
        $segment_3 = $this->uri->segment(3);
        $segment_4 = $this->uri->segment(4);
        $segment_5 = $this->uri->segment(5);

        // POST     /users/     anyone
        if ((($request_method == 'POST') && ($segment_1 == 'users') && (!$segment_2))) {

        } else {
            // 获取HTTP请求头中的X-USER-ID和X-Auth-Token
            #$user_id = $this->input->get_request_header('X-user-id', TRUE);
            #$token = $this->input->get_request_header('X-auth-token', TRUE);
            if (!function_exists('getallheaders')) {
                foreach ($_SERVER as $name => $value) {
                    /* RFC2616 (HTTP/1.1) defines header fields as case-insensitive entities. */
                    if (strtolower(substr($name, 0, 5)) == 'http_') {
                        $headers[str_replace(' ', '-', ucwords(strtolower(str_replace('_', ' ', substr($name, 5)))))] = $value;
                    }
                }
                $this->request_headers = $headers;
                $user_id = $this->request_headers['X-User-Id'];
                $token = $this->request_headers['X-Auth-Token'];
            } else {
                #$this->request_headers = getallheaders();
                #$user_id = $this->request_headers['X-user-id'];
                #$token = $this->request_headers['X-auth-token'];
                $user_id = $this->input->get_request_header('X-user-id', TRUE);
                $token = $this->input->get_request_header('X-auth-token', TRUE);
            }

            if (!$user_id || !$token) {
                $this->response(null, 401); //HTTP请求中缺少X-USER-ID或X-API-KEY，需要授权
            }

            $this->load->model('Auth_model', 'auth');
            $user_get = $this->auth->auth($user_id);
            if($user_get){
                $user_get = json_decode(json_encode($user_get[0]),true);
                if (!$user_get) {
                    $this->response(null, 404); //该X-USER-ID对应的用户不存在
                } else if (!$user_get['token']) {
                    $this->response(null, 404); //该X-USER-ID对应的用户没有X-Auth-Token
                } else if ($token != $user_get['token']) {
                    $this->response(null, 403); //X-USER-ID与X-Auth-Token不对应，没有访问权限
                } else if ($user_get['role'] == 'member') {
                    //以下API是member用户没有权限访问的
                    // 1 DELETE   /users/{user_id}/       admin
                    // 2 GET      /users/                 admin
                    // 3 POST     /devices/               admin
                    // 4 DELETE   /devices/{device_id}/   admin
                    // 5 POST     /devices/{device_id}/   admin
                    // 6 GET      /devices/{device_id}/   admin

                    // var_dump($segment_1);
                    // 1 DELETE   /users/{user_id}/       admin
                    if (($request_method == 'DELETE') && ($segment_1 == 'users') && ($segment_2) && (!$segment_3)) {
                        $this->response(null, 403);
                    }
                    // 2 GET      /users/                 admin
                    if (($request_method == 'GET') && ($segment_1 == 'users') && (!$segment_2)) {
                        $this->response(null, 403);
                    }

                    // 3 POST     /devices/               admin  权限改为user
                    // if (($request_method == 'POST') && ($segment_1 == 'devices') && (!$segment_2)) {
                    //     $this->response(null, 403);
                    // }

                    // 4 DELETE   /devices/{device_id}/   admin
                    if (($request_method == 'DELETE') && ($segment_1 == 'devices') && ($segment_2) && (!$segment_3)) {
                        $this->response(null, 403);
                    }
                    // 5 POST     /devices/{device_id}/   admin
                    if (($request_method == 'POST') && ($segment_1 == 'devices') && ($segment_2) && (!$segment_3)) {
                        $this->response(null, 403);
                    }
                    // 6 GET      /devices/{device_id}/   admin
                    if (($request_method == 'GET') && ($segment_1 == 'devices') && ($segment_2) && (!$segment_3)) {
                        $this->response(null, 403);
                    }
                    //member用户只有当请求的user_id等于自己的user_id时才能访问以下API
                    // 1 POST       /users/{user_id}/           admin/member
                    // 2 GET        /users/{user_id}/           admin/member
                    // 3 POST       /users/{user_id}/devices/   admin/member
                    // 4 POST       /users/{user_id}/devices/{device_id}/   admin/member
                    // 5 DELETE     /users/{user_id}/devices/{device_id}/   admin/member
                    // 6 GET        /users/{user_id}/devices/{device_id}/   admin/member
                    // 7 GET        /users/{user_id}/devices/               admin/member
                    // 8 POST       /users/{user_id}/devices/{device_id}/data/  admin/member
                    // 9 GET        /users/{user_id}/devices/{device_id}/data/  admin/member
                    //10 POST       /devices/                                   admin/member
                    //11 GET        /users/{user_id}/realtimedate               admin/member
                    //12 GET        /users/{user_id}/cityinfo                   admin/member
                    if (($request_method == 'POST') && ($segment_1 == 'users') && ($segment_2 != $user_get['user_id']) && (!$segment_3)) {
                        $this->response(null, 403);
                    }
                    // 2 GET        /users/{user_id}/           admin/member
                    if (($request_method == 'GET') && ($segment_1 == 'users') && ($segment_2 != $user_get['user_id']) && (!$segment_3)) {
                        $this->response(null, 403);
                    }
                    // 3 POST       /users/{user_id}/devices/   admin/member
                    if (($request_method == 'POST') && ($segment_1 == 'users') && ($segment_2 != $user_get['user_id']) && ($segment_3 == 'devices') && (!$segment_4)) {
                        $this->response(null, 403);
                    }
                    // 4 POST       /users/{user_id}/devices/{device_id}/   admin/member
                    if (($request_method == 'POST') && ($segment_1 == 'users') && ($segment_2 != $user_get['user_id']) && ($segment_3 == 'devices') && ($segment_4) && (!$segment_5)) {
                        $this->response(null, 403);
                    }
                    // 5 DELETE     /users/{user_id}/devices/{device_id}/   admin/member
                    if (($request_method == 'DELETE') && ($segment_1 == 'users') && ($segment_2 != $user_get['user_id']) && ($segment_3 == 'devices') && ($segment_4) && (!$segment_5)) {
                        $this->response(null, 403);
                    }
                    // 6 GET        /users/{user_id}/devices/{device_id}/   admin/member
                    if (($request_method == 'GET') && ($segment_1 == 'users') && ($segment_2 != $user_get['user_id']) && ($segment_3 == 'devices') && ($segment_4) && (!$segment_5)) {
                        $this->response(null, 403);
                    }
                    // 7 GET        /users/{user_id}/devices/               admin/member
                    if (($request_method == 'GET') && ($segment_1 == 'users') && ($segment_2 != $user_get['user_id']) && ($segment_3 == 'devices') && (!$segment_4)) {
                        $this->response(null, 403);
                    }
                    // 8 POST       /users/{user_id}/devices/{device_id}/data/  admin/member
                    if (($request_method == 'POST') && ($segment_1 == 'users') && ($segment_2 != $user_get['user_id']) && ($segment_3 == 'devices') && ($segment_5 == 'data')) {
                        $this->response(null, 403);
                    }
                    // 9 GET        /users/{user_id}/devices/{device_id}/data/  admin/member
                    if (($request_method == 'GET') && ($segment_1 == 'users') && ($segment_2 != $user_get['user_id']) && ($segment_3 == 'devices') && ($segment_5 == 'data')) {
                        $this->response(null, 403);
                    }
                    //11 GET        /users/{user_id}/realtimedate               admin/member
                    if (($request_method == 'POST') && ($segment_1 == 'users') && ($segment_2 != $user_get['user_id']) && ($segment_3 == 'realtimedate') && (!$segment_4)) {
                        $this->response(null, 403);
                    }
                    //12 GET        /users/{user_id}/weather                   admin/member
                    if (($request_method == 'POST') && ($segment_1 == 'users') && ($segment_2 != $user_get['user_id']) && ($segment_3 == 'weather') && (!$segment_4)) {
                        $this->response(null, 403);
                    }
                }
            }
            else{
                $this->response(array('error' => 'no such user'), 404);
            }
        }

    }
    /**
     * Destructor function
     * @author Bin Zhang
     * @todo
     */
    public function __destruct() {

    }
}

?>
