/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pangeanic.FilePlugin;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author A
 */
public class Engine {
    
    public static List<Engine> engines = new ArrayList<Engine>();
    int myId;
    String descr;
    String domain;
    String flavor;
    String mySrc="";
    String myTgt="";
    
    public static Engine getEngine(int id){
        Engine engine=null;
        for (Engine e : engines) {
            if (e.myId==id) engine=e;
        } 
        return(engine);
    }
    
    public Engine(int id, String domain, String flavor, String src, String tgt, String description) {
        //System.out.println("Re Creating engine " + description);
        this.descr = description;
        this.mySrc=src;
        this.myTgt=tgt;
        this.myId=id;
        this.domain = domain;
        this.flavor = flavor;
        //model.addRow(new Object[]{this.name, this.processName,this.mySrc, this.myTgt, this.status,""});
        //this.myIndex=ficIndex++;
        engines.add(this);
        //System.out.println("Re Created Engine " + this.descr);
    }
    public String getTitle(){
        return (this.mySrc + "->" + this.myTgt + " " + this.domain + " " + this.flavor+ " " + this.descr);
    }
    
}
