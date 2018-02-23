package mcam.Core;

import mcam.Config;

import com.github.sarxos.webcam.Webcam;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Nived on 26-07-2017.
 */
public class Recorder
{
    public Webcam webcam;
    public IMediaWriter writer;
    public int segamentCount =0;
    public File currentSegment;
    public String currentSegmentStartTime;
    public String currentSegmentEndTime;
    public ArrayList<File> segaments = new ArrayList<>(); //segment holds start time and end time.
    public String playBackID;
    public int camID;
    public boolean log = true;
    public PrintWriter fInfo;
    public boolean Blocked = false;
    public boolean Shutdown = false;

    public static Recorder use(Webcam webcam)
    {
        Recorder recorder = new Recorder();
        recorder.webcam = webcam;
        return recorder;
    }

    public Recorder setPlayBack(String playBackID,int camID)
    {
        this.playBackID = playBackID;
        this.camID = camID;
        File tmp = new File(Config.TV_DIR+this.playBackID);
        if(!tmp.exists())
            tmp.mkdirs();
        else
            segamentCount = Playback.getSegementCount(playBackID,camID);
        return this;
    }

    public void recordHour() throws IOException, InterruptedException
    {
        this.currentSegment = new File(Config.TV_DIR+this.playBackID+"\\cam_"+Integer.toString(this.camID)+"_"+Integer.toString(this.segamentCount)+".mp4");
        this.fInfo = new PrintWriter(new FileWriter(Config.TV_DIR+this.playBackID+"\\info.txt",true));

        writer = ToolFactory.makeWriter(this.currentSegment.getCanonicalPath());
        Dimension size = this.webcam.getViewSize();
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);

        BufferedImage image;
        IConverter converter;
        IVideoPicture frame;

        this.currentSegmentStartTime = new SimpleDateFormat("hh:mm:ss:a").format(Calendar.getInstance().getTime());
        long start = System.currentTimeMillis();
        
        for (int i = 0; i < 28800; i++)
        {
            if(this.Blocked)//EXP not rec!!!!
            {
                i--;
                continue;
            }
            if(this.Shutdown)
                break;
            
            image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
            converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
            frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
            frame.setKeyFrame(i == 0);
            frame.setQuality(0);
            this.writer.encodeVideo(0, frame);
            Thread.sleep(10);
        }
        this.currentSegmentEndTime = new SimpleDateFormat("hh:mm:ss:a").format(Calendar.getInstance().getTime());

        this.writer.close();
        this.segamentCount++;
        this.segaments.add(currentSegment);
        fInfo.println(this.currentSegment.getName()+","+currentSegmentStartTime+","+currentSegmentEndTime);
        fInfo.close();
    }

    public void log(String line)
    {
        if(this.log)
            System.out.println(line);
    }
}
