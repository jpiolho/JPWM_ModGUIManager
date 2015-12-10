/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpiolho.wurmmod.modguimanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;


/**
 *
 * @author JPiolho
 */
public class ModPropertySheet extends VBox {
    
    private static final String[] ignoredKeys = new String[] {
        "classpath",
        "classname",
        "sharedclassloader",
        "serverpacks"
    };
    
    private ModEntry mod;
    private final ObservableList list;
    
    private HashMap<String,PropertyEntry> properties;
    
    public ModPropertySheet()
    {
        this.list = FXCollections.observableArrayList();
        
        PropertySheet propertySheet = new PropertySheet(this.list);
        VBox.setVgrow(propertySheet, Priority.ALWAYS);
        this.getChildren().add(propertySheet);
        
    }
    
    public void save()
    {
        File newFile = new File(mod.file.getPath() + ".temp");
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mod.file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
            
            String line;
            while((line = reader.readLine()) != null) {
                if(line.length() == 0) {
                    writer.newLine();
                    continue;
                }
                
                String trimmedLine = line.trim();
                
                
                if(!trimmedLine.startsWith("#")) {
                    
                    int idx = trimmedLine.indexOf('=');

                    if(idx >= 0) {
                        String key = trimmedLine.substring(0,idx);

                        if(properties.containsKey(key)) {
                            writer.write(key + "=" + properties.get(key).getValue().toString());
                            writer.newLine();
                            continue;
                        }
                    }
                }
                
                writer.write(line);
                writer.newLine();
            }

            reader.close();
            writer.close();
            
            mod.file.delete();
            newFile.renameTo(mod.file);
            
            
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        
        for(String key : this.properties.keySet())
            this.properties.get(key).saved();
    }
    
    public boolean askIfSave() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Unsaved changes!");
        alert.setHeaderText("There are unsaved changes in the mod tab");
        alert.setContentText("Do you want to save the changes?");
        Optional result = alert.showAndWait();
        if(result.get() == ButtonType.OK) {
           this.save();
           return true;
        }
        
