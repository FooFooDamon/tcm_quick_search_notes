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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android_assistant.App;
import com.android_assistant.Hint;

public class DbHelper {
	private Context mContext = null;
	private String mDbDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
	
	public DbHelper(Context context) {
		if (null == context)
			throw new NullPointerException();
		
		mContext = context;
	}
	
	public DbHelper(Context context, String dbDirectory) {
		if (null == context)
			throw new NullPointerException();
		
		mContext = context;
		
		File dir = new File(dbDirectory);
		
		if (!dir.exists())
			dir.mkdirs();
		
		mDbDir = dbDirectory;
	}
	
	public SQLiteDatabase openOrCreate() {
		return com.android_assistant.DbHelper.openOrCreate(getDatabaseDirectory(),
				getDatabaseName());
	}
	
	public void close(SQLiteDatabase db) {
		com.android_assistant.DbHelper.close(db);
	}
	
	public String getDatabaseName() {
		return App.getAppName(mContext) + ".db";
	}
	
	public String getDatabaseDirectory() {
		// TODO: Get it with SharedPreferences!
		return mDbDir;
	}
	
	public void setDatabaseDirectory(String dir) {
		if (null == dir)
			return;
		
		// TODO: Set it with SharedPreferences!
		mDbDir = dir;
	}
	
	public void precheck() {
		String dbPath = getDatabaseDirectory() + "/" + getDatabaseName();
		File dbFile = new File(dbPath);
		boolean dbExits = dbFile.exists();
		
		if (dbExits)
			return;
		
		Hint.shortToast(mContext, R.string.hint_db_creating);
			
		prepareTestData();
		prepareMedicineData();
		
		Hint.longToast(mContext, mContext.getResources().getString(R.string.hint_db_created)
			+ "\n" + dbPath);
	}
	
	public void prepareTestData() {
		createTable(R.string.sql_create_test_table);
		makePresetData(R.string.sql_make_test_data, R.array.sql_args_make_test_data, 2, true);
	}
	
	public void prepareMedicineData() {
		createTable(R.string.sql_create_medicine_categories_table);
		makePresetData(R.string.sql_make_medicine_categories_data,
			R.array.medicine_categories, 1, true);
		
		createTable(R.string.sql_create_medicine_items_table);
		makePresetData(R.string.sql_make_medicine_items_data,
			R.array.sql_args_make_frequently_used_medicines, 2, true);
		
		createTable(R.string.sql_create_prescription_categories_table);
		makePresetData(R.string.sql_make_prescription_categories_data,
			R.array.prescription_categories, 2, true);
		
		createTable(R.string.sql_create_prescription_items_table);
		makePresetData(R.string.sql_make_prescription_items_data,
			R.array.sql_args_make_frequently_used_prescriptions, 2, true);
		
		createTable(R.string.sql_create_reference_material_table);
		makePresetData(R.string.sql_add_reference_material,
			R.array.sql_args_add_reference_material, 7, true);
		
		Context ctx = mContext;
		int attrResIds[][] = {
			{ R.string.attr_table_prefix_level_word, R.array.level_words },
			{ R.string.attr_table_prefix_medicine_nature, R.array.medicine_natures },
			{ R.string.attr_table_prefix_medicine_taste, R.array.medicine_tastes },
			{ R.string.attr_table_prefix_channel_tropism, R.array.channel_tropism },
			{ R.string.attr_table_prefix_zang_fu_viscera, R.array.zang_fu_viscera },
			{ R.string.attr_table_prefix_life_fundamental, R.array.life_fundamentals },
			{ R.string.attr_table_prefix_motion_form, R.array.motion_forms },
			{ R.string.attr_table_prefix_processing_method, R.array.processing_methods },
			{ R.string.attr_table_prefix_medicine_unit, R.array.medicine_units },
			{ R.string.attr_table_prefix_medicine_role, R.array.medicine_roles },
			{ R.string.attr_table_prefix_dosage_form, R.array.dosage_forms },
			{ R.string.attr_table_prefix_method_of_taking_medicine, R.array.methods_of_taking_medicine },
			{ R.string.attr_table_prefix_nature_of_symptom, R.array.nature_of_symptom },
			{ R.string.attr_table_prefix_medicine_effect, R.array.medicine_effects },
			{ R.string.attr_table_prefix_medicine_action_and_indication, R.array.medicine_actions_and_indications },
			{ R.string.attr_table_prefix_action_verb, R.array.action_verbs }
		};
		
		for (int i = 0; i < attrResIds.length; ++i) {
			createAttributeTable(ctx.getString(attrResIds[i][0]));
			makeAttributeData(ctx.getString(attrResIds[i][0]), attrResIds[i][1]);
		}
	}
	
