<?php if (!defined('BASEPATH')) {
    exit('No direct script access allowed');
}

class Test_model extends CI_Model
{
    public function getAll()
    {
        $user_get = $this->db->get('user');
        return $user_get->result();
    }
}

?>
