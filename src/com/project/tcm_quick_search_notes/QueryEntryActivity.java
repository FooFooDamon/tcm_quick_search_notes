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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android_assistant.Hint;
import com.android_assistant.ResourceExports;
import com.android_assistant.Version;

public class QueryEntryActivity extends Activity
	implements OnItemClickListener {
	
	public static final int OP_TYPE_MEDICINE = 0;
	public static final int OP_TYPE_PRESCRIPTION = 1;
	
	public static final String FIELD_DELIM = "\b";
	public static final String ITEM_DELIM = "\f";
	
	private DbHelper mDbHelper = null;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_entry);
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.default_action_bar_style));
		
		mDbHelper = new DbHelper(this);
		mDbHelper.openOrCreate();
		
		if (Version.SDK <= Version.getDeprecatedVersionUpperBound())
			doExtraJobsForLowerVersions();
		
		fillCategorySpinner();
		
		Button btnAddMedication = (Button) findViewById(R.id.btn_add_medication);
		
		btnAddMedication.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String sqlCheckMedicine = getString(R.string.sql_query_medicine_items_by_name);
				String newMedicine = getString(R.string.new_medicine);
				String[] sqlArgs = (new String[]{ "%" + newMedicine + "%" });
				SQLiteDatabase db = mDbHelper.getDatabase();
				Cursor c = db.rawQuery(sqlCheckMedicine, sqlArgs);
				
				if (c.moveToNext()) {
					Hint.alert(QueryEntryActivity.this, R.string.alert_reusing_medicine_title, R.string.alert_reusing_medicine_contents);
					c.close();
					return;
				}
				c.close();
				
				String sqlAddMedicine = getString(R.string.sql_make_medicine_items_data);
				
				db.execSQL(sqlAddMedicine, new String[] { newMedicine, String.valueOf(0) });
				Hint.alert(QueryEntryActivity.this, R.string.hint_adding_medicine_successfully, R.string.hint_after_adding_medicine);
			}
		});
	}
	
	@Override
	public void onDestroy() {
		if (null != mDbHelper)
			mDbHelper.close();
		
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.query_entry, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if (id == R.id.menu_query) {
			queryMedicationItems();
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void doExtraJobsForLowerVersions() {
		com.android_assistant.TextView.setDefaultTextShadow(
			(TextView) findViewById(R.id.txv_name));
		
		com.android_assistant.TextView.setDefaultTextShadow(
			(TextView) findViewById(R.id.txv_category));
	}
	
	private void fillCategorySpinner() {
		Spinner spnCategory = (Spinner) findViewById(R.id.spn_category);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
			R.drawable.default_spinner_text, mDbHelper.queryMedicineCategories(getString(R.string.not_limited)));
		
		spnCategory.setAdapter(spinnerAdapter);
		//((TextView)spnCategory).setLineSpacing(0, 1); // compile error
		// run-time error below
		/*for (int i = 0; i < spnCategory.getCount(); ++i) {
			TextView item = (TextView) spnCategory.getItemAtPosition(i);
			
			com.android_assistant.TextView.setDefaultTextShadow(item);
			item.setLineSpacing(0, 1.5F);
		}*/
	}
	
	private void queryMedicationItems() {
		ArrayList<MedicationBrief> itemList = new ArrayList<MedicationBrief>();
		EditText etxName = (EditText) findViewById(R.id.etx_name);
		String medicineName = etxName.getText().toString();
		Spinner spnCategory = (Spinner) findViewById(R.id.spn_category);
		int selectedCategoryPos = spnCategory.getSelectedItemPosition();
		int sqlResId = (selectedCategoryPos > 0)
			? ((medicineName.length() > 0)
				? R.string.sql_query_medicine_items_by_name_and_category
				: R.string.sql_query_medicine_items_by_category)
			: ((medicineName.length() > 0)
				? R.string.sql_query_medicine_items_by_name
				: R.string.sql_query_all_medicine_items);
		String sql = getString(sqlResId);
		String[] sqlArgs = (selectedCategoryPos > 0)
			? ((medicineName.length() > 0)
				? (new String[]{ "%" + medicineName + "%", String.valueOf(selectedCategoryPos)})
				: (new String[]{ String.valueOf(selectedCategoryPos) }) )
			: ((medicineName.length() > 0)
				? (new String[]{ "%" + medicineName + "%" })
				: null);
		Cursor c = mDbHelper.getDatabase().rawQuery(sql, sqlArgs);
		
		//Hint.longToast(this, sql);
		while (c.moveToNext()) {
			itemList.add(new MedicationBrief(String.valueOf(c.getInt(c.getColumnIndex("mid"))),
				c.getString(c.getColumnIndex("name"))));
		}
		c.close();
		
		int resultCount = itemList.size();
		
		if (resultCount <= 0)
			Hint.alert(this, getString(R.string.medicine) + " " + getString(R.string.not_found),
				getString(R.string.hint_add_when_not_found));
		
		Hint.shortToast(this, ResourceExports.getString(this, R.array.query_result)
			+ ResourceExports.getString(this, R.array.quantity)
			+ ": " + resultCount);
		
		ListView lsvQueryResult = (ListView) findViewById(R.id.lsv_query_items);
		MedicationBriefAdapter adapter = new MedicationBriefAdapter(this, itemList);

		lsvQueryResult.setAdapter(adapter);
		lsvQueryResult.setOnItemClickListener(this);
	}
	
	private class MedicationBrief {
		public String id;
		public String name;

		public MedicationBrief() {}

		public MedicationBrief(String id, String name) {
			this.id = id;
			this.name = name;
		}
	}
	
	private class MedicationBriefAdapter extends BaseAdapter {

		private final Context context;
		private final List<MedicationBrief> itemList;
		private final LayoutInflater inflater;

		public MedicationBriefAdapter(Context context, List<MedicationBrief> itemList) {
			super();
			this.itemList = itemList;
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return itemList.size();
		}

		@Override
		public Object getItem(int position) {
			return itemList.get(position);
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
				convertView = inflater.inflate(R.layout.brief_item, null);
				holder.checkBox = (CheckBox) convertView.findViewById(R.id.chkbox_medication_brief);
				holder.id = (TextView) convertView.findViewById(R.id.txv_medication_brief_id);
				holder.name = (TextView) convertView.findViewById(R.id.txv_medication_brief_name);
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}

			MedicationBrief item = itemList.get(position);
			
			holder.checkBox.setVisibility(TextView.GONE);

			holder.id.setText(item.id);
			com.android_assistant.TextView.setDefaultTextShadow(holder.id);
			holder.id.setLineSpacing(0, 1.5F);
			holder.id.setVisibility(TextView.GONE);
			
			holder.name.setText(item.name);
			com.android_assistant.TextView.setDefaultTextShadow(holder.name);
			holder.name.setLineSpacing(0, 1.5F);

			return convertView;
		}

		private class ViewHolder {
			CheckBox checkBox;
			TextView id;
			TextView name;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		ListView listView = (ListView) parent;
		MedicationBrief item = (MedicationBrief) listView.getItemAtPosition(pos);
		final Intent intent = new Intent(this, ReadWriteItemDetailsActivity.class);
		
		intent.putExtra("op_type", OP_TYPE_MEDICINE);
		intent.putExtra("id", item.id);
		intent.putExtra("name", item.name);
		startActivity(intent);
	}
}
