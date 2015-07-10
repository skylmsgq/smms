package com.sjtu.util;

public class UserData {
	private String username;
	private String password;
	private String email;
	private String action;
	
	public void set_username(String name){
		this.username=name;
	}
	public void set_mail(String mc){
		this.email=mc;
	}
	public void set_password(String word){
		this.password = word;
	}
	
	public void set_action(String action){
		this.action = action;
	}
	
	public String get_username(){
		return username;
	}
	public String get_password(){
		return password;
	}
	public String get_mail(){
		return email;
	}
}
