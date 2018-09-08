/*
 * Copyright (c) 2017-2018, Wen Xiongchang <udc577 at 126 dot com>
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

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android_assistant.App;
import com.android_assistant.Hint;

public class SettingsActivity extends Activity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getActionBar().setBackgroundDrawable(
            getResources().getDrawable(R.drawable.default_action_bar_style));

        final DbHelper dbHelper = new DbHelper(this);
        final String[] existentDbNames = DbHelper.getExistentDatabaseNames(this);
        String dbNameFromSharedPreferences = DbHelper.getDatabaseNameFromSharedPreferences(this);
        TextView txvDbFile = (TextView) findViewById(R.id.txv_db_file);
        Spinner spnDbFile = (Spinner) findViewById(R.id.spn_db_file);
        int currentDbNameIndex = 0;

        com.android_assistant.TextView.setDefaultTextShadow(txvDbFile);

        spnDbFile.setAdapter(new ArrayAdapter<String>(this, R.drawable.default_spinner_text, existentDbNames));
        if (null != existentDbNames && existentDbNames.length > 0) {
            for (int i = 0; i < existentDbNames.length; ++i) {
                if (dbNameFromSharedPreferences.equals(existentDbNames[i])) {
                    currentDbNameIndex = i;
                    break;
                }
            }
        }

        // NOTE: MUST be called after setAdapter() and before setOnItemSelectedListener()
        //     with the argument animate = true to prevent onItemSelected() being called
        //     on initialization!
        spnDbFile.setSelection(currentDbNameIndex, true);

        spnDbFile.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View view,
                int position, long id) {

                DbHelper.setDatabaseNameToSharedPreferences(SettingsActivity.this, existentDbNames[position]);
                TcmCommon.upgradeDatabase(SettingsActivity.this, dbHelper, false);
                Hint.alert(SettingsActivity.this, getString(R.string.config_successful), existentDbNames[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
                ;
            }
        });
    }

    @Override
    public void onDestroy() {
        App.cancelNotification(this);

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.cancelNotification(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (KeyEvent.KEYCODE_BACK == keyCode) {
            this.finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onUserLeaveHint() {
        App.moveTaskToBack(this, App.getAppName(this), true, R.drawable.ic_launcher);
    }
}
