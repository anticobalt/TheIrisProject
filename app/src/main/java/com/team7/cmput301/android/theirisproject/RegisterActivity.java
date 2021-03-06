/*
 * Copyright (c) Team 7, CMPUT301, University of Alberta - All Rights Reserved. You may use, distribute, or modify this code under terms and conditions of the Code of Students Behavior at University of Alberta
 */

package com.team7.cmput301.android.theirisproject;

import android.content.Intent;
import android.os.Bundle;

import com.team7.cmput301.android.theirisproject.controller.IrisController;

public class RegisterActivity extends IrisActivity {

    IrisController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.controller = createController(getIntent());
    }

    @Override
    protected IrisController createController(Intent intent) {
        return null;
    }

    @Override
    protected Intent fillExtras(Intent intent, IrisController controller) {
        return null;
    }
}
