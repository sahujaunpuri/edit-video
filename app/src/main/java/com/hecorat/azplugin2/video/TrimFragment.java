package com.hecorat.azplugin2.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.hecorat.azplugin2.R;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.helper.picktime.PickTimePanel;
import com.hecorat.azplugin2.main.MainActivity;
import com.hecorat.azplugin2.timeline.VideoTL;

/**
 * Created by Bkmsx on 12/8/2016.
 */

public class TrimFragment extends Fragment implements RangeSeekBar.OnSeekBarChangedListener,
        PickTimePanel.OnPickTimeListener {
    MainActivity mActivity;
    VideoView mVideoView;
    FrameLayout mLayoutSeekbar;
    RelativeLayout mLayoutVideoView;
    LinearLayout mLayoutFragment;
    RangeSeekBar mRangeSeekBar;
    Button mBtnOk, mBtnCancel;
    FrameLayout mLayoutPickTime;
    PickTimePanel mPickTimePanel;

    int startTimeMs, endTimeMs;
    String mVideoPath;
    VideoTL mVideoTL;

    public static TrimFragment newInstance(MainActivity activity, VideoTL videoTL){
        TrimFragment trimFragment = new TrimFragment();
        trimFragment.mActivity = activity;
        trimFragment.mVideoPath = videoTL.videoPath;
        trimFragment.mVideoTL = videoTL;
        return trimFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trim_fragment, container, false);
        mVideoView = (VideoView) view.findViewById(R.id.video_view);
        mLayoutSeekbar = (FrameLayout) view.findViewById(R.id.layout_seekbar);
        mLayoutVideoView = (RelativeLayout) view.findViewById(R.id.videoview_layout);
        mLayoutFragment = (LinearLayout) view.findViewById(R.id.layout_fragment);
        mBtnOk = (Button) view.findViewById(R.id.btn_ok);
        mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);
        mLayoutPickTime = (FrameLayout) view.findViewById(R.id.layout_pick_time_trim);

        mBtnOk.setOnClickListener(onBtnOkClick);
        mBtnCancel.setOnClickListener(onBtnCancelClick);
        setLayoutTrimVideo();
        mVideoView.setVideoPath(mVideoPath);
        int seekTime = Math.max(10, mVideoTL.startTimeMs);
        mVideoView.seekTo(seekTime);
        return view;
    }

    @Override
    public void onPickTimeCompleted(int minMs, int maxMs) {
        setSeekbarPosition(minMs, maxMs);
        mPickTimePanel.setTextValues(minMs, maxMs);
    }

    View.OnClickListener onBtnCancelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            close();
        }
    };

    public void close() {
        mVideoView.setVisibility(View.GONE);
        mActivity.setActiveVideoViewVisible(true);
        mActivity.setLayoutFragmentVisible(false);
        setInvisible();
        mActivity.hideStatusBar();
        mActivity.mOpenLayoutTrimVideo = false;
    }

    public void setSeekbarPosition(int startMs, int endMs){
        startTimeMs = startMs;
        endTimeMs = endMs;
        mRangeSeekBar.setSelectedValue(startMs, endMs);
        log("start: "+startMs+" end: "+ endMs);
    }

    private void log(String msg){
        Log.e("Trim fragment",msg);
    }

    View.OnClickListener onBtnOkClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            close();
            mActivity.onTrimVideoCompleted(startTimeMs, endTimeMs);
        }
    };

    private void setInvisible(){
        View view = getView();
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void seekVideoTo(int value) {
        mVideoView.seekTo(value);
    }

    @Override
    public void updateSelectedTime(int minMs, int maxMs) {
        startTimeMs = minMs;
        endTimeMs = maxMs;
        mPickTimePanel.setTextValues(minMs, maxMs);
    }

    private void setLayoutTrimVideo(){
        float layoutHeight = Utils.getScreenHeight()*0.9f;
        ViewGroup.LayoutParams layoutParams = mLayoutFragment.getLayoutParams();
        layoutParams.height = (int) layoutHeight;

        float videoLayoutHeight = layoutHeight*0.55f;
        float videoLayoutWidth = videoLayoutHeight*16/9;
        ViewGroup.LayoutParams videoLayoutParams = mLayoutVideoView.getLayoutParams();
        videoLayoutParams.height = (int) videoLayoutHeight;
        videoLayoutParams.width = (int) videoLayoutWidth;

        float layoutWidth = videoLayoutWidth*1.3f;
        layoutParams.width = (int) layoutWidth;

        mRangeSeekBar = new RangeSeekBar(mActivity, (int)layoutWidth, 100, mVideoPath);
        mLayoutSeekbar.addView(mRangeSeekBar);
        setSeekbarPosition(mVideoTL.startTimeMs, mVideoTL.endTimeMs);
        log("mVideoTL.startTimeMs = " + mVideoTL.startTimeMs);
        log("mVideoTL.endTimeMs = " + mVideoTL.endTimeMs);

        mPickTimePanel = new PickTimePanel(mActivity, this, mVideoTL.durationVideo);
        mLayoutPickTime.addView(mPickTimePanel);
        mPickTimePanel.setTextValues(mVideoTL.startTimeMs, mVideoTL.endTimeMs);
    }
}