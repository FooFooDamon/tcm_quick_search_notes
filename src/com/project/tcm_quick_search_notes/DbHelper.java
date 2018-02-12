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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android_assistant.App;
import com.android_assistant.Hint;

public class DbHelper {
    public static final int MAX_COLUMN_COUNT = 18;

    public static final String[] MEDICINE_COLUMNS = {
        "name",
        "alias",
        "category",
        "nature",
        "tastes",
        "channel_tropism",
        "relations_with_life_fundamentals",
        "motion_form_of_action",
        "effects",
        "actions_and_indications",
        "details",
        "common_prescriptions",
        "common_partners",
        "similar_medicines",
        "dosage_reference",
        "contraindications",
        "reference_material",
        "remarks"
    };

    public static final int MEDICINE_COLUMN_INDEX_NAME = 0;
    public static final int MEDICINE_COLUMN_INDEX_ALIASES = 1;
    public static final int MEDICINE_COLUMN_INDEX_CATEGORY = 2;
    public static final int MEDICINE_COLUMN_INDEX_NATURE = 3;
    public static final int MEDICINE_COLUMN_INDEX_TASTES = 4;
    public static final int MEDICINE_COLUMN_INDEX_CHANNEL_TROPISM = 5;
    public static final int MEDICINE_COLUMN_INDEX_LIFE_FUNDAMENTALS = 6;
    public static final int MEDICINE_COLUMN_INDEX_MOTION_FORMS_OF_ACTION = 7;
    public static final int MEDICINE_COLUMN_INDEX_EFFECTS = 8;
    public static final int MEDICINE_COLUMN_INDEX_ACTIONS_AND_INDICATIONS = 9;
    public static final int MEDICINE_COLUMN_INDEX_DETAILS = 10;
    public static final int MEDICINE_COLUMN_INDEX_COMMON_PRESCRIPTIONS = 11;
    public static final int MEDICINE_COLUMN_INDEX_COMMON_PARTNERS = 12;
    public static final int MEDICINE_COLUMN_INDEX_SIMILAR_MEDICINES = 13;
    public static final int MEDICINE_COLUMN_INDEX_DOSAGE_REFERENCE = 14;
    public static final int MEDICINE_COLUMN_INDEX_CONTRAINDICATIONS = 15;
    public static final int MEDICINE_COLUMN_INDEX_REFERENCE_MATERIAL = 16;
    public static final int MEDICINE_COLUMN_INDEX_REMARKS = 17;

    public static final String[] PRESCRIPTION_COLUMNS = {
        "name",
        "alias",
        "category",
        "effects",
        "actions_and_indications",
        "composition",
        "decoction",
        "method_of_taking_medicine",
        "contraindications",
        "relative_prescriptions",
        "reference_material",
        "remarks"
    };

    public static final int PRESCRIPTION_COLUMN_INDEX_NAME = 0;
    public static final int PRESCRIPTION_COLUMN_INDEX_ALIASES = 1;
    public static final int PRESCRIPTION_COLUMN_INDEX_CATEGORY = 2;
    public static final int PRESCRIPTION_COLUMN_INDEX_EFFECTS = 3;
    public static final int PRESCRIPTION_COLUMN_INDEX_ACTIONS_AND_INDICATIONS = 4;
    public static final int PRESCRIPTION_COLUMN_INDEX_COMPOSITION = 5;
    public static final int PRESCRIPTION_COLUMN_INDEX_DECOCTION = 6;
    public static final int PRESCRIPTION_COLUMN_INDEX_METHOD_OF_TAKING_MEDICINE = 7;
    public static final int PRESCRIPTION_COLUMN_INDEX_CONTRAINDICATIONS = 8;
    public static final int PRESCRIPTION_COLUMN_INDEX_RELATIVE_PRESCRIPTIONS = 9;
    public static final int PRESCRIPTION_COLUMN_INDEX_REFERENCE_MATERIAL = 10;
    public static final int PRESCRIPTION_COLUMN_INDEX_REMARKS = 11;

    public static final String[] REFERENCE_MATERIAL_COLUMNS = {
        "name",
        "version",
        "original_authors",
        "editors",
        "issuing_source",
        "issuing_date",
        "remarks"
    };

    public static final int REFERENCE_MATERIAL_COLUMN_INDEX_NAME = 0;
    public static final int REFERENCE_MATERIAL_COLUMN_INDEX_VERSION = 1;
    public static final int REFERENCE_MATERIAL_COLUMN_INDEX_ORIGINAL_AUTHORS = 2;
    public static final int REFERENCE_MATERIAL_COLUMN_INDEX_EDITORS = 3;
    public static final int REFERENCE_MATERIAL_COLUMN_INDEX_ISSUING_SOURCE = 4;
    public static final int REFERENCE_MATERIAL_COLUMN_INDEX_ISSUING_DATE = 5;
    public static final int REFERENCE_MATERIAL_COLUMN_INDEX_REMARKS = 6;

