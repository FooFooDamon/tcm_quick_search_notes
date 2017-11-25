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

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android_assistant.App;
import com.android_assistant.Hint;
import com.android_assistant.ResourceExports;

public class MainActivity extends Activity
	implements OnItemClickListener {
	
	private static final int LIST_ITEM_POS_MEDICINE = 0;
	private static final int LIST_ITEM_POS_PRESCRIPTION = 1;
	private static final int LIST_ITEM_POS_MISC_MANAGEMENT = 2;
	
	private static final int LIST_ITEM_POS_SETTINGS = 0;
	private static final int LIST_ITEM_POS_ABOUT = 1;
	private static final int LIST_ITEM_POS_HELP = 2;
	private static final int LIST_ITEM_POS_TERMS_OF_NOTE = 3;
	
	private static final int LIST_ITEM_POS_EXIT = 0;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setBackgroundDrawable(
			getResources().getDrawable(R.drawable.default_action_bar_style));
		
		LinearLayout pageLayout = (LinearLayout) findViewById(R.id.llayout_main_page);
		
		pageLayout.setBackgroundColor(android.graphics.Color.parseColor("#88b27e50"));
		
		final String[] MEDICATION_STRINGS = getMedicationItems();
		final MainPageItem[] MEDICATION_ITEMS = {
			new MainPageItem(getResources().getDrawable(R.drawable.ic_medicine),
				MEDICATION_STRINGS[LIST_ITEM_POS_MEDICINE]),
				
			new MainPageItem(getResources().getDrawable(R.drawable.ic_prescription),
				MEDICATION_STRINGS[LIST_ITEM_POS_PRESCRIPTION]),
				
			new MainPageItem(getResources().getDrawable(R.drawable.ic_misc),
				MEDICATION_STRINGS[LIST_ITEM_POS_MISC_MANAGEMENT])
		};
		
		inflateMainPageListView(MEDICATION_ITEMS, R.id.lsv_medication_group);
		
		final String[] HELP_STRINGS = getHelpItems();
		final MainPageItem[] HELP_ITEMS = {
			new MainPageItem(getResources().getDrawable(R.drawable.ic_settings),
				HELP_STRINGS[LIST_ITEM_POS_SETTINGS]),
				
			new MainPageItem(getResources().getDrawable(R.drawable.ic_about),
				HELP_STRINGS[LIST_ITEM_POS_ABOUT]),
				
			new MainPageItem(getResources().getDrawable(R.drawable.ic_help),
				HELP_STRINGS[LIST_ITEM_POS_HELP]),
				
			new MainPageItem(getResources().getDrawable(R.drawable.ic_terms_of_note),
				HELP_STRINGS[LIST_ITEM_POS_TERMS_OF_NOTE])
		};
		
		inflateMainPageListView(HELP_ITEMS, R.id.lsv_help_group);
		
		final String[] EXIT_STRINGS = getExitItems();
		final MainPageItem[] EXIT_ITEMS = {
			new MainPageItem(getResources().getDrawable(R.drawable.ic_exit),
				EXIT_STRINGS[LIST_ITEM_POS_EXIT])
		};
		
		inflateMainPageListView(EXIT_ITEMS, R.id.lsv_exit_group);
		
		DbHelper dbHelper = new DbHelper(this);
		
		try {
			dbHelper.precheck();
		} catch (Exception e) {
			Hint.alert(this, getString(R.string.data_init_error), e.getMessage(), new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MainActivity.this.finish();
				}
			});
		}
	}
	
	@Override
	protected void onDestroy() {
		App.cancelNotification(this);
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		if (App.isInBackground(this)/*0 != (getIntent().getFlags() & Intent.FLAG_FROM_BACKGROUND)*/) {
			String appName = App.getAppName(this);

			App.displayNotification(this, appName, getString(R.string.click_to_restore),
				appName + getString(R.string.running_in_background), R.drawable.ic_launcher);
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		/*if (0 != (getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)) {
			finish();
			return;
		}*/
		App.cancelNotification(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if (R.id.menu_version_log == id)
			Hint.alert(this, R.string.version_log, R.string.version_log_contents);
		else if (R.id.menu_copyright == id)
			Hint.alert(this, getString(R.string.copyright),
				getString(R.string.copyright_info) + "\n\n"
				+ getString(R.string.cht_copyright_info) + "\n\n"
				+ getString(R.string.en_copyright_info));
		else
			; // more things in future ...

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (KeyEvent.KEYCODE_BACK != keyCode)
			return super.onKeyDown(keyCode, event);

		App.moveTaskToBack(this, App.getAppName(this), true, R.drawable.ic_launcher);

		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		ListView listView = (ListView) parent;
		MainPageItem item = (MainPageItem) listView.getItemAtPosition(pos);
		
		if (listView == findViewById(R.id.lsv_medication_group)) {
			if (LIST_ITEM_POS_MEDICINE == pos)
				startActivity(new Intent(this, QueryEntryActivity.class));
			else if (LIST_ITEM_POS_PRESCRIPTION == pos) {
				Hint.alert(this, ResourceExports.getString(this, R.array.function_not_implemented),
					getString(R.string.please_look_forward_to_it));
				// startActivity(new Intent(this, SettingsActivity.class));
			}
			else if (LIST_ITEM_POS_MISC_MANAGEMENT == pos)
				startActivity(new Intent(this, MiscManagementActivity.class));
			else
				Hint.shortToast(this, String.valueOf(pos) + ": Unknown " + item.name);
			
			return;
		}
		
		if (listView == findViewById(R.id.lsv_help_group)) {
			if (LIST_ITEM_POS_SETTINGS == pos) {
				Hint.alert(this, ResourceExports.getString(this, R.array.function_not_implemented),
					getString(R.string.settings_not_implemented));
				// startActivity(new Intent(this, SettingsActivity.class));
			}
			else if (LIST_ITEM_POS_ABOUT == pos)
				startActivity(new Intent(this, AboutActivity.class));
			else if (LIST_ITEM_POS_HELP == pos)
				App.showHelpText(this, getString(R.string.help_info_for_main_page));
			else if (LIST_ITEM_POS_TERMS_OF_NOTE == pos)
				Hint.alert(this, R.string.terms_of_note, R.string.terms_of_note_contents);
			else
				Hint.shortToast(this, String.valueOf(pos) + ": Unknown " + item.name);
			
			return;
		}
		
		if (listView == findViewById(R.id.lsv_exit_group)) {
			if (LIST_ITEM_POS_EXIT == pos)
				App.exit(this);
			else
				Hint.shortToast(this, String.valueOf(pos) + ": Unknown " + item.name);
			
			return;
		}

		Hint.shortToast(this, "UNKNOWN ListView");
	}
	
	private final String[] getMedicationItems() {
		final String[] MEDICATION_ITEMS = {
			getString(R.string.main_item_medicine),
			getString(R.string.main_item_prescription),
			getString(R.string.main_item_misc_management)
		};
		
		return MEDICATION_ITEMS;
	}
	
	private final String[] getHelpItems() {
		final String[] HELP_ITEMS = {
			ResourceExports.getString(this, R.array.settings),
			ResourceExports.getString(this, R.array.about),
			ResourceExports.getString(this, R.array.help),
			ResourceExports.getString(this, R.array.terms_of_note)
		};
		
		return HELP_ITEMS;
	}
	
	private final String[] getExitItems() {
		final String[] EXIT_ITEMS = {
			ResourceExports.getString(this, R.array.exit)
		};
		
		return EXIT_ITEMS;
	}
	
	@SuppressWarnings("deprecation")
	private void inflateMainPageListView(final MainPageItem[] pageItems, int listViewId) {
		List<MainPageItem> itemList = new ArrayList<MainPageItem>();
		
		for (MainPageItem item : pageItems)
		{
			itemList.add(item);
		}
		
		MainPageItemAdapter adapter = new MainPageItemAdapter(this, itemList);
		ListView listView = (ListView) findViewById(listViewId);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setBackground(getResources().getDrawable(R.drawable.background));
	}
	
	private class MainPageItem {
		public Drawable icon;
		public String name;

		public MainPageItem() {
		}

		public MainPageItem(Drawable icon, String name) {
			this.icon = icon;
			this.name = name;
		}
	}
	
	private class MainPageItemAdapter extends BaseAdapter {

		private final Context mContext;
		private final List<MainPageItem> mItemList;
		private final LayoutInflater mInflater;

		public MainPageItemAdapter(Context context, List<MainPageItem> itemList) {
			super();
			this.mItemList = itemList;
			this.mContext = context;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return mItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			ViewHolder holder = null;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.main_page_item, null);
				holder.icon = (ImageView) convertView.findViewById(R.id.imgv_main_page_item);
				holder.name = (TextView) convertView.findViewById(R.id.txv_main_page_item);
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}

			MainPageItem item = mItemList.get(position);

			holder.icon.setImageDrawable(item.icon);
			holder.name.setText(item.name);
			com.android_assistant.TextView.setDefaultTextShadow(holder.name);

			return convertView;
		}

		private class ViewHolder {
			ImageView icon;
			TextView name;
		}
	}
}
