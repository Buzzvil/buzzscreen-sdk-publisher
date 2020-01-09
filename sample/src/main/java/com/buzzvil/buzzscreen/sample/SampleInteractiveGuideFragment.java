package com.buzzvil.buzzscreen.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.buzzvil.buzzscreen.sdk.tutorial.BaseLockerInteractiveGuideFragment;

public class SampleInteractiveGuideFragment extends BaseLockerInteractiveGuideFragment {
    private static final String DOWNLOAD_URL = "https://play.google.com/store/apps/details?id=com.buzzvil.adhours&hl=en";

    private View rootView;
    private ProgressBar stepProgressView;
    private TextView stepNumberView;
    private TextView stepSubTitleView;
    private TextView stepDescriptionView;
    private ImageView landingImageView;
    private TextView nextButton;
    private TextView closeButton;
    private int step = 0;

    public static SampleInteractiveGuideFragment newInstance() {
        SampleInteractiveGuideFragment sampleInteractiveGuideFragment = new SampleInteractiveGuideFragment();
        return sampleInteractiveGuideFragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample_interactive_guide_migration, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.rootView = view.findViewById(R.id.tutorialContainer);
        this.stepProgressView = view.findViewById(R.id.tutorialProgressBar);
        this.stepNumberView = view.findViewById(R.id.tutorialStepNumber);
        this.stepSubTitleView = view.findViewById(R.id.tutorialSubTitle);
        this.stepDescriptionView = view.findViewById(R.id.tutorialDescription);

        this.nextButton = view.findViewById(R.id.nextButton);
        if (nextButton != null) {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (step == 3) {
                        landing(DOWNLOAD_URL);
                        completeInteractiveGuide();
                    } else {
                        proceedToNextStep();
                    }

                    // Toast for test
                    Toast.makeText(getContext(), "NEXT!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        this.closeButton = view.findViewById(R.id.closeButton);
        if (closeButton != null) {
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideGuides();
                    close();
                }
            });
        }

        this.landingImageView = view.findViewById(R.id.landingImageView);
        if (landingImageView != null) {
            landingImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    landing(DOWNLOAD_URL);
                    completeInteractiveGuide();
                }
            });
        }
    }

    @Override
    public void showStep(final int step) {
        super.showStep(step);

        this.step = step;
        stepProgressView.setProgress((step + 1) * 25);
        stepNumberView.setText(String.valueOf(step + 1));
        stepSubTitleView.setText(String.valueOf(step + 1) + " OF 4");
        landingImageView.setVisibility(View.GONE);
        switch (step) {
            case 0:
            default:
                stepDescriptionView.setText("위로 밀면?\n재미있는 컨텐츠가 한가득!");
                break;
            case 1:
                stepDescriptionView.setText("왼쪽으로 밀면?\n컨텐츠 보고 포인트 적립!");
                break;
            case 2:
                stepDescriptionView.setText("오른쪽으로 밀면,\n잠금해제하고 매일매일 포인트 적립!");
                break;
            case 3:
                nextButton.setText("Download");
                closeButton.setVisibility(View.VISIBLE);
                landingImageView.setVisibility(View.VISIBLE);
                stepSubTitleView.setText("더 많은 포인트를 모으려면?");
                stepDescriptionView.setText("이제 앱을 설치하고\n더 많은 포인트 받아 가세요!");
                break;
        }
    }

    private void hideGuides() {
        rootView.setVisibility(View.GONE);
    }
}
