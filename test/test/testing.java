/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

/**
 *
 * @author amandeep
 */
public class testing {
    static int i=0;
    //jp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pspcl/circle_animate.gif")));
    public static void main(String[] args)
    {
        JWindow window= new  JWindow();
        JProgressBar pb= new JProgressBar();
        //JPanel jp= new JPanel();
        URL url=testing.class.getResource("/pspcl/circle_animate.gif");
        ImageIcon ic= new ImageIcon(url);
        JLabel jp= new JLabel(ic);
        window.add(jp);
        jp.setSize(30,30);
        
//        pb.setStringPainted(true);
//        pb.setForeground(Color.DARK_GRAY);
//        pb.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        window.setSize(100, 60);
        window.setVisible(true);
        window.setAlwaysOnTop(true);
         window.setLocation(600, 400);
//       Thread t1 = new Thread(new Runnable() {
//            public void run() {
//                for (i = 0; i <= 100; i++) {
//                    pb.setValue(i);
//                    try {
//                        Thread.sleep(20);
//                    } catch (Exception e) {
//                    }
//                    if (i == 100) {
//                       
//                        window.dispose();
//                    }
//
//                }
//            }
//        });
//        t1.start();
}
                
}
