<?php if (!defined('BASEPATH')) {
    exit('No direct script access allowed');
}

class Auth_model extends CI_Model
{
    public function auth($user_id='')
    {
        if (!$user_id) {
            return null;
        } else {
            $user_get = $this->db->get_where('user', array('user_id' => $user_id));
            return $user_get->result();
        }
    }
}

?>
