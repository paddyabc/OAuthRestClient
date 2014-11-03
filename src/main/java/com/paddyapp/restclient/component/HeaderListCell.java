package com.paddyapp.restclient.component;

import com.paddyapp.restclient.pojo.RequestHeader;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class HeaderListCell extends ListCell<RequestHeader> {

	@Override
	protected void updateItem(RequestHeader header, boolean empty) {
		// TODO Auto-generated method stub
		super.updateItem(header, empty);
		if(header != null){
			
			HBox box = new HBox(10);
			Label keyLabel = new Label();
			keyLabel.setText(header.getKey());
			keyLabel.setAlignment(Pos.CENTER);
			keyLabel.setMaxWidth(USE_COMPUTED_SIZE);
			
			Label valueLabel = new Label();
			valueLabel.setText(header.getValue());
			valueLabel.setMaxWidth(USE_COMPUTED_SIZE);
			valueLabel.setAlignment(Pos.CENTER);
			
			box.getChildren().add(keyLabel);
			box.getChildren().add(valueLabel);
			box.setMaxWidth(Double.MAX_VALUE);
			
			
			this.setGraphic(box);
		}
	}

}
