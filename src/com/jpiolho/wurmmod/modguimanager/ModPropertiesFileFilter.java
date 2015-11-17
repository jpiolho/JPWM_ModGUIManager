/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpiolho.wurmmod.modguimanager;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author JPiolho
 */
public class ModPropertiesFileFilter implements FileFilter {

    
    
    
    @Override
    public boolean accept(File file) {
        String extension = Utils.getFileExtension(file);
        
        return extension.equalsIgnoreCase("properties") || extension.equalsIgnoreCase("disabled");
    }
    
}

