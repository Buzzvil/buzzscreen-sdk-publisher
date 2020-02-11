package com.sample;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.sample.R;
import com.buzzvil.buzzscreen.sdk.BuzzScreen;
import com.buzzvil.buzzscreen.sdk.SecurityConfiguration;
import com.buzzvil.buzzscreen.sdk.SimpleLockerActivity;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // app_key : SDK 사용을 위한 앱키로, 어드민에서 확인 가능
        // SimpleLockerActivity.class : 잠금화면 액티비티 클래스
        // R.drawable.image_on_fail : 네트워크 에러 혹은 일시적으로 잠금화면에 보여줄 캠페인이 없을 경우 보여주게 되는 이미지.
        BuzzScreen.init("419318955785795", this, SimpleLockerActivity.class, R.drawable.image_on_fail, Constants.useGDPR ? BuzzScreen.PrivacyPolicy.GDPR : BuzzScreen.PrivacyPolicy.NONE);
        BuzzScreen.getInstance().setSecurityConfiguration(
                new SecurityConfiguration.Builder()
                        .withSecurity(Constants.useSecurity)
                        .backgroundResourceId(R.drawable.image_stu_center)
                        .backgroundImageScaleType(ImageView.ScaleType.FIT_CENTER)
                        .backgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSecurityBackground))
                        .backgroundDimAlpha(0f)
                        .showClock(false)
                        .showDescription(true)
                        .build());
    }
}
