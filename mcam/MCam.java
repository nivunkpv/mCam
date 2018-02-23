/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcam;

import com.github.sarxos.webcam.Webcam;
import mcam.UI.MUI;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mcam.Core.CManager;
import mcam.Core.Recorder;
import static mcam.Core.Utils.print;
/**
 *
 * @author Nived
 */
public class MCam 
{

    
    
    public static void main(String[] args) 
    {
        
            CManager manager =new CManager();
            //add prompt for date..
            manager.useDate("01-07-2017");
            manager.startWebcams();
            manager.createRecords();
                
    }
    
   
    
}
