package com.example.chienbk.connectbluetoothdemo.views;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.chienbk.connectbluetoothdemo.MainActivity;
import com.example.chienbk.connectbluetoothdemo.R;
import com.example.chienbk.connectbluetoothdemo.adapter.ViewPagerAdapter;
import com.example.chienbk.connectbluetoothdemo.fragment.ErrorCodeFragment;
import com.example.chienbk.connectbluetoothdemo.fragment.StatusFragment;

public class HomeActivity extends AppCompatActivity {

    //private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(BluetoothDevice.EXTRA_DEVICE);

        setTitle("Demo Bluetooth");
        initViews();
    }

    private void initViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StatusFragment(), "Status");
        adapter.addFragment(new ErrorCodeFragment(), "Error Code");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish();
    }
}
