/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpiolho.wurmmod.modguimanager;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.action.Action;
import org.controlsfx.property.editor.PropertyEditor;

/**
 *
 * @author JPiolho
 */
public class MoneyEditor implements PropertyEditor<Integer> {

    private PropertySheet.Item item;
    
    private TextField textfieldGold;
    private TextField textfieldSilver;
    private TextField textfieldCopper;
    private TextField textfieldIron;
    
    private Money money;
    
    
    public MoneyEditor(PropertySheet.Item property) 
    {
        this.item = property;
    }
    
    @Override
    public Node getEditor() {
        HBox box = new HBox();
        
        box.setSpacing(4.0);
        
        textfieldGold = new TextField();
        textfieldSilver = new TextField();
        textfieldCopper = new TextField();
        textfieldIron = new TextField();
        
        textfieldGold.setPrefWidth(32.0);
        textfieldSilver.setPrefWidth(32.0);
        textfieldCopper.setPrefWidth(32.0);
        textfieldIron.setPrefWidth(32.0);
        
        textfieldGold.setText(Integer.toString(money.getGold()));
        textfieldSilver.setText(Integer.toString(money.getSilver()));
        textfieldCopper.setText(Integer.toString(money.getCopper()));
        textfieldIron.setText(Integer.toString(money.getIron()));
        
        textfieldGold.textProperty().addListener(this::handleTextFieldChange);
        textfieldSilver.textProperty().addListener(this::handleTextFieldChange);
        textfieldCopper.textProperty().addListener(this::handleTextFieldChange);
        textfieldIron.textProperty().addListener(this::handleTextFieldChange);

        textfieldGold.setOnAction(this::handleTextFieldAction);
        textfieldSilver.setOnAction(this::handleTextFieldAction);
        textfieldCopper.setOnAction(this::handleTextFieldAction);
        textfieldIron.setOnAction(this::handleTextFieldAction);
       
        box.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().addAll(textfieldGold,new Text("G"), textfieldSilver, new Text("S"),textfieldCopper, new Text("C"),textfieldIron, new Text("I"));
        return box;
    }
    
    private void handleTextFieldAction(ActionEvent event)
    {
        textfieldGold.setText(Integer.toString(money.getGold()));
        textfieldSilver.setText(Integer.toString(money.getSilver()));
        textfieldCopper.setText(Integer.toString(money.getCopper()));
        textfieldIron.setText(Integer.toString(money.getIron()));
    }
    
    private void handleTextFieldChange(ObservableValue<? extends String> observableValue, String s, String s2) {
        
        int gold,silver,copper,iron;
        
        try {
            gold = Integer.parseInt(textfieldGold.getText());
            silver = Integer.parseInt(textfieldSilver.getText());
            copper = Integer.parseInt(textfieldCopper.getText());
            iron = Integer.parseInt(textfieldIron.getText());
        }
        catch(NumberFormatException ex) {
            textfieldGold.setStyle("-fx-background-color: red;");
            textfieldSilver.setStyle("-fx-background-color: red;");
            textfieldCopper.setStyle("-fx-background-color: red;");
            textfieldIron.setStyle("-fx-background-color: red;");
            return;
        }
        
        textfieldGold.setStyle("");
        textfieldSilver.setStyle("");
        textfieldCopper.setStyle("");
        textfieldIron.setStyle("");
        
        this.money.setGold(gold);
        this.money.setSilver(silver);
        this.money.setCopper(copper);
        this.money.setIron(iron);
        
        this.item.setValue(money.getMoney());
        
        
        
    }

    @Override
    public Integer getValue() {
        return money.getMoney();
    }

    @Override
    public void setValue(Integer t) {
        if(this.money == null)
            this.money = new Money();

        this.money.setMoney(t);
    }
    
}
