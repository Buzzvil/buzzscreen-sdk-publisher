package com.sample;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.buzzvil.buzzscreen.sdk.BaseLockerActivity;
import com.buzzvil.buzzscreen.sdk.Campaign;
import com.buzzvil.buzzscreen.sdk.widget.Slider;
import com.buzzvil.buzzscreen.sdk.widget.SliderIcon;
import com.buzzvil.locker.AutoplayState;

import java.text.DateFormatSymbols;
import java.util.Calendar;


public class CustomLockerActivity extends BaseLockerActivity {
    final static String TAG = "LockerActivity";

    // 화면 하단 슬라이더
    // Slider UI for unlocking the lock screen
    Slider slider;

    // 시계
    // Clock UI
    TextView tvTime;
    TextView tvAmPm;
    TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_locker);

        initUI();
    }

    private void initUI() {
        tvTime = (TextView)findViewById(R.id.locker_time);
        tvAmPm = (TextView)findViewById(R.id.locker_am_pm);
        tvDate = (TextView)findViewById(R.id.locker_date);
        tvTime.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));

        slider = (Slider)findViewById(R.id.locker_slider);

        // 슬라이더에서 왼쪽 아이콘이 선택(왼쪽 아이콘 위치에서 터치 업)되었을 때 호출되는 리스너
        // Listner that is called when the icon on the left of the Slider is selected (on touchUp)
        slider.setLeftOnSelectListener(new SliderIcon.OnSelectListener() {
            @Override
            public void onSelect() {
                landing();
            }
        });

        // 슬라이더에서 오른쪽 아이콘이 선택(오른쪽 아이콘 위치에서 터치 업)되었을 때 호출되는 리스너
        // Listner that is called when the icon on the right of the Slider is selected (on touchUp)
        slider.setRightOnSelectListener(new SliderIcon.OnSelectListener() {
            @Override
            public void onSelect() {
                unlock();
            }
        });

        // 화면 터치시 상하 페이시 화살표 표시
        // Indicators (usually arrows) for scrolling up and down when there's a touchDown event on the lockscreen
        setPageIndicators(
                findViewById(R.id.locker_arrow_top),
                findViewById(R.id.locker_arrow_bottom)
        );

        findViewById(R.id.locker_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카메라 앱 오픈
                // Open Camera app
                Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                startActivity(intent);
            }
        });

        // 클릭시 Context Menu 보여줄 뷰
        findViewById(R.id.locker_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLockerContextMenu();
            }
        });
    }

    @Override
    protected void onCurrentCampaignUpdated(Campaign campaign) {

        // 현재 보여지고 있는 캠페인이 업데이트 될때 호출된다.
        // 현재 캠페인에 따라 UI를 변화시키고 싶으면 여기서 작업하면 된다.
        //This listner is called when the ad campaign item that is shown on the page gets updated - eg) user scrolls up, etc
        //If there's any effect/feature you'd like to implement when the current item is updated, you can implement it here.
        Log.i(TAG, campaign.toString());

        // 좌우 포인트가 변경되었을 때 UI 업데이트
        // Update the point values when the current campaign gets updated.
        int landingPoints = campaign.getLandingPoints();
        int unlockPoints = campaign.getUnlockPoints();

        if (landingPoints > 0) {
            slider.setLeftText(String.format("+ %d", campaign.getLandingPoints()));
        } else {
            slider.setLeftText("");
        }

        if (unlockPoints > 0) {
            slider.setRightText(String.format("+ %d", campaign.getUnlockPoints()));
        } else {
            slider.setRightText("");
        }
    }

    @Override
    protected void onTimeUpdated(Calendar cal) {
        // set time
        int hour = cal.get(Calendar.HOUR);
        String time = String.format("%d:%02d", hour == 0 ? 12 : hour, cal.get(Calendar.MINUTE));
        tvTime.setText(time);

        // set am pm
        String am_pm = String.format("%s", cal.get(Calendar.AM_PM) == 0 ? "AM" : "PM");
        tvAmPm.setText(am_pm);

        // set date
        DateFormatSymbols symbols = new DateFormatSymbols();
        String dayName = symbols.getWeekdays()[cal.get(Calendar.DAY_OF_WEEK)];
        String date = String.format("%d월 %d일", cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        tvDate.setText(String.format("%s %s", date, dayName));
    }

    @Override
    protected void onVideoAutoplay(AutoplayState autoplayState) {
        int messageResourceId;
        switch (autoplayState) {
            case AutoPlay:
                messageResourceId = R.string.autoplay_enabled_always;
                break;
            case AutoPlayOnWIFI:
                messageResourceId = R.string.autoplay_enabled_on_wifi;
                break;
            case NotAllowed:
                messageResourceId = R.string.autoplay_disabled;
                break;
            default:
                return;
        }
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), messageResourceId, Snackbar.LENGTH_SHORT);
        final View snackBarView = snackbar.getView();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int marginBottom = (int)(0.2f * displayMetrics.heightPixels);
        int marginSide = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46, displayMetrics);

        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)snackBarView.getLayoutParams();
        params.setMargins(marginSide, 0, marginSide, marginBottom);
        snackBarView.setLayoutParams(params);

        snackBarView.setBackgroundResource(R.drawable.bg_snackbar);
        snackbar.setActionTextColor(getResources().getColor(android.R.color.holo_green_light));
        snackbar.show();
    }
}
