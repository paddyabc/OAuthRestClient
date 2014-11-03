package com.paddyapp.restclient.controller;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.http.HttpParameters;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpCoreContext;

import com.paddyapp.restclient.App;
import com.paddyapp.restclient.pojo.OAuthConfig;
import com.paddyapp.restclient.pojo.OAuthRequestHeader;
import com.paddyapp.restclient.pojo.RequestHeader;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController implements Initializable {

	@FXML private TextField requestUrl;
	@FXML private ToggleGroup requestMethod;
	@FXML private ChoiceBox<String> contentType;
	@FXML private TableView<RequestHeader> headerTable;
	@FXML private TextArea requestBody;
	@FXML private TextArea responseBody;
	@FXML private ContextMenu headerMenu;
	@FXML private TextField headerKey;
	@FXML private TextField headerValue;
	@FXML private ListView<String> responseHeaderList;
	@FXML private ListView<String> reqHeaderList;
	@FXML private Button sendBtn;
	@FXML private Button reqTokenBtn;
	@FXML private Button accessTokenBtn;
	@FXML private BorderPane contentLayout;
	
	private ObservableList<RequestHeader> requestHeaderList = FXCollections.observableArrayList();
	private ObservableList<String> responseList = FXCollections.observableArrayList();
	private ObservableList<String> finalReqHeader = FXCollections.observableArrayList();
	private OAuthConfig oauth;
	private Map<String, RequestHeader> headerMap;
	private ThreadPoolExecutor backgroundPool;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
		oauth = new OAuthConfig();
		headerMap = new Hashtable<String, RequestHeader>();
		backgroundPool =  new ThreadPoolExecutor(2, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		
		contentType.setItems(FXCollections.observableArrayList(
				"text/plain",
				"application/json",
				"application/x-www-form-urlencoded"
		));
		contentType.setValue("text/plain");
		
		TableColumn<RequestHeader, String> keyCol = (TableColumn<RequestHeader, String>) headerTable.getColumns().get(0);
		TableColumn<RequestHeader, String> valueCol = (TableColumn<RequestHeader, String>) headerTable.getColumns().get(1);

		headerTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		keyCol.prefWidthProperty().bind(headerTable.widthProperty().divide(2));
		valueCol.prefWidthProperty().bind(headerTable.widthProperty().divide(2));
		headerTable.setItems(requestHeaderList);
		keyCol.setCellValueFactory(new PropertyValueFactory("key"));
		valueCol.setCellValueFactory(new PropertyValueFactory("value"));
		
		responseHeaderList.setItems(responseList);
		reqHeaderList.setItems(finalReqHeader);
	}
	
	public void sendRequest(){
		
		if(this.requestUrl.getText().length() > 0){
			
			try {
				RadioButton btn = (RadioButton) requestMethod.getSelectedToggle();
				String method = btn.getText();
				
				HttpRequestBase http = null;
				if(method.equals("GET")){
					http = new HttpGet(this.requestUrl.getText());
				} else if (method.equals("DELETE")){
					http = new HttpDelete(this.requestUrl.getText());
				} else if (method.equals("PUT")){
					HttpPut put = new HttpPut(this.requestUrl.getText());
					String type = contentType.getValue();
					put.addHeader("Content-type", type);
					put.setEntity(new StringEntity(this.requestBody.getText()));
					
					http = put;
				} else if (method.equals("POST")){
					HttpPost post = new HttpPost(this.requestUrl.getText());
					String type = contentType.getValue();
					post.addHeader("Content-type", type);
					post.setEntity(new StringEntity(this.requestBody.getText()));
					
					http = post;
				}
				
				if(http != null){
					for (String key : headerMap.keySet()){
						if(headerMap.get(key) instanceof OAuthRequestHeader){
							if(oauth.getConsumerKey() != null && oauth.getConsumerKey().length() > 0 &&
									oauth.getConsumerSecret() != null && oauth.getConsumerSecret().length() > 0){
								
								OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
										this.oauth.getConsumerKey(),
										this.oauth.getConsumerSecret()
								);
								
								if(oauth.getTokenKey() != null && oauth.getTokenSecret() != null && 
										oauth.getTokenKey().length() >0 && oauth.getTokenSecret().length()>0){
									consumer.setTokenWithSecret(oauth.getTokenKey(), oauth.getTokenSecret());
								}
								
								consumer.sign(http);
								
							}
						}else {
							http.addHeader(key, headerMap.get(key).getValue());
						}
					}
					
					
//					for(Header header: http.getAllHeaders()){
//						finalReqHeader.add(header.getName() +": " + header.getValue());
//					}
					
					sendHttpReqeuest(http);
				}
				
				
			} catch (Exception e){
				e.printStackTrace();
				this.createMessage(e.getMessage(), "Error");
			}
		}
	}
	
	public void requestTokenClick(){
		
		try {
			if(oauth.getReqTokenUrl() != null && oauth.getReqTokenUrl().length() > 0 
					&& oauth.getConsumerKey() != null && oauth.getConsumerSecret().length() > 0
					&& oauth.getConsumerSecret() != null && oauth.getConsumerKey().length() > 0){
				
				HttpPost post = new HttpPost(oauth.getReqTokenUrl());
				String type = contentType.getValue();
				post.addHeader("Content-type", type);
				post.setEntity(new StringEntity(this.requestBody.getText()));
				
				OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
						this.oauth.getConsumerKey(),
						this.oauth.getConsumerSecret()
				);
				String callback = oauth.getCallback() == null || oauth.getCallback().length() ==0 ? "oob" : oauth.getCallback();
				HttpParameters param = new HttpParameters();
				param.put("oauth_callback", URLEncoder.encode(callback,"UTF-8"));
				consumer.setAdditionalParameters(param);
				consumer.sign(post);
				
				sendHttpReqeuest(post);
				
			} else {
				MainController.this.createMessage("OAuth info have not been set\r\nGo to Edit->Setup OAuth", "Info");
			}
		} catch (Exception e){
			e.printStackTrace();
			MainController.this.createMessage(e.getMessage(), "Error");
		}
	}
	
	public void accessTokenClick(){
		if(oauth.getTokenKey() != null && oauth.getTokenSecret() != null && oauth.getVerifier() != null &&
				oauth.getTokenKey().length() > 0 && oauth.getTokenSecret().length() > 0 && oauth.getVerifier().length() >0
				&& oauth.getAccessTokenUrl() != null && oauth.getAccessTokenUrl().length() > 0
				&& oauth.getConsumerKey() != null && oauth.getConsumerSecret().length() > 0
				&& oauth.getConsumerSecret() != null && oauth.getConsumerKey().length() > 0){
			
			try {
				HttpPost post = new HttpPost(oauth.getAccessTokenUrl());
				String type = contentType.getValue();
				post.addHeader("Content-type", type);
				post.setEntity(new StringEntity(this.requestBody.getText()));
				
				OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
						this.oauth.getConsumerKey(),
						this.oauth.getConsumerSecret()
				);
				
				consumer.setTokenWithSecret(oauth.getTokenKey(), oauth.getTokenSecret());
				HttpParameters param = new HttpParameters();
				param.put("oauth_verifier", oauth.getVerifier());
				consumer.setAdditionalParameters(param);
				consumer.sign(post);
				
				sendHttpReqeuest(post);
			} catch (Exception e){
				e.printStackTrace();
				MainController.this.createMessage(e.getMessage(), "Error");
			}
		} else {
			MainController.this.createMessage("OAuth info have not been set\r\nGo to Edit->Setup OAuth", "Info");
		}
	}
	
	public void disableBody(){
		requestBody.setEditable(false);
		contentType.setDisable(true);
	}
	
	public void enableBody(){
		requestBody.setEditable(true);
		contentType.setDisable(false);
	}
	
	public void addHeader(){
		String key = headerKey.getText(); 
		String value = headerValue.getText();
		
		if(headerMap.containsKey(key)){
			headerMap.get(key).setValue(value);
		} else {
			RequestHeader header = new RequestHeader(key, value);
			requestHeaderList.add(header);
			headerMap.put(key, header);
		}
		
	}
	
	public void setupOAuth(){
		try{
			FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/oauth_setting.fxml"));
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Setup OAuth");
			dialogStage.setResizable(false);
			dialogStage.initModality(Modality.WINDOW_MODAL);
		    dialogStage.initOwner(App.getPrimaryStage());
		    Scene scene = new Scene(loader.load());
		    dialogStage.setScene(scene);
		    
		    SetupOAuthController controller = loader.getController();
		    controller.setConfig(oauth);
		    controller.setDialogStage(dialogStage);
		    
		    dialogStage.showAndWait();
		    
		} catch (Exception e){
			e.printStackTrace();
		}
	    
	}
	
	public void addOAuth(){
		
		if(oauth.getConsumerKey() != null && oauth.getConsumerKey().length() > 0 &&
				oauth.getConsumerSecret() != null && oauth.getConsumerSecret().length() > 0){
			OAuthRequestHeader header = new OAuthRequestHeader(oauth);
	    	requestHeaderList.add(header);
			headerMap.put(header.getKey(), header);
		} else {
			MainController.this.createMessage("OAuth info have not been set\r\nGo to Edit->Setup OAuth", "Info");
		}
	}
	
	public void removeHeader(){
		int index = headerTable.getSelectionModel().getSelectedIndex();
		if(index >= 0){
			String key = this.requestHeaderList.get(index).getKey();
			this.headerMap.remove(key);
			this.requestHeaderList.remove(index);
		}
			
	}
	
	public void onClickAbout(){
		try{
			FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/about.fxml"));
			Stage dialogStage = new Stage();
			dialogStage.setTitle("About OAuth REST Client");
			dialogStage.setResizable(false);
			dialogStage.initModality(Modality.WINDOW_MODAL);
		    dialogStage.initOwner(App.getPrimaryStage());
		    Scene scene = new Scene(loader.load());
		    dialogStage.setScene(scene);
		    		    
		    dialogStage.showAndWait();
		    
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void onClickClose(){
		App.getPrimaryStage().close();
	}
	
	public void copyResHeader(){
		int index = responseHeaderList.getSelectionModel().getSelectedIndex();
		if(index >= 0){
			String text = this.responseList.get(index);
			StringSelection stringSelection = new StringSelection (text);
			Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
			clpbrd.setContents (stringSelection, null);
		}
	}
	
	public void copyReqHeader(){
		int index = reqHeaderList.getSelectionModel().getSelectedIndex();
		if(index >= 0){
			String text = this.finalReqHeader.get(index);
			StringSelection stringSelection = new StringSelection (text);
			Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
			clpbrd.setContents (stringSelection, null);
		}
	}

	private void createMessage(String message, String title){
		try{
			FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/messageBox.fxml"));
			Stage dialogStage = new Stage();
			dialogStage.setTitle(title);
			dialogStage.setResizable(false);
			dialogStage.initModality(Modality.WINDOW_MODAL);
		    dialogStage.initOwner(App.getPrimaryStage());
		    Scene scene = new Scene(loader.load());
		    dialogStage.setScene(scene);
		    
		    MessageBoxController controller = loader.getController();
		    controller.setMessage(message);
		    controller.setDialogStage(dialogStage);
		    
		    dialogStage.showAndWait();
		    
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void sendHttpReqeuest(HttpRequestBase http){
		
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				MainController.this.sendBtn.setDisable(true);
				MainController.this.accessTokenBtn.setDisable(true);
				MainController.this.reqTokenBtn.setDisable(true);
				
				try {
					HttpClient client = HttpClientBuilder.create().build();
					HttpCoreContext localContext = new HttpCoreContext();
					HttpResponse response = client.execute(http, localContext);
					
					
					Platform.runLater(new Runnable(){

						@Override
						public void run() {
							
							try {
								//generate final header
								finalReqHeader.clear();
								finalReqHeader.add(localContext.getRequest().getRequestLine().getMethod() + " " + localContext.getRequest().getRequestLine().getUri());
								for(Header header :localContext.getRequest().getAllHeaders()){
									finalReqHeader.add(header.getName() +": " + header.getValue());
								}
								
								//handle response
								responseList.clear();
								StatusLine status = response.getStatusLine();
								responseList.add(status.getProtocolVersion() + " " + status.getStatusCode() + " " + status.getReasonPhrase());
								for(Header header :response.getAllHeaders()){
									responseList.add(header.getName() +": " + header.getValue());
								}
								
								BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
								StringBuffer result = new StringBuffer();
								String line = "";
								while ((line = rd.readLine()) != null) {
									result.append(line);
								}
								responseBody.setText(result.toString());
							} catch (Exception e){
								e.printStackTrace();
								MainController.this.createMessage(e.getMessage(), "Error");
							}
							
						}
						
					});
					
				} catch (Exception e){
					e.printStackTrace();
					MainController.this.createMessage(e.getMessage(), "Error");
				} finally{
					Platform.runLater(new Runnable(){
						@Override
						public void run() {
							MainController.this.sendBtn.setDisable(false);
							MainController.this.accessTokenBtn.setDisable(false);
							MainController.this.reqTokenBtn.setDisable(false);
						}
					});
				}
			}
			
		});
		backgroundPool.execute(t);
		
	}
}
