/*
 * Copyright (c) Team 7, CMPUT301, University of Alberta - All Rights Reserved. You may use, distribute, or modify this code under terms and conditions of the Code of Students Behavior at University of Alberta
 *
 *
 */

package com.team7.cmput301.android.theirisproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.team7.cmput301.android.theirisproject.UserListAdapter;
import com.team7.cmput301.android.theirisproject.IrisProjectApplication;
import com.team7.cmput301.android.theirisproject.R;
import com.team7.cmput301.android.theirisproject.controller.IrisController;
import com.team7.cmput301.android.theirisproject.model.CareProvider;
import com.team7.cmput301.android.theirisproject.model.Patient;
import com.team7.cmput301.android.theirisproject.model.TransferCode;
import com.team7.cmput301.android.theirisproject.model.User;
import com.team7.cmput301.android.theirisproject.model.User.UserType;
import com.team7.cmput301.android.theirisproject.task.AddTransferCodeTask;
import com.team7.cmput301.android.theirisproject.task.Callback;
import com.team7.cmput301.android.theirisproject.task.GetTransferCodeTask;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * ViewProfileActivity is for allowing the user to view their profile information.
 * @author caboteja
 */


public class ViewProfileActivity extends IrisActivity {

    private static final int REQUEST_EDIT_PROFILE = 0;

    private User user;

    private TextView name;
    private TextView email;
    private TextView phone;
    private TextView addCode;
    private TextView addCodeLabel;
    private Button generateCode;
    private Button editProfile;

    private TextView label;
    private ListView usersListView;

    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        user = IrisProjectApplication.getCurrentUser();

        name = findViewById(R.id.view_profile_name_text_view);
        email = findViewById(R.id.view_profile_email_text_view);
        phone = findViewById(R.id.view_profile_phone_text_view);
        addCode = findViewById(R.id.view_profile_add_code_text_view);
        addCodeLabel = findViewById(R.id.view_profile_add_code_label);
        generateCode = findViewById(R.id.view_profile_generate_code_button);
        editProfile = findViewById(R.id.view_profile_edit_profile_button);

        label = findViewById(R.id.view_profile_users_text_view);
        usersListView = findViewById(R.id.view_profile_users_list_view);

        name.setText(user.getUsername());
        email.setText(user.getEmail());
        phone.setText(user.getPhone());

        if (user.getType() == UserType.PATIENT) {
            addCode.setText(((Patient) user).getAddCode());
        } else {
            addCode.setVisibility(View.GONE);
            addCodeLabel.setVisibility(View.GONE);
        }

        generateCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if a transfer code already exists for this user
                Toast.makeText(ViewProfileActivity.this, R.string.view_profile_generate_code_toast, Toast.LENGTH_SHORT).show();

                new GetTransferCodeTask(new Callback<String>() {
                    @Override
                    public void onComplete(String code) {
                        if (code == null) {
                            // Couldn't find an existing code, generate one and save it to DB
                            code = RandomStringUtils.random(5, true, true);

                            TransferCode transferCode = new TransferCode(code, user.getUsername());
                            new AddTransferCodeTask().execute(transferCode);
                        }

                        TransferCodeDialogFragment dialog = TransferCodeDialogFragment.newInstance(code);
                        dialog.show(getSupportFragmentManager(), TransferCodeDialogFragment.class.getSimpleName());

                    }
                }).execute(user.getUsername());
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileActivity.this, EditProfileActivity.class);
                startActivityForResult(intent, REQUEST_EDIT_PROFILE);
            }
        });

        String labelText;
        if (user.getType() == UserType.PATIENT) {
            Patient patient = (Patient) user;
            adapter = new UserListAdapter(this, R.layout.list_user_item, patient.getCareProviders());
            labelText = getString(R.string.view_profile_care_providers);
        } else {
            CareProvider careProvider = (CareProvider) user;
            adapter = new UserListAdapter(this, R.layout.list_user_item, careProvider.getPatients());
            labelText = getString(R.string.view_profile_patients);
        }

        usersListView.setAdapter(adapter);
        label.setText(labelText);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_EDIT_PROFILE) {
                email.setText(user.getEmail());
                phone.setText(user.getPhone());
            }
        }
    }

    @Override
    protected IrisController createController(Intent intent) {
        return null;
    }
}
