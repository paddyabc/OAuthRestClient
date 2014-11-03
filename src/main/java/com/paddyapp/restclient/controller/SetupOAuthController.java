package com.paddyapp.restclient.controller;

import com.paddyapp.restclient.pojo.OAuthConfig;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SetupOAuthController {

	@FXML private TextField consumerKey;
	@FXML private TextField consumerSecret;
	@FXML private TextField tokenKey;
	@FXML private TextField tokenSecret;
	@FXML private TextField verifier;
	@FXML private TextField reqTokenUrl;
	@FXML private TextField accessTokenUrl;
	@FXML private TextField callback;
	
	private Stage dialogStage;
	private OAuthConfig config;
	private boolean okClicked = false;
	
	public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
	
	public void setConfig(OAuthConfig config){
		this.config = config;
		
//		consumerKey.textProperty().bind(config.getProperty("consumerKey"));
//		consumerSecret.textProperty().bind(config.getProperty("consumerSecret"));
//		tokenKey.textProperty().bind(config.getProperty("tokenKey"));
//		tokenSecret.textProperty().bind(config.getProperty("tokenSecret"));
//		verifier.textProperty().bind(config.getProperty("verifier"));
//		callback.textProperty().bind(config.getProperty("callback"));
		
		consumerKey.setText(config.getConsumerKey());
		consumerSecret.setText(config.getConsumerSecret());
		tokenKey.setText(config.getTokenKey());
		tokenSecret.setText(config.getTokenSecret());
		verifier.setText(config.getVerifier());
		callback.setText(config.getCallback());
		accessTokenUrl.setText(config.getAccessTokenUrl());
		reqTokenUrl.setText(config.getReqTokenUrl());
	}
	
	public boolean isOkClicked() {
        return okClicked;
    }
	
	public void onOkClick(){
		config.setConsumerKey(consumerKey.getText());
		config.setConsumerSecret(consumerSecret.getText());
		config.setTokenKey(tokenKey.getText());
		config.setTokenSecret(tokenSecret.getText());
		config.setVerifier(verifier.getText());
		config.setCallback(callback.getText());
		config.setReqTokenUrl(reqTokenUrl.getText());
		config.setAccessTokenUrl(accessTokenUrl.getText());
		this.okClicked = true;
		this.dialogStage.close();
	}
	
	public void onCancelClick(){
		dialogStage.close();
	}
}