	public String[] queryMedicineCategories(String firstItem) {
		String sql = mContext.getString(R.string.sql_query_medicine_category_names);
		SQLiteDatabase db = openOrCreate();
		Cursor c = db.rawQuery(sql, null);
		ArrayList<String> results = new ArrayList<String>();
		
		if (null != firstItem)
			results.add(firstItem);
		while (c.moveToNext()) {
			results.add(c.getString(c.getColumnIndex("name")));
		}
		c.close();
		db.close();
		
		if (0 == results.size())
			return null;
		
		return results.toArray(new String[results.size()]);
	}
	
	public HashMap<String, String> queryMedicineDetails(String mid) {
		SQLiteDatabase db = openOrCreate();
		Cursor c = db.rawQuery(mContext.getString(R.string.sql_query_medicine_details_by_id),
			new String[] { mid });
		HashMap<String, String> results = new HashMap<String, String>();
		
		if (!c.moveToNext()) {
			c.close();
			db.close();
			
			return null;
		}
		
		final String[] columnNames = {
			"name", "alias",
			"category", "nature",
			"tastes", "channel_tropism",
			"relations_with_life_fundamentals", "motion_form_of_action",
			"effects", "actions_and_indications",
			"details", "common_prescriptions",
			"common_partners", "similar_medicines",
			"dosage_reference", "contraindications",
			"reference_material", "remarks"
		};
		
		for (int i = 0; i < columnNames.length; ++i) {
			String key = columnNames[i];
			int columnIndex = c.getColumnIndex(key);
			String value = "category".equals(key)
				? String.valueOf(c.getInt(columnIndex))
				: c.getString(columnIndex);
			
			results.put(key, value);
		}
		
		c.close();
		db.close();
		
		return results;
	}
	
	public String[] queryAttributeNames(int attrPrefixResId, String firstItem) {
		/*String firstItem = null;
		
		switch (attrPrefixResId) {
		case R.string.attr_table_prefix_medicine_nature:
		case R.string.attr_table_prefix_medicine_taste:
		case R.string.attr_table_prefix_channel_tropism:
		case R.string.attr_table_prefix_zang_fu_viscera:
		case R.string.attr_table_prefix_life_fundamental:
		case R.string.attr_table_prefix_motion_form:
		case R.string.attr_table_prefix_medicine_role:
			firstItem = mContext.getString(R.string.please_select);
			break;
			
		case R.string.attr_table_prefix_action_verb:
		case R.string.attr_table_prefix_level_word:
		case R.string.attr_table_prefix_medicine_effect:
		case R.string.attr_table_prefix_medicine_action_and_indication:
		case R.string.attr_table_prefix_dosage_form:
		case R.string.attr_table_prefix_medicine_unit:
		case R.string.attr_table_prefix_method_of_taking_medicine:
		case R.string.attr_table_prefix_nature_of_symptom:
		case R.string.attr_table_prefix_processing_method:
			firstItem = mContext.getString(R.string.customized);
			break;
			
		default:
			firstItem = mContext.getString(R.string.unknown);
			break;
		}*/
		
		return queryAllNames(mContext.getString(attrPrefixResId) + "_definitions",
			"aid", firstItem);
	}
	
	public String[] queryReferenceMaterialNames() {
		return queryAllNames(mContext.getString(R.string.reference_material_table),
			"rid", mContext.getString(R.string.customized));
	}
	
	public void tmpUpdateMedicineItem(String[] bindArgs) {
		SQLiteDatabase db = openOrCreate();
		String sql = mContext.getString(R.string.tmp_sql_update_medicine_item_by_id);
		
		db.execSQL(sql, bindArgs);
		
		db.close();
	}
	
