<?php
class users_devices_cityinfo_model extends CI_Model
{
        function __construct()
        {
                parent::__construct();
                $this->load->database();
        }
        
        function getdatas($data)
        {       
                $stime='2000-01-01 00:00:00';
                $etime='2020-01-01 00:00:00';
                $code='';
                $order='desc';
                $limit=10;

                if(array_key_exists('stime',$data)){$stime=$data['stime'];}
                if(array_key_exists('etime',$data)){$etime=$data['etime'];}
                if(array_key_exists('code',$data)){$code=$data['code'];}
                if(array_key_exists('order',$data)){$order=$data['order'];}
                if(array_key_exists('limit',$data)){$limit=$data['limit'];}
                $sql="select dateTime as time,code,city as district,temperature, humidity, pressure from cityinfo where (dateTime>='$stime' and dateTime<='$etime'
                and code like '%$code%') order by time $order limit $limit "; 
   
                $result=$this->db->query($sql)->result_array();
                return $result; 
        }
}