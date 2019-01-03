/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pangeanic.FilePlugin;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.*;
/**
 *
 * @author A
 */

public class GlossaryDialog extends JDialog {
 
    private JTextField src;
    private JTextField tgt;
    private JTextField descr;
    private JLabel lbSrc;
    private JLabel lbTgt;
    private JLabel lbDescr;
    private JButton btnUpload;
    private JButton btnCancel;
    private boolean succeeded;
 
    public GlossaryDialog(FilePlugin parent, File file) {
        super(parent, "Upload of Glossary " + file.getName(), true);
        //
        JPanel panel = new JPanel(new GridBagLayout());
        //panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints cs = new GridBagConstraints();
 
        cs.fill = GridBagConstraints.HORIZONTAL;
 
        cs.gridx=0;   
        cs.gridy=0;   
        panel.add(new JLabel("  "),cs);
        
        lbSrc = new JLabel("Source Language: ");
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 1;
        //panel.add(lbSrc, cs);
 
        src = new JTextField(5);
        cs.gridx = 2;
        cs.gridy = 1;
        cs.gridwidth = 1;
        //panel.add(src, cs);
 
        lbTgt = new JLabel("Target Language: ");
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 1;
        panel.add(lbTgt, cs);
 
        tgt = new JTextField(5);
        cs.gridx = 2;
        cs.gridy = 2;
        cs.gridwidth = 1;
        panel.add(tgt, cs);
        panel.setBorder(new LineBorder(Color.GRAY));

        lbDescr = new JLabel("Short Description: ");
        cs.gridx = 1;
        cs.gridy = 3;
        cs.gridwidth = 1;
        panel.add(lbDescr, cs);
 
        descr = new JTextField(20);
        cs.gridx = 2;
        cs.gridy = 3;
        cs.gridwidth = 4;
        panel.add(descr, cs);
        panel.setBorder(new LineBorder(Color.GRAY));

        
        
        btnUpload = new JButton("Upload");
 
        btnUpload.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e) {
                    try{
                        parent.sendGlossary(file, getSrc(), getTgt(), getDescr());
                        parent.sendRestGlossaries();                                
                    } catch (Exception ee){
                        System.out.println("Exception sendGlossary " + ee);
                        JOptionPane.showMessageDialog(null, "Upload Failed!");
                    }    
                    dispose();
                
                /*
                if (Login.authenticate(getUsername(), getPassword())) {
                    JOptionPane.showMessageDialog(GlossaryDialog.this,
                            "Hi " + getUsername() + "! You have successfully logged in.",
                            "Login",
                            JOptionPane.INFORMATION_MESSAGE);
                    succeeded = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(GlossaryDialog.this,
                            "Invalid username or password",
                            "Login",
                            JOptionPane.ERROR_MESSAGE);
                    // reset username and password
                    tfUsername.setText("");
                    pfPassword.setText("");
                    succeeded = false;
 
                }
                */
            }
        });
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JPanel bp = new JPanel();
        //bp.setBorder(new EmptyBorder(10, 10, 10, 10));
        bp.add(btnUpload);
        bp.add(btnCancel);
 
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);
 
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }
 
    public String getSrc() {
        return src.getText().trim();
    }
 
    public String getTgt() {
        return tgt.getText().trim();
    }
 
    public String getDescr() {
        return descr.getText().trim();
    }
 
}