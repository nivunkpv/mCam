/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcam.Core;

import com.github.sarxos.webcam.Webcam;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author Nived
 */
public class ImagePanel extends JPanel
{

    private BufferedImage image;
    public Thread camPreviewThread;
    private boolean run = false;

    public ImagePanel(Webcam webcam)
    {
        camPreviewThread = new Thread(new Runnable() {
            @Override
            public void run() 
            {
                while(run && webcam.isOpen())
                {
                    image = webcam.getImage();
                    repaint();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ImagePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }
    
    public void StartPreview()
    {
        run = true;
        camPreviewThread.start();
    }
    
    public void StopPreview()
    {
        run = false;
    }


    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters
    }

}