package com.sample;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.buzzvil.buzzscreen.sdk.BaseLockerActivity;
import com.buzzvil.buzzscreen.sdk.Campaign;
import com.buzzvil.buzzscreen.sdk.widget.Slider;
import com.buzzvil.buzzscreen.sdk.widget.SliderIcon;

import java.text.DateFormatSymbols;
import java.util.Calendar;


public class SimpleLockerActivity extends BaseLockerActivity {
    final static String TAG = "LockerActivity";

    // 화면 하단 슬라이더
    Slider slider;

    // 시계
    TextView tvTime;
    TextView tvAmPm;
    TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_locker);

        initUI();
    }

    private void initUI() {
        tvTime = (TextView)findViewById(R.id.locker_time);
        tvAmPm = (TextView)findViewById(R.id.locker_am_pm);
        tvDate = (TextView)findViewById(R.id.locker_date);
        tvTime.setTypeface(Typeface.create("sans-serif-thin", Typeface.NORMAL));

        slider = (Slider)findViewById(R.id.locker_slider);

        // 슬라이더에서 왼쪽 아이콘이 선택(왼쪽 아이콘 위치에서 터치 업)되었을 때 호출되는 리스너
        slider.setLeftOnSelectListener(new SliderIcon.OnSelectListener() {
            @Override
            public void onSelect() {
                landing();
            }
        });

        // 슬라이더에서 오른쪽 아이콘이 선택(오른쪽 아이콘 위치에서 터치 업)되었을 때 호출되는 리스너
        slider.setRightOnSelectListener(new SliderIcon.OnSelectListener() {
            @Override
            public void onSelect() {
                unlock();
            }
        });

        // 화면 터치시 상하 페이시 화살표 표시
        setPageIndicators(
                findViewById(R.id.locker_arrow_top),
                findViewById(R.id.locker_arrow_bottom)
        );

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
        Log.i(TAG, campaign.toString());

        // 좌우 포인트가 변경되었을 때 UI 업데이트
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
}