    public static final String[] GENERAL_MISC_ITEM_COLUMNS = {
        "name",
        "sub_categories",
        "remarks"
    };

    public static final int GENERAL_MISC_ITEM_COLUMN_INDEX_NAME = 0;
    public static final int GENERAL_MISC_ITEM_COLUMN_INDEX_SUB_CATEGORIES = 1;
    public static final int GENERAL_MISC_ITEM_COLUMN_INDEX_REMARKS = 2;

    private Context mContext = null;
    private String mDbDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private SQLiteDatabase mDbInstance = null;

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

    public static String[] getTableColumnsList(int opType, int positionAtFunctionalityList) {
        if (TcmCommon.OP_TYPE_VALUE_MEDICINE == opType)
            return MEDICINE_COLUMNS;
        else if (TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == opType)
            return PRESCRIPTION_COLUMNS;
        else {
            if (MiscManagementActivity.LIST_ITEM_POS_REFERENCE_MATERIAL == positionAtFunctionalityList)
                return REFERENCE_MATERIAL_COLUMNS;
            else
                return GENERAL_MISC_ITEM_COLUMNS;
        }
    }

    public void openOrCreate() {
        if (null == mDbInstance) {
            mDbInstance = com.android_assistant.DbHelper.openOrCreate(getDatabaseDirectory(),
                    getDatabaseName());
        }
    }

    // TODO: finalize() { close(); }

    public void close() {
        com.android_assistant.DbHelper.close(mDbInstance);
    }

