package com.collinguarino.nowmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main extends Activity {

    final Context context = this;
    private LinearLayout mContainerView;
    boolean countUpMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newTimeFragment:

                inflateEditRow();


                return true;

            case R.id.goTop:

                // go to top of scrollview
                final ScrollView scrollView = (ScrollView) findViewById(R.id.mainView);
                scrollView.setSmoothScrollingEnabled(true);
                scrollView.fullScroll(ScrollView.FOCUS_UP);

                return true;

            case R.id.settings:

                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);

                return true;

            case R.id.deleteAll:

                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Delete All Events?");
                builder1.setMessage("This action cannot be undone.");
                builder1.setCancelable(true);

                // delete
                builder1.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                mContainerView.removeAllViews();
                                Toast.makeText(getApplicationContext(),"All Events Deleted", Toast.LENGTH_SHORT).show();

                            }
                        });

                // don't proceed
                builder1.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();

                            }
                        });

                AlertDialog alert = builder1.create();
                alert.show();

                return true;

        } return true;
    }

    private void inflateEditRow() {
        // handling the inflation of a new timestamped card

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.time_card, null);

        final TextView dateText = (TextView) rowView.findViewById(R.id.dateText);
        final TextView timeText = (TextView) rowView.findViewById(R.id.timeText);

        Time time = new Time();
        time.setToNow();

        String ampm = "";

        Calendar datetime = Calendar.getInstance();

        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            ampm = "AM";
        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
            ampm = "PM";

        int hourString = Calendar.getInstance().get(Calendar.HOUR);
        if (hourString == 0) {
            hourString = 12;
        }

        int secondString = Calendar.getInstance().get(Calendar.SECOND);

        if (secondString < 10){
            timeText.setText(hourString + ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":" + "0" + secondString); // 12 hour version: add if statement on 24hr version
        } else {
            timeText.setText(hourString + ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":" + secondString); // 12 hour version: add if statement on 24hr version
        }

        dateText.setText(new SimpleDateFormat("MM-dd").format(new Date())+" "+ampm);

        // count up mode
        if (countUpMode) {
            final EditText eventNameInput = (EditText) rowView.findViewById(R.id.eventNameInput);
            eventNameInput.setText(String.valueOf(mContainerView.getChildCount() +1));
        }

        RelativeLayout timeCardFragmentLayout = (RelativeLayout) rowView.findViewById(R.id.timeCardFragmentLayout);
        timeCardFragmentLayout.setOnTouchListener(new OnSwipeTouchListener() {
            public void onSwipeTop() {
                //Toast.makeText(getApplicationContext(), "top", Toast.LENGTH_SHORT).show();



            }

            public void onSwipeRight() {

                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();

                // Deletes the fragment
                mContainerView.removeViewAt(mContainerView.indexOfChild(rowView));

                // hide keyboard
                try
                {
                    InputMethodManager inputManager = (InputMethodManager) Main.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(Main.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                catch (Exception e)
                {

                }

            }

            public void onSwipeLeft() {

                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();

                // Deletes the fragment
                mContainerView.removeViewAt(mContainerView.indexOfChild(rowView));

                // hide keyboard
                try
                {
                    InputMethodManager inputManager = (InputMethodManager) Main.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(Main.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                catch (Exception e)
                {
                    // Ignore exceptions if any
                    Log.e("KeyBoardUtil", e.toString(), e);
                }

            }

            public void onSwipeBottom() {

            }

            public void onLongPressed() {
                //Toast.makeText(getApplicationContext(),"LONG PRESSED", Toast.LENGTH_SHORT).show();

            }

        });

        // animation for popping in new card
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(50);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(500);
        set.addAnimation(animation);
        rowView.setAnimation(animation);

        mContainerView = (LinearLayout) findViewById(R.id.parentView);
        mContainerView.addView(rowView, mContainerView.getChildCount());


        Handler handler=new Handler();
        final Runnable r = new Runnable()
        {
            public void run()
            {
                // temportary fix to inflate issue. Scrolls down to the bottom to show most recent. Working on opposite order.
                final ScrollView scrollView1 = (ScrollView) findViewById(R.id.mainView);
                scrollView1.setSmoothScrollingEnabled(true);
                scrollView1.fullScroll(View.FOCUS_DOWN);
            }
        };

        handler.postDelayed(r, 300);
    }

    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector = new GestureDetector(new GestureListener());

        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener implements GestureDetector.OnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {

                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {


            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {



                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

                onLongPressed();

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                    } else {
                        if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffY > 0) {
                                onSwipeBottom();
                            } else {
                                onSwipeTop();
                            }
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }

        public void onLongPressed() {

        }
    }


        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);

        }

        @Override
        protected void onRestoreInstanceState(Bundle inState) {
            super.onRestoreInstanceState(inState);

        }

        @Override
        protected void onResume() {
            super.onResume();

            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(this);

            countUpMode = preferences.getBoolean("COUNT_UP_MODE", false);
        }
    }
