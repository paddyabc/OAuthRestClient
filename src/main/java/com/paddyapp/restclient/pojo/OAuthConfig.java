package com.paddyapp.restclient.pojo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class OAuthConfig {
	private StringProperty consumerKey;
	private StringProperty consumerSecret;
	private StringProperty tokenKey;
	private StringProperty tokenSecret;
	private StringProperty verifier;
	private StringProperty callback;
	private StringProperty accessTokenUrl;
	private StringProperty reqTokenUrl;
	
	public OAuthConfig(){
		this.consumerKey = new SimpleStringProperty();
		this.consumerSecret = new SimpleStringProperty();
		this.tokenKey = new SimpleStringProperty();
		this.tokenSecret = new SimpleStringProperty();
		this.verifier = new SimpleStringProperty();
		this.callback = new SimpleStringProperty();
		this.accessTokenUrl = new SimpleStringProperty();
		this.reqTokenUrl = new SimpleStringProperty();
	}
	
	public StringProperty getProperty(String name){
		try {
			return (StringProperty)this.getClass().getDeclaredField(name).get(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	 
	public String getConsumerKey() {
		return consumerKey.get();
	}
	public void setConsumerKey(String consumerKey) {
		this.consumerKey.set(consumerKey);
	}
	public String getConsumerSecret() {
		return consumerSecret.get();
	}
	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret.set( consumerSecret);
	}
	public String getTokenKey() {
		return tokenKey.get();
	}
	public void setTokenKey(String tokenKey) {
		this.tokenKey.set(tokenKey);
	}
	public String getTokenSecret() {
		return tokenSecret.get();
	}
	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret.set(tokenSecret);
	}
	public String getVerifier() {
		return verifier.get();
	}
	public void setVerifier(String verifier) {
		this.verifier.set(verifier);
	}
	public String getCallback() {
		return callback.get();
	}
	public void setCallback(String callback) {
		this.callback.set(callback);
	}
	public void setReqTokenUrl(String url){
		this.reqTokenUrl.set(url);
	}
	public String getReqTokenUrl(){
		return this.reqTokenUrl.get();
	}
	public void setAccessTokenUrl(String url){
		this.accessTokenUrl.set(url);
	}
	public String getAccessTokenUrl(){
		return this.accessTokenUrl.get();
	}
}
