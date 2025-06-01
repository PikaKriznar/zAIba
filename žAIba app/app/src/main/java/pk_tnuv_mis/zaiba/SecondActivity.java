package pk_tnuv_mis.zaiba;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.Calendar;

public class SecondActivity extends AppCompatActivity {
    RelativeLayout soundOverlay;
    TextView frogName, timer, duration;
    ImageButton btnPlay, btnClose;
    VideoView videoWaveform;
    Handler handler = new Handler();
    enum TimeFilter { TODAY, WEEKLY, MONTHLY }
    TimeFilter currentFilter = TimeFilter.TODAY;
    private Button selectedSpeciesButton = null;
    private CalendarAndGraphHelper helper;
    private static final String CURRENT_MONTH = "May 2025";
    private RelativeLayout currentOverlay = null; // Track the current overlay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        helper = new CalendarAndGraphHelper(this);

        setupBottomMenu();

        // Initialize with Today filter
        currentFilter = TimeFilter.TODAY;
        updateViewForFilter();

        ImageButton backButton = findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        DonutView donutView = findViewById(R.id.donutView);
        if (donutView != null) {
            donutView.setPercentage(85);
        }

        soundOverlay = findViewById(R.id.sound_overlay);
        frogName = findViewById(R.id.frog_name);
        timer = findViewById(R.id.timer);
        duration = findViewById(R.id.duration);
        btnPlay = findViewById(R.id.btn_play);
        btnClose = findViewById(R.id.btn_close);
        videoWaveform = findViewById(R.id.video_waveform);

        // Loudest frog card click listener
        View loudestFrogCard = findViewById(R.id.loudest_frog_card);
        if (loudestFrogCard != null) {
            loudestFrogCard.setOnClickListener(v -> {
                showSoundOverlayAndPlayVideo();
            });
        }

        Intent intent = getIntent();
        String frog = intent.getStringExtra("frog_name");
        if (frogName != null && frog != null) {
            frogName.setText(frog);
        }

        if (videoWaveform != null) {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.no_bckgrnd_2);
            videoWaveform.setVideoURI(videoUri);

            // Ensure the VideoView plays the video
            videoWaveform.setOnPreparedListener(mp -> {
                if (duration != null) {
                    duration.setText(formatTime(mp.getDuration()));
                }
                soundOverlay.setVisibility(View.VISIBLE); // Show the sound overlay
            });

            videoWaveform.setOnCompletionListener(mp -> {
                soundOverlay.setVisibility(View.GONE);
            });

