/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpiolho.wurmmod.modguimanager;

import com.jpiolho.wurmmod.modguimanager.ModPropertySheet.PropertyEntryChoice;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

/**
 *
 * @author JPiolho
 */
public class ChoiceEditor implements PropertyEditor<String> {

    
    private ModPropertySheet.CustomPropertyItem item;
    
    private ComboBox<PropertyEntryChoice> comboboxValue;
    
    private ObservableList<PropertyEntryChoice> values;
    
    public ChoiceEditor(PropertySheet.Item property) {
        this.item = (ModPropertySheet.CustomPropertyItem)property;
    }
    
    @Override
    public Node getEditor() {
        HBox box = new HBox();
        
        box.setSpacing(4.0);
        
        values = FXCollections.observableArrayList(this.item.getEntry().getChoices());
        comboboxValue = new ComboBox<>(values);
        comboboxValue.setPrefWidth(128f);
        
        comboboxValue.valueProperty().addListener(new ChangeListener<PropertyEntryChoice>() {
            @Override
            public void changed(ObservableValue<? extends PropertyEntryChoice> observable, PropertyEntryChoice oldValue, PropertyEntryChoice newValue) {
                item.setValue(newValue.getValue());
            }
        });
        
        setValue((String)item.getValue());
        
        box.getChildren().add(comboboxValue);
        return box;
    }

    @Override
    public String getValue() {
        return comboboxValue.getValue().getValue();
    }

    @Override
    public void setValue(String t) {
        if(values == null)
            return;
        
        for(PropertyEntryChoice value : values) {
            if(value.getValue().equals(t)) {
                comboboxValue.setValue(value);
                return;
            }
        }
    }
    
    
}
