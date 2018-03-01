/*
 * Copyright (c) 2018, Wen Xiongchang <udc577 at 126 dot com>
 * All rights reserved.
 *
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any
 * damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any
 * purpose, including commercial applications, and to alter it and
 * redistribute it freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must
 * not claim that you wrote the original software. If you use this
 * software in a product, an acknowledgment in the product documentation
 * would be appreciated but is not required.
 *
 * 2. Altered source versions must be plainly marked as such, and
 * must not be misrepresented as being the original software.
 *
 * 3. This notice may not be removed or altered from any source
 * distribution.
 */

// NOTE: The original author also uses (short/code) names listed below,
//       for convenience or for a certain purpose, at different places:
//       wenxiongchang, wxc, Damon Wen, udc577

package com.project.tcm_quick_search_notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android_assistant.App;
import com.android_assistant.Hint;

public class UpgradeManager extends SQLiteOpenHelper {

    private static String TAG = "UpgradeManager";
    private Context mContext = null;
    private DbHelper mBusinessDbHelper = null;
    private boolean mHasException = false;
    private String mExceptionMessage = null;

    public UpgradeManager(Context context, DbHelper businessDbHelper, String managerDbName, CursorFactory factory) {
        super(context, managerDbName, factory, App.getAppVersionNumber(context));
        mContext = context;
        mBusinessDbHelper = businessDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        int currentVersion = App.getAppVersionNumber(mContext);

        onUpgrade(db, 0, currentVersion);
        Log.v(TAG, "onCreate: " + currentVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "oldVersion: " + oldVersion + ", newVersion: " + newVersion);

        try {
            switch (oldVersion) {
            case 0:
                Log.v(TAG, "First installation, no data need to be made.");

            case 1:
            case 2:
            case 3:
            case 4:
            case 100:
            case 101:
            case 102:
            case 103:
            case 201:
            case 10000:
            case 10001:
            case 10101:
            case 10102:
            case 10103:
            case 10104:
            case 10105:
            case 10106:
            case 10107:
            case 10108:
            case 10109:
            case 10110:
                Log.v(TAG, "Making data of version " + 10111 + " ...");
                mBusinessDbHelper.upgradeV10111();

            case 10111:
                Log.v(TAG, "Making data of version " + 10112 + " ...");
                mBusinessDbHelper.upgradeV10112();

            case 10112:
                ;

            default:
                ; // nothing
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHasException = true;
            mExceptionMessage = new String((null != e.getCause()) ? e.getCause().getMessage() : e.getMessage());
        }

        Hint.alert(mContext, R.string.alert_view_delusion_title, R.string.alert_view_delusion_contents);
        Hint.alert(mContext, R.string.version_log, R.string.version_log_contents);
        if (0 == oldVersion)
            Hint.alert(mContext, R.string.terms_of_note, R.string.terms_of_note_contents);
    }

    public boolean hasException() {
        return mHasException;
    }

    public String getExceptionMessage() {
        return mExceptionMessage;
    }
}
