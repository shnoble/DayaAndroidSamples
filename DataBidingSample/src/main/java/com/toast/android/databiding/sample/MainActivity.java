package com.toast.android.databiding.sample;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.toast.android.databiding.sample.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        User user = new User("Test", "User");
        mBinding.setUser(user);

        Counter counter = new Counter();
        mBinding.setCounter(counter);

        mBinding.setPresenter(new Presenter() {
            @Override
            public boolean onIncreaseCounterClick() {
                Log.d(TAG, "onIncreaseCounterClick");
                //mBinding.getCounter().counter.set(String.valueOf(Integer.valueOf(mBinding.getCounter().counter.get()) + 1));
                mBinding.getUser().setFirstName("aa");
                mBinding.getUser().setLastName("bb");
                return true;
            }
        });
    }
}
