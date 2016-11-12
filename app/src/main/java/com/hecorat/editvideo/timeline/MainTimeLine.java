package com.hecorat.editvideo.timeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hecorat.editvideo.R;
import com.hecorat.editvideo.main.Constants;
import com.hecorat.editvideo.main.MainActivity;

import java.util.ArrayList;

/**
 * Created by bkmsx on 31/10/2016.
 */
public class MainTimeLine extends ImageView {
    public int width, height;
    public Rect rectBackground, rectLeft, rectRight;
    public Paint paint;
    public MediaMetadataRetriever retriever;
    public int durationVideo;
    public Bitmap defaultBitmap;
    public RelativeLayout.LayoutParams params;
    public int startTime, endTime;
    public int startPosition;
    public int left, right;
    public int min, max;
    public String videoPath;
    public int startInTimeLine, endInTimeLine;
    public MainActivity mActivity;

    public int MARGIN_LEFT_TIME_LINE;

    public static final int PADDING = 4;

    ArrayList<Bitmap> listBitmap;
    public MainTimeLine(Context context, String videoPath, int height) {
        super(context);
        mActivity = (MainActivity) context;
        MARGIN_LEFT_TIME_LINE = mActivity.mLeftMarginTimeLine;
        this.videoPath = videoPath;
        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        listBitmap = new ArrayList<>();
        durationVideo = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        startTime = 0;
        endTime = durationVideo;

        this.height = height;

        paint = new Paint();
        left = 0;
        width = durationVideo/ Constants.SCALE_VALUE;
        min = left;
        max = min + width;
        right = left + width;
        log("init min: "+min);

        params = new RelativeLayout.LayoutParams(width, height);
        setLayoutParams(params);
        defaultBitmap = createDefaultBitmap();
        drawTimeLine(left, width);

        new AsyncTaskExtractFrame().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setLeftMargin(int value) {
        int moveX = value-left;
        left = value;
        params.leftMargin = left;
        min += moveX;
        max += moveX;
        right = left+width;
        setLayoutParams(params);
        log("margin min: ");
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
        rectLeft = new Rect(0, 0, PADDING, height);
        rectRight = new Rect(width-PADDING, 0, width, height);
        invalidate();
        updateTimeLineStatus();
    }

    public void updateTimeLineStatus() {
        startTime = startPosition*Constants.SCALE_VALUE;
        endTime = (right - min) * Constants.SCALE_VALUE;
        startInTimeLine = (left - MARGIN_LEFT_TIME_LINE)*Constants.SCALE_VALUE;
        endInTimeLine = (right - MARGIN_LEFT_TIME_LINE) * Constants.SCALE_VALUE;
        log("startTime: "+startTime+" endTime: "+endTime);
        log("startTimeLine: "+startInTimeLine+"endTimeLine: "+endInTimeLine);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayoutParams(params);
        paint.setColor(getResources().getColor(R.color.background_timeline));
        canvas.drawRect(rectBackground, paint);
        for (int i=0; i<listBitmap.size(); i++){
            canvas.drawBitmap(listBitmap.get(i), i*150 - startPosition, PADDING, paint);
        }
        canvas.drawRect(rectLeft, paint);
        canvas.drawRect(rectRight, paint);
    }

    private Bitmap createDefaultBitmap(){
        Paint paint = new Paint();
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        return bitmap;
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
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, 150, height-PADDING*2, false);
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