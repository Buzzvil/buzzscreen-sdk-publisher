package com.buzzvil.buzzscreen.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.buzzvil.buzzscreen.sdk.BuzzScreen;
import com.buzzvil.buzzscreen.sdk.UserProfile;

import java.util.Random;

public class MainActivity extends Activity {
    final static String TAG = "MainActivity";
    boolean privacyConsentEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 앱 실행시 처음 실행되는 액티비티에 추가해 준다.
        BuzzScreen.getInstance().launch();

        Button btnLockerOn = (Button) findViewById(R.id.locker_on);
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
                if (privacyConsentEnabled) {
                    BuzzScreen.getInstance().activate();
                } else {
                    /*
                    BuzzScreen.getInstance().activateIfConsent(MainActivity.this, new BuzzScreen.OnActivateListener() {
                        @Override
                        public void onActivated() {
                            Toast.makeText(MainActivity.this, "Activated", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelledByUser() {
                            Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });
                    */
                }
            }
        });

        Button btnLockerOff = (Button) findViewById(R.id.locker_off);
        btnLockerOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버즈스크린 비활성화
                BuzzScreen.getInstance().deactivate();
            }
        });

        final Button privacyConsentRevokeButton = findViewById(R.id.privacy_consent_revoke);
        ((CheckBox) findViewById(R.id.checkbox_test_privacy_consent)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                privacyConsentEnabled = isChecked;
                privacyConsentRevokeButton.setVisibility(privacyConsentEnabled ? View.VISIBLE : View.GONE);
            }
        });

        privacyConsentRevokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                BuzzScreen.getInstance().showRevokeConsentDialog(MainActivity.this, new BuzzScreen.OnConsentRevokeListener() {
                    @Override
                    public void onConsentRevoked() {
                        Toast.makeText(MainActivity.this, "Revoked", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDeactivated() {
                        Toast.makeText(MainActivity.this, "Deactivated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelledByUser() {
                        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                */
            }
        });
    }
}
