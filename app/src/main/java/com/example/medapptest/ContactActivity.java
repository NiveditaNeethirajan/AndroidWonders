package com.example.medapptest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import com.example.medapptest.common.Constants;
import com.example.medapptest.common.FileHelper;
import com.example.medapptest.common.MedAppUserPreference;

public class ContactActivity extends Fragment {

    private Button emailBtn;
    private Button callBtn;
    private Button takeImgBtn;
    private ImageView imgView;
    private EditText nameEditText;
    private Context context;
    private Activity activity;

    public ContactActivity() {
        // Required empty public constructor
    }

    public ContactActivity(Context context_) {
        context = context_;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_contactmedapp, container, false);
        emailBtn = v.findViewById(R.id.emailBtn);
        callBtn =  v.findViewById(R.id.callBtn);
        takeImgBtn = v.findViewById(R.id.takeImgBtn);
        imgView =  v.findViewById(R.id.imageView);
        nameEditText = v.findViewById(R.id.nameEditText);
        activity = getActivity();
        if(activity == null) {
            activity = (Activity) context;
        }

        //Set initial value
        enableButtons(false);
        String name = MedAppUserPreference.getUserName();
        if(!TextUtils.isEmpty(name)) {
            nameEditText.setText(name);
        }
        imgView.setVisibility(View.GONE);

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEmailMABtnClicked();
            }
        });
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCallMABtnClicked();
            }
        });
        takeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTakeImgBtnClicked();
            }
        });
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(isButtonsEnabled())
                {
                    enableButtons(true);
                    return;
                }
                enableButtons(false);
            }
        });
        nameEditText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                //Save to preference
                                MedAppUserPreference.storeUserName(nameEditText.getText().toString());
                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                });
       return v;
    }

    //region button clickevents
    private void onEmailMABtnClicked() {
        if(!isButtonsEnabled()) {
            Toast.makeText(context, "Please enter name/click picture", Toast.LENGTH_SHORT).show();
            return;
        }
        //Save pref
        MedAppUserPreference.storeUserName(nameEditText.getText().toString());
        //Open a new activity
        Intent startEmailActivity = new Intent(context, EmailActivity.class);
        //Create a temp file for image and pass its location to EMail Activity
        Bitmap bitmap = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
        String filePath= FileHelper.CreateTempFile(context,bitmap, Constants.IMAGE_NAME);
        startEmailActivity.putExtra(Constants.KEY_EMAILINTENT_PICTURE, filePath);
        startActivity(startEmailActivity);
    }

    private void onCallMABtnClicked() {
        if(!isButtonsEnabled()) {
            Toast.makeText(context, "Please enter name/click picture", Toast.LENGTH_SHORT).show();
            return;
        }
        //Open a new activity
        Intent callNowActivity = new Intent(context, CallActivity.class);
        startActivity(callNowActivity);
    }

    private void onTakeImgBtnClicked() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Constants.CAMERA_PERMISSION_CODE);
            return;
        }
        startActivityForResult(intent, Constants.CAMERA_ACTIVITYRESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CAMERA_ACTIVITYRESULT_CODE && resultCode == activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgView.setVisibility(View.VISIBLE);
            imgView.setImageBitmap(bitmap);
            Toast.makeText(context, "Image captured successfully", Toast.LENGTH_SHORT).show();
            if(!TextUtils.isEmpty(nameEditText.getText().toString())) {
                //Also name is filled by the user
                //enable the btns
                enableButtons(true);
            }
            return;
        }
        Toast.makeText(context, "Unknown error while taking picture", Toast.LENGTH_SHORT).show();
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == Constants.CAMERA_PERMISSION_CODE) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onTakeImgBtnClicked();
            }
        }
    }
    //endregion

    //region helper Methods
    private void enableButtons(boolean enabled) {
        if(enabled) {
            callBtn.setBackgroundColor(getResources().getColor(R.color.btnBg));
            emailBtn.setBackgroundColor(getResources().getColor(R.color.btnBg));
            return;
        }
        callBtn.setBackgroundColor(getResources().getColor(R.color.btnBgDisabled));
        emailBtn.setBackgroundColor(getResources().getColor(R.color.btnBgDisabled));
    }

    private boolean isButtonsEnabled() {
        if(imgView.getDrawable() == null ||
                TextUtils.isEmpty(nameEditText.getText().toString())){
            return false;
        }
        return true;
    }
    //endregion
}
