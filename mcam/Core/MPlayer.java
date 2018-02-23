/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcam.Core;

import com.github.sarxos.webcam.Webcam;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import mcam.Core.Playback.Segment;
import mcam.UI.playerUI;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.runtime.x.LibXUtil;

/**
 *
 * @author Nived
 */
public class MPlayer 
{
   
    public playerUI pUI;
    public String CurrentDate;
    public JLabel DateLabel;
    private JList<String> CamListView;
    private JList<String> PlaybackListView;
    private JPanel previewPanel;
    private Component pcontrols;
    private Component pvideo;
    private Playback playBack;
    private ArrayList<String> CamNames = new ArrayList<>();
    private DefaultListModel<String> CamListmodel;
    private DefaultListModel<String> PlaybackListmodel;
    private ArrayList<Segment> currentSegemnts = new ArrayList<>();
    private boolean onChange = false;
    private MediaPlayerFactory mediaPlayerFactory;
    private EmbeddedMediaPlayer mediaPlayer;
   
   public MPlayer()
   {
       pUI = new playerUI();
       pUI.setTitle("MCam Playbacks");
       pUI.setResizable(false);
       pUI.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
       pUI.setPlayer(this);
       for(Webcam webcam:Webcam.getWebcams())
       {
           CamNames.add(webcam.getName());
       }
       NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:/Program Files/VideoLAN/VLC");
       Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
       LibXUtil.initialise();
       
   }
   
   public void init()
   {
       CamListmodel = new DefaultListModel<>();
       CamListView.setModel(CamListmodel);
       
       PlaybackListmodel = new DefaultListModel<>();
       PlaybackListView.setModel(PlaybackListmodel);
       
       Canvas c = new Canvas();
       previewPanel.add(c, BorderLayout.CENTER);
       mediaPlayerFactory = new MediaPlayerFactory();
       mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
       mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(c));
   }
   
   public void useDate(String date)
   {
        this.CamListmodel.removeAllElements();
        this.PlaybackListmodel.removeAllElements();
        this.DateLabel.setText(date);
        try 
        {
            this.CurrentDate = date;
            playBack = new Playback(this.CurrentDate);
            
            for(int i=0;i<playBack.getCamCount();i++)
            {
                this.CamListmodel.addElement("Cam "+Integer.toString(i));
            }
            
            this.CamListView.addListSelectionListener(new ListSelectionListener() 
            {
                @Override
                public void valueChanged(ListSelectionEvent e) 
                {
                    onChange = true;
                    currentSegemnts = new ArrayList<>();
                    PlaybackListmodel.removeAllElements();
                    int selected = CamListView.getSelectedIndex();
                    for(Playback.Segment pbs:playBack.getPlaybackForCam(selected))
                    {
                        PlaybackListmodel.addElement(pbs.StartTime+" - "+pbs.EndTime);
                        currentSegemnts.add(pbs);
                    }
                    onChange = false;
                    PlaybackListView.addListSelectionListener(new ListSelectionListener() 
                    {
                        @Override
                        public void valueChanged(ListSelectionEvent e) 
                        {
                            if(!onChange)
                            {
                                System.out.println("Playing "+currentSegemnts.get(PlaybackListView.getSelectedIndex()).file.getName());
                                try {
                                    Play(currentSegemnts.get(PlaybackListView.getSelectedIndex()).file);
                                } catch (IOException ex) {
                                    Logger.getLogger(MPlayer.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    });
                }
            });
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(MPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
   
   public void usePreviewPane(JPanel panel)
   {
       this.previewPanel = panel;
   }
   
   
   public void useDateLabel(JLabel label)
   {
       DateLabel = label;
   }
   
   public void useCamListView(JList<String> list)
   {
       CamListView = list;
   }
   
   public void usePlaybackListView(JList<String> list)
   {
       PlaybackListView = list;
   }
   
   public void Play(File segment) throws IOException
   {
      if(mediaPlayer.isPlaying())
          mediaPlayer.stop();
      mediaPlayer.playMedia(segment.getAbsolutePath());
      
   }
   
   public void Show()
   {
       pUI.pack();
       pUI.setVisible(true);
       
   }
   
   public void nextPlayback(JButton button)
   {
       
   }
   
   public void previousPlayback(JButton button)
   {
       
   }
   
   public void Close()
   {
       
   }
}
