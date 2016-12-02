package com.hecorat.editvideo.export;

import android.media.MediaMetadataRetriever;

import com.hecorat.editvideo.timeline.VideoTL;

import java.util.ArrayList;

/**
 * Created by TienDam on 11/23/2016.
 */

public class MergeFilter {
    public static String getFilter(ArrayList<VideoTL> listVideo, int quality, int order){
        String filter="";
        String in = "";
        for (int i=0; i<listVideo.size(); i++){
            int index = i+order;
            VideoHolder video = listVideo.get(i).videoHolder;
            filter += prepareVideo(video, quality, index);
            in += "[v"+index+"][a"+index+"]";
        }
        filter += in+"concat=n="+listVideo.size()+":v=1:a=1[v][a];";
        return filter;
    }

    private static String prepareVideo(VideoHolder video, int quality, int index){
        String filter="";
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(video.videoPath);
        int height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        String in = "["+index+":v]";

        filter += in+"scale=-1:"+quality+"[v_scale];";
        in = "[v_scale]";

        filter += "[0:v]"+in+"overlay=(main_w-overlay_w)/2:" +
                "(main_h-overlay_h)/2:shortest=1[v"+index+"];";

        filter += "["+index+":a]"+"aformat=sample_fmts=fltp:sample_rates=44100" +
                ":channel_layouts=stereo,volume="+video.volume+"[a"+index+"];";

        return filter;
    }
}
