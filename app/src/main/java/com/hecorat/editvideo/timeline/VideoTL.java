package com.hecorat.editvideo.timeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.export.VideoHolder;
import com.hecorat.editvideo.helper.Utils;
import com.hecorat.editvideo.main.Constants;
import com.hecorat.editvideo.main.MainActivity;

import java.util.ArrayList;

/**
 * Created by bkmsx on 31/10/2016.
 */
public class VideoTL extends ImageView {
    public Rect rectBackground, rectLeft, rectRight, rectTop, rectBottom;
    public Paint paint;
    public MediaMetadataRetriever retriever;
    public Bitmap defaultBitmap;
    public RelativeLayout.LayoutParams params;
    public String videoPath, audioPreview;
    public MainActivity mActivity;
    public ArrayList<Bitmap> listBitmap;
    public VideoHolder videoHolder;

    public int width, height;
    public int MARGIN_LEFT_TIME_LINE;
    public int startInTimeLine, endInTimeLine;
    public int startTime, endTime;
    public int startPosition;
    public int left, right;
    public int min, max;
    public int durationVideo;
    public float volume, volumePreview;
    public boolean hasAudio;
    public int mBackgroundColor;
    public int mBorderColor;
    public boolean isHighLight;

    public VideoTL(Context context, String videoPath, int height) {
        super(context);
        mActivity = (MainActivity) context;
        MARGIN_LEFT_TIME_LINE = mActivity.mLeftMarginTimeLine;
        this.videoPath = videoPath;
        this.audioPreview = videoPath;
        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        listBitmap = new ArrayList<>();
        hasAudio = true;
        durationVideo = Integer.parseInt(retriever.extractMetadata
                (MediaMetadataRetriever.METADATA_KEY_DURATION));

        startTime = 0;
        endTime = durationVideo;

        this.height = height;

        paint = new Paint();
        left = 0;
        width = durationVideo/ Constants.SCALE_VALUE;
        min = left;
        max = min + width;
        right = left + width;

        params = new RelativeLayout.LayoutParams(width, height);
        setLayoutParams(params);
        defaultBitmap = Utils.createDefaultBitmap();
        drawTimeLineWith(startTime, endTime);
        videoHolder = new VideoHolder();
        volume = 1f;
        volumePreview = 1f;
        mBackgroundColor = ContextCompat.getColor(mActivity, R.color.background_timeline);
        new AsyncTaskExtractFrame().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void highlightTL() {
        isHighLight = true;
        mBorderColor = ContextCompat.getColor(mActivity, R.color.video_timeline_highline);
        invalidate();
    }

    public void setNormalTL(){
        isHighLight = false;
        mBorderColor = ContextCompat.getColor(mActivity, R.color.background_timeline);
        invalidate();
    }

    public void setLeftMargin(int value) {
        int moveX = value-left;
        left = value;
        params.leftMargin = left;
        min += moveX;
        max += moveX;
        right = left+width;
        setLayoutParams(params);
        updateTimeLineStatus();
    }

    public void drawTimeLine(int leftPosition, int width) {
        int moveX = left - leftPosition;
        min += moveX;
        max += moveX;
        this.width = width;
        right = left + width;
        startPosition = left - min;
        params.width = width;
        setLayoutParams(params);
        rectBackground = new Rect(0, 0, width, height);
        rectLeft = new Rect(0, 0, Constants.BORDER_WIDTH, height);
        rectRight = new Rect(width- Constants.BORDER_WIDTH, 0, width, height);
        invalidate();
        updateTimeLineStatus();
    }

    public void drawTimeLineWith(int startTime, int endTime){
        startPosition = startTime/Constants.SCALE_VALUE;
        width = (endTime-startTime)/Constants.SCALE_VALUE;
        right = left + width;
        min = left - startPosition;
        params.width = width;
        setLayoutParams(params);
        rectBackground = new Rect(0, 0, width, height);
        rectLeft = new Rect(0, 0, Constants.BORDER_WIDTH, height);
        rectRight = new Rect(width- Constants.BORDER_WIDTH, 0, width, height);
        rectTop = new Rect(0, 0 , width, Constants.BORDER_WIDTH);
        rectBottom = new Rect(0, height-Constants.BORDER_WIDTH, width, height);

        this.startTime = startTime;
        this.endTime = endTime;
        startInTimeLine = (left - MARGIN_LEFT_TIME_LINE)* Constants.SCALE_VALUE;
        endInTimeLine = (right - MARGIN_LEFT_TIME_LINE) * Constants.SCALE_VALUE;
        invalidate();
    }

    public void updateTimeLineStatus() {
        startTime = startPosition* Constants.SCALE_VALUE;
        endTime = (right - min) * Constants.SCALE_VALUE;
        startInTimeLine = (left - MARGIN_LEFT_TIME_LINE)* Constants.SCALE_VALUE;
        endInTimeLine = (right - MARGIN_LEFT_TIME_LINE) * Constants.SCALE_VALUE;
    }

    public VideoHolder updateVideoHolder(){
        videoHolder.videoPath = videoPath;
        videoHolder.startTime = startTime/1000f;
        videoHolder.duration = (endTime-startTime)/1000f;
        videoHolder.volume = volume;
        return videoHolder;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayoutParams(params);
        paint.setColor(mBackgroundColor);
        canvas.drawRect(rectBackground, paint);
        for (int i=0; i<listBitmap.size(); i++){
            canvas.drawBitmap(listBitmap.get(i), i*150 - startPosition, Constants.BORDER_WIDTH, paint);
        }
        paint.setColor(mBorderColor);
        canvas.drawRect(rectTop, paint);
        canvas.drawRect(rectBottom, paint);
        canvas.drawRect(rectLeft, paint);
        canvas.drawRect(rectRight, paint);
    }



    private class AsyncTaskExtractFrame extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            int microDuration = durationVideo * 1000;
            int timeStamp = 0;
            while (timeStamp < microDuration) {
                Bitmap bitmap = null;
                int currentTimeStamp = timeStamp;
                while (bitmap==null && currentTimeStamp < Math.min(timeStamp+2900000, microDuration)) {
                    bitmap = retriever.getFrameAtTime(timeStamp, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    currentTimeStamp += 60000;
                }
                if (bitmap == null) {
                    bitmap = defaultBitmap;
                }
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, 150, height- Constants.BORDER_WIDTH*2, false);
                listBitmap.add(scaleBitmap);
                publishProgress();
                timeStamp += 3000000;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            invalidate();
        }
    }

    private void log(String msg){
        Log.e("Video TimeLine",msg);
    }
}
