/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pspcl;

import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

/**
 *
 * @author amandeep
 */
public class waiting {
    
    JWindow window= new  JWindow();
    URL url=datatoexcel.class.getResource("/pspcl/circle_animate.gif");
    ImageIcon ic= new ImageIcon(url);
    JLabel jp= new JLabel(ic);
    waiting()
    {
        window.add(jp);
        jp.setSize(30,30);
        window.setSize(100, 60);
        window.setVisible(false);
        window.setAlwaysOnTop(true);
        window.setLocation(600, 300);
    }
    void show()
    {
        window.setVisible(true);
    }
    void suppress()
    {
        window.setVisible(false);
    }
    void close()
    {
        window.dispose();
    }
                
    
}
