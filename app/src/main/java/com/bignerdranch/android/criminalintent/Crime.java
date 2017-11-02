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
    private String mSuspect;
    private Date mDate;
    private boolean mSolved;
    private String mSuspectId;
    private long mContactId;

    public long getContactId() {
        return mContactId;
    }

    public void setContactId(long contactId) {
        mContactId = contactId;
    }
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

    public String getSuspect() {
        return mSuspect;
    }

    public String getSuspectId() {
        return mSuspectId;
    }

    public void setSuspectId(String suspectId) {

        mSuspectId = suspectId;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
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
    public String getPhotoFilename(){
        return "IMG_"+getId().toString()+".jpg";
    }


}
