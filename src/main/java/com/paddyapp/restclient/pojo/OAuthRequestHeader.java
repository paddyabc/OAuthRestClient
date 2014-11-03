package com.paddyapp.restclient.pojo;

public class OAuthRequestHeader extends RequestHeader {

	private OAuthConfig oauthConfig;
	
	public OAuthRequestHeader(OAuthConfig config) {
		super("Authorization", "<auto generated content>");
		this.oauthConfig = config;
	}

	public OAuthConfig getOauthConfig() {
		return oauthConfig;
	}

	public void setOauthConfig(OAuthConfig oauthConfig) {
		this.oauthConfig = oauthConfig;
	}
	
}