    public SQLiteDatabase getDatabase() {
        if (null == mDbInstance)
            openOrCreate();

        return mDbInstance;
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

    public void initData() throws Exception {
        String dbPath = getDatabaseDirectory() + "/" + getDatabaseName();
        File dbFile = new File(dbPath);
        boolean dbExits = dbFile.exists();

        if (dbExits)
            return;

        Hint.shortToast(mContext, R.string.hint_db_creating);

        prepareMedicationData();

        Hint.longToast(mContext, mContext.getResources().getString(R.string.hint_db_created)
            + "\n" + dbPath);
    }

    public void prepareMedicationData() throws Exception {
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
        String sql = mContext.getString(R.string.sql_query_all_medicine_category_names);
        Cursor c = getDatabase().rawQuery(sql, null);
        ArrayList<String> results = new ArrayList<String>();

        if (null != firstItem)
            results.add(firstItem);
        while (c.moveToNext()) {
            results.add(c.getString(c.getColumnIndex("name")));
        }
        c.close();

        if (0 == results.size())
            return null;

        return results.toArray(new String[results.size()]);
    }

    public String[] queryMedicineIdNamePairsByName(String name) {
        String sql = mContext.getString(R.string.sql_query_medicine_items_by_name);
        String[] args = { "%" + name + "%" };
        Cursor c = getDatabase().rawQuery(sql, args);
        ArrayList<String> results = new ArrayList<String>();

        //showSqlInfo("queryMedicineIdNamePairsByName()", sql, args);
        while (c.moveToNext()) {
            results.add(c.getString(c.getColumnIndex("mid")) + ":" + c.getString(c.getColumnIndex("name")));
        }
        c.close();

        if (0 == results.size())
            return null;

        return results.toArray(new String[results.size()]);
    }

    public String[] queryPrescriptionCategories(String firstItem) {
        String sql = mContext.getString(R.string.sql_query_all_prescription_category_names);
        Cursor c = getDatabase().rawQuery(sql, null);
        ArrayList<String> results = new ArrayList<String>();

        if (null != firstItem)
            results.add(firstItem);
        while (c.moveToNext()) {
            results.add(c.getString(c.getColumnIndex("name")));
        }
        c.close();

        if (0 == results.size())
            return null;

        return results.toArray(new String[results.size()]);
    }

    public HashMap<String, String> queryItemDetails(String id, int opType, int positionAtFunctionalityList) {
        String[] sqlArgs = new String[] { id };
        String sql = null;
        final String[] COLUMN_NAMES = getTableColumnsList(opType, positionAtFunctionalityList);

        if (TcmCommon.OP_TYPE_VALUE_MEDICINE == opType) {
            sql = mContext.getString(R.string.sql_query_medicine_details_by_id);
        }
        else if (TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == opType) {
            sql = mContext.getString(R.string.sql_query_prescription_details_by_id);
        }
        else {
            if (MiscManagementActivity.LIST_ITEM_POS_REFERENCE_MATERIAL == positionAtFunctionalityList) {
                sql = mContext.getString(R.string.sql_query_reference_material_details_by_id);
            }
            else if (MiscManagementActivity.LIST_ITEM_POS_PRESCRIPTION_CATEGORY == positionAtFunctionalityList) {
                sql = mContext.getString(R.string.sql_query_prescription_category_details_by_id);
            }
            else {
                String tableName = MiscManagementActivity.getTableNameByPosition(positionAtFunctionalityList);
                String primaryIdName = MiscManagementActivity.getDbPrimaryIdNameByPosition(positionAtFunctionalityList);

                sql = "select name, remarks from " + tableName + " where " + primaryIdName + " = ?";
            }
        }

        Cursor c = getDatabase().rawQuery(sql, sqlArgs);

        if (!c.moveToNext()) {
            c.close();

            return null;
        }

        HashMap<String, String> results = new HashMap<String, String>();

        for (int i = 0; i < COLUMN_NAMES.length; ++i) {
            String key = COLUMN_NAMES[i];

            if (TcmCommon.OP_TYPE_VALUE_MISC_MANAGEMENT == opType
                && MiscManagementActivity.LIST_ITEM_POS_PRESCRIPTION_CATEGORY != positionAtFunctionalityList
                && "sub_categories".equals(key))
                continue;

            int columnIndex = c.getColumnIndex(key);
            String value = "category".equals(key)
                ? String.valueOf(c.getInt(columnIndex))
                : c.getString(columnIndex);

            results.put(key, value);
        }

        c.close();

        return results;
    }

    public String[][] queryBriefOfNearItems(int opType, int positionAtFunctionalityList,
        boolean isBefore, int expectedCount, String id, int categoryIfNeeded) {

        String[] sqlArgs = null ;
        String sql = null;

        if (expectedCount < 1)
            expectedCount = 1;

        if (TcmCommon.OP_TYPE_VALUE_MEDICINE == opType) {
            sql = mContext.getString(isBefore
                ? R.string.sql_query_forward_near_medicine_items
                : R.string.sql_query_afterward_near_medicine_items);
            sqlArgs = new String[] { id, String.valueOf(categoryIfNeeded), String.valueOf(expectedCount) };
        }
        else if (TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == opType) {
            sql = mContext.getString(isBefore
                ? R.string.sql_query_forward_near_prescription_items
                : R.string.sql_query_afterward_near_prescription_items);
            sqlArgs = new String[] { id, String.valueOf(categoryIfNeeded), String.valueOf(expectedCount) };
        }
        else {
            if (MiscManagementActivity.LIST_ITEM_POS_REFERENCE_MATERIAL == positionAtFunctionalityList) {
                sql = mContext.getString(isBefore
                    ? R.string.sql_query_forward_near_reference_material_items
                    : R.string.sql_query_afterward_near_reference_material_items);
            }
            else {
                String tableName = MiscManagementActivity.getTableNameByPosition(positionAtFunctionalityList);
                String primaryIdName = MiscManagementActivity.getDbPrimaryIdNameByPosition(positionAtFunctionalityList);

                if (isBefore) {
                    sql = "select " + primaryIdName + " as iid, name from " + tableName
                        + " where " + primaryIdName + " < ?"
                        + " order by iid desc limit ?";
                } else {
                    sql = "select " + primaryIdName + " as iid, name from " + tableName
                        + " where " + primaryIdName + " > ?"
                        + " order by iid asc limit ?";
                }
            }
            sqlArgs = new String[] { id, String.valueOf(expectedCount) };
        }

        Cursor c = getDatabase().rawQuery(sql, sqlArgs);

        if (!c.moveToNext()) {
            c.close();

            return null;
        }

        ArrayList<String[]> results = new ArrayList<String[]>();

        do {
            String[] brief = new String[2];

            brief[0] = c.getString(c.getColumnIndex("iid"));
            brief[1] = c.getString(c.getColumnIndex("name"));
            results.add(brief);
        } while (c.moveToNext());
        c.close();

        return results.toArray(new String[results.size()][2]);
    }

    public String[] queryAttributeNames(int attrPrefixResId, String firstItem) {
        return queryAllNames(mContext.getString(attrPrefixResId) + "_definitions",
            "aid", firstItem);
    }

    public String[] queryReferenceMaterialNames() {
        return queryAllNames(mContext.getString(R.string.reference_material_table),
            "rid", mContext.getString(R.string.customized));
    }

    public void updateMedicineItem(String[] bindArgs) {
        SQLiteDatabase db = getDatabase();
        String sql = mContext.getString(R.string.sql_update_medicine_item_by_id);

        //showSqlInfo("updateMedicineItem()", sql, bindArgs);
        db.execSQL(sql, bindArgs);
    }

    public void updatePrescriptionItem(String[] bindArgs) {
        SQLiteDatabase db = getDatabase();
        String sql = mContext.getString(R.string.sql_update_prescription_item_by_id);

        //showSqlInfo("updatePrescriptionItem()", sql, bindArgs);
        db.execSQL(sql, bindArgs);
    }

    public void updateReferenceMaterialItem(String[] bindArgs) {
        SQLiteDatabase db = getDatabase();
        String sql = mContext.getString(R.string.sql_update_reference_material_item_by_id);

        db.execSQL(sql, bindArgs);
    }

    public void updateMiscItem(int positionAtMiscList, String[] bindArgs) {
        if (MiscManagementActivity.LIST_ITEM_POS_REFERENCE_MATERIAL == positionAtMiscList)
            return;

        String tableName = MiscManagementActivity.getTableNameByPosition(positionAtMiscList);
        String primaryIdName = MiscManagementActivity.getDbPrimaryIdNameByPosition(positionAtMiscList);
        SQLiteDatabase db = getDatabase();
        String sql = null;

        if (MiscManagementActivity.LIST_ITEM_POS_PRESCRIPTION_CATEGORY == positionAtMiscList) {
            sql = "update " + tableName
                + " set name = ?, sub_categories = ?,"
                + "     remarks = ?"
                + " where " + primaryIdName + " = ?";
        }
        else {
            sql = "update " + tableName
                + " set name = ?,"
                + "     remarks = ?"
                + " where " + primaryIdName + " = ?";
        }

        db.execSQL(sql, bindArgs);
    }

    public void upgradeV10111() throws Exception {
        makeAttributeData(mContext.getString(R.string.attr_table_prefix_medicine_nature), R.array.medicine_natures_v10111);
        makeAttributeData(mContext.getString(R.string.attr_table_prefix_life_fundamental), R.array.life_fundamentals_v10111);
    }

    // NOTE: This method should be used to tables with a small quantity of data!
    private String[] queryAllNames(String table, String primaryIdName, String firstItem) {
        String sql = "select name from `"
            + table + "`"
            + " order by " + primaryIdName + " asc";
        Cursor c = getDatabase().rawQuery(sql, null);
        ArrayList<String> results = new ArrayList<String>();

        if (null != firstItem)
            results.add(firstItem);
        while (c.moveToNext()) {
            results.add(c.getString(c.getColumnIndex("name")));
        }
        c.close();

        if (0 == results.size())
            return null;

        return results.toArray(new String[results.size()]);
    }

    private void createAttributeTable(String attributeName) {
        String sql = "CREATE TABLE `" + attributeName + "_definitions` ("
            + "`aid`        INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,"
            + "`name`       TEXT NOT NULL UNIQUE,"
            + "`remarks`    TEXT)";

        createTable(sql);
    }

    private void makeAttributeData(String attributeName, int valuesArrayResId) throws Exception {
        String sql = "insert into `" + attributeName + "_definitions`(name)"
            + " values(?)";

        makePresetData(sql, valuesArrayResId, 1, true);
    }

    private void createTable(String createSql) {
        getDatabase().execSQL(createSql);
    }

    private void createTable(int createSqlResId) {
        createTable(mContext.getResources().getString(createSqlResId));
    }

    private void makePresetData(String sqlString, int valuesArrayResId,
        int bindArgsCount, boolean enablesTransaction) throws Exception {
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

            SQLiteDatabase db = getDatabase();
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
            }

            return;
        }

        getDatabase().execSQL(sqlString);
    }

    private void makePresetData(int sqlStringResId, int valuesArrayResId,
        int bindArgsCount, boolean enablesTransaction) throws Exception {
        String sql = mContext.getResources().getString(sqlStringResId);

        makePresetData(sql, valuesArrayResId, bindArgsCount, enablesTransaction);
    }

    private void showSqlInfo(String targetFunction, String sql, String[] bindArgs) {
        StringBuilder args = new StringBuilder();

        args.append("sql: ").append(sql).append("\nargsCount: ").append(bindArgs.length)
            .append("\nbindArgs: ");

        for (int i = 0; i < bindArgs.length; ++i) {
            if (0 == i)
                args.append("[").append(i).append("]: ").append(bindArgs[i]);
            else
                args.append(" | [").append(i).append("]: ").append(bindArgs[i]);
        }

        Hint.alert(mContext, targetFunction, args.toString());
    }
}
