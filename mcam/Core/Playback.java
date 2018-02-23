package mcam.Core;


import mcam.Config;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nived on 26-07-2017.
 */
public class Playback
{

    public Map<Integer,ArrayList<Segment>> camSegments = new HashMap<>();

    public class Segment
    {
        String StartTime;
        String EndTime;
        File file;

        public File getFile() {
            return file;
        }

        public String getEndTime() {
            return EndTime;
        }

        public String getStartTime() {
            return StartTime;
        }

        public void setEndTime(String endTime) {
            EndTime = endTime;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public void setStartTime(String startTime) {
            StartTime = startTime;
        }
    }

    public Playback(String playback) throws IOException
    {
        ArrayList<String[]> infolist = Playback.getPlaybackInfo(playback);
        Segment segment;
        ArrayList<Segment> tmpList ;

        for(String[] line:infolist)
        {
            segment = new Segment();
            String[] namevalues;
            int camId;
            segment.setFile(new File(Config.TV_DIR+playback+"\\"+line[0]));
            segment.setStartTime(line[1]);
            segment.setEndTime(line[2]);

            namevalues = segment.getFile().getName().split("_");
            camId = Integer.parseInt(namevalues[1]);
            if(camSegments.containsKey(camId))
            {
                tmpList = camSegments.get(camId);
                tmpList.add(segment);
            }
            else
            {
                tmpList = new ArrayList<>();
                tmpList.add(segment);
                camSegments.put(camId,tmpList);
            }
        }
    }

    public ArrayList<Segment> getPlaybackForCam(int id)
    {
        if(camSegments.containsKey(id))
            return camSegments.get(id);
        else
            return null;
    }

    public int getCamCount()
    {
        return camSegments.keySet().size();
    }

    public static int getSegementCount(String playbackID,int CamID)
    {
        int i = 0;
        File file = new File(Config.TV_DIR+playbackID+"\\info.txt");
        if(!file.exists())
            return i;
        String[] listEntry;
        String fname;
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            for(String line; (line = br.readLine()) != null; )
            {
                listEntry = line.split(",");
                fname = "cam_"+Integer.toString(CamID)+"_"+Integer.toString(i)+".mp4";
                if(listEntry[0].contentEquals(fname))
                    i++;
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return i;
    }

    public static ArrayList<String[]> getPlaybackInfo(String playbackID)
    {
        ArrayList<String[]> segmentInfos = new ArrayList<>();
        File file = new File(Config.TV_DIR+playbackID+"\\info.txt");
        String[] listEntry;
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            for(String line; (line = br.readLine()) != null; )
            {
                listEntry = line.split(",");
                segmentInfos.add(listEntry);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return segmentInfos;
    }
}
