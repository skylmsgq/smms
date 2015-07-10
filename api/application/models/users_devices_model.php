<?php
class users_devices_model extends CI_Model
{
        function __construct()
        {
                parent::__construct();
                $this->load->database();
        }

        function get_device($device_id)
        {
                $sql="select device_id,device_name,owner_num,user_num,authorization_code from device where device_id='$device_id'";
                $result=$this->db->query($sql)->row_array();
                return $result;
        }

        function get_owner_num($device_id)
        {
                $sql="select owner_num from device where device_id='$device_id' ";
                $result=$this->db->query($sql)->row_array();
                return $result;
        }

        function set_owner_num_to_one($device_id)
        {
                $sql="update device set owner_num=1 where device_id='$device_id' ";
                return $this->db->query($sql);
        }

        function add_one_to_user_num($device_id)
        {
                $sql="update device set user_num=user_num+1 where device_id='$device_id' ";
                return $this->db->query($sql);
        }
        function minus_one_from_user_num($device_id)
        {
                $sql="update device set user_num=user_num-1 where device_id='$device_id' ";
                return $this->db->query($sql);
        }
        function get_auth_code_by_deviceid($device_id)
        {
                $sql="select authorization_code from device where device_id='$device_id' ";
                return $this->db->query($sql)->row_array();
        }

        function get_devlist_by_userid($user_id)
        {
                $sql="select device_id from user_device_map where user_id='$user_id'";
                $result=$this->db->query($sql)->result_array();
                return $result;
        }

        function get_devlist_by_userid_limit($user_id,$limit)
        {
                $sql="select device_id from user_device_map where user_id='$user_id' limit $limit";
                $result=$this->db->query($sql)->result_array();
                return $result;
        }
        function get_userdevlist_by_userid($user_id)
        {
                $sql="select device_id from user_device_map where user_id='$user_id' and role='device_user'";
                $result=$this->db->query($sql)->result_array();
                return $result;
        }

        function insert($data)
        {        
                return $this->db->insert('user_device_map',$data);    
        }

        function del_by_userid_deviceid($user_id,$device_id)
        {
                $sql="delete from user_device_map where user_id='$user_id' and device_id='$device_id'";            
                return $this->db->query($sql);
        }


}