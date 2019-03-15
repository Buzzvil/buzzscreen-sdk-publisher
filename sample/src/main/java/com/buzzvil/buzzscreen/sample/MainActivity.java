package com.buzzvil.buzzscreen.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.buzzvil.buzzscreen.sdk.BuzzScreen;
import com.buzzvil.buzzscreen.sdk.UserProfile;

import java.util.Random;

public class MainActivity extends Activity {

    final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 앱 실행시 처음 실행되는 액티비티에 추가해 준다.
        BuzzScreen.getInstance().launch();

        Button btnLockerOn = findViewById(R.id.locker_on);
        btnLockerOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserProfile userProfile = BuzzScreen.getInstance().getUserProfile();

                // 포인트 적립을 위해서는 setUserId를 반드시 호출해야 함
//                userProfile.setUserId("testuserid12345");
                userProfile.setUserId("testuserid" + new Random().nextInt(100000));

                // 캠페인 할당을 위한 타게팅 정보
                userProfile.setBirthYear(1985);
                userProfile.setGender(UserProfile.USER_GENDER_MALE);
                userProfile.setRegion("서울특별시 관악구");

                // 버즈스크린 활성화
                if (Constants.useGDPR) {
                    BuzzScreen.getInstance().activateIfConsent(MainActivity.this, new BuzzScreen.OnActivateListener() {
                        @Override
                        public void onActivated() {
                            Toast.makeText(MainActivity.this, "Activated", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelledByUser() {
                            Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNetworkError() {
                            Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    BuzzScreen.getInstance().activate();
                }
            }
        });

        Button btnLockerOff = findViewById(R.id.locker_off);
        btnLockerOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버즈스크린 비활성화
                BuzzScreen.getInstance().deactivate();
            }
        });

        Button privacyConsentRevokeButton = findViewById(R.id.privacy_consent_revoke);
        privacyConsentRevokeButton.setVisibility(Constants.useGDPR ? View.VISIBLE : View.GONE);
        privacyConsentRevokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuzzScreen.getInstance().showRevokeConsentDialog(MainActivity.this, new BuzzScreen.OnConsentRevokeListener() {
                    @Override
                    public void onConsentRevoked() {
                        Toast.makeText(MainActivity.this, "Revoked", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelledByUser() {
                        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        RadioGroup radioAutoplay = findViewById(R.id.radio_autoplay);
        BuzzScreen.AutoplayType autoplayType = BuzzScreen.getInstance().getAutoplayType();
        if (autoplayType != null) {
            switch (autoplayType) {
                case ENABLED:
                    radioAutoplay.check(R.id.autoplay_always);
                    break;
                case ON_WIFI:
                    radioAutoplay.check(R.id.autoplay_on_wifi);
                    break;
                case DISABLED:
                    radioAutoplay.check(R.id.autoplay_off);
                    break;
            }
        }
        radioAutoplay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                BuzzScreen.AutoplayType selectedAutoplayType = null;
                if (checkedId == R.id.autoplay_always) {
                    selectedAutoplayType = BuzzScreen.AutoplayType.ENABLED;
                } else if (checkedId == R.id.autoplay_on_wifi) {
                    selectedAutoplayType = BuzzScreen.AutoplayType.ON_WIFI;
                } else if (checkedId == R.id.autoplay_off) {
                    selectedAutoplayType = BuzzScreen.AutoplayType.DISABLED;
                }

                BuzzScreen.getInstance().setAutoplayType(selectedAutoplayType);
            }
        });
    }
}
