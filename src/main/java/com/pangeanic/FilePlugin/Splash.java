/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pangeanic.FilePlugin;

import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author aestela
 */
public class Splash extends Frame implements ActionListener {
    public SplashScreen splash; 
    static void renderSplashFrame(Graphics2D g, int frame) {
        final String[] comps = {".", "...", "....."};
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(120,140,200,40);
        g.setPaintMode();
        g.setColor(Color.WHITE);
        g.drawString("Loading "+comps[(frame/5)%3]+"...", 385, 190);
        
    }
    public Splash(FilePlugin test) {
        super("SplashScreen");
        System.out.println("Splash starts");
        setSize(300, 200);
        setLayout(new BorderLayout());
        Menu m1 = new Menu("File");
        MenuItem mi1 = new MenuItem("Exit");
        m1.add(mi1);
        mi1.addActionListener(this);
        this.addWindowListener(closeWindow);
 
        MenuBar mb = new MenuBar();
        setMenuBar(mb);
        mb.add(m1);
        splash = SplashScreen.getSplashScreen();
        if (splash == null) {
            System.out.println("SplashScreen.getSplashScreen() returned null");
            return;
        }
        Graphics2D g = splash.createGraphics();
        if (g == null) {
            System.out.println("g is null");
            return;
        }
        
        for(int i=0; i<60; i++) {
            renderSplashFrame(g, i);
            splash.update();
            try {
                Thread.sleep(90);
            }
            catch(InterruptedException e) {
            }
        }

        splash.close();
        setVisible(false);
        test.splashShowing = false;
        //toFront();
    }
    public void actionPerformed(ActionEvent ae) {
        System.exit(0);
    }
     
    private static WindowListener closeWindow = new WindowAdapter(){
        public void windowClosing(WindowEvent e){
            e.getWindow().dispose();
        }
    };
     
}