            videoWaveform.start(); // Start the video immediately when set
        }

        if (btnPlay != null) {
            btnPlay.setOnClickListener(v -> {
                if (videoWaveform != null) {
                    if (videoWaveform.isPlaying()) {
                        videoWaveform.pause();
                        btnPlay.setImageResource(android.R.drawable.ic_media_play);
                    } else {
                        videoWaveform.start();
                        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                        updateTimer();
                    }
                    soundOverlay.setVisibility(View.VISIBLE); // Ensure overlay shows when playing
                }
            });
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> {
                if (videoWaveform != null && videoWaveform.isPlaying()) {
                    videoWaveform.stopPlayback();
                }
                if (soundOverlay != null) {
                    soundOverlay.setVisibility(View.GONE);
                }
            });
        }

        TextView cityNameTextView = findViewById(R.id.location_smallTitle);
        TextView locationNameTextView = findViewById(R.id.location_bigTitle);

        String cityName = intent.getStringExtra("CITY_NAME");
        String locationName = intent.getStringExtra("LOCATION_NAME");

        if (cityNameTextView != null && cityName != null) {
            cityNameTextView.setText(cityName);
        }
        if (locationNameTextView != null && locationName != null) {
            locationNameTextView.setText(locationName);
        }

        // Setup time filter buttons
        Button btnToday = findViewById(R.id.location_today_btn);
        Button btnWeekly = findViewById(R.id.location_weekly_btn);
        Button btnMonthly = findViewById(R.id.location_monthly_btn);

        if (btnToday != null) {
            btnToday.setOnClickListener(v -> handleTimeFilterButtonClick(btnToday, TimeFilter.TODAY));
        }
        if (btnWeekly != null) {
            btnWeekly.setOnClickListener(v -> handleTimeFilterButtonClick(btnWeekly, TimeFilter.WEEKLY));
        }
        if (btnMonthly != null) {
            btnMonthly.setOnClickListener(v -> handleTimeFilterButtonClick(btnMonthly, TimeFilter.MONTHLY));
        }

        // Setup species buttons
        Button btnSpeciesA = findViewById(R.id.location_nav_krastaca_btn);
        Button btnSpeciesB = findViewById(R.id.location_zel_rega_btn);
        Button btnSpeciesC = findViewById(R.id.location_sekulja_btn);
        Button[] speciesButtons = { btnSpeciesA, btnSpeciesB, btnSpeciesC };

        if (btnSpeciesA != null) {
            btnSpeciesA.setOnClickListener(v -> {
                handleSpeciesButtonClick(btnSpeciesA, speciesButtons);
                if (currentFilter == TimeFilter.MONTHLY) {
                    updateCalendarColors();
                } else {
                    updateBarGraphs();
                }
            });
        }

        if (btnSpeciesB != null) {
            btnSpeciesB.setOnClickListener(v -> {
                handleSpeciesButtonClick(btnSpeciesB, speciesButtons);
                if (currentFilter == TimeFilter.MONTHLY) {
                    updateCalendarColors();
                } else {
                    updateBarGraphs();
                }
            });
        }

        if (btnSpeciesC != null) {
            btnSpeciesC.setOnClickListener(v -> {
                handleSpeciesButtonClick(btnSpeciesC, speciesButtons);
                if (currentFilter == TimeFilter.MONTHLY) {
                    updateCalendarColors();
                } else {
                    updateBarGraphs();
                }
            });
        }
    }

    private void handleTimeFilterButtonClick(Button clickedButton, TimeFilter filter) {
        Log.d("SecondActivity", "handleTimeFilterButtonClick: Changing filter to " + filter);
        if (currentFilter == filter) {
            currentFilter = TimeFilter.TODAY;
            Log.d("SecondActivity", "Filter reset to TODAY");
        } else {
            currentFilter = filter;
            Log.d("SecondActivity", "Filter set to " + filter);
        }

        updateViewForFilter();
    }

    private void updateViewForFilter() {
        Log.d("SecondActivity", "updateViewForFilter: Current filter is " + currentFilter);
        Button btnToday = findViewById(R.id.location_today_btn);
        Button btnWeekly = findViewById(R.id.location_weekly_btn);
        Button btnMonthly = findViewById(R.id.location_monthly_btn);

        highlightSelectedFilterButton(btnToday, btnWeekly, btnMonthly);

        View barGraphContainer = findViewById(R.id.graphContainer);
        View calendarGrid = findViewById(R.id.calendarView);

        if (currentFilter == TimeFilter.MONTHLY) {
            if (barGraphContainer != null) {
                barGraphContainer.setVisibility(View.GONE);
            }
            if (calendarGrid != null) {
                calendarGrid.setVisibility(View.VISIBLE);
            }
            GridLayout monthlyGrid = findViewById(R.id.monthlyCalendarGrid);
            if (monthlyGrid != null) {
                helper.populateCalendar(monthlyGrid, CURRENT_MONTH, this::showSpeciesOverlay);
                disableFutureDates(monthlyGrid); // Disable dates after today and remove blue highlight
                updateCalendarColors();
            }
        } else {
            if (calendarGrid != null) {
                calendarGrid.setVisibility(View.GONE);
            }
            if (barGraphContainer != null) {
                barGraphContainer.setVisibility(View.VISIBLE);
                updateBarGraphs();
            }
        }
    }

    private void updateCalendarColors() {
        GridLayout monthlyGrid = findViewById(R.id.monthlyCalendarGrid);
        if (monthlyGrid != null) {
            helper.colorCalendarSquares(
                    monthlyGrid,
                    selectedSpeciesButton,
                    R.id.location_nav_krastaca_btn,
                    R.id.location_zel_rega_btn,
                    R.id.location_sekulja_btn,
                    CURRENT_MONTH
            );
        }
    }

    private void updateBarGraphs() {
        LinearLayout barGraphContainer = findViewById(R.id.graphContainer);
        if (barGraphContainer != null) {
            Log.d("SecondActivity", "updateBarGraphs: Calling populate with filter " + currentFilter);
            if (currentFilter == TimeFilter.MONTHLY) {
                helper.populateBarGraphs(
                        barGraphContainer,
                        selectedSpeciesButton,
                        R.id.location_nav_krastaca_btn,
                        R.id.location_zel_rega_btn,
                        R.id.location_sekulja_btn,
                        CURRENT_MONTH,
                        currentFilter
                );
            } else {
                helper.populateDailyAndWeeklyBarGraphs(
                        barGraphContainer,
                        selectedSpeciesButton,
                        R.id.location_nav_krastaca_btn,
                        R.id.location_zel_rega_btn,
                        R.id.location_sekulja_btn,
                        CURRENT_MONTH,
                        currentFilter
                );
            }
            helper.highlightTodayLabel(barGraphContainer, CURRENT_MONTH, currentFilter);
        }
    }

    @SuppressLint("SetTextI18n")
    private void showSpeciesOverlay(int day) {
        if (currentOverlay != null) {
            return;
        }
        if (isAfterToday(day)) {
            return;
        }
        RelativeLayout overlayContainer = new RelativeLayout(this);
        overlayContainer.setBackgroundColor(Color.parseColor("#80000000"));
        RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        overlayContainer.setLayoutParams(containerParams);
        overlayContainer.setClickable(true);
        overlayContainer.setFocusable(true);

        RelativeLayout overlayContent = new RelativeLayout(this);
        overlayContent.setBackgroundColor(Color.WHITE);
        overlayContent.setPadding(40, 40, 40, 40);
        RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        contentParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        overlayContent.setLayoutParams(contentParams);

        TextView speciesInfo = new TextView(this);
        speciesInfo.setId(View.generateViewId());
        speciesInfo.setText(getSpeciesDataForDay(day));
        speciesInfo.setTextSize(18);
        speciesInfo.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams infoParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        speciesInfo.setLayoutParams(infoParams);

        Button closeOverlayButton = new Button(this);
        closeOverlayButton.setId(View.generateViewId());
        closeOverlayButton.setText("Close");
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.BELOW, speciesInfo.getId());
        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        buttonParams.setMargins(0, 20, 0, 0);
        closeOverlayButton.setLayoutParams(buttonParams);
        closeOverlayButton.setOnClickListener(v -> {
            if (overlayContainer.getParent() != null) {
                ((ViewGroup) overlayContainer.getParent()).removeView(overlayContainer);
                currentOverlay = null;
            }
        });

        overlayContent.addView(speciesInfo);
        overlayContent.addView(closeOverlayButton);
        overlayContainer.addView(overlayContent);

        RelativeLayout rootLayout = findViewById(R.id.activity_second_root_layout);
        if (rootLayout != null) {
            rootLayout.addView(overlayContainer);
            currentOverlay = overlayContainer;
        }
    }

    private boolean isAfterToday(int day) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        // Assuming CURRENT_MONTH is "May 2025" and day is the day of the month
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(Calendar.YEAR, 2025);
        selectedDate.set(Calendar.MONTH, Calendar.MAY); // May is 4 (0-based index)
        selectedDate.set(Calendar.DAY_OF_MONTH, day);
        selectedDate.set(Calendar.HOUR_OF_DAY, 0);
        selectedDate.set(Calendar.MINUTE, 0);
        selectedDate.set(Calendar.SECOND, 0);
        selectedDate.set(Calendar.MILLISECOND, 0);

        return selectedDate.after(today);
    }

    private void disableFutureDates(GridLayout monthlyGrid) {
        if (monthlyGrid == null) return;

        int childCount = monthlyGrid.getChildCount();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        int todayDay = today.get(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < childCount; i++) {
            View child = monthlyGrid.getChildAt(i);
            if (child instanceof TextView) {
                TextView dayView = (TextView) child;
                try {
                    int day = Integer.parseInt(dayView.getText().toString());
                    // Remove default click highlight by setting a static background
                    dayView.setBackgroundColor(Color.TRANSPARENT); // Default to transparent
                    if (day > todayDay) {
                        dayView.setClickable(false);
                        dayView.setBackgroundColor(Color.LTGRAY); // Visual cue for future dates
                        dayView.setTextColor(Color.GRAY); // Visual cue for future dates
                    }
                } catch (NumberFormatException e) {
                    Log.w("SecondActivity", "Non-numeric day found: " + dayView.getText(), e);
                }
            }
        }
    }

    private String getSpeciesDataForDay(int day) {
        try {
            String jsonFile = helper.getJsonFileForMonth(CURRENT_MONTH);
            InputStream is = getAssets().open(jsonFile);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            JSONObject json = new JSONObject(new String(buffer, "UTF-8"));
            JSONObject dayData = json.getJSONObject(CURRENT_MONTH).getJSONObject(String.valueOf(day));
            return "Day " + day + " species count:\n" +
                    "Navadna krastača: " + dayData.getInt("navadna krastača") + "\n" +
                    "Zelena rega: " + dayData.getInt("zelena rega") + "\n" +
                    "Sekulja: " + dayData.getInt("sekulja");
        } catch (Exception e) {
            Log.e("SecondActivity", "Error reading JSON for day " + day, e);
            return "Day " + day + " species count:\nError loading data";
        }
    }

    private void handleSpeciesButtonClick(Button clickedButton, Button[] allButtons) {
        Log.d("SecondActivity", "handleSpeciesButtonClick: Selected button " + clickedButton.getText());
        if (selectedSpeciesButton == clickedButton) {
            selectedSpeciesButton = null;
            Log.d("SecondActivity", "Deselected species");
        } else {
            selectedSpeciesButton = clickedButton;
            Log.d("SecondActivity", "Selected species: " + clickedButton.getText());
        }

        highlightSelectedSpeciesButton(allButtons);
        if (currentFilter == TimeFilter.MONTHLY) {
            updateCalendarColors();
        } else {
            updateBarGraphs();
        }
    }

    private void highlightSelectedSpeciesButton(Button[] allButtons) {
        int selectedColor = ContextCompat.getColor(this, R.color.light_green);
        int defaultColor = ContextCompat.getColor(this, R.color.default_button_bg);

        for (Button btn : allButtons) {
            if (btn != null) {
                btn.setBackgroundColor(btn == selectedSpeciesButton ? selectedColor : defaultColor);
            }
        }
    }

    private void highlightSelectedFilterButton(Button today, Button weekly, Button monthly) {
        int selectedColor = ContextCompat.getColor(this, R.color.light_green);
        int defaultColor = ContextCompat.getColor(this, R.color.default_button_bg);

        Button[] buttons = { today, weekly, monthly };
        for (Button btn : buttons) {
            if (btn != null) {
                boolean isSelected =
                        (btn == today && currentFilter == TimeFilter.TODAY) ||
                                (btn == weekly && currentFilter == TimeFilter.WEEKLY) ||
                                (btn == monthly && currentFilter == TimeFilter.MONTHLY);
                btn.setBackgroundColor(isSelected ? selectedColor : defaultColor);
            }
        }
    }

    private void showSoundOverlayAndPlayVideo() {
        if (soundOverlay != null) {
            soundOverlay.setVisibility(View.VISIBLE);
        }

        if (videoWaveform != null) {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.no_bckgrnd_2);
            videoWaveform.setVideoURI(videoUri);

            videoWaveform.setOnPreparedListener(mp -> {
                if (duration != null) {
                    duration.setText(formatTime(mp.getDuration()));
                }
            });

            videoWaveform.start();

            if (btnPlay != null) {
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                updateTimer();
            }
        }
    }

    private void updateTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (videoWaveform != null && videoWaveform.isPlaying()) {
                    int currentPosition = videoWaveform.getCurrentPosition();
                    if (timer != null) {
                        timer.setText(formatTime(currentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void setupBottomMenu() {
        ImageButton homeButton = findViewById(R.id.menu_bar_button_home);
        ImageButton infoButton = findViewById(R.id.menu_bar_button_info);
        ImageButton settingsButton = findViewById(R.id.menu_bar_button_settings);

        if (homeButton != null) {
            homeButton.setOnClickListener(v -> {
                Intent homeIntent = new Intent(SecondActivity.this, MainActivity.class);
                startActivity(homeIntent);
            });
        }

        if (infoButton != null) {
            infoButton.setOnClickListener(v -> {
                Intent infoIntent = new Intent(SecondActivity.this, ThirdActivity.class);
                startActivity(infoIntent);
            });
        }

        if (settingsButton != null) {
            settingsButton.setOnClickListener(v -> {
                Intent settingsIntent = new Intent(SecondActivity.this, FourthActivity.class);
                startActivity(settingsIntent);
            });
        }
    }

    // Override onBackPressed to prevent closing the activity while overlay is visible
    @Override
    public void onBackPressed() {
        if (currentOverlay != null) {
            // Close the overlay instead of the activity
            if (currentOverlay.getParent() != null) {
                ((ViewGroup) currentOverlay.getParent()).removeView(currentOverlay);
                currentOverlay = null;
            }
        } else {
            super.onBackPressed(); // Default back behavior
        }
    }
}