        return false;
    }
    
    public boolean hasChanges() {
        if(this.properties == null)
            return false;
        
        for(String key : this.properties.keySet())
        {
            if(this.properties.get(key).hasChanged())
                return true;
        }
        
        return false;
    }
    
    public void setMod(ModEntry mod) throws FileNotFoundException, IOException
    {        
        this.mod = mod;
        
        
        
        this.list.clear();
        
        
        this.properties = new HashMap<>();
        
        Properties prop = new Properties();
        
        BufferedReader br = new BufferedReader(new FileReader(mod.file));
        
        String line;
 
        PropertyEntry entry = null;
        while((line = br.readLine()) != null)
        {
            if(entry == null) entry = new PropertyEntry();
            
            line = line.trim();
            
            if(line.length() == 0)
                continue;
            
            if(line.startsWith("#"))
            {
                int idx = line.indexOf(":");
                
                boolean gotMeta = true;
                if(idx >= 0) {
                    String metaKey = line.substring(1,idx).trim();
                    String metaValue = line.substring(idx+1).trim();
                    
                    switch(metaKey)
                    {
                        case "name": entry.setName(metaValue); break;
                        case "type": 
                        {
                            String[] split = metaValue.split(",");
                            switch(split[0])
                            {
                                default:
                                case "string": entry.setType(PropertyEntryType.String); break;
                                case "boolean": entry.setType(PropertyEntryType.Boolean); break;
                                case "integer": entry.setType(PropertyEntryType.Integer); break;
                                case "float": entry.setType(PropertyEntryType.Float); break;
                                case "double": entry.setType(PropertyEntryType.Double); break;
                                case "money": entry.setType(PropertyEntryType.Money); break;
                                case "choice": entry.setType(PropertyEntryType.Choice); break;
                            }
                            break;
                        }
                        case "values":
                            String[] split = metaValue.split(",");
                            
                            PropertyEntryChoice[] choices = new PropertyEntryChoice[split.length];
                            
                            for(int x=0;x<split.length;x++) {
                                String[] split2 = split[x].split("=");
                                
                                choices[x] = new PropertyEntryChoice(split2[0], split2[1]);
                            }
                            
                            entry.setChoices(choices);
                            break;
                        case "description": entry.setDescription(metaValue); break;
                        case "category": entry.setCategory(metaValue); break;
                        default: gotMeta = false; break;
                    }
                } else {
                    gotMeta = false;
                }
                
                if(!gotMeta)
                    entry.setDescription(entry.getDescription() + line.substring(1).trim() + "\n");
                
                continue;
            }
            
            int idx = line.indexOf("=");
            
            String key = line.substring(0,idx);
            String value = line.substring(idx+1);
         
            
            if(entry.getName().length() == 0)
                entry.setName(key);
            
            if(entry.getType() == PropertyEntryType.Unspecified)
                entry.setType(PropertyEntryType.String);
            
            
            switch(entry.getType())
            {
                case Boolean: entry.setValue(Boolean.parseBoolean(value)); break;
                case Money:
                case Integer: entry.setValue(Integer.parseInt(value)); break;
                case Float: entry.setValue(Float.parseFloat(value)); break;
                case Double: entry.setValue(Double.parseDouble(value)); break;
                case Choice:
                case String: entry.setValue(value);
            }
            
            entry.setOriginalValue(entry.getValue());
            
            properties.put(key, entry);
            
            entry = null;
        }
        
        br.close();
        
        
        for(String key : properties.keySet())
        {
            boolean ignore = false;
            for(String ignored : ignoredKeys) {
                if(key.equalsIgnoreCase(ignored))
                {
                    ignore = true;
                    break;
                }
            }
            
            if(ignore)
                continue;
            
            this.list.add(new CustomPropertyItem(this, properties.get(key)));
        }
        
        if(this.list.size() == 0) {
            this.setVisible(false);
        } else {
            this.setVisible(true);
        }
        
        
    }
    
    
    class PropertyEntryChoice
    {
        private String name;
        private String value;

        public PropertyEntryChoice(String name,String value) {
            this.name = name;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.name;
        }
        
        
    }
    
    class PropertyEntry 
    {
        private String name = "";
        private Object value = null;
        private String description = "";
        private String category = "Mod";
        private PropertyEntryType type = PropertyEntryType.Unspecified;
        
        private PropertyEntryChoice[] choices;
        
        private Object originalValue = null;
        
        public PropertyEntry()
        {
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public String getName()
        {
            return this.name;
        }
        
        public void setDescription(String description)
        {
            this.description = description;
        }
        
        public String getDescription()
        {
            return this.description;
        }
        
        public void setOriginalValue(Object value)
        {
            this.originalValue = value;
        }
        
        public void setValue(Object value)
        {
            this.value = value;
        }
        
        public Object getValue()
        {
            return this.value;
        }
        
        public void setType(PropertyEntryType type)
        {
            this.type = type;
        }
        
        public PropertyEntryType getType()
        {
            return this.type;
        }

        public PropertyEntryChoice[] getChoices() {
            return choices;
        }

        public void setChoices(PropertyEntryChoice[] choices) {
            this.choices = choices;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public boolean hasChanged() {
            return !this.originalValue.equals(this.value);
        }
     
        public void saved() {
            this.originalValue = this.value;
        }
    }
    
    enum PropertyEntryType
    {
        Unspecified,
        String,
        Boolean,
        Integer,
        Float,
        Double,
        Money,
        Choice
    }
    
    class CustomPropertyItem implements PropertySheet.Item {
        private PropertyEntry entry;
        
        CustomPropertyItem(ModPropertySheet sheet, PropertyEntry entry) {
            this.entry = entry;
        }

        @Override
        public Class<?> getType() {
            switch(entry.type) {
                default:
                case String: return String.class;
                case Boolean: return Boolean.class;
                case Float: return Float.class;
                case Double: return Double.class;
                case Integer: return Integer.class;
                case Money: return Money.class;
                case Choice: return String.class;
            }
        }

        @Override
        public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
            
            switch(entry.type) {
                case Money: return Optional.of(MoneyEditor.class);
                case Choice: return Optional.of(ChoiceEditor.class);
                default: return Optional.empty();
            }
        }

        
        
        @Override
        public String getCategory() {
            return entry.getCategory();
        }

        @Override
        public String getName() {
            return entry.getName();
        }

        @Override
        public String getDescription() {
            return entry.getDescription();
        }

        @Override
        public Object getValue() {
            return entry.getValue();
        }

        @Override
        public void setValue(Object o) {
            entry.value = o;
        }
        
        public PropertyEntry getEntry() {
            return this.entry;
        }
    };
}
