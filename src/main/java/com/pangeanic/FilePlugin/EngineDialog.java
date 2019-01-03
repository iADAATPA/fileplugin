/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pangeanic.FilePlugin;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author A
 */
public class EngineDialog extends JDialog {
    JList list; 
    ListSelectionModel listSelectionModel;
    ActionListener actionListener;
    static String[] listData = {};
    static EngineDialog demo;
    FilePlugin myParent;
    private JSplitPane splitH;
    
    public EngineDialog(FilePlugin parent) {
        super(parent, "Choosing an engine", true);
        //src/main/resources/pangeamt_icon.png
        
        myParent=parent;
        
        //populate engines
        listData = new String[Engine.engines.size()];
        int n=0;
        for (Engine e: Engine.engines){
            listData[n] = e.getTitle();
            n++;
        }
        //Create and set up the window.
        JFrame frame = new JFrame("Engine Selection");
         list = new JList(listData);
         //listSelectionModel = list.getSelectionModel();
        
        JButton btnOK = new JButton("OK");
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }});
        
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //String a = jList1.getSelectedValue().toString();
                
                if (listSelectionModel.getMinSelectionIndex()!= -1){
                    String s = (String) list.getSelectedValue();
                     for (Engine engine :Engine.engines) 
                        if (engine.getTitle().equals(s)) {
                            myParent.setEngine(engine);
                        }
                  setVisible(false);  
                }else{
                    JOptionPane.showMessageDialog(frame, "An engine must be selected");
                            //QuestionDialog questionDialog = new QuestionDialog(this);
                            
                }
                System.out.println(listSelectionModel);
                //
            }});
        
        
        
        splitH = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
       

        listSelectionModel = list.getSelectionModel();
        listSelectionModel.addListSelectionListener( new SharedListSelectionHandler());
        
        listSelectionModel.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane listPane = new JScrollPane(list);
        //JPanel listContainer = new JPanel(new GridLayout(2,1));
        //listContainer.setBorder(BorderFactory.createTitledBorder("List"));
        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        //btnPnl.setBackground(new Color(0x00FF00FF));
        btnPnl.add(btnOK);
        btnPnl.add(btnCancel);
        
        listPane.setMinimumSize(new Dimension(400, 150));
        listPane.setPreferredSize(new Dimension(400, 210));
        
        //JPanel buttonsContainer = new JPanel(new GridLayout(1,1));
        //buttonsContainer.add(button);
         splitH.setTopComponent(listPane);
         splitH.setBottomComponent(btnPnl);
         splitH.setDividerLocation(0.8);
        
        
        add(splitH);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
        
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static void createAndShowGUI(ActionListener al) {
        //populate engines
        listData = new String[Engine.engines.size()];
        int n=0;
        for (Engine e: Engine.engines){
            listData[n] = e.getTitle();
            n++;
        }

        //Create and set up the window.
        JFrame frame = new JFrame("Engine Selection");
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        //demo = new EngineDialog();
        //demo.actionListener=al;
        //demo.setOpaque(true);
        //frame.setContentPane(demo);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }


    class SharedListSelectionHandler implements ListSelectionListener {
        
        
        public void valueChanged(ListSelectionEvent e) { 
            
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();

            int index = list.getSelectedIndex();
            System.out.println("Index Selected: " + index);
            String s = (String) list.getSelectedValue();
            System.out.println("Value Selected: " + s);
            
            
           
        }
    }
}
