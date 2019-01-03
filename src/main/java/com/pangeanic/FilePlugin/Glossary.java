/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pangeanic.FilePlugin;

import java.io.File;

/**
 *
 * @author A
 */
public class Glossary {
        String name;
        File file;
        int myIndex;
        int myId;
        String mySrc="";
        String myTgt="";
        String link="---";
        String description="";
    
        public Glossary(int id, String filename, String src, String tgt, String description) {
            System.out.println("Re Creating glossary " + filename);
            this.name = filename;
            this.mySrc=src;
            this.myTgt=tgt;
            this.myId=id;
            this.description=description;
            //model.addRow(new Object[]{this.name, this.processName,this.mySrc, this.myTgt, this.status,""});
            //this.myIndex=ficIndex++;
            
            System.out.println("Re Created Glossary " + this.name + " descr: " + description + "  SRC: " + this.mySrc + "  TGT: " + this.myTgt);
        }
    
    
}
