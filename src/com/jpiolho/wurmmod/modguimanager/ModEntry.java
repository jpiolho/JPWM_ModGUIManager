/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpiolho.wurmmod.modguimanager;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author JPiolho
 */
public class ModEntry {
    public File file;

    public ModEntry(File file)
    {
        this.file = file;
    }
    
    public boolean isDisabled()
    {
        return Utils.getFileExtension(file).equalsIgnoreCase("disabled");
    }
    
    public void setEnabled(boolean enabled)
    {
        Path p = Paths.get(file.getPath());
        
        File f = new File(p.getParent().toString() + File.separator + Utils.getFileNameWithoutExtension(file) + (enabled ? ".properties" : ".disabled" ));
        this.file.renameTo(f);
        this.file = f;
    }
    
    public String getName() {
        return Utils.getFileNameWithoutExtension(file);
    }
    
    public String getFolder() {
        Path p = Paths.get(file.getPath());
        return p.toString() + File.separator + Utils.getFileNameWithoutExtension(file);
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    
}
