package com.sjtu.DataType;

public class ToReport {
	//The data type to report 

	private String time;
	
	private String longitude;
	private String latitude;
	private String e_temperature;
	private String humidity;
	private String pressure;
	private String s_temperature;
	private String indoor;
	
	
	public String get_time() {
		return this.time; 
	}
	
	public String get_longitude() {
		return this.longitude;
	}
	
	public String get_latitude() {
		return this.latitude;
	}
	
	public String get_eTemp() {
		return this.e_temperature;
	}
	
	public String get_sTemp() {
		return this.s_temperature;
	}
	
	public String get_humidity() {
		return this.humidity ;
	}
	
	public String get_pressure() {
		return this.pressure;
	}
	public String get_indoor() {
		return this.indoor;
	}
	
	public void set_time(String time) {
		this.time = time;
	}
	
	public void set_longitude(String longtitude) {
		this.longitude = longtitude;
	}
	
	public void set_latitude(String latitude) {
		this.latitude = latitude;
	}
	
	public void set_eTemp(String e_temp) {
		this.e_temperature = e_temp;
	}
	
	public void set_sTemp(String s_temp) {
		this.s_temperature = s_temp;
	}
	
	public void set_humidity(String humidity) {
		this.humidity =humidity;
	}
	
	public void set_pressure(String pressure) {
		this.pressure = pressure;
	}
	public void set_indoor(String indoor) {
		this.indoor = indoor;
	}

}
