package com.sjtu.util;

public class DeviceData {
String username;
String devicename;
String mac;
String pass;
public void set_username(String name){
	username=name;
}
public void set_mac(String name){
	mac=name;
}
public void set_pass(String passw){
	pass=passw;
}
public void set_devicename(String dn){
	devicename=dn;
}
public String get_username(){
	return username;
}
public String get_mac(){
	return mac;
}
public String get_pass(){
	return pass;
}
public String get_devicename(){
	return devicename;
}
}
