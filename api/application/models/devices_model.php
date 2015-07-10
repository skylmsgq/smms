<?php
class devices_model extends CI_Model
{
        function __construct()
        {
                parent::__construct();
                $this->load->database();
        }
        function get_by_deviceid($device_id)
        {
                $this->db->where('device_id',$device_id);
                $this->db->select('*');
                $query=$this->db->get('device');
                return $query->row_array();
        }
        function del_by_deviceid($device_id)
        {            
                $this->db->where('device_id',$device_id);
                return $this->db->delete('device');
        }
        function insert($data)
        {        
                return $this->db->insert('device',$data);    
        }
        function update($data)
        {   
                $device_id=$data['device_id'];
                $this->db->where('device_id',$device_id);     
                return $this->db->update('device',$data);    
        }



}