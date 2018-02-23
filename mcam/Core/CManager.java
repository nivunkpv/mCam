package mcam.Core;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import mcam.UI.MUI;

/**
 * Created by Nived on 28-07-2017.
 */
public class CManager
{
    MUI mUI;
    boolean Recording = false;
    boolean Streaming = false;
    boolean Paused = false;
    ArrayList<Webcam> webcams = new ArrayList<>();
    ArrayList<Recorder> recorders = new ArrayList<>();
    String dateRC;
    private ImagePanel panel;
    
    public class RecThread implements Runnable
    {

        Recorder recorder;
        
        public RecThread(Recorder rec)
        {
            this.recorder = rec;
        }
        
        @Override
        public void run() 
        {
            while(!this.recorder.Shutdown)
            {
                try 
                {
                    recorder.recordHour();
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(CManager.class.getName()).log(Level.SEVERE, null, ex);
                } 
                catch (InterruptedException ex) 
                {
                    Logger.getLogger(CManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }
    
    public CManager()
    {
        this.mUI = new MUI();
        this.mUI.setTitle("MCam v2");
        this.mUI.setResizable(false);
        this.mUI.pack();
        this.mUI.setVisible(true);
        this.mUI.setManager(this);
    }
    
    
    public void init()
    {
        /// automated all !
    }
    
    public void useDate(String date)
    {
        this.dateRC = date;
    }
    
    public String getDate()
    {
        return this.dateRC;
    }
    
    public ArrayList<Webcam> getWebcams()
    {
        return webcams;
    }
    
    public void startWebcams()
    {
        DefaultListModel<String> model = new DefaultListModel<>();
        this.mUI.UIpreviewList.setModel(model);
        
        for(Webcam webcam:Webcam.getWebcams())
        {
            webcam.setViewSize(webcam.getViewSizes()[webcam.getViewSizes().length-1]);
            webcam.open();
            this.webcams.add(webcam);
        }
        
        for(int i=0;i<webcams.size();i++)
        {
            model.add(i,webcams.get(i).getName());
        }
        
        this.mUI.UIpreviewList.addListSelectionListener(new ListSelectionListener() 
        {
            @Override
            public void valueChanged(ListSelectionEvent e) 
            {
                int index = mUI.UIpreviewList.getSelectedIndex();
                previewCam(webcams.get(index));
            }
        });
        this.mUI.UIpreviewList.setSelectedIndex(0);
    }
    
    public void createRecords()
    {
        int i =0;
        for(Webcam webcam:this.webcams)
        {
            this.recorders.add(Recorder.use(webcam).setPlayBack(this.dateRC,i));
            i++;
        }
    }
    
    public void previewCam(Webcam webcam)
    {
        if(panel!=null)
            panel.StopPreview();
        panel = new ImagePanel(webcam);
        panel.setSize(webcam.getViewSize());
        panel.StartPreview();
        this.mUI.previewPanel.removeAll();
        this.mUI.previewPanel.add(panel);
        
    }
    
    public void startRecAll()
    {
        for(Recorder r:recorders)
        {
            new Thread(new RecThread(r)).start();
        }
    }
    
    public void stopRecAll()
    {
        for(Recorder r:recorders)
        {
            r.Shutdown = true;
        }
    }
    
    public void startStream()
    {
        
    }
    
    public void stopStream()
    {
        
    }
    
    public void showPlayback()
    {
        
    }
    
    

    public void recButtonToggle(JButton jButton) 
    {
        if(Recording)
        {
            System.out.println("REC STOPPED!");
            Recording = false;
            jButton.setText("Start Recording");
            stopRecAll();
        }
        else
        {
            System.out.println("REC STARTED!");
            Recording = true;
            jButton.setText("Stop Recording");
            startRecAll();
        }
    }

    public void pauseButtonToggle(JButton jButton) 
    {
        if(!Recording)
            return;
        if(Paused)
        {
            System.out.println("REC Resumed!");
            Paused = false;
            jButton.setText("Pause Recording");
        }
        else
        {
            System.out.println("REC PASUED");
            Paused = true;
            jButton.setText("Resume Recording");
        }
    }

    public void streamButtonToggle(JButton jButton) 
    {
        if(Streaming)
        {
            System.out.println("STREAM STOPPED");
            Streaming = false;
            jButton.setText("Start Streaming");
        }
        else
        {
            System.out.println("STREAM STARTED!");
            Streaming = true;
            jButton.setText("Stop Streaming");
        }
    }

}
