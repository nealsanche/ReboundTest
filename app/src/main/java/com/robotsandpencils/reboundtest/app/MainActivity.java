package com.robotsandpencils.reboundtest.app;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringConfigRegistry;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.ui.SpringConfiguratorView;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        @InjectView(R.id.testView)
        View mTestView;
        @InjectView(R.id.spring_configurator)
        SpringConfiguratorView mSpringConfigurator;

        private SpringSystem mSpringSystem;
        private SimpleSpringListener mScaleListener;
        private SimpleSpringListener mTranslateListener;
        private Spring mScaleSpring;
        private Spring mTranslateSpring;

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mSpringSystem = SpringSystem.create();
            mScaleSpring = mSpringSystem.createSpring();
            mTranslateSpring = mSpringSystem.createSpring();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ButterKnife.inject(this, rootView);

            mScaleListener = new SimpleSpringListener() {
                @Override
                public void onSpringAtRest(Spring spring) {
                    super.onSpringAtRest(spring);
                    mTestView.setBackgroundColor(Color.parseColor("#333333"));
                }

                @Override
                public void onSpringUpdate(Spring spring) {
                    super.onSpringUpdate(spring);
                    mTestView.setBackgroundColor(Color.parseColor("#0000FF"));

                    float value = (float) spring.getCurrentValue();
                    float scale = 1f - (value * 0.1f);
                    mTestView.setScaleX(scale);
                    mTestView.setScaleY(scale);
                }
            };

            mTranslateListener = new SimpleSpringListener() {
                @Override
                public void onSpringAtRest(Spring spring) {
                    super.onSpringAtRest(spring);
                }

                @Override
                public void onSpringUpdate(Spring spring) {
                    super.onSpringUpdate(spring);
                    float value = (float) spring.getCurrentValue();
                    mTestView.setTranslationY(value);
                }
            };
            mTranslateSpring.addListener(mTranslateListener);

            mTestView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mScaleSpring.setEndValue(0);
                        mTranslateSpring.setEndValue(0);
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mScaleSpring.setEndValue(1);
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        mTranslateSpring.setEndValue(event.getY());
                    }
                    return false;
                }
            });
            mTestView.setClickable(true);

            final SpringConfig springConfig = new SpringConfig(38, 30);
            mScaleSpring.setSpringConfig(springConfig);
            SpringConfigRegistry.getInstance().addSpringConfig(springConfig, "Scale");

            final SpringConfig springConfig2 = new SpringConfig(62, 9);
            mTranslateSpring.setSpringConfig(springConfig2);
            SpringConfigRegistry.getInstance().addSpringConfig(springConfig2, "Translate");

            mSpringConfigurator.refreshSpringConfigurations();


            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            mScaleSpring.addListener(mScaleListener);
            mTranslateSpring.addListener(mTranslateListener);
        }

        @Override
        public void onPause() {
            super.onPause();
            mScaleSpring.removeListener(mScaleListener);
            mTranslateSpring.removeListener(mTranslateListener);
        }
    }
}
