<?php
class users_model extends CI_Model
{
        function __construct()
        {
                parent::__construct();
                $this->load->database();
        }
        function getall($limit,$query_name)
        {
                $sql="select user_id,username,email from user where username like '%$query_name%' limit $limit";
                $result=$this->db->query($sql)->result_array();
                return $result;
        }
        function get_by_userid($user_id)
        {
                $this->db->where('user_id',$user_id);
                $this->db->select('user_id,username,email,token,role');
                $query=$this->db->get('user');
                return $query->row_array();
        }
        function get_by_username_password($username,$password)
        {
                $sql="select user_id,token from user where username='$username' and password='$password'";
                return $this->db->query($sql)->row_array();
        }


        function del_by_userid($user_id)
        {            
                $this->db->where('user_id',$user_id);
                return $this->db->delete('user');
        }
        function insert($data)
        {        
                return $this->db->insert('user',$data);   
        }
        function update($data)
        {   
                $user_id=$data['user_id'];
                $this->db->where('user_id',$user_id);     
                return $this->db->update('user',$data);    
        }



}