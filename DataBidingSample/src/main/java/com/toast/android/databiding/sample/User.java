package com.toast.android.databiding.sample;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;

public class User extends BaseObservable {
    private String mFirstName;
    private String mLastName;

    public User(@NonNull String firstName, @NonNull String lastName) {
        this.mFirstName = firstName;
        this.mLastName = lastName;
    }

    @Bindable
    public String getFirstName() {
        return mFirstName;
    }

    @Bindable
    public String getLastName() {
        return mLastName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
        notifyPropertyChanged(BR.firstName);
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
        notifyPropertyChanged(BR.lastName);
    }
}
