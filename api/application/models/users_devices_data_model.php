<?php
class users_devices_data_model extends CI_Model
{
        function __construct()
        {
                parent::__construct();
                $this->load->database();
        }
        
        function getdatas($data)
        {        
                $device_id=$data['device_id'];
                $stime='2000-01-01 00:00:00';
                $etime='2020-01-01 00:00:00';
                $wlongt='115';
                $elongt='130';
                $nlatit='32';
                $slatit='20';
                $order='desc';
                $limit=10;

                if(array_key_exists('stime',$data)){$stime=$data['stime'];}
                if(array_key_exists('etime',$data)){$etime=$data['etime'];}
                if(array_key_exists('wlongt',$data)){$wlongt=$data['wlongt'];}
                if(array_key_exists('elongt',$data)){$elongt=$data['elongt'];}
                if(array_key_exists('nlatit',$data)){$nlatit=$data['nlatit'];}
                if(array_key_exists('slatit',$data)){$slatit=$data['slatit'];}
                if(array_key_exists('order',$data)){$order=$data['order'];}
                if(array_key_exists('limit',$data)){$limit=$data['limit'];}
                $sql="select * from device_data where (device_id='$device_id'
                and time>='$stime' and time<='$etime'
                and longitude>='$wlongt' and longitude<='$elongt' 
                and latitude>='$slatit' and latitude<='$nlatit') order by time $order limit $limit ";     
                $result=$this->db->query($sql)->result_array();
                return $result; 
        }

        function insert_batch($data)
        {        
                return $this->db->insert_batch('device_data',$data);    
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

}