	// NOTE: This method should be used to tables with a small quantity of data!
	private String[] queryAllNames(String table, String primaryId, String firstItem) {
		String sql = "select name from `"
			+ table + "`"
			+ " order by " + primaryId + " asc";
		SQLiteDatabase db = openOrCreate();
		Cursor c = db.rawQuery(sql, null);
		ArrayList<String> results = new ArrayList<String>();
		
		if (null != firstItem)
			results.add(firstItem);
		while (c.moveToNext()) {
			results.add(c.getString(c.getColumnIndex("name")));
		}
		c.close();
		db.close();
		
		if (0 == results.size())
			return null;
		
		return results.toArray(new String[results.size()]);
	}
	
	private void createAttributeTable(String attributeName) {
		String sql = "CREATE TABLE `" + attributeName + "_definitions` ("
			+ "`aid`		INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,"
			+ "`name`		TEXT NOT NULL UNIQUE,"
			+ "`remarks`	TEXT)";
		
		createTable(sql);
	}
	
	private void makeAttributeData(String attributeName, int valuesArrayResId) {
		String sql = "insert into `" + attributeName + "_definitions`(name)"
			+ " values(?)";
		
		makePresetData(sql, valuesArrayResId, 1, true);
	}
	
	private void createTable(String createSql) {
		SQLiteDatabase db = openOrCreate();
		
		db.execSQL(createSql);
		
		db.close();
	}
	
	private void createTable(int createSqlResId) {
		createTable(mContext.getResources().getString(createSqlResId));
	}
	
	private void makePresetData(String sqlString, int valuesArrayResId,
		int bindArgsCount, boolean enablesTransaction) {
		String dbError = mContext.getResources().getString(R.string.db_error);
		
		if (valuesArrayResId > 0) {
			if (bindArgsCount <= 0) {
				Hint.alert(mContext, dbError,
					sqlString + "\n\nbindArgsCount = " + String.valueOf(bindArgsCount));
				return;
			}
			
			String[] bindArgValues = mContext.getResources().getStringArray(valuesArrayResId);
			int actualArgCount = bindArgValues.length;
			
			if (0 != actualArgCount % bindArgsCount) {
				Hint.alert(mContext, dbError,
					sqlString + "\n\nactualArgCount(" + String.valueOf(actualArgCount) + ")"
					+ " % bindArgsCount(" + String.valueOf(bindArgsCount) + ") != 0");
				return;
			}
			
			SQLiteDatabase db = openOrCreate();
			String args[] = new String[bindArgsCount];
			
			if (!enablesTransaction) {
				for (int i = 0; i < actualArgCount; i += bindArgsCount) {
					for (int j = 0; j < bindArgsCount; ++j) {
						args[j] = bindArgValues[i + j];
					}
					
					db.execSQL(sqlString, args);
				}
				
				return;
			}
			
			db.beginTransaction();
			try {
				for (int i = 0; i < actualArgCount; i += bindArgsCount) {
					for (int j = 0; j < bindArgsCount; ++j) {
						args[j] = bindArgValues[i + j];
					}
					
					db.execSQL(sqlString, args);
				}
				
				db.setTransactionSuccessful();
			} catch (Exception e) {
				throw e;
			} finally {
				db.endTransaction();
				db.close();
			}
			
			return;
		}

		SQLiteDatabase db = openOrCreate();
		
		db.execSQL(sqlString);
		
		db.close();
	}
	
	private void makePresetData(int sqlStringResId, int valuesArrayResId,
		int bindArgsCount, boolean enablesTransaction) {
		String sql = mContext.getResources().getString(sqlStringResId);
		
		makePresetData(sql, valuesArrayResId, bindArgsCount, enablesTransaction);
	}
	
	public void showTestData() {
		SQLiteDatabase db = openOrCreate();
		Cursor c = db.rawQuery(mContext.getResources().getString(R.string.sql_query_test_data),
			mContext.getResources().getStringArray(R.array.sql_args_query_test_data));
		
		while (c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex("test_id"));
			String name = c.getString(c.getColumnIndex("test_name"));
			String remarks = c.getString(c.getColumnIndex("test_remarks"));
			
			Hint.shortToast(mContext, String.valueOf(id) + " | " + name + " | " + remarks);
		}
		c.close();
		db.close();
	}
}
