package io.github.keep2iron.bezierindicator;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(3); // viewpager缓存页数
        mViewPager.setPageMargin(60);
        PagerAdapter pagerAdapter = new PagerAdapter() {
            int[] drawables = {R.drawable.pos1, R.drawable.pos2, R.drawable.pos0};

            @Override
            public int getCount() {
                return drawables.length;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = new ImageView(getBaseContext());
                imageView.setImageResource(drawables[position]);
                container.addView(imageView);

                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        };

        mViewPager.setAdapter(pagerAdapter);

        BezierIndicator bezierIndicator = (BezierIndicator) findViewById(R.id.bezierIndicator);
        bezierIndicator.setUpWithViewPager(mViewPager);
    }
}
