package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by test on 9/10/17.
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }
}
