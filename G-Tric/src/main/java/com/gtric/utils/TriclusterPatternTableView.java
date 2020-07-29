package com.gtric.utils;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TriclusterPatternTableView {

	private Integer num;
	private String rowPattern;
	private String columnPattern;
	private String contextPattern;
	private String imageName;
	private Button example;
	private CheckBox select;
	
	public TriclusterPatternTableView(Integer num, String rowPattern, String columnPattern, String contextPattern, String imageName,
			Button example, CheckBox select) {
		this.num = num;
		this.rowPattern = rowPattern;
		this.columnPattern = columnPattern;
		this.contextPattern = contextPattern;
		this.select = select;
		this.example = example;
		this.imageName = imageName;
		
		this.example.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
            	Image img = new Image(getClass().getResourceAsStream(getImageName() + ".png"));
            	ImageView imgView = new ImageView();
            	imgView.setImage(img);
            	imgView.setPreserveRatio(true);
                
                StackPane secondaryLayout = new StackPane();
                secondaryLayout.getChildren().add(imgView);
                
                
                Scene secondScene = new Scene(secondaryLayout, 1200, 420);

                Stage secondStage = new Stage();
                secondStage.setTitle("Pattern overview");
                secondStage.setScene(secondScene);
                
                secondStage.show();
            }
		});
	}

	public String getImageName() {
		return this.imageName;
	}
	
	public Button getExample() {
		return this.example;
	}
	
	public void setExample(Button example) {
		this.example = example;
	}
	
	public String getRowPattern() {
		return rowPattern;
	}

	public void setRowPattern(String rowPattern) {
		this.rowPattern = rowPattern;
	}

	public String getColumnPattern() {
		return columnPattern;
	}

	public void setColumnPattern(String columnPattern) {
		this.columnPattern = columnPattern;
	}

	public String getContextPattern() {
		return contextPattern;
	}

	public void setContextPattern(String contextPattern) {
		this.contextPattern = contextPattern;
	}

	public CheckBox getSelect() {
		return select;
	}

	public void setSelect(CheckBox select) {
		this.select = select;
	}
	
	public void setNum(int num) {
		this.num = num;
	}
	
	public Integer getNum() {
		return this.num;
	}
	
}
