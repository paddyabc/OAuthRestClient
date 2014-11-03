package com.paddyapp.restclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MessageBoxController {
	
	
	@FXML private Label messageTxt;
	private Stage dialogStage;
	
	public void setDialogStage(Stage stage){
		this.dialogStage = stage;
	}
	
	public void onClickOk(){
		dialogStage.close();
	}
	
	public void setMessage(String message){
		messageTxt.setText(message);
	}
}
