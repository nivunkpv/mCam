/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcam.Core;

import com.github.sarxos.webcam.Webcam;
import java.util.ArrayList;

/**
 *
 * @author Nived
 */
public class Streamer 
{

    private final ArrayList<Webcam> Webcams;
    
    public Streamer(ArrayList<Webcam> webcams)
    {
        this.Webcams = webcams;
    }
    
    
}
