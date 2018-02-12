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

public class UpdateManager extends SQLiteOpenHelper {

    private static String TAG = "UpdateManager";
    private Context mContext = null;
    private DbHelper mBusinessDbHelper = null;

    public UpdateManager(Context context, DbHelper businessDbHelper, String managerDbName, CursorFactory factory) {
        super(context, managerDbName, factory, App.getAppVersionNumber(context));
        mContext = context;
        mBusinessDbHelper = businessDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "onCreate: " + App.getAppVersionNumber(mContext));

        onUpgrade(db, 0, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "oldVersion: " + oldVersion + ", newVersion: " + newVersion);

        try {
            switch (newVersion) {
            case 1:
                Log.v(TAG, "First installation, no data needs to be made.");

            case 10111:
                Log.v(TAG, "Making data of version " + 10111 + " ...");
                mBusinessDbHelper.upgradeV10111();

            // case N: ...

            default:
                ; // nothing
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Hint.alert(mContext, R.string.alert_view_delusion_title, R.string.alert_view_delusion_contents);
        Hint.alert(mContext, R.string.version_log, R.string.version_log_contents);
        if (0 == oldVersion)
            Hint.alert(mContext, R.string.terms_of_note, R.string.terms_of_note_contents);
    }

}
