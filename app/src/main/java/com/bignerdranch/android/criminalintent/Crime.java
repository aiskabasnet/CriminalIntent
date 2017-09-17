package com.bignerdranch.android.criminalintent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by test on 9/6/17.
 */

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
   // SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    public Crime(){
        this(UUID.randomUUID());
    }
    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public Date getDate() {
        //String S = sdf.format(mDate);
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public UUID getId() {

        return mId;

    }


}
