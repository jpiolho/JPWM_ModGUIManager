/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpiolho.wurmmod.modguimanager;

import com.wurmonline.server.gui.propertysheet.ServerPropertySheet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

/**
 *
 * @author JPiolho
 */
public class Mod implements WurmMod,Initable {

    private Scene scene;
    
    
    private ComboBox<ModEntry> cbMod;
    private CheckBox checkModEnabled;
    private ModPropertySheet propertySheet;
    private Button buttonSave;
    
    
    private int tabID;
    
    private boolean madeChanges = false;
    
    private ModEntry currentMod = null;
    
    @Override
    public void init() {
        final Mod thisMod = this;
        try {
            ClassPool cpool = HookManager.getInstance().getClassPool();
            HookManager.getInstance().registerHook("com.wurmonline.server.gui.WurmServerGuiMain", "start", Descriptor.ofMethod(CtClass.voidType, new CtClass[] { cpool.get("javafx.stage.Stage") } ), new InvocationHandlerFactory() {

                @Override
                public InvocationHandler createInvocationHandler() {
                    return new InvocationHandler() {

                        @Override
                        public Object invoke(Object o, Method method, Object[] os) throws Throwable {
                            method.invoke(o, os);
                            
                            Stage stage = (Stage)os[0];
                            thisMod.createGUI(stage);
                            
                            return null;
                        }
                    };
                }
            });
            
            HookManager.getInstance().registerHook("com.wurmonline.server.gui.WurmServerGuiController", "checkIfWeWantToSaveTab", Descriptor.ofMethod(CtClass.voidType, new CtClass[] { CtClass.intType, CtClass.intType }), new InvocationHandlerFactory() {

                @Override
                public InvocationHandler createInvocationHandler() {
                    return new InvocationHandler() {

                        @Override
                        public Object invoke(Object o, Method method, Object[] os) throws Throwable {
                            int oldvalue = (Integer)os[0];
                            int newvalue = (Integer)os[1];
                            
                            if(oldvalue != newvalue && oldvalue == tabID) {
                                if(propertySheet.hasChanges()) {
                                    if(propertySheet.askIfSave())
                                        madeChanges = true;
                                }
                                
                                if(madeChanges) {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Restart required");
                                    alert.setHeaderText("Restart required");
                                    alert.setContentText("You have to restart the server (including the GUI) to have the changes to apply");
                                    alert.showAndWait();
                                    madeChanges = false;
                                }
                            }
                            
                            return method.invoke(o, os);
                        }
                    };
                }
            });
        }
        catch(NotFoundException ex)
        {
            ex.printStackTrace();
        }
    }
    
    
    private void handleComboboxMod(ActionEvent event)
    {    
        ModEntry entry = cbMod.getSelectionModel().getSelectedItem();
        
        if(entry == null)
            return;
        
        if(entry != this.currentMod) {
            if(propertySheet.hasChanges())
                propertySheet.askIfSave();
        }
        
        this.currentMod = entry;
        
        checkModEnabled.setSelected(!entry.isDisabled());   
        
        try {
            propertySheet.setMod(entry);
            
            buttonSave.setVisible(propertySheet.isVisible());
        } 
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void handleCheckboxModDisabled(ActionEvent event)
    {
        ModEntry selectedMod = cbMod.getSelectionModel().getSelectedItem();
        
        selectedMod.setEnabled(checkModEnabled.isSelected());
        
        madeChanges = true;
        
        ObservableList<ModEntry> entries = cbMod.getItems();
        ArrayList<ModEntry> arr = new ArrayList<>();
        for(ModEntry e : entries)
            arr.add(e);
        
        cbMod.getItems().clear();
        cbMod.getItems().addAll(arr);
        cbMod.getSelectionModel().select(selectedMod);
    }
    
    private void handleButtonSave(ActionEvent event)
    {
        propertySheet.save();
        madeChanges = true;
    }
    
    private void createGUI(Stage stage)
    {
        this.scene = stage.getScene();
        
        VBox content = new VBox();
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(13.0));

        Text text = new Text("Select mod:");

        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(16.0);

        cbMod = new ComboBox<>();
        cbMod.setOnAction(this::handleComboboxMod);
        cbMod.setCellFactory(new Callback<ListView<ModEntry>, ListCell<ModEntry>>() {

            @Override
            public ListCell<ModEntry> call(ListView<ModEntry> param) {
                return new ListCell<ModEntry>() {

                    @Override
                    protected void updateItem(ModEntry item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.

                        if(item != null) {
                            setText(item.toString());

                            if(item.isDisabled()) {    
                                setTextFill(Color.RED);
                            } else {
                                setTextFill(Color.BLACK);
                            }
                        } else {
                            setText("");
                        }
                    }

                };
            }
        });

        // Load all the scripts in the scripts folder
        File folder = new File("mods");

        for(final File file : folder.listFiles(new ModPropertiesFileFilter())) {
            cbMod.getItems().add(new ModEntry(file));
        }


        checkModEnabled = new CheckBox("Enabled");
        checkModEnabled.setOnAction(this::handleCheckboxModDisabled);


        row.getChildren().addAll(cbMod,checkModEnabled);
        
        buttonSave = new Button("Save");
        buttonSave.setOnAction(this::handleButtonSave);
        buttonSave.setVisible(false);
        
        
        propertySheet = new ModPropertySheet();
        propertySheet.setVisible(false);
        content.getChildren().addAll(text,row,buttonSave,propertySheet);



        AnchorPane anchorPane = new AnchorPane(content);
        ScrollPane scrollPane = new ScrollPane(anchorPane);

        TabPane pane = (TabPane)stage.getScene().getRoot();
        Tab tab = new Tab("Mods",scrollPane);
        
        this.tabID = pane.getTabs().size();
        pane.getTabs().add(tab);
        
        
    }
}
