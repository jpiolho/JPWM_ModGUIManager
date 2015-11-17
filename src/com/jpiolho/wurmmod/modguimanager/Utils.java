/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jpiolho.wurmmod.modguimanager;

import java.io.File;

/**
 *
 * @author JPiolho
 */
public class Utils {
    public static String getFileExtension(File file)
    {
        String name = file.getName();
        
        int i = name.lastIndexOf('.');
        int p = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));

        String extension = "";
        if (i > p) {
            extension = name.substring(i+1);
        }
        
        return extension;
    }
    
    public static String getFileNameWithoutExtension(File file)
    {
        String name = file.getName();
        int pos = name.lastIndexOf('.');
        
        if(pos == -1) return name;
        
        return name.substring(0,pos);
    }
}
