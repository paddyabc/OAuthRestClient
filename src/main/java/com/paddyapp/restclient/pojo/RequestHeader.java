package com.paddyapp.restclient.pojo;

import javafx.beans.property.SimpleStringProperty;

public class RequestHeader {

	private SimpleStringProperty key;
	private SimpleStringProperty value;
	
	public RequestHeader(String key, String value){
		this.key = new SimpleStringProperty(key);
		this.value = new SimpleStringProperty(value);
	}
	
	public String getKey() {
		return key.get();
	}
	public void setKey(String key) {
		this.key.set(key);;
	}
	public String getValue() {
		return value.get();
	}
	public void setValue(String value) {
		this.value.set(value);
	}
	
	
}
