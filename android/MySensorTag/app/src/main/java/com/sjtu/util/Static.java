package com.sjtu.util;

public class Static {
public static String usname="";
public static boolean islogin;
public void set_usname(String name){
	usname=name;
}
public void set_login(boolean goin){
	islogin=goin;
}
public String get_usname() {
    return usname;
}
public boolean get_login(){
	return islogin;
}
}
