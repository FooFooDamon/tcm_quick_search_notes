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
import android.content.DialogInterface;
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

    private DbHelper mDbHelper = null;

    private Intent mNextIntent = null;

    private View.OnClickListener mAddItemDialog = null;
    private DialogInterface.OnClickListener mAddItemAction = null;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_entry);
        getActionBar().setBackgroundDrawable(
            getResources().getDrawable(R.drawable.default_action_bar_style));

        initResources();

        if (Version.SDK <= Version.getDeprecatedVersionUpperBound())
            doExtraJobsForLowerVersions();

        Intent prevIntent = getIntent();
        int opType = prevIntent.getIntExtra(TcmCommon.OP_TYPE_KEY, TcmCommon.OP_TYPE_VALUE_MEDICINE);

        if (TcmCommon.OP_TYPE_VALUE_MEDICINE == opType
            || TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == opType) {
            TextView txvCategory = (TextView)findViewById(R.id.txv_category);
            Spinner spnCategory = (Spinner) findViewById(R.id.spn_category);
            Button btnCategory = (Button)findViewById(R.id.btn_add_category);

            txvCategory.setVisibility(TextView.VISIBLE);
            spnCategory.setVisibility(TextView.VISIBLE);
            btnCategory.setVisibility(TextView.INVISIBLE);
            fillCategorySpinner(opType);

            if (TcmCommon.OP_TYPE_VALUE_MEDICINE == opType)
                setTitle(getString(R.string.main_item_medicine));
            else
                setTitle(getString(R.string.main_item_prescription));
        }
        else {
            int miscItemPos = prevIntent.getIntExtra(TcmCommon.MISC_ITEM_POS_KEY, MiscManagementActivity.LIST_ITEM_POS_MEDICINE_CATEGORY);

            setTitle(MiscManagementActivity.getItemNameByPosition(miscItemPos));
        }

        Button btnAddItem = (Button) findViewById(R.id.btn_add_item);

        btnAddItem.setOnClickListener(mAddItemDialog);
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
            queryItems();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        Intent prevIntent = getIntent();
        int opType = prevIntent.getIntExtra(TcmCommon.OP_TYPE_KEY, TcmCommon.OP_TYPE_VALUE_MEDICINE);
        int miscItemPos = prevIntent.getIntExtra(TcmCommon.MISC_ITEM_POS_KEY, MiscManagementActivity.LIST_ITEM_POS_MEDICINE_CATEGORY);
        ListView listView = (ListView) parent;
        ItemBrief item = (ItemBrief) listView.getItemAtPosition(pos);

        mNextIntent.putExtra(TcmCommon.OP_TYPE_KEY, opType);
        mNextIntent.putExtra(TcmCommon.ID_KEY, item.id);
        mNextIntent.putExtra(TcmCommon.NAME_KEY, item.name);
        mNextIntent.putExtra(TcmCommon.MISC_ITEM_POS_KEY, miscItemPos);
        startActivity(mNextIntent);
    }

    private void initResources() {
        if (null == mNextIntent)
            mNextIntent = new Intent(this, ReadWriteItemDetailsActivity.class);

        if (null == mDbHelper) {
            mDbHelper = new DbHelper(this);
            mDbHelper.openOrCreate();
        }

        if (null == mAddItemAction) {
            mAddItemAction = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent prevIntent = getIntent();
                    int opType = prevIntent.getIntExtra(TcmCommon.OP_TYPE_KEY, TcmCommon.OP_TYPE_VALUE_MEDICINE);
                    String sqlCheckItem = null;
                    String sqlAddItem = null;
                    String newItem = null;
                    String hintReusingItemTitle = null;
                    String hintReusingItemContents = null;
                    String hintAddingItemSuccessfully = null;
                    String hintAfterAddingItem = null;

                    if (TcmCommon.OP_TYPE_VALUE_MEDICINE == opType) {
                        sqlCheckItem = getString(R.string.sql_query_medicine_items_by_name);
                        sqlAddItem = getString(R.string.sql_make_medicine_items_data);
                        newItem = getString(R.string.new_medicine);
                        hintReusingItemTitle = getString(R.string.alert_reusing_medicine_title);
                        hintReusingItemContents = getString(R.string.alert_reusing_medicine_contents);
                        hintAddingItemSuccessfully = getString(R.string.hint_adding_medicine_successfully);
                        hintAfterAddingItem = getString(R.string.hint_after_adding_medicine);
                    }
                    else if (TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == opType) {
                        sqlCheckItem = getString(R.string.sql_query_prescription_items_by_name);
                        sqlAddItem = getString(R.string.sql_make_prescription_items_data);
                        newItem = getString(R.string.new_prescription);
                        hintReusingItemTitle = getString(R.string.alert_reusing_prescription_title);
                        hintReusingItemContents = getString(R.string.alert_reusing_prescription_contents);
                        hintAddingItemSuccessfully = getString(R.string.hint_adding_prescription_successfully);
                        hintAfterAddingItem = getString(R.string.hint_after_adding_prescription);
                    }
                    else {
                        int miscItemPos = prevIntent.getIntExtra(TcmCommon.MISC_ITEM_POS_KEY, MiscManagementActivity.LIST_ITEM_POS_MEDICINE_CATEGORY);
                        String miscItemName = MiscManagementActivity.getItemNameByPosition(miscItemPos);
                        String primaryKey = MiscManagementActivity.getDbPrimaryIdNameByPosition(miscItemPos);
                        String tableName = MiscManagementActivity.getTableNameByPosition(miscItemPos);

                        sqlCheckItem = "select " + primaryKey +", name"
                            + " from " + tableName
                            + " where name like ?"
                            + " order by " + primaryKey + " asc";
                        sqlAddItem = "insert into " + tableName + "(name)"
                            + " values(?)";
                        newItem = "新" + miscItemName;
                        hintReusingItemTitle = "已存在未使用的" + newItem + "信息";
                        hintReusingItemContents = "数据库已存在一个“" + newItem + "”，"
                            + "可在名称栏输入“" + newItem + "”查询出该" + miscItemName + "详细页面，"
                            + "对其进行重命名，并补全其余信息。";
                        hintAddingItemSuccessfully = "新增" + miscItemName + "成功";
                        hintAfterAddingItem = "已成功添加" + newItem
                            + "，请在名称栏输入“" + newItem +"”进行查询并编辑详细的药物信息。";
                    }

                    String[] sqlArgs = (new String[]{ newItem });
                    SQLiteDatabase db = mDbHelper.getDatabase();
                    Cursor c = db.rawQuery(sqlCheckItem, sqlArgs);

                    if (c.moveToNext()) {
                        Hint.alert(QueryEntryActivity.this, hintReusingItemTitle, hintReusingItemContents);
                        c.close();
                        return;
                    }
                    c.close();

                    int categorySpinnerPos = 0;
                    String[] sqlArgsOfAddingItem = (TcmCommon.OP_TYPE_VALUE_MEDICINE == opType || TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == opType)
                        ? (new String[] { newItem, String.valueOf(categorySpinnerPos) })
                        : (new String[] { newItem });

                    db.execSQL(sqlAddItem, sqlArgsOfAddingItem);
                    Hint.alert(QueryEntryActivity.this, hintAddingItemSuccessfully, hintAfterAddingItem);

                    EditText etxName = (EditText) findViewById(R.id.etx_name);
                    Spinner spnCategory = (Spinner) findViewById(R.id.spn_category);

                    etxName.setText(newItem);
                    spnCategory.setSelection(categorySpinnerPos);
                    queryItems();
                }
            };
        }

        if (null == mAddItemDialog) {
            mAddItemDialog = new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent prevIntent = getIntent();
                    int opType = prevIntent.getIntExtra(TcmCommon.OP_TYPE_KEY, TcmCommon.OP_TYPE_VALUE_MEDICINE);
                    String hint = null;

                    if (TcmCommon.OP_TYPE_VALUE_MEDICINE == opType)
                        hint = getString(R.string.asking_before_adding_medicine);
                    else if (TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == opType)
                        hint = getString(R.string.asking_before_adding_prescription);
                    else {
                        int miscItemPos = prevIntent.getIntExtra(TcmCommon.MISC_ITEM_POS_KEY, MiscManagementActivity.LIST_ITEM_POS_MEDICINE_CATEGORY);

                        hint = "确定要新增" + MiscManagementActivity.getItemNameByPosition(miscItemPos) + "吗？";
                    }

                    Hint.alert(QueryEntryActivity.this, hint, R.string.confirm_or_cancel_guide,
                        mAddItemAction, null);
                }
            };
        }
    }

    private void doExtraJobsForLowerVersions() {
        com.android_assistant.TextView.setDefaultTextShadow(
            (TextView) findViewById(R.id.txv_name));

        com.android_assistant.TextView.setDefaultTextShadow(
            (TextView) findViewById(R.id.txv_category));
    }

    private void fillCategorySpinner(int opType) {
        if (TcmCommon.OP_TYPE_VALUE_MEDICINE != opType
            && TcmCommon.OP_TYPE_VALUE_PRESCRIPTION != opType)
            return;

        String[] categoryItems = (TcmCommon.OP_TYPE_VALUE_MEDICINE == opType)
            ? mDbHelper.queryMedicineCategories(getString(R.string.not_limited))
            : mDbHelper.queryPrescriptionCategories(getString(R.string.not_limited));
        Spinner spnCategory = (Spinner) findViewById(R.id.spn_category);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
            R.drawable.default_spinner_text, categoryItems);

        spnCategory.setAdapter(spinnerAdapter);
    }

    private void queryItems() {
        Intent prevIntent = getIntent();
        int opType = prevIntent.getIntExtra(TcmCommon.OP_TYPE_KEY, TcmCommon.OP_TYPE_VALUE_MEDICINE);
        ArrayList<ItemBrief> itemList = new ArrayList<ItemBrief>();
        EditText etxName = (EditText) findViewById(R.id.etx_name);
        String itemName = etxName.getText().toString();
        Spinner spnCategory = (Spinner) findViewById(R.id.spn_category);
        int selectedCategoryPos = spnCategory.getSelectedItemPosition();
        int sqlResId = 0;
        String sql = null;
        String[] sqlArgs = null;
        String primaryKey = null;

        if (TcmCommon.OP_TYPE_VALUE_MEDICINE == opType) {
            sqlResId = (selectedCategoryPos > 0)
                ? ((itemName.length() > 0)
                    ? R.string.sql_query_medicine_items_by_name_and_category
                    : R.string.sql_query_medicine_items_by_category)
                : ((itemName.length() > 0)
                    ? R.string.sql_query_medicine_items_by_name
                    : R.string.sql_query_all_medicine_items);
            sql = getString(sqlResId);
            sqlArgs = (selectedCategoryPos > 0)
                ? ((itemName.length() > 0)
                    ? (new String[]{ "%" + itemName + "%", String.valueOf(selectedCategoryPos)})
                    : (new String[]{ String.valueOf(selectedCategoryPos) }) )
                : ((itemName.length() > 0)
                    ? (new String[]{ "%" + itemName + "%" })
                    : null);
            primaryKey = "mid";
        }
        else if (TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == opType) {
            sqlResId = (selectedCategoryPos > 0)
                ? ((itemName.length() > 0)
                    ? R.string.sql_query_prescription_items_by_name_and_category
                    : R.string.sql_query_prescription_items_by_category)
                : ((itemName.length() > 0)
                    ? R.string.sql_query_prescription_items_by_name
                    : R.string.sql_query_all_prescription_items);
            sql = getString(sqlResId);
            sqlArgs = (selectedCategoryPos > 0)
                ? ((itemName.length() > 0)
                    ? (new String[]{ "%" + itemName + "%", String.valueOf(selectedCategoryPos)})
                    : (new String[]{ String.valueOf(selectedCategoryPos) }) )
                : ((itemName.length() > 0)
                    ? (new String[]{ "%" + itemName + "%" })
                    : null);
            primaryKey = "pid";
        }
        else {
            int miscItemPos = prevIntent.getIntExtra(TcmCommon.MISC_ITEM_POS_KEY, MiscManagementActivity.LIST_ITEM_POS_MEDICINE_CATEGORY);
            String tableName = MiscManagementActivity.getTableNameByPosition(miscItemPos);

            primaryKey = MiscManagementActivity.getDbPrimaryIdNameByPosition(miscItemPos);
            sql = (itemName.length() > 0)
                ? ("select " + primaryKey + ", name"
                    + " from " + tableName
                    + " where name like ?"
                    + " order by " + primaryKey + " asc")
                : ("select " + primaryKey + ", name"
                    + " from " + tableName
                    + " order by " + primaryKey + " asc");
            sqlArgs = (itemName.length() > 0)
                ? (new String[]{ "%" + itemName + "%" })
                : null;
        }

        Cursor c = mDbHelper.getDatabase().rawQuery(sql, sqlArgs);

        while (c.moveToNext()) {
            itemList.add(new ItemBrief(String.valueOf(c.getInt(c.getColumnIndex(primaryKey))),
                c.getString(c.getColumnIndex("name"))));
        }
        c.close();

        int resultCount = itemList.size();

        if (resultCount <= 0)
            Hint.alert(this, R.string.not_found, R.string.hint_add_when_not_found);

        Hint.shortToast(this, ResourceExports.getString(this, R.array.query_result)
            + ResourceExports.getString(this, R.array.quantity)
            + ": " + resultCount);

        ListView lsvQueryResult = (ListView) findViewById(R.id.lsv_query_items);
        ItemBriefAdapter adapter = new ItemBriefAdapter(this, itemList);

        lsvQueryResult.setAdapter(adapter);
        lsvQueryResult.setOnItemClickListener(this);
    }

    private class ItemBrief {
        public String id;
        public String name;

        public ItemBrief() {}

        public ItemBrief(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private class ItemBriefAdapter extends BaseAdapter {

        private final Context mContext;
        private final List<ItemBrief> mItemList;
        private final LayoutInflater mInflater;

        public ItemBriefAdapter(Context context, List<ItemBrief> itemList) {
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
                convertView = mInflater.inflate(R.layout.brief_item, null);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.chkbox_medication_brief);
                holder.id = (TextView) convertView.findViewById(R.id.txv_medication_brief_id);
                holder.name = (TextView) convertView.findViewById(R.id.txv_medication_brief_name);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            ItemBrief item = mItemList.get(position);

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
}
