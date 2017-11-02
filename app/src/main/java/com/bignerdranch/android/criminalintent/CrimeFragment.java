package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by aiska on 9/8/17.
 */

public class CrimeFragment extends Fragment {
    public Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE =0;
    public UUID crimeId;
    private Button mReportButton;
    private static final int REQUEST_CONTACT = 1;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private static final int REQUEST_PHOTO = 3;

    private static final int REQUEST_PHONE = 2;

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        setHasOptionsMenu(true);

    }

    @Override
    public void onPause(){
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_crime,container,false);

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);

                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton = (Button)v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .createChooserIntent();
                /*Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));*/
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mCallButton = (Button) v.findViewById(R.id.call_suspect);
        mCallButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivityForResult(pickContact,REQUEST_PHONE);
                /*Uri contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selectClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                String[] fields = {
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };
                String[] selectParams = {
                        Long.toString(mCrime.getContactId())
                };
                Cursor cursor = getActivity().getContentResolver().query(contentUri,fields,selectClause,selectParams,null);
                if (cursor != null && cursor.getCount() > 0){
                    try {
                        cursor.moveToFirst();
                        String number = cursor.getString(0);
                        Uri phoneNumber = Uri.parse("tel:"+ number);
                        Intent intent = new Intent(Intent.ACTION_DIAL, phoneNumber);
                        startActivity(intent);
                    }
                    finally {
                        cursor.close();
                    }
                }*/
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY)==null){
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        if(canTakePhoto){
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                ImageFragment dialog = ImageFragment.newInstance(mPhotoFile.getPath());
                dialog.show(manager, null);
            }
        });
        updatePhotoView();


        return v;
    }
    private Cursor queryContacts(Uri uri, String[] fields,String whereClause, String[] args){
        Cursor c = getActivity().getContentResolver().query(uri,fields,whereClause,args,null);
        if (c.getCount() == 0){
            c.close();
            return null;
        }
        c.moveToFirst();
        return c;
    }

   /* private ArrayList<String> getPhoneNumber() {
        ArrayList<String> Result = new ArrayList<>();
        String[] queryFields = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts._ID
        };
        Cursor contact = getActivity().getContentResolver().query(
             ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                queryFields,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?",
                new String[]{
                        mCrime.getSuspectId()
                },
                null
        );
        try {
            if (contact.getCount() == 0){
                return Result;
            }
            contact.moveToFirst();
            while (contact.moveToNext()){
                String contactName = contact.getString(contact.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contactNumber = contact.getString(contact.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID));
                Result.add(contactNumber);
            }
        }
        finally {
            contact.close();
        }
        return Result;
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
        if (requestCode == REQUEST_CONTACT || requestCode == REQUEST_PHONE){
            Uri contactUri = data.getData();
            int suspectID;
            String phoneNumber;
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts._ID
            };
          //  Cursor c = getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);
            Cursor c = queryContacts(contactUri,queryFields,null,null);
            if (c == null)
                return;

            try{

                String suspect = c.getString(0);
                suspectID = c.getInt(1);
                mCrime.setSuspect(suspect);
               // mCrime.setContactId(contactId);
                mSuspectButton.setText(suspect);

            }finally {
                c.close();
            }

            if (requestCode == REQUEST_PHONE){
                queryFields = new String[]{
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };
                String whereClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?";
                String[] args = {
                        Integer.toString(suspectID)
                };
                c=queryContacts(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,queryFields,whereClause,args);
                if (c == null){
                    return;
                }
                try {
                    phoneNumber = c.getString(0);
                }finally {
                    c.close();

                }
                Uri phoneUri = Uri.parse("tel:" + phoneNumber);
                System.out.println(phoneNumber);
                Intent i = new Intent(Intent.ACTION_DIAL,phoneUri);
                startActivity(i);
            }

        }else if (requestCode == REQUEST_PHOTO){
            updatePhotoView();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_delete,menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(crimeId);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    }


    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }
    private String getCrimeReport(){
        String solvedString = null;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }
        else{
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        }else {
            suspect = getString(R.string.crime_report_suspect,suspect);
        }

        String report = getString(R.string.crime_report,mCrime.getTitle(),dateString,solvedString,suspect);
        return report;

    }

    private void updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

}

