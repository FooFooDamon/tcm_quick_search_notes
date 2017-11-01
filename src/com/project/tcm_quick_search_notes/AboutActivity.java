/*
 * Copyright (c) 2017, Wen Xiongchang <udc577 at 126 dot com>
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android_assistant.App;
import com.android_assistant.ResourceExports;

public class AboutActivity extends Activity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		getActionBar().setBackgroundDrawable(
			getResources().getDrawable(R.drawable.default_action_bar_style));

		final String COLON = ResourceExports.getString(this, R.array.colon);
		final String APP_NAME_KEY = ResourceExports.getString(this, R.array.app_name_key);
		final String APP_NAME = ResourceExports.getString(this, R.array.app_name);
		final String APP_VERSION = ResourceExports.getString(this, R.array.app_version);
		final String APP_AUTHOR = ResourceExports.getString(this, R.array.author);
		final String AUTHOR_INFO = ResourceExports.getString(this, R.array.author_info);
		final String USAGE = ResourceExports.getString(this, R.array.usage);
		final String USAGE_INFO = getString(R.string.usage_contents);
		final String REMARKS = ResourceExports.getString(this, R.array.remarks);
		final String REMARKS_INFO = getString(R.string.remarks_contents);
		final String LINE_DELIMITER = "";// "----------------------";
		final String[] ABOUT_ITEMS = {
			APP_NAME_KEY + COLON + APP_NAME,
			LINE_DELIMITER,
			APP_VERSION + COLON + App.getAppVersionString(this),
			LINE_DELIMITER,
			APP_AUTHOR + COLON + AUTHOR_INFO,
			LINE_DELIMITER,
			USAGE + COLON + USAGE_INFO,
			LINE_DELIMITER,
			REMARKS + COLON + REMARKS_INFO
		};
		ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this,
				R.drawable.default_text_style, ABOUT_ITEMS);
		ListView listView = (ListView) findViewById(R.id.lsv_about_items);

		listView.setAdapter(itemsAdapter);
		listView.setClickable(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.about, menu); // not needed
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
