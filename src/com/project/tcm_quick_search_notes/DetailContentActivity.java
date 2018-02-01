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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android_assistant.Hint;

public class DetailContentActivity extends Activity {

    private static String TAG = "DetailContentActivity";

    private static final String[] DETAIL_CONTENT_FIELD_NAMES = {
        "checkBoxFlags",
        "spnKey",
        "etxKey",
        "spnValuePrefix_1",
        "etxValuePrefix_1",
        "spnValuePrefix_2",
        "etxValuePrefix_2",
        "spnValue",
        "etxValueShort",
        "spnValueSuffix_1",
        "etxValueSuffix_1",
        "spnValueSuffix_2",
        "etxValueSuffix_2",
        "etxValueLong"
    };

    private static final int DETAIL_CONTENT_FIELD_CHKBOX = 0;
    private static final int DETAIL_CONTENT_FIELD_SPN_KEY = 1;
    private static final int DETAIL_CONTENT_FIELD_ETX_KEY = 2;
    private static final int DETAIL_CONTENT_FIELD_SPN_VALUE_PREFIX_1 = 3;
    private static final int DETAIL_CONTENT_FIELD_ETX_VALUE_PREFIX_1 = 4;
    private static final int DETAIL_CONTENT_FIELD_SPN_VALUE_PREFIX_2 = 5;
    private static final int DETAIL_CONTENT_FIELD_ETX_VALUE_PREFIX_2 = 6;
    private static final int DETAIL_CONTENT_FIELD_SPN_VALUE = 7;
    private static final int DETAIL_CONTENT_FIELD_ETX_VALUE_SHORT = 8;
    private static final int DETAIL_CONTENT_FIELD_SPN_VALUE_SUFFIX_1 = 9;
    private static final int DETAIL_CONTENT_FIELD_ETX_VALUE_SUFFIX_1 = 10;
    private static final int DETAIL_CONTENT_FIELD_SPN_VALUE_SUFFIX_2 = 11;
    private static final int DETAIL_CONTENT_FIELD_ETX_VALUE_SUFFIX_2 = 12;
    private static final int DETAIL_CONTENT_FIELD_ETX_VALUE_LONG = 13;

    private static final String[] DETAIL_CONTENT_SPINNER_NAMES = {
        DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_SPN_KEY],
        DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_SPN_VALUE_PREFIX_1],
        DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_SPN_VALUE_PREFIX_2],
        DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_SPN_VALUE],
        DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_SPN_VALUE_SUFFIX_1],
        DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_SPN_VALUE_SUFFIX_2]
    };

    private static final String[] DETAIL_CONTENT_EDIT_TEXT_NAMES = {
        DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_KEY],
        DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_PREFIX_1],
        DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_PREFIX_2],
        DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_SHORT],
        DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_SUFFIX_1],
        DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_SUFFIX_2],
        DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_LONG]
    };

    private static final int CHKBOX_VISIBLE = 1;
    private static final int CHKBOX_CLICKABLE = 2;
    private static final int CHKBOX_SELECTED = 4;

    private static final String ITEM_READ_ONLY = "read-only";
    private static final String SPACE = " ";
    private static final String STRING_NONE = "";

    private /*static */DialogInterface.OnClickListener mExitActivity = null;

    private /*static */String[] mPageItemNames = null;

    private /*static */DetailContentTemplate[] mDetailContentTemplatesArray = null;

    private /*static */HashMap<String, DetailContentTemplate> mDetailContentTemplatesMap = new HashMap<String, DetailContentTemplate>();

    private Menu gMenu = null;

    private int mOpType = -1;
    private String mDetailItemId = null;
    private String mDetailItemName = null;
    private int mPositionAtFunctionalityList = -1;

    private DbHelper mDbHelper = null;
    private HashMap<String, String> mDetailContentFromDb = null;

    private boolean mEditable = false;
    private boolean mIsMultiLineMode = true;

    private Intent mNextIntent = null;

    private TextView mTxvPrevItemId = null;
    private TextView mTxvPrevItemName = null;
    private TextView mTxvPrevArrow = null;

    private TextView mTxvNextItemId = null;
    private TextView mTxvNextItemName = null;
    private TextView mTxvNextArrow = null;

    private View.OnClickListener mJumpToNeiborDetailsPage = null;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();

        actionBar.setDisplayShowHomeEnabled(false); // hides the icon
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.default_action_bar_style));

        initResources();

        if (TcmCommon.OP_TYPE_VALUE_MEDICINE == mOpType) {
            setContentView(R.layout.activity_medicine_item_details);
            setTitle(getString(R.string.main_item_medicine));
        }
        else if (TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == mOpType) {
            setContentView(R.layout.activity_prescription_item_details);
            setTitle(getString(R.string.main_item_prescription));
        }
        else {
            String miscItemName = MiscManagementActivity.getItemNameByPosition(mPositionAtFunctionalityList);

            if (MiscManagementActivity.LIST_ITEM_POS_REFERENCE_MATERIAL == mPositionAtFunctionalityList)
                setContentView(R.layout.activity_reference_material_details);
            else
                setContentView(R.layout.activity_general_misc_item_details);

            setTitle(miscItemName);
        }

        reloadDetailContentFromDatabase();

        fillDetailContentTemplates(mOpType, mPositionAtFunctionalityList); // MUST be executed before fillDetailTitles() and fillDetailContents()!!!

        fillDetailTitles(mOpType, mPositionAtFunctionalityList);
        fillDetailContents(mOpType, mPositionAtFunctionalityList);

        initNeiboringItemViews();
        updateNeiboringItems();
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
        getMenuInflater().inflate(R.menu.item_details_page, menu);
        gMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (R.id.menu_edit == id) {
            item.setVisible(false);
            gMenu.findItem(R.id.menu_save).setVisible(true);
            gMenu.findItem(R.id.menu_cancel).setVisible(true);
            mEditable = true;
            refreshAllViews();
        }
        else if (R.id.menu_mode_switch == id) {
            mIsMultiLineMode = !mIsMultiLineMode;
            gMenu.findItem(id).setTitle(mIsMultiLineMode ? R.string.switch_to_single_line_mode : R.string.switch_to_multi_line_mode);
            refreshAllViews();
            Hint.shortToast(this, mIsMultiLineMode ? R.string.multi_line_mode : R.string.single_line_mode);
        }
        else if (R.id.menu_save == id) {
            int[][] detailResIds = getDetailFieldResourceIds(mOpType, mPositionAtFunctionalityList);
            ArrayList<String> updateArgs = new ArrayList<String>();
            DetailContentData contentData = null;
            boolean isMedicine = (TcmCommon.OP_TYPE_VALUE_MEDICINE == mOpType);
            boolean isPrescription = (TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == mOpType);
            final String SHORT_VALUE_SPN_NAME = DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_SPN_VALUE];
            final String LONG_ETX_NAME = DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_LONG];

            for (int i = 0; i < detailResIds.length; ++i) {
                if (detailFieldIsNotUsed(mOpType, mPositionAtFunctionalityList, i))
                    continue;

                ListView lsvContents = (ListView) findViewById(detailResIds[i][2]);
                DetailContentAdapter contentsAdapter = (DetailContentAdapter) lsvContents.getAdapter();

                if (detailFieldIsSimpleCharText(mOpType, mPositionAtFunctionalityList, i)) {
                    // NOTE: The commented code below may cause data chaos and performance problem, but keep it for a warning.
                    //View convertView = null;
                    //DetailContentAdapter.ViewHolder viewHolder = null;
                    //convertView = contentsAdapter.getView(0, null, null);
                    //viewHolder = (DetailContentAdapter.ViewHolder) convertView.getTag();
                    //updateArgs.add(viewHolder.etxValueLong.getText().toString());

                    contentData = contentsAdapter.getItemList().get(0);
                    updateArgs.add(contentData.editTextContents.get(LONG_ETX_NAME));
                    continue;
                }

                if ((isMedicine && DbHelper.MEDICINE_COLUMN_INDEX_CATEGORY == i)
                    || (isPrescription && DbHelper.PRESCRIPTION_COLUMN_INDEX_CATEGORY == i)) {
                    // NOTE: The commented code below may cause data chaos and performance problem, but keep it for a warning.
                    //View convertView = null;
                    //DetailContentAdapter.ViewHolder viewHolder = null;
                    //convertView = contentsAdapter.getView(0, null/*convertView*/, null);
                    //viewHolder = (DetailContentAdapter.ViewHolder) convertView.getTag();
                    //updateArgs.add(String.valueOf(viewHolder.spnValue.getSelectedItemPosition()));

                    contentData = contentsAdapter.getItemList().get(0);
                    updateArgs.add(String.valueOf(contentData.selectedSpinnerPositions.get(SHORT_VALUE_SPN_NAME)));
                    continue;
                }

                String fieldValue = serializeToDatabaseField(contentsAdapter.getItemList());

                updateArgs.add(fieldValue);
            }

            updateArgs.add(mDetailItemId);

            try {
                if (TcmCommon.OP_TYPE_VALUE_MEDICINE == mOpType)
                    mDbHelper.updateMedicineItem(updateArgs.toArray(new String[updateArgs.size()]));
                else if (TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == mOpType)
                    mDbHelper.updatePrescriptionItem(updateArgs.toArray(new String[updateArgs.size()]));
                else {
                    if (MiscManagementActivity.LIST_ITEM_POS_REFERENCE_MATERIAL == mPositionAtFunctionalityList)
                        mDbHelper.updateReferenceMaterialItem(updateArgs.toArray(new String[updateArgs.size()]));
                    else
                        mDbHelper.updateMiscItem(mPositionAtFunctionalityList, updateArgs.toArray(new String[updateArgs.size()]));
                }
                Hint.alert(this, R.string.save_successfully, R.string.asking_after_save_operation, mExitActivity, null);
            } catch(SQLException e) {
                Hint.alert(this, getString(R.string.alert_failed_to_update),
                    getString(R.string.alert_checking_input) + e.getMessage());
            }
        }
        else if (R.id.menu_cancel == id) {
            item.setVisible(false);
            gMenu.findItem(R.id.menu_save).setVisible(false);
            gMenu.findItem(R.id.menu_edit).setVisible(true);
            mEditable = false;
            refreshAllViews();
        }
        else if (R.id.menu_details_help == id)
            Hint.alert(this, R.string.help, R.string.help_info_for_details_page);
        else
            ;

        return super.onOptionsItemSelected(item);
    }

    private int[] getNeiboringItemResIds() {
        final int[] MEDICINE_IDS = {
            R.id.txv_prev_medicine_item,
            R.id.txv_prev_medicine_id,
            R.id.txv_prev_medicine_name,
            R.id.txv_next_medicine_item,
            R.id.txv_next_medicine_id,
            R.id.txv_next_medicine_name
        };
        final int[] PRESCRIPTION_IDS = {
            R.id.txv_prev_prescription_item,
            R.id.txv_prev_prescription_id,
            R.id.txv_prev_prescription_name,
            R.id.txv_next_prescription_item,
            R.id.txv_next_prescription_id,
            R.id.txv_next_prescription_name
        };
        final int[] REF_IDS = {
            R.id.txv_prev_ref_item,
            R.id.txv_prev_ref_id,
            R.id.txv_prev_ref_name,
            R.id.txv_next_ref_item,
            R.id.txv_next_ref_id,
            R.id.txv_next_ref_name
        };
        final int[] MISC_IDS = {
            R.id.txv_prev_misc_item,
            R.id.txv_prev_misc_id,
            R.id.txv_prev_misc_name,
            R.id.txv_next_misc_item,
            R.id.txv_next_misc_id,
            R.id.txv_next_misc_name
        };

        if (TcmCommon.OP_TYPE_VALUE_MEDICINE == mOpType)
            return MEDICINE_IDS;
        else if (TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == mOpType)
            return PRESCRIPTION_IDS;
        else {
            if (MiscManagementActivity.LIST_ITEM_POS_REFERENCE_MATERIAL == mPositionAtFunctionalityList)
                return REF_IDS;
            else
                return MISC_IDS;
        }
    }

    private void initResources() {
        if (null == mDbHelper) {
            mDbHelper = new DbHelper(this);
            mDbHelper.openOrCreate();
        }

        if (null == mExitActivity) {
            mExitActivity = new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DetailContentActivity.this.finish();
                }
            };
        }

        Intent prevIntent = getIntent();

        mOpType = prevIntent.getIntExtra(TcmCommon.OP_TYPE_KEY, TcmCommon.OP_TYPE_VALUE_MEDICINE);
        mDetailItemId = prevIntent.getStringExtra(TcmCommon.ID_KEY);
        mDetailItemName = prevIntent.getStringExtra(TcmCommon.NAME_KEY);
        mPositionAtFunctionalityList = prevIntent.getIntExtra(TcmCommon.FUNC_LIST_POS_KEY, 0);

        //Hint.longToast(this, "opType: " + mOpType + ", positionAtFunctionalityList: " + mPositionAtFunctionalityList
            //+ ", id: " + mDetailItemId + ", name: " + mDetailItemName);

        if (null == mNextIntent)
            mNextIntent = new Intent(this, DetailContentActivity.class);

        if (null == mDetailContentTemplatesArray) {
            mDetailContentTemplatesArray = new DetailContentTemplate[DbHelper.MAX_COLUMN_COUNT];
            for (int i = 0; i < mDetailContentTemplatesArray.length; ++i) {
                mDetailContentTemplatesArray[i] = new DetailContentTemplate();
            }
        }

        refreshPageItemNames();
    }

    private void initNeiboringItemViews() {
        if (null == mJumpToNeiborDetailsPage) {
            mJumpToNeiborDetailsPage = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (v == mTxvPrevItemId
                        || v == mTxvPrevItemName
                        || v == mTxvPrevArrow) {
                        if (mTxvPrevItemId.getText().toString().equals("0"))
                            return;

                        mDetailItemId = mTxvPrevItemId.getText().toString();
                        mDetailItemName = mTxvPrevItemName.getText().toString();
                    } else {
                        if (mTxvNextItemId.getText().toString().equals("0"))
                            return;

                        mDetailItemId = mTxvNextItemId.getText().toString();
                        mDetailItemName = mTxvNextItemName.getText().toString();
                    }

                    //boolean isEditableBeforeJumping = mEditable;

                    reloadDetailContentFromDatabase();

                    mEditable = false; // Necessary before filling detail contents!!!
                    fillDetailContents(mOpType, mPositionAtFunctionalityList);

                    /*if (isEditableBeforeJumping) {
                        gMenu.findItem(R.id.menu_edit).setVisible(false);
                        gMenu.findItem(R.id.menu_save).setVisible(true);
                        gMenu.findItem(R.id.menu_cancel).setVisible(true);
                        mEditable = true;
                        refreshAllViews();
                    } else {*/
                        gMenu.findItem(R.id.menu_edit).setVisible(true);
                        gMenu.findItem(R.id.menu_save).setVisible(false);
                        gMenu.findItem(R.id.menu_cancel).setVisible(false);
                    //}

                    updateNeiboringItems();
                }

            };
        }

        int[] neiborResIds = getNeiboringItemResIds();

        if (null == mTxvPrevArrow) {
            mTxvPrevArrow = (TextView) findViewById(neiborResIds[0]);
            mTxvPrevArrow.setOnClickListener(mJumpToNeiborDetailsPage);
        }

        if (null == mTxvPrevItemId) {
            mTxvPrevItemId = (TextView) findViewById(neiborResIds[1]);
            mTxvPrevItemId.setOnClickListener(mJumpToNeiborDetailsPage);
        }

        if (null == mTxvPrevItemName) {
            mTxvPrevItemName = (TextView) findViewById(neiborResIds[2]);
            mTxvPrevItemName.setOnClickListener(mJumpToNeiborDetailsPage);
        }

        if (null == mTxvNextArrow) {
            mTxvNextArrow = (TextView) findViewById(neiborResIds[3]);
            mTxvNextArrow.setOnClickListener(mJumpToNeiborDetailsPage);
        }

        if (null == mTxvNextItemId) {
            mTxvNextItemId = (TextView) findViewById(neiborResIds[4]);
            mTxvNextItemId.setOnClickListener(mJumpToNeiborDetailsPage);
        }

        if (null == mTxvNextItemName) {
            mTxvNextItemName = (TextView) findViewById(neiborResIds[5]);
            mTxvNextItemName.setOnClickListener(mJumpToNeiborDetailsPage);
        }
    }

    private void updateNeiboringItems() {
        int category = com.android_assistant.Integer.parseInt(mDetailContentFromDb.get(DbHelper.MEDICINE_COLUMNS[DbHelper.MEDICINE_COLUMN_INDEX_CATEGORY]), 10, 0);
        String[][] prevItem = mDbHelper.queryBriefOfNearItems(mOpType, mPositionAtFunctionalityList, true, 1, mDetailItemId, category);
        String[][] nextItem = mDbHelper.queryBriefOfNearItems(mOpType, mPositionAtFunctionalityList, false, 1, mDetailItemId, category);

        if (null == prevItem) {
            mTxvPrevItemId.setText(String.valueOf(0));
            mTxvPrevItemName.setText(R.string.none);
        } else {
            mTxvPrevItemId.setText(prevItem[0][0]);
            mTxvPrevItemName.setText(prevItem[0][1]);
        }

        if (null == nextItem) {
            mTxvNextItemId.setText(String.valueOf(0));
            mTxvNextItemName.setText(R.string.none);
        } else {
            mTxvNextItemId.setText(nextItem[0][0]);
            mTxvNextItemName.setText(nextItem[0][1]);
        }
    }

    private void reloadDetailContentFromDatabase() {
        if (TcmCommon.OP_TYPE_VALUE_MEDICINE == mOpType)
            mDetailContentFromDb = mDbHelper.queryItemDetails(mDetailItemId, TcmCommon.OP_TYPE_VALUE_MEDICINE, mPositionAtFunctionalityList);
        else if (TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == mOpType)
            mDetailContentFromDb = mDbHelper.queryItemDetails(mDetailItemId, TcmCommon.OP_TYPE_VALUE_PRESCRIPTION, mPositionAtFunctionalityList);
        else
            mDetailContentFromDb = mDbHelper.queryItemDetails(mDetailItemId, TcmCommon.OP_TYPE_VALUE_MISC_MANAGEMENT, mPositionAtFunctionalityList);

        if (null == mDetailContentFromDb) {
            Hint.alert(this, getString(R.string.db_error),
                getString(R.string.not_found) + ": " + "id = " + mDetailItemId,
                mExitActivity);
        }
    }

    private void refreshAllViews() {
        int[][] detailResIds = getDetailFieldResourceIds(mOpType, mPositionAtFunctionalityList);
        boolean isEditable = mEditable;

        for (int i = 0; i < detailResIds.length; ++i) {
            if (detailFieldIsNotUsed(mOpType, mPositionAtFunctionalityList, i))
                continue;

            if (!detailFieldIsSimpleCharText(mOpType, mPositionAtFunctionalityList, i)) {
                ListView lsvTitle = (ListView)findViewById(detailResIds[i][1]);
                DetailTitleAdapter titleAdapter = (DetailTitleAdapter)lsvTitle.getAdapter();

                titleAdapter.notifyDataSetChanged();
            }

            ListView lsvContents = (ListView) findViewById(detailResIds[i][2]);
            DetailContentAdapter contentsAdapter = (DetailContentAdapter) lsvContents.getAdapter();
            DetailContentTemplate template = mDetailContentTemplatesArray[i];

            for (int j = 0; j < contentsAdapter.getCount(); ++j) {
                DetailContentData contentData = (DetailContentData) contentsAdapter.getItem(j);

                if (0 != (template.checkBoxFlags & CHKBOX_VISIBLE)) {
                    if (isEditable)
                        contentData.checkBoxFlags |= CHKBOX_CLICKABLE;
                    else
                        contentData.checkBoxFlags &= (~CHKBOX_CLICKABLE);
                }

                contentData.spinnersEnabled = isEditable;
                contentData.editTextsEnabled = isEditable;
            }
            contentsAdapter.notifyDataSetChanged();
        }
    }

    // NOTE: for complicated items only.
    // TODO: Parameter expectedMinResultCount and resultCountIsFixed seem no use, optimize them later.
    private DetailContentData[] parseFromDataseField(final String rawDbValue, int pageItemIndex,
        int expectedMinResultCount, boolean resultCountIsFixed) {
        String handledValue = (null == rawDbValue || 0 == rawDbValue.length()) ? STRING_NONE/*SPACE*/ : rawDbValue;
        String[] items = (0 == handledValue.length()) ? null : handledValue.split(TcmCommon.LINE_DELIM, -1);
        ArrayList<String> itemList = new ArrayList<String>();

        expectedMinResultCount = mDetailContentTemplatesArray[pageItemIndex].minRecords;
        if (expectedMinResultCount < 1)
            expectedMinResultCount = 1;

        resultCountIsFixed = mDetailContentTemplatesArray[pageItemIndex].isFixed;

        if (resultCountIsFixed) {
            for (int i = 0; i < expectedMinResultCount; ++i) {
                itemList.add(null);
            }
        }
        else {
            int count = 0;

            if (null == items)
                count = expectedMinResultCount;
            else
                count = (items.length > expectedMinResultCount) ? items.length : expectedMinResultCount;

            for (int i = 0; i < count; ++i) {
                itemList.add(null);
            }
        }

        if (null != items) {
            int countWithValue = (items.length <= itemList.size()) ? items.length : itemList.size();

            for (int i = 0; i < countWithValue; ++i) {
                itemList.set(i, items[i]);
            }
        }

        return parseFromDataseField(itemList, pageItemIndex);
    }

    private DetailContentData[] parseFromDataseField(final List<String> semiParsedValueList, int pageItemIndex) {
        ArrayList<DetailContentData> dataList = new ArrayList<DetailContentData>();

        if (null == semiParsedValueList || 0 == semiParsedValueList.size()) {
            dataList.add(parseFromDataseField(STRING_NONE, pageItemIndex)); // at least one
        }
        else {
            for (int i = 0; i < semiParsedValueList.size(); ++i) {
                dataList.add(parseFromDataseField(semiParsedValueList.get(i), pageItemIndex));
            }
        }

        return dataList.toArray(new DetailContentData[dataList.size()]);
    }

    private DetailContentData parseFromDataseField(final String singleLineValue, int pageItemIndex) {
        DetailContentData result = new DetailContentData(pageItemIndex);
        String[] fieldValues = (null == singleLineValue || 0 == singleLineValue.length())
            ? null : singleLineValue.split(TcmCommon.INNER_FIELD_DELIM, -1);
        int actualFieldCount = (null != fieldValues) ? fieldValues.length : 0;

        final String[] FIELD_NAMES = DETAIL_CONTENT_FIELD_NAMES;
        final int EXPECTED_FIELD_COUNT = FIELD_NAMES.length;

        if (0 == actualFieldCount) {
            result.checkBoxFlags &= (~CHKBOX_SELECTED);

            for (int i = 1; i < EXPECTED_FIELD_COUNT - 1; ++i) {
                boolean isSpinner = (1 == i % 2);

                if (isSpinner)
                    result.selectedSpinnerPositions.put(FIELD_NAMES[i], 0);
                else
                    result.editTextContents.put(FIELD_NAMES[i], STRING_NONE/*SPACE*/); // null is ok, too
            }

            result.editTextContents.put(FIELD_NAMES[EXPECTED_FIELD_COUNT - 1], STRING_NONE/*SPACE*/);

            return result;
        }

        if (0 != (com.android_assistant.Integer.parseInt(fieldValues[0], 10, 0) & CHKBOX_SELECTED))
            result.checkBoxFlags |= CHKBOX_SELECTED;
        else
            result.checkBoxFlags &= (~CHKBOX_SELECTED);

        for (int i = 1; i < actualFieldCount - 1; ++i) {
            if (i >= EXPECTED_FIELD_COUNT - 1)
                break;

            boolean isSpinner = (1 == i % 2);

            if (isSpinner)
                result.selectedSpinnerPositions.put(FIELD_NAMES[i], com.android_assistant.Integer.parseInt(fieldValues[i], 10, 0));
            else
                result.editTextContents.put(FIELD_NAMES[i], fieldValues[i]);
        }

        if (actualFieldCount >= EXPECTED_FIELD_COUNT)
            result.editTextContents.put(FIELD_NAMES[EXPECTED_FIELD_COUNT - 1], fieldValues[EXPECTED_FIELD_COUNT - 1]);

        return result;
    }

    private String serializeToDatabaseField(final List<DetailContentData> input) {
        if (null == input)
            return STRING_NONE;

        int size = input.size();

        if (0 == size)
            return STRING_NONE;

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < size; ++i) {
            boolean isLastLine = (i == size - 1);

            result.append(serializeToDatabaseField(input.get(i)));
            if (!isLastLine)
                result.append(TcmCommon.LINE_DELIM);
        }

        return result.toString();
    }

    private String serializeToDatabaseField(final DetailContentData input) {
        StringBuilder result = new StringBuilder();
        final String[] FIELD_NAMES = DETAIL_CONTENT_FIELD_NAMES;
        final int FIELD_COUNT = FIELD_NAMES.length;

        if (null == input) {
            for (int i = 0; i < FIELD_COUNT - 1; ++i) {
                result.append(TcmCommon.INNER_FIELD_DELIM);
            }

            return result.toString();
        }

        result.append(input.checkBoxFlags);
        result.append(TcmCommon.INNER_FIELD_DELIM);

        String fieldValue = null;

        for (int i = 1; i < FIELD_COUNT - 1; ++i) {
            boolean isSpinner = (1 == i % 2);

            if (isSpinner)
                fieldValue = String.valueOf(input.selectedSpinnerPositions.get(FIELD_NAMES[i]));
            else
                fieldValue = input.editTextContents.get(FIELD_NAMES[i]);

            result.append((null == fieldValue || 0 == fieldValue.length()) ? STRING_NONE/*SPACE*/ : fieldValue);
            result.append(TcmCommon.INNER_FIELD_DELIM);
        }

        fieldValue = input.editTextContents.get(FIELD_NAMES[FIELD_COUNT - 1]);
        result.append((null == fieldValue || 0 == fieldValue.length()) ? STRING_NONE/*SPACE*/ : fieldValue);

        return result.toString();
    }

    private void fillDetailTitles(int opType, int positionAtFunctionalityList) {
        int[][] detailItemResIds = getDetailFieldResourceIds(opType, positionAtFunctionalityList);

        for (int i = 0; i < detailItemResIds.length; ++i) {
            if (detailFieldIsNotUsed(opType, positionAtFunctionalityList, i))
                continue;

            ArrayList<DetailTitleData> titleList = new ArrayList<DetailTitleData>();
            ListView lsvTitle = (ListView) findViewById(detailItemResIds[i][1]);
            DetailTitleAdapter titleAdapter = new DetailTitleAdapter(this, titleList);

            titleList.add(new DetailTitleData(i, getString(detailItemResIds[i][0])));

            lsvTitle.setAdapter(titleAdapter);
        }
    }

    private boolean detailFieldIsSimpleCharText(int opType, int positionAtFunctionalityList, int fieldIndex) {
        if (TcmCommon.OP_TYPE_VALUE_MEDICINE == opType) {
            return (DbHelper.MEDICINE_COLUMN_INDEX_NAME == fieldIndex
                || DbHelper.MEDICINE_COLUMN_INDEX_ALIASES == fieldIndex
                || DbHelper.MEDICINE_COLUMN_INDEX_EFFECTS == fieldIndex
                || DbHelper.MEDICINE_COLUMN_INDEX_ACTIONS_AND_INDICATIONS == fieldIndex
                || DbHelper.MEDICINE_COLUMN_INDEX_DETAILS == fieldIndex
                || DbHelper.MEDICINE_COLUMN_INDEX_COMMON_PRESCRIPTIONS == fieldIndex
                || DbHelper.MEDICINE_COLUMN_INDEX_COMMON_PARTNERS == fieldIndex
                || DbHelper.MEDICINE_COLUMN_INDEX_SIMILAR_MEDICINES == fieldIndex
                || DbHelper.MEDICINE_COLUMN_INDEX_DOSAGE_REFERENCE == fieldIndex
                || DbHelper.MEDICINE_COLUMN_INDEX_CONTRAINDICATIONS == fieldIndex
                || DbHelper.MEDICINE_COLUMN_INDEX_REMARKS == fieldIndex);
        }
        else if (TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == opType) {
            return (DbHelper.PRESCRIPTION_COLUMN_INDEX_NAME == fieldIndex
                || DbHelper.PRESCRIPTION_COLUMN_INDEX_ALIASES == fieldIndex
                || DbHelper.PRESCRIPTION_COLUMN_INDEX_EFFECTS == fieldIndex
                || DbHelper.PRESCRIPTION_COLUMN_INDEX_ACTIONS_AND_INDICATIONS == fieldIndex
                || DbHelper.PRESCRIPTION_COLUMN_INDEX_CONTRAINDICATIONS == fieldIndex
                || DbHelper.PRESCRIPTION_COLUMN_INDEX_RELATIVE_PRESCRIPTIONS == fieldIndex
                || DbHelper.PRESCRIPTION_COLUMN_INDEX_REMARKS == fieldIndex);
        }
        else
            return true;
    }

    private boolean detailFieldIsNotUsed(int opType, int positionAtFunctionalityList, int fieldIndex) {
        if (TcmCommon.OP_TYPE_VALUE_MEDICINE == opType)
            return (DbHelper.MEDICINE_COLUMN_INDEX_MOTION_FORMS_OF_ACTION == fieldIndex);
        else if (TcmCommon.OP_TYPE_VALUE_MISC_MANAGEMENT == opType) {
            if (MiscManagementActivity.LIST_ITEM_POS_REFERENCE_MATERIAL == positionAtFunctionalityList
                || MiscManagementActivity.LIST_ITEM_POS_PRESCRIPTION_CATEGORY == positionAtFunctionalityList)
                return false;
            else
                return (DbHelper.GENERAL_MISC_ITEM_COLUMN_INDEX_SUB_CATEGORIES == fieldIndex);
        }
        else
            return false;
    }

    private int[][] getDetailFieldResourceIds(int opType, int positionAtFunctionalityList) {
        final int[][] MEDICINE_RES_IDS = {
            { R.string.name, R.id.lsv_title_medicine_name, R.id.lsv_content_medicine_name },
            { R.string.alias, R.id.lsv_title_medicine_aliases, R.id.lsv_content_medicine_aliases },
            { R.string.category, R.id.lsv_title_medicine_category, R.id.lsv_content_medicine_category },
            { R.string.medicine_nature, R.id.lsv_title_medicine_nature, R.id.lsv_content_medicine_nature },
            { R.string.medicine_taste, R.id.lsv_title_medicine_tastes, R.id.lsv_content_medicine_tastes },
            { R.string.channel_tropism, R.id.lsv_title_channel_tropism, R.id.lsv_content_channel_tropism },
            { R.string.life_fundamental_diplay_name, R.id.lsv_title_relations_with_life_fundamentals, R.id.lsv_content_relations_with_life_fundamentals },
            { R.string.motion_form, R.id.lsv_title_motion_form, R.id.lsv_content_motion_form },
            { R.string.medicine_effect, R.id.lsv_title_medicine_effects, R.id.lsv_content_medicine_effects },
            { R.string.medicine_action_and_indication, R.id.lsv_title_medicine_actions_and_indications, R.id.lsv_content_medicine_actions_and_indications },
            { R.string.medication_details, R.id.lsv_title_medicine_details, R.id.lsv_content_medicine_details },
            { R.string.common_prescriptions, R.id.lsv_title_common_prescriptions, R.id.lsv_content_common_prescriptions },
            { R.string.common_medicine_partners, R.id.lsv_title_common_medicine_partners, R.id.lsv_content_common_medicine_partners },
            { R.string.similar_medicines, R.id.lsv_title_similar_medicines, R.id.lsv_content_similar_medicines },
            { R.string.dosage_reference, R.id.lsv_title_dosage_reference, R.id.lsv_content_dosage_reference },
            { R.string.contraindications, R.id.lsv_title_medicine_contraindications, R.id.lsv_content_medicine_contraindications },
            { R.string.reference_material, R.id.lsv_title_medicine_reference_material, R.id.lsv_content_medicine_reference_material },
            { R.string.remarks, R.id.lsv_title_medicine_remarks, R.id.lsv_content_medicine_remarks }
        };
        final int[][] PRESCRIPTION_RES_IDS = {
            { R.string.name, R.id.lsv_title_prescription_name, R.id.lsv_content_prescription_name },
            { R.string.alias, R.id.lsv_title_prescription_aliases, R.id.lsv_content_prescription_aliases },
            { R.string.category, R.id.lsv_title_prescription_category, R.id.lsv_content_prescription_category },
            { R.string.medicine_effect, R.id.lsv_title_prescription_effects, R.id.lsv_content_prescription_effects },
            { R.string.medicine_action_and_indication, R.id.lsv_title_prescription_actions_and_indications, R.id.lsv_content_prescription_actions_and_indications },
            { R.string.composition, R.id.lsv_title_prescription_composition, R.id.lsv_content_prescription_composition },
            { R.string.decoction, R.id.lsv_title_decoction, R.id.lsv_content_decoction },
            { R.string.method_of_taking_medicine, R.id.lsv_title_method_of_taking_prescription, R.id.lsv_content_method_of_taking_prescription },
            { R.string.contraindications, R.id.lsv_title_prescription_contraindications, R.id.lsv_content_prescription_contraindications },
            { R.string.relative_prescriptions, R.id.lsv_title_relative_prescriptions, R.id.lsv_content_relative_prescriptions },
            { R.string.reference_material, R.id.lsv_title_prescription_reference_material, R.id.lsv_content_prescription_reference_material },
            { R.string.remarks, R.id.lsv_title_prescription_remarks, R.id.lsv_content_prescription_remarks }
        };
        final int[][] REFERENCE_MATERIAL_RES_IDS = {
            { R.string.name, R.id.lsv_title_reference_material_name, R.id.lsv_content_reference_material_name },
            { R.string.version, R.id.lsv_title_reference_material_version, R.id.lsv_content_reference_material_version },
            { R.string.original_authors, R.id.lsv_title_reference_material_original_authors, R.id.lsv_content_reference_material_original_authors },
            { R.string.editors, R.id.lsv_title_reference_material_editors, R.id.lsv_content_reference_material_editors },
            { R.string.issuing_source, R.id.lsv_title_reference_material_issuing_source, R.id.lsv_content_reference_material_issuing_source },
            { R.string.issuing_date, R.id.lsv_title_reference_material_issuing_date, R.id.lsv_content_reference_material_issuing_date },
            { R.string.remarks, R.id.lsv_title_reference_material_remarks, R.id.lsv_content_reference_material_remarks }
        };
        final int[][] MISC_ITEM_RES_IDS = {
            { R.string.name, R.id.lsv_title_misc_item_name, R.id.lsv_content_misc_item_name },
            { R.string.sub_category, R.id.lsv_title_misc_item_sub_categories, R.id.lsv_content_misc_item_sub_categories },
            { R.string.remarks, R.id.lsv_title_misc_item_remarks, R.id.lsv_content_misc_item_remarks }
        };

        if (TcmCommon.OP_TYPE_VALUE_MEDICINE == opType)
            return MEDICINE_RES_IDS;
        else if (TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == opType)
            return PRESCRIPTION_RES_IDS;
        else {
            if (MiscManagementActivity.LIST_ITEM_POS_REFERENCE_MATERIAL == positionAtFunctionalityList)
                return REFERENCE_MATERIAL_RES_IDS;
            else
                return MISC_ITEM_RES_IDS;
        }
    }

    private String[] getPageItemNames(boolean needsRefresh) {
        if (null == mPageItemNames || needsRefresh)
            refreshPageItemNames();

        return mPageItemNames;
    }

    private void refreshPageItemNames() {
        if (null == mPageItemNames) {
            mPageItemNames = new String[DbHelper.MAX_COLUMN_COUNT];
        }

        int[][] detailItemResIds = getDetailFieldResourceIds(mOpType, mPositionAtFunctionalityList);

        for (int i = 0; i < detailItemResIds.length; ++i) {
            mPageItemNames[i] = getString(detailItemResIds[i][0]);
        }
    }

    private void fillDetailContentTemplates(int opType, int positionAtFunctionalityList) {
        DetailContentTemplate[] array = mDetailContentTemplatesArray;
        HashMap<String, DetailContentTemplate> map = mDetailContentTemplatesMap;
        final String[] COLUMN_NAMES = DbHelper.getTableColumnsList(opType, positionAtFunctionalityList);

        for (int i = 0; i < COLUMN_NAMES.length; ++i) {
            map.put(COLUMN_NAMES[i], array[i]);
        }

        // Simple character items need not filling up.
        if (TcmCommon.OP_TYPE_VALUE_MEDICINE != opType
            && TcmCommon.OP_TYPE_VALUE_PRESCRIPTION != opType)
            return;

        DbHelper dbHelper = mDbHelper;
        boolean isMedicine = (TcmCommon.OP_TYPE_VALUE_MEDICINE == opType);
        final String UNKNOWN_STRING = getString(R.string.unknown);
        final String HINT_PLEASE_SELECT = getString(R.string.please_select);
        final String HINT_INPUT_EXTRA_CONTENTS = getString(R.string.hint_input_extra_contents_if_necessary);
        final String REFERENCE_MATERIAL_CONTENTS_EXAMPLE = getString(R.string.reference_material_contents_example);
        //String hintPleaseSelectOrCustomize = getString(R.string.please_select_or_customize);
        String[] levelWords = dbHelper.queryAttributeNames(R.string.attr_table_prefix_level_word, SPACE);
        String[] processingMethod = dbHelper.queryAttributeNames(R.string.attr_table_prefix_processing_method, SPACE);
        String[] referenceMaterialNames = dbHelper.queryReferenceMaterialNames();

        final int[][] INTEGERS_FOR_MEDICINE_FIELDS = {
            // field index, isFixed, minRecords, checkBoxFlags
            { DbHelper.MEDICINE_COLUMN_INDEX_CATEGORY, 1, 1, 0 },
            { DbHelper.MEDICINE_COLUMN_INDEX_NATURE, 0, 1, 0 },
            { DbHelper.MEDICINE_COLUMN_INDEX_TASTES, 0, 1, 0 },
            { DbHelper.MEDICINE_COLUMN_INDEX_CHANNEL_TROPISM, 0, 1, 0 },
            { DbHelper.MEDICINE_COLUMN_INDEX_LIFE_FUNDAMENTALS, 1, 5, CHKBOX_VISIBLE | CHKBOX_CLICKABLE },
            { DbHelper.MEDICINE_COLUMN_INDEX_MOTION_FORMS_OF_ACTION, 1, 6, 0 },
            { DbHelper.MEDICINE_COLUMN_INDEX_EFFECTS, 1, 1, 0 },
            { DbHelper.MEDICINE_COLUMN_INDEX_ACTIONS_AND_INDICATIONS, 1, 1, 0 },
            { DbHelper.MEDICINE_COLUMN_INDEX_REFERENCE_MATERIAL, 0, 1, 0 }
        };

        final int[][] INTEGERS_FOR_PRESCRIPTION_FIELDS = {
            // field index, isFixed, minRecords, checkBoxFlags
            { DbHelper.PRESCRIPTION_COLUMN_INDEX_CATEGORY, 1, 1, 0 },
            { DbHelper.PRESCRIPTION_COLUMN_INDEX_COMPOSITION, 0, 1, 0 },
            { DbHelper.PRESCRIPTION_COLUMN_INDEX_DECOCTION, 1, 1, 0 },
            { DbHelper.PRESCRIPTION_COLUMN_INDEX_METHOD_OF_TAKING_MEDICINE, 1, 1, 0 },
            { DbHelper.PRESCRIPTION_COLUMN_INDEX_REFERENCE_MATERIAL, 0, 1, 0 }
        };

        final int[][] INTEGERS = isMedicine ? INTEGERS_FOR_MEDICINE_FIELDS : INTEGERS_FOR_PRESCRIPTION_FIELDS;

        final String[] SPINNER_NAMES = DETAIL_CONTENT_SPINNER_NAMES;

        String[][][] spinnerItemsForMedicineFields = {
            // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
            { null, null, null, dbHelper.queryMedicineCategories(UNKNOWN_STRING), null, null },
            { null, processingMethod, levelWords, dbHelper.queryAttributeNames(R.string.attr_table_prefix_medicine_nature, HINT_PLEASE_SELECT), null, null },
            { null, processingMethod, levelWords, /*null*/dbHelper.queryAttributeNames(R.string.attr_table_prefix_medicine_taste, HINT_PLEASE_SELECT/*ITEM_READ_ONLY*/), null, null },
            { null, null, null, /*null*/dbHelper.queryAttributeNames(R.string.attr_table_prefix_channel_tropism, HINT_PLEASE_SELECT/*ITEM_READ_ONLY*/), null, null },
            { null, null, null/*dbHelper.queryAttributeNames(R.string.attr_table_prefix_action_verb, SPACE)*/, null/*dbHelper.queryAttributeNames(R.string.attr_table_prefix_life_fundamental, ITEM_READ_ONLY)*/, null, null },
            { null, null, null, null/*dbHelper.queryAttributeNames(R.string.attr_table_prefix_motion_form, hintPleaseSelect)*/, null, null },
            { null, null, null, null/*dbHelper.queryAttributeNames(R.string.attr_table_prefix_medicine_effect, hintPleaseSelectOrCustomize)*/, null, null },
            { null, null, null, null/*dbHelper.queryAttributeNames(R.string.attr_table_prefix_medicine_action_and_indication, hintPleaseSelectOrCustomize)*/, null, null },
            { null, null, null, /*null*/referenceMaterialNames, null, null }
        };

        String[][][] spinnerItemsForPrescriptionFields = {
            // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
            { null, null, null, dbHelper.queryPrescriptionCategories(UNKNOWN_STRING), null, null },
            { dbHelper.queryAttributeNames(R.string.attr_table_prefix_medicine_role, SPACE), dbHelper.queryAttributeNames(R.string.attr_table_prefix_medicine_unit, null), processingMethod, null, null, null },
            { null, null, null, dbHelper.queryAttributeNames(R.string.attr_table_prefix_dosage_form, HINT_PLEASE_SELECT), null, null },
            { null, null, null, dbHelper.queryAttributeNames(R.string.attr_table_prefix_method_of_taking_medicine, getString(R.string.customized)), null, null },
            { null, null, null, /*null*/referenceMaterialNames, null, null }
        };

        String[][][] spinnerItems = isMedicine ? spinnerItemsForMedicineFields : spinnerItemsForPrescriptionFields;

        String[] EDIT_TEXT_NAMES = DETAIL_CONTENT_EDIT_TEXT_NAMES;

        final String[][] DEFAULT_EDIT_TEXT_VALUES_FOR_MEDICINE_FIELDS = {
            // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
            { null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null },
            { null, null, null, null/*ITEM_READ_ONLY*/, null, null, null },
            { null, null, null, null/*ITEM_READ_ONLY*/, null, null, null },
            { null, null, null, /*null*/ITEM_READ_ONLY, null, null, null },
            { null, null, null, null, null, null, null },
            { null, null, null, null, null, null, HINT_INPUT_EXTRA_CONTENTS },
            { null, null, null, null, null, null, HINT_INPUT_EXTRA_CONTENTS },
            { null, null, null, null, null, null, REFERENCE_MATERIAL_CONTENTS_EXAMPLE }
        };

        final String[][] DEFAULT_EDIT_TEXT_VALUES_FOR_PRESCRIPTION_FIELDS = {
            // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
            { null, null, null, null, null, null, null },
            { getString(R.string.quantity), null, null, null, null, null, getString(R.string.name) },
            { null, null, null, ITEM_READ_ONLY, null, null, HINT_INPUT_EXTRA_CONTENTS },
            { null, null, null, null, null, null, HINT_INPUT_EXTRA_CONTENTS },
            { null, null, null, null, null, null, REFERENCE_MATERIAL_CONTENTS_EXAMPLE }
        };

        final String[][] DEFAULT_EDIT_TEXT_VALUES = isMedicine ? DEFAULT_EDIT_TEXT_VALUES_FOR_MEDICINE_FIELDS : DEFAULT_EDIT_TEXT_VALUES_FOR_PRESCRIPTION_FIELDS;

        for (int i = 0; i < INTEGERS.length; ++i) {
            int fieldPos = INTEGERS[i][0];
            DetailContentTemplate one = array[fieldPos];

            one.isFixed = (0 != INTEGERS[i][1]);
            one.minRecords = INTEGERS[i][2];
            one.checkBoxFlags = INTEGERS[i][3];

            for (int j = 0; j < SPINNER_NAMES.length; ++j) {
                one.mapSpinnerItems.put(SPINNER_NAMES[j], spinnerItems[i][j]);
            }

            for (int j = 0; j < EDIT_TEXT_NAMES.length; ++j) {
                one.mapDefaultEditTextValues.put(EDIT_TEXT_NAMES[j], DEFAULT_EDIT_TEXT_VALUES[i][j]);
            }
        }
    }

    private void fillDetailContents(int opType, int positionAtFunctionalityList) {
        if (TcmCommon.OP_TYPE_VALUE_MEDICINE == opType)
            fillMedicineDetailContents();
        else if (TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == opType)
            fillPrescriptionDetailContents();
        else {
            if (MiscManagementActivity.LIST_ITEM_POS_REFERENCE_MATERIAL == positionAtFunctionalityList)
                fillReferenceMaterialDetailContents();
            else
                fillMiscItemDetailContents(opType, positionAtFunctionalityList);
        }
    }

    private void fillMedicineDetailContents() {
        HashMap<String, String> mapDetails = mDetailContentFromDb;
        final String[] FIELDS = DbHelper.MEDICINE_COLUMNS;
        String[] lifeFundamental = mDbHelper.queryAttributeNames(R.string.attr_table_prefix_life_fundamental, null);
        String referenceMaterial = mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_REFERENCE_MATERIAL]);
        String SPN_VALUE_SHORT = DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_SPN_VALUE];
        String ETX_VALUE_SHORT = DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_SHORT];

        DetailContentData[] natureData = parseFromDataseField(
            mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_NATURE]),
            DbHelper.MEDICINE_COLUMN_INDEX_NATURE, 1, false);

        DetailContentData[] tastesData = parseFromDataseField(
            mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_TASTES]),
            DbHelper.MEDICINE_COLUMN_INDEX_TASTES, 1, false);

        DetailContentData[] channelTropismData = parseFromDataseField(
            mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_CHANNEL_TROPISM]),
            DbHelper.MEDICINE_COLUMN_INDEX_CHANNEL_TROPISM, 1, false);

        DetailContentData[] lifeFundamentalData = parseFromDataseField(
            mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_LIFE_FUNDAMENTALS]),
            DbHelper.MEDICINE_COLUMN_INDEX_LIFE_FUNDAMENTALS, lifeFundamental.length, true);

        DetailContentData[] referenceMaterialData = parseFromDataseField(referenceMaterial,
            DbHelper.MEDICINE_COLUMN_INDEX_REFERENCE_MATERIAL, 1, false);

        for (int i = 0; i < lifeFundamentalData.length; ++i) {
            lifeFundamentalData[i].selectedSpinnerPositions.put(SPN_VALUE_SHORT, i + 1);
            lifeFundamentalData[i].editTextContents.put(ETX_VALUE_SHORT, lifeFundamental[i]);
        }

        DetailContentData[][] contentData = {
            // contentFieldIndex, checkBoxFlags,
            // selectedSpinnerPositions,
            // editTextContents

            {
                new DetailContentData(DbHelper.MEDICINE_COLUMN_INDEX_NAME, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_NAME]) } )
            },

            {
                new DetailContentData(DbHelper.MEDICINE_COLUMN_INDEX_ALIASES, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_ALIASES]) } )
            },

            {
                new DetailContentData(DbHelper.MEDICINE_COLUMN_INDEX_CATEGORY, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, Integer.parseInt(mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_CATEGORY])), 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, null } )
            },

            natureData,

            tastesData,

            channelTropismData,

            lifeFundamentalData,

            {
                new DetailContentData(DbHelper.MEDICINE_COLUMN_INDEX_MOTION_FORMS_OF_ACTION, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, null } )
            },

            {
                new DetailContentData(DbHelper.MEDICINE_COLUMN_INDEX_EFFECTS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_EFFECTS]) } )
            },

            {
                new DetailContentData(DbHelper.MEDICINE_COLUMN_INDEX_ACTIONS_AND_INDICATIONS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_ACTIONS_AND_INDICATIONS]) } )
            },

            {
                new DetailContentData(DbHelper.MEDICINE_COLUMN_INDEX_DETAILS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_DETAILS]) } )
            },

            {
                new DetailContentData(DbHelper.MEDICINE_COLUMN_INDEX_COMMON_PRESCRIPTIONS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_COMMON_PRESCRIPTIONS]) } )
            },

            {
                new DetailContentData(DbHelper.MEDICINE_COLUMN_INDEX_COMMON_PARTNERS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_COMMON_PARTNERS]) } )
            },

            {
                new DetailContentData(DbHelper.MEDICINE_COLUMN_INDEX_SIMILAR_MEDICINES, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_SIMILAR_MEDICINES]) } )
            },

            {
                new DetailContentData(DbHelper.MEDICINE_COLUMN_INDEX_DOSAGE_REFERENCE, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_DOSAGE_REFERENCE]) } )
            },

            {
                new DetailContentData(DbHelper.MEDICINE_COLUMN_INDEX_CONTRAINDICATIONS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_CONTRAINDICATIONS]) } )
            },

            referenceMaterialData,

            {
                new DetailContentData(DbHelper.MEDICINE_COLUMN_INDEX_REMARKS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.MEDICINE_COLUMN_INDEX_REMARKS]) } )
            }
        };

        fillDetailContentsWithPreparedData(TcmCommon.OP_TYPE_VALUE_MEDICINE, 0, contentData);
    }

    private void fillPrescriptionDetailContents() {
        HashMap<String, String> mapDetails = mDetailContentFromDb;
        final String[] FIELDS = DbHelper.PRESCRIPTION_COLUMNS;
        String composition = mapDetails.get(FIELDS[DbHelper.PRESCRIPTION_COLUMN_INDEX_COMPOSITION]);
        String decoction = mapDetails.get(FIELDS[DbHelper.PRESCRIPTION_COLUMN_INDEX_DECOCTION]);
        String methodOfTakingMedicine = mapDetails.get(FIELDS[DbHelper.PRESCRIPTION_COLUMN_INDEX_METHOD_OF_TAKING_MEDICINE]);
        String referenceMaterial = mapDetails.get(FIELDS[DbHelper.PRESCRIPTION_COLUMN_INDEX_REFERENCE_MATERIAL]);
        String ETX_VALUE_SHORT = DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_SHORT];

        DetailContentData[] compositionData = parseFromDataseField(composition,
            DbHelper.PRESCRIPTION_COLUMN_INDEX_COMPOSITION, 1, false);

        DetailContentData[] decoctionData = parseFromDataseField(decoction,
            DbHelper.PRESCRIPTION_COLUMN_INDEX_DECOCTION, 1, true);

        DetailContentData[] methodOfTakingMedicineData = parseFromDataseField(methodOfTakingMedicine,
            DbHelper.PRESCRIPTION_COLUMN_INDEX_METHOD_OF_TAKING_MEDICINE, 1, true);

        DetailContentData[] referenceMaterialData = parseFromDataseField(referenceMaterial,
            DbHelper.PRESCRIPTION_COLUMN_INDEX_REFERENCE_MATERIAL, 1, false);

        decoctionData[0].editTextContents.put(ETX_VALUE_SHORT, getString(R.string.dosage_form));

        DetailContentData[][] contentData = {
            // contentFieldIndex, checkBoxFlags,
            // selectedSpinnerPositions,
            // editTextContents

            {
                new DetailContentData(DbHelper.PRESCRIPTION_COLUMN_INDEX_NAME, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.PRESCRIPTION_COLUMN_INDEX_NAME]) } )
            },

            {
                new DetailContentData(DbHelper.PRESCRIPTION_COLUMN_INDEX_ALIASES, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.PRESCRIPTION_COLUMN_INDEX_ALIASES]) } )
            },

            {
                new DetailContentData(DbHelper.PRESCRIPTION_COLUMN_INDEX_CATEGORY, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, Integer.parseInt(mapDetails.get(FIELDS[DbHelper.PRESCRIPTION_COLUMN_INDEX_CATEGORY])), 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, null } )
            },

            {
                new DetailContentData(DbHelper.PRESCRIPTION_COLUMN_INDEX_EFFECTS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.PRESCRIPTION_COLUMN_INDEX_EFFECTS]) } )
            },

            {
                new DetailContentData(DbHelper.PRESCRIPTION_COLUMN_INDEX_ACTIONS_AND_INDICATIONS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.PRESCRIPTION_COLUMN_INDEX_ACTIONS_AND_INDICATIONS]) } )
            },

            compositionData,

            decoctionData,

            methodOfTakingMedicineData,

            {
                new DetailContentData(DbHelper.PRESCRIPTION_COLUMN_INDEX_CONTRAINDICATIONS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.PRESCRIPTION_COLUMN_INDEX_CONTRAINDICATIONS]) } )
            },

            {
                new DetailContentData(DbHelper.PRESCRIPTION_COLUMN_INDEX_RELATIVE_PRESCRIPTIONS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.PRESCRIPTION_COLUMN_INDEX_RELATIVE_PRESCRIPTIONS]) } )
            },

            referenceMaterialData,

            {
                new DetailContentData(DbHelper.PRESCRIPTION_COLUMN_INDEX_REMARKS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.PRESCRIPTION_COLUMN_INDEX_REMARKS]) } )
            }
        };

        fillDetailContentsWithPreparedData(TcmCommon.OP_TYPE_VALUE_PRESCRIPTION, 0, contentData);
    }

    private void fillReferenceMaterialDetailContents() {
        HashMap<String, String> mapDetails = mDetailContentFromDb;
        final String[] FIELDS = DbHelper.REFERENCE_MATERIAL_COLUMNS;

        DetailContentData[][] contentData = {
            // contentFieldIndex, checkBoxFlags,
            // selectedSpinnerPositions,
            // editTextContents

            {
                new DetailContentData(DbHelper.REFERENCE_MATERIAL_COLUMN_INDEX_NAME, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.REFERENCE_MATERIAL_COLUMN_INDEX_NAME]) } )
            },

            {
                new DetailContentData(DbHelper.REFERENCE_MATERIAL_COLUMN_INDEX_VERSION, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.REFERENCE_MATERIAL_COLUMN_INDEX_VERSION]) } )
            },

            {
                new DetailContentData(DbHelper.REFERENCE_MATERIAL_COLUMN_INDEX_ORIGINAL_AUTHORS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.REFERENCE_MATERIAL_COLUMN_INDEX_ORIGINAL_AUTHORS]) } )
            },

            {
                new DetailContentData(DbHelper.REFERENCE_MATERIAL_COLUMN_INDEX_EDITORS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.REFERENCE_MATERIAL_COLUMN_INDEX_EDITORS]) } )
            },

            {
                new DetailContentData(DbHelper.REFERENCE_MATERIAL_COLUMN_INDEX_ISSUING_SOURCE, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.REFERENCE_MATERIAL_COLUMN_INDEX_ISSUING_SOURCE]) } )
            },

            {
                new DetailContentData(DbHelper.REFERENCE_MATERIAL_COLUMN_INDEX_ISSUING_DATE, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.REFERENCE_MATERIAL_COLUMN_INDEX_ISSUING_DATE]) } )
            },

            {
                new DetailContentData(DbHelper.REFERENCE_MATERIAL_COLUMN_INDEX_REMARKS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.REFERENCE_MATERIAL_COLUMN_INDEX_REMARKS]) } )
            }
        };

        fillDetailContentsWithPreparedData(TcmCommon.OP_TYPE_VALUE_MISC_MANAGEMENT, MiscManagementActivity.LIST_ITEM_POS_REFERENCE_MATERIAL, contentData);
    }

    private void fillMiscItemDetailContents(int opType, int positionAtFunctionalityList) {
        HashMap<String, String> mapDetails = mDetailContentFromDb;
        final String[] FIELDS = DbHelper.GENERAL_MISC_ITEM_COLUMNS;
        String subCategories = (MiscManagementActivity.LIST_ITEM_POS_PRESCRIPTION_CATEGORY == positionAtFunctionalityList)
            ? mapDetails.get(FIELDS[DbHelper.GENERAL_MISC_ITEM_COLUMN_INDEX_SUB_CATEGORIES])
            : null;

        DetailContentData[][] contentData = {
            // contentFieldIndex, checkBoxFlags,
            // selectedSpinnerPositions,
            // editTextContents

            {
                new DetailContentData(DbHelper.GENERAL_MISC_ITEM_COLUMN_INDEX_NAME, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.GENERAL_MISC_ITEM_COLUMN_INDEX_NAME]) } )
            },

            {
                new DetailContentData(DbHelper.GENERAL_MISC_ITEM_COLUMN_INDEX_SUB_CATEGORIES, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, subCategories } )
            },

            {
                new DetailContentData(DbHelper.GENERAL_MISC_ITEM_COLUMN_INDEX_REMARKS, 0,
                    // spnKey, spnValuePrefix_1, spnValuePrefix_2, spnValue, spnValueSuffix_1, spnValueSuffix_2
                    new int[]{ 0, 0, 0, 0, 0, 0 },
                    // etxKey, etxValuePrefix_1, etxValuePrefix_2, etxValueShort, etxValueSuffix_1, etxValueSuffix_2, etxValueLong
                    new String[] { null, null, null, null, null, null, mapDetails.get(FIELDS[DbHelper.GENERAL_MISC_ITEM_COLUMN_INDEX_REMARKS]) } )
            }
        };

        fillDetailContentsWithPreparedData(TcmCommon.OP_TYPE_VALUE_MISC_MANAGEMENT, positionAtFunctionalityList, contentData);
    }

    private void fillDetailContentsWithPreparedData(int opType, int positionAtFunctionalityList, DetailContentData[][] contentData) {
        int[][] detailItemResIds = getDetailFieldResourceIds(opType, positionAtFunctionalityList);
        final String[] CONTENT_FIELDS = DbHelper.getTableColumnsList(opType, positionAtFunctionalityList);

        for (int i = 0; i < CONTENT_FIELDS.length; ++i) {
            if (detailFieldIsNotUsed(opType, positionAtFunctionalityList, i))
                continue;

            ArrayList<DetailContentData> contentsList = new ArrayList<DetailContentData>();
            DetailContentAdapter contentsAdapter = new DetailContentAdapter(this, contentsList);
            ListView lsvContents = (ListView) findViewById(detailItemResIds[i][2]);

            for (int j = 0; j < contentData[i].length; ++j) {
                contentsList.add(contentData[i][j]);
                for (int k = 0; k < DETAIL_CONTENT_EDIT_TEXT_NAMES.length; ++k) {
                    String editTextName = DETAIL_CONTENT_EDIT_TEXT_NAMES[k];
                    String editTextValue = contentData[i][j].editTextContents.get(editTextName);

                    if (null == editTextValue)
                        continue;

                    Log.v(TAG, "fillDetailContentsWithPreparedData(): "
                        + CONTENT_FIELDS[i] + "[" + j + "]: " + editTextName
                        + ": " + editTextValue);
                }
            }

            lsvContents.setAdapter(contentsAdapter);
        }
    }

    private class DetailTitleData {
        public final int contentFieldIndex;
        public final String name;

        public DetailTitleData(int contentFieldIndex, String name) {
            this.contentFieldIndex = contentFieldIndex;
            this.name = name;
        }
    }

    private class DetailTitleAdapter extends BaseAdapter {

        private final Context mContext;
        private final List<DetailTitleData> mItemList;
        private final LayoutInflater mInflater;
        private boolean mContinueToAddNewItem;

        final DialogInterface.OnClickListener notifyToStopAddingItem = new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mContinueToAddNewItem = false;
            }
        };

        public DetailTitleAdapter(Context context, List<DetailTitleData> itemList) {
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.details_item_title, parent, false);
                holder.name = (TextView) convertView.findViewById(R.id.txv_details_title);
                holder.iconAdd = (ImageView) convertView.findViewById(R.id.imgv_add_details_icon);
                holder.iconDelete = (ImageView) convertView.findViewById(R.id.imgv_delete_details_icon);
                holder.btnConfirm = (Button) convertView.findViewById(R.id.btn_confirm_details_change);
                holder.btnCancel = (Button) convertView.findViewById(R.id.btn_cancel_details_change);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            final DetailTitleData item = mItemList.get(position);

            holder.name.setText(item.name);
            com.android_assistant.TextView.setDefaultTextShadow(holder.name);

            if (!mDetailContentTemplatesArray[item.contentFieldIndex].isFixed) {
                final int FIELD_INDEX = item.contentFieldIndex;
                final String[] PAGE_ITEMS = getPageItemNames(false);

                holder.iconAdd.setVisibility(mEditable ? TextView.VISIBLE : TextView.GONE);
                holder.iconAdd.setClickable(mEditable);
                holder.iconAdd.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Hint.alert(DetailContentActivity.this, getString(R.string.add_content_item), "要为[" + PAGE_ITEMS[FIELD_INDEX] + "]新增条目吗？",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //addMultiContentItems(); // will result in crash
                                    addOneContentItem();
                                }
                            }, null);
                    }
                });

                holder.iconDelete.setVisibility(mEditable ? TextView.VISIBLE : TextView.GONE);
                holder.iconDelete.setClickable(mEditable);
                holder.iconDelete.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Hint.alert(mContext, getString(R.string.clear_content_items), "要清空[" + PAGE_ITEMS[FIELD_INDEX] + "]所有的条目吗？",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    clearContentItems(false);
                                }
                            }, null);
                    }
                });

                // Uncomment them if you want to see how title bars look with them.
                /*holder.btnConfirm.setVisibility(TextView.VISIBLE);
                holder.btnCancel.setVisibility(TextView.VISIBLE);*/
            }

            return convertView;
        }

        private boolean addOneContentItem() {
            final int FIELD_INDEX = mItemList.get(0).contentFieldIndex;
            int[][] detailFieldResIds = getDetailFieldResourceIds(mOpType, mPositionAtFunctionalityList);
            ListView lsvContents = (ListView) findViewById(detailFieldResIds[FIELD_INDEX][2]);
            DetailContentAdapter contentsAdapter = (DetailContentAdapter) lsvContents.getAdapter();
            List<DetailContentData> contentItemList = contentsAdapter.getItemList();

            if (!contentItemList.add(new DetailContentData(FIELD_INDEX)))
                return false;

            DetailContentData contentData = contentItemList.get(contentItemList.size() - 1);

            contentData.checkBoxFlags |= CHKBOX_CLICKABLE;
            contentData.spinnersEnabled = true;
            contentData.editTextsEnabled = true;

            contentsAdapter.notifyDataSetChanged();

            return true;
        }

        /* TODO: This function will cause an exception:
         *
         * java.lang.IllegalStateException: The specified child already has a parent.
         * You must call removeView() on the child's parent first.
         *
         * The solution has not been found out yet.
         */
        private void addMultiContentItems() {
            final String[] PAGE_ITEMS = getPageItemNames(false);
            final int FIELD_INDEX = mItemList.get(0).contentFieldIndex;
            int[][] detailFieldResIds = getDetailFieldResourceIds(mOpType, mPositionAtFunctionalityList);
            ListView lsvContents = (ListView) findViewById(detailFieldResIds[FIELD_INDEX][2]);
            final DetailContentAdapter contentsAdapter = (DetailContentAdapter) lsvContents.getAdapter();
            final List<DetailContentData> contentItemList = contentsAdapter.getItemList();

            final String[] FIELDS = DbHelper.getTableColumnsList(mOpType, mPositionAtFunctionalityList);
            final String CURRENT_FIELD_NAME = FIELDS[FIELD_INDEX];
            final DetailContentTemplate TEMPLATE = mDetailContentTemplatesMap.get(CURRENT_FIELD_NAME);
            View dialogView = getLayoutInflater().inflate(R.layout.details_item_content_dialog, null);
            final CheckBox checkBox = (CheckBox)dialogView.findViewById(R.id.dlg_chkbox_details_item);
            final Spinner[] spinners = {
                (Spinner) dialogView.findViewById(R.id.dlg_spn_details_key),
                (Spinner) dialogView.findViewById(R.id.dlg_spn_details_value_prefix_1),
                (Spinner) dialogView.findViewById(R.id.dlg_spn_details_value_prefix_2),
                (Spinner) dialogView.findViewById(R.id.dlg_spn_details_value),
                (Spinner) dialogView.findViewById(R.id.dlg_spn_details_value_suffix_1),
                (Spinner) dialogView.findViewById(R.id.dlg_spn_details_value_suffix_2)
            };
            final EditText[] editTexts = {
                (EditText) dialogView.findViewById(R.id.dlg_etx_details_key),
                (EditText) dialogView.findViewById(R.id.dlg_etx_details_value_prefix_1),
                (EditText) dialogView.findViewById(R.id.dlg_etx_details_value_prefix_2),
                (EditText) dialogView.findViewById(R.id.dlg_etx_details_value_short_texts),
                (EditText) dialogView.findViewById(R.id.dlg_etx_details_value_suffix_1),
                (EditText) dialogView.findViewById(R.id.dlg_etx_details_value_suffix_2),
                (EditText) dialogView.findViewById(R.id.dlg_etx_details_value_long_texts)
            };

            checkBox.setVisibility((0 != (TEMPLATE.checkBoxFlags & CHKBOX_VISIBLE))
                ? TextView.VISIBLE : TextView.GONE);

            for (int i = 0; i < spinners.length; ++i) {
                final String spinnerName = DETAIL_CONTENT_SPINNER_NAMES[i];
                String[] spinnerItems = TEMPLATE.mapSpinnerItems.get(spinnerName);

                if (null == spinnerItems) {
                    spinners[i].setVisibility(TextView.GONE);
                    continue;
                }

                spinners[i].setAdapter(new ArrayAdapter<String>(mContext,
                    R.drawable.default_spinner_text, spinnerItems));
            }

            for (int i = 0; i < editTexts.length; ++i) {
                String editTextName = DETAIL_CONTENT_EDIT_TEXT_NAMES[i];
                String templateTextValue = TEMPLATE.mapDefaultEditTextValues.get(editTextName);

                if (null == templateTextValue)
                    editTexts[i].setVisibility(TextView.GONE);

                com.android_assistant.TextView.setDefaultTextShadow(editTexts[i]);
            }

            mContinueToAddNewItem = true;

            while (mContinueToAddNewItem) {

                new AlertDialog.Builder(DetailContentActivity.this)
                    .setTitle(PAGE_ITEMS[FIELD_INDEX])
                    .setView(dialogView)
                    .setPositiveButton(R.string.add_new,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                contentItemList.add(new DetailContentData(FIELD_INDEX));
                                DetailContentData contentData = contentItemList.get(contentItemList.size() - 1);
                                contentData.checkBoxFlags |= CHKBOX_CLICKABLE;
                                contentData.spinnersEnabled = true;
                                contentData.editTextsEnabled = true;

                                if (checkBox.isChecked())
                                    contentData.checkBoxFlags |= CHKBOX_SELECTED;
                                else
                                    contentData.checkBoxFlags &= (~CHKBOX_SELECTED);

                                for (int i = 0; i < spinners.length; ++i) {
                                    String spinnerName = DETAIL_CONTENT_SPINNER_NAMES[i];
                                    String[] spinnerItems = TEMPLATE.mapSpinnerItems.get(spinnerName);

                                    if (null == spinnerItems) {
                                        contentData.selectedSpinnerPositions.put(spinnerName, 0);
                                        continue;
                                    }

                                    contentData.selectedSpinnerPositions.put(spinnerName, spinners[i].getSelectedItemPosition());
                                }

                                for (int i = 0; i < editTexts.length; ++i) {
                                    String editTextName = DETAIL_CONTENT_EDIT_TEXT_NAMES[i];
                                    String templateTextValue = TEMPLATE.mapDefaultEditTextValues.get(editTextName);

                                    if (null == templateTextValue)
                                        continue;

                                    contentData.editTextContents.put(editTextName, editTexts[i].getText().toString());
                                }

                                contentsAdapter.notifyDataSetChanged();

                                Hint.alert(DetailContentActivity.this, R.string.asking_whether_to_continue_or_not, R.string.continue_or_stop_guide, null, notifyToStopAddingItem);
                                mContinueToAddNewItem = false;
                            }
                        })
                    .setNegativeButton(R.string.cancal, notifyToStopAddingItem)
                    .show();
            } // while (mContinueToAddNewItem)
        }

        private void clearContentItems(boolean keepsOneAsPlaceholder) {
            final int FIELD_INDEX = mItemList.get(0).contentFieldIndex;
            int[][] detailFieldResIds = getDetailFieldResourceIds(mOpType, mPositionAtFunctionalityList);
            ListView lsvContents = (ListView) findViewById(detailFieldResIds[FIELD_INDEX][2]);
            DetailContentAdapter contentsAdapter = (DetailContentAdapter) lsvContents.getAdapter();
            List<DetailContentData> contentItemList = contentsAdapter.getItemList();

            contentItemList.clear();

            if (keepsOneAsPlaceholder) {
                contentItemList.add(new DetailContentData(FIELD_INDEX)); // uses a new uninitialized one as placeholder

                DetailContentData contentData = contentItemList.get(contentItemList.size() - 1);

                contentData.checkBoxFlags |= CHKBOX_CLICKABLE;
                contentData.spinnersEnabled = true;
                contentData.editTextsEnabled = true;
            }

            contentsAdapter.notifyDataSetChanged();
        }

        private class ViewHolder {
            TextView name;
            ImageView iconAdd;
            ImageView iconDelete;
            Button btnConfirm;
            Button btnCancel;
        }
    }

    private class DetailContentData {
        public final int contentFieldIndex;
        public int checkBoxFlags;
        public HashMap<String, Integer> selectedSpinnerPositions;
        public HashMap<String, String> editTextContents;
        public boolean spinnersEnabled;
        public boolean editTextsEnabled;

        public DetailContentData(int contentFieldIndex) {
            this.contentFieldIndex = contentFieldIndex;
            selectedSpinnerPositions = new HashMap<String, Integer>();
            editTextContents = new HashMap<String, String>();
            spinnersEnabled = false;
            editTextsEnabled = false;
        }

        public DetailContentData(int contentFieldIndex, int checkBoxFlags,
            int[] selectedSpinnerPositions, String[] editTextContents) {
            this.contentFieldIndex = contentFieldIndex;
            this.checkBoxFlags = checkBoxFlags;
            this.spinnersEnabled = false;
            this.editTextsEnabled = false;
            this.selectedSpinnerPositions = new HashMap<String, Integer>();
            for (int i = 0; i < DETAIL_CONTENT_SPINNER_NAMES.length; ++i) {
                this.selectedSpinnerPositions.put(DETAIL_CONTENT_SPINNER_NAMES[i],
                    selectedSpinnerPositions[i]);
            }
            this.editTextContents = new HashMap<String, String>();
            for (int i = 0; i < DETAIL_CONTENT_EDIT_TEXT_NAMES.length; ++i) {
                this.editTextContents.put(DETAIL_CONTENT_EDIT_TEXT_NAMES[i],
                    editTextContents[i]);
            }
        }
    }

    private class DetailContentTemplate {
        public boolean isFixed;
        public int minRecords;
        public int checkBoxFlags;
        public HashMap<String, String[]> mapSpinnerItems;
        public HashMap<String, String> mapDefaultEditTextValues;

        public DetailContentTemplate() {
            isFixed = true;
            minRecords = 1;
            checkBoxFlags = 0;
            mapSpinnerItems = new HashMap<String, String[]>();
            mapDefaultEditTextValues = new HashMap<String, String>();
        }
    }

    private class DetailContentAdapter extends BaseAdapter {

        private final Context mContext;
        private final List<DetailContentData> mItemList;
        private final LayoutInflater mInflater;
        private int mEtxTouchPosition = 0;
        private boolean mTouchHappens = false;
        private final View.OnTouchListener mUpdateEditTextTouchPosition = new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean FIXED_RETURN_VALUE = false;
                EditText targetEditText = (EditText)v;

                if (!mEditable)
                    return FIXED_RETURN_VALUE;

                mTouchHappens = true;

                mEtxTouchPosition = (java.lang.Integer)targetEditText.getTag();

                for (int i = 0; i < TEXT_WATCHERS.length; ++i) {
                    TEXT_WATCHERS[i].updatePositionAtListView(mEtxTouchPosition);
                }
                Log.v(TAG, "mEtxTouchPosition: " + mEtxTouchPosition);

                return FIXED_RETURN_VALUE; // TODO: How about v.performClick() or true?
            }
        };

        private final SmarterTextWatcher[] TEXT_WATCHERS = {
            new SmarterTextWatcher(DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_KEY]),
            new SmarterTextWatcher(DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_PREFIX_1]),
            new SmarterTextWatcher(DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_PREFIX_2]),
            new SmarterTextWatcher(DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_SHORT]),
            new SmarterTextWatcher(DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_SUFFIX_1]),
            new SmarterTextWatcher(DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_SUFFIX_2]),
            new SmarterTextWatcher(DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_LONG])
        };

        public DetailContentAdapter(Context context, List<DetailContentData> itemList) {
            super();
            this.mItemList = itemList;
            this.mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mItemList.size();
        }

        public List<DetailContentData> getItemList() {
            return mItemList;
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
        public View getView(int position, View convertView,
                ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.details_item_content, parent, false);
                holder = createViewHolder(convertView, parent);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            setViewHolder(holder, position);

            return convertView;
        }

        private ViewHolder createViewHolder(View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.details_item_content, parent, false);

            ViewHolder holder = new ViewHolder();

            holder.checkBox = (CheckBox) convertView.findViewById(R.id.chkbox_details_item);
            holder.spnKey = (Spinner) convertView.findViewById(R.id.spn_details_key);
            holder.etxKey = (EditText) convertView.findViewById(R.id.etx_details_key);
            holder.spnValuePrefix_1 = (Spinner) convertView.findViewById(R.id.spn_details_value_prefix_1);
            holder.etxValuePrefix_1 = (EditText) convertView.findViewById(R.id.etx_details_value_prefix_1);
            holder.spnValuePrefix_2 = (Spinner) convertView.findViewById(R.id.spn_details_value_prefix_2);
            holder.etxValuePrefix_2 = (EditText) convertView.findViewById(R.id.etx_details_value_prefix_2);
            holder.spnValue = (Spinner) convertView.findViewById(R.id.spn_details_value);
            holder.etxValueShort = (EditText) convertView.findViewById(R.id.etx_details_value_short_texts);
            holder.spnValueSuffix_1 = (Spinner) convertView.findViewById(R.id.spn_details_value_suffix_1);
            holder.etxValueSuffix_1 = (EditText) convertView.findViewById(R.id.etx_details_value_suffix_1);
            holder.spnValueSuffix_2 = (Spinner) convertView.findViewById(R.id.spn_details_value_suffix_2);
            holder.etxValueSuffix_2 = (EditText) convertView.findViewById(R.id.etx_details_value_suffix_2);
            holder.etxValueLong = (EditText) convertView.findViewById(R.id.etx_details_value_long_texts);

            return holder;
        }

        private void checkAndRedirectToMedicineDetailsPage(String originalMedicineName,
            String[] idNamePairsFromDb) {

            if (null == originalMedicineName || 0 == originalMedicineName.length())
                return;

            if (null == idNamePairsFromDb) {
                Hint.alert(mContext, R.string.not_found, originalMedicineName);
                return;
            }

            String id = null;
            String name = null;
            boolean hasMultiRecords = (idNamePairsFromDb.length > 1);
            boolean nameMatchesPrecisely = false;
            StringBuilder hintContent = new StringBuilder();

            for (int i = 0; i < idNamePairsFromDb.length; ++i) {
                String[] valuesInPair = idNamePairsFromDb[i].split(":", -1);

                id = valuesInPair[0];
                name = valuesInPair[1];

                if (originalMedicineName.equals(name)) {
                    nameMatchesPrecisely = true;
                    break;
                }
            }

            if (hasMultiRecords && !nameMatchesPrecisely) {
                StringBuilder hintTitle = new StringBuilder();

                hintTitle.append(originalMedicineName).append(": ").append(getString(R.string.multi_records_found));
                hintContent.append(getString(R.string.please)).append(" ").append(getString(R.string.search_manually));
                Hint.alert(mContext, hintTitle.toString(), hintContent.toString());

                return;
            }

            mNextIntent.putExtra(TcmCommon.OP_TYPE_KEY, TcmCommon.OP_TYPE_VALUE_MEDICINE);
            mNextIntent.putExtra(TcmCommon.ID_KEY, id);
            mNextIntent.putExtra(TcmCommon.NAME_KEY, name);
            mNextIntent.putExtra(TcmCommon.FUNC_LIST_POS_KEY, 0);
            hintContent.append(originalMedicineName).append(": ").append(getString(R.string.redirecting)).append(getString(R.string.please_wait));
            Hint.longToast(mContext, hintContent);
            startActivity(mNextIntent);
        }

        private void setViewHolder(ViewHolder holder, int position) {

            final DetailContentData item = mItemList.get(position);

            if (null == item)
                return;

            //holder.contentFieldIndex = item.contentFieldIndex;

            final String[] FIELDS = DbHelper.getTableColumnsList(mOpType, mPositionAtFunctionalityList);
            final String CURRENT_FIELD_NAME = FIELDS[item.contentFieldIndex];
            final String COMPOSITION_FIELD = DbHelper.PRESCRIPTION_COLUMNS[DbHelper.PRESCRIPTION_COLUMN_INDEX_COMPOSITION];
            boolean isCompositionField = COMPOSITION_FIELD.equals(CURRENT_FIELD_NAME);
            DetailContentTemplate template = mDetailContentTemplatesMap.get(CURRENT_FIELD_NAME);

            Spinner[] holderSpinners = {
                holder.spnKey,
                holder.spnValuePrefix_1,
                holder.spnValuePrefix_2,
                holder.spnValue,
                holder.spnValueSuffix_1,
                holder.spnValueSuffix_2
            };

            EditText[] holderEditTexts = {
                holder.etxKey,
                holder.etxValuePrefix_1,
                holder.etxValuePrefix_2,
                holder.etxValueShort,
                holder.etxValueSuffix_1,
                holder.etxValueSuffix_2,
                holder.etxValueLong
            };

            boolean hasSimpleCharTextOnly = detailFieldIsSimpleCharText(mOpType, mPositionAtFunctionalityList, item.contentFieldIndex);

            /*
             * the public part
             */
            for (int i = 0; i < holderEditTexts.length; ++i) {
                String editTextName = DETAIL_CONTENT_EDIT_TEXT_NAMES[i];
                String templateTextValue = template.mapDefaultEditTextValues.get(editTextName);

                if (null == templateTextValue && !hasSimpleCharTextOnly) {
                    holderEditTexts[i].setVisibility(TextView.GONE);
                    continue;
                }

                boolean isLongValueEditText = DETAIL_CONTENT_FIELD_NAMES[DETAIL_CONTENT_FIELD_ETX_VALUE_LONG].equals(editTextName);
                int textFlags = holderEditTexts[i].getPaint().getFlags();

                if (null != templateTextValue && templateTextValue.equals(ITEM_READ_ONLY))
                    holderEditTexts[i].setEnabled(false);
                holderEditTexts[i].setClickable(true);
                if (!mEditable
                    && TcmCommon.OP_TYPE_VALUE_PRESCRIPTION == mOpType
                    && isCompositionField
                    && isLongValueEditText) {

                    holderEditTexts[i].setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            EditText etxTouched = (EditText)v;
                            String medicineName = etxTouched.getText().toString();
                            String[] idNamePairs = mDbHelper.queryMedicineIdNamePairsByName(medicineName);

                            //Hint.alert(mContext, CURRENT_FIELD_NAME, medicineName);
                            checkAndRedirectToMedicineDetailsPage(medicineName, idNamePairs);
                        }
                    });
                    holderEditTexts[i].getPaint().setFlags(textFlags | Paint.UNDERLINE_TEXT_FLAG);
                }
                else {
                    holderEditTexts[i].setOnClickListener(null);
                    holderEditTexts[i].getPaint().setFlags(textFlags & (~Paint.UNDERLINE_TEXT_FLAG));
                }
                holderEditTexts[i].setFocusable(item.editTextsEnabled);
                holderEditTexts[i].setCursorVisible(item.editTextsEnabled);
                holderEditTexts[i].setFocusableInTouchMode(item.editTextsEnabled);
                holderEditTexts[i].setTag(position);
                holderEditTexts[i].setOnTouchListener(mUpdateEditTextTouchPosition);
                holderEditTexts[i].addTextChangedListener(TEXT_WATCHERS[i]);
                holderEditTexts[i].setSingleLine(!mIsMultiLineMode);
                /////////////////// begin: deals with the EditText focus problem ///////////////////
                // TODO: It does not work, why? Because of cursor position recovery due to the view re-painting?
                /*if (mIsMultiLineMode) {
                    if (mEtxTouchPosition == position) {
                        holderEditTexts[i].requestFocus();
                        holderEditTexts[i].setCursorVisible(item.editTextsEnabled);

                        final int START_POSITION = 1;
                        int currentTextLength = holderEditTexts[i].getText().toString().length();
                        final int FIXED_POSITION = START_POSITION; // currentTextLength
                        int expectedCursorPos = FIXED_POSITION; // acceptable but unwise

                        expectedCursorPos = TEXT_WATCHERS[i].getCursorPositionAfterChanged();

                        if (expectedCursorPos < 0)
                            expectedCursorPos = START_POSITION;
                        else if (expectedCursorPos > currentTextLength)
                            expectedCursorPos = currentTextLength;

                        holderEditTexts[i].setSelection(expectedCursorPos);
                    }
                    else {
                        holderEditTexts[i].clearFocus();
                    }
                }*/
                /////////////////// end: deals with the EditText focus problem ///////////////////

                com.android_assistant.TextView.setDefaultTextShadow(holderEditTexts[i]);

                if (mEditable)
                    continue; // NOTE: Calling setText() under edit mode may cause data chaos during TextWatcher.xxTextChanged() execution.

                String editTextValue = item.editTextContents.get(editTextName);

                if (null != editTextValue && editTextValue.length() > 0)
                    holderEditTexts[i].setText(editTextValue);
                else {
                    if (null != templateTextValue) {
                        if (templateTextValue.equals(ITEM_READ_ONLY))
                            holderEditTexts[i].setText(templateTextValue);
                        else
                            holderEditTexts[i].setHint(templateTextValue);
                    }
                }

                if (!hasSimpleCharTextOnly) {
                    Log.v(TAG, CURRENT_FIELD_NAME + "[" + position + "]: "
                        + "holderEditTexts[" + editTextName + "].setText(" + editTextValue + ")");
                }
            }

            /*
             *  case 1: for simple character text contents
             */
            if (hasSimpleCharTextOnly) {
                holder.checkBox.setVisibility(TextView.GONE);

                for (int i = 0; i < holderSpinners.length; ++i) {
                    holderSpinners[i].setVisibility(TextView.GONE);

                    // Hides all EditTexts excepts etxValueLong.
                    holderEditTexts[i].setVisibility(TextView.GONE);
                }

                return;
            }

            /*
             * case 2: for complicated contents
             */

            holder.checkBox.setVisibility((0 != (template.checkBoxFlags & CHKBOX_VISIBLE))
                ? TextView.VISIBLE : TextView.GONE);
            holder.checkBox.setClickable(0 != (item.checkBoxFlags & CHKBOX_CLICKABLE));
            holder.checkBox.setChecked(0 != (item.checkBoxFlags & CHKBOX_SELECTED));
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                        boolean isChecked) {
                    if (!buttonView.isPressed())
                        return;

                    if (isChecked)
                        item.checkBoxFlags |= CHKBOX_SELECTED;
                    else
                        item.checkBoxFlags &= (~CHKBOX_SELECTED);
                }

            });

            for (int i = 0; i < holderSpinners.length; ++i) {
                final String spinnerName = DETAIL_CONTENT_SPINNER_NAMES[i];
                String[] spinnerItems = template.mapSpinnerItems.get(spinnerName);

                if (null == spinnerItems) {
                    holderSpinners[i].setVisibility(TextView.GONE);
                    continue;
                }

                holderSpinners[i].setAdapter(new ArrayAdapter<String>(mContext,
                    R.drawable.default_spinner_text, spinnerItems));
                Integer selectedPosition = item.selectedSpinnerPositions.get(spinnerName);
                holderSpinners[i].setSelection((null != selectedPosition) ? selectedPosition : 0);
                holderSpinners[i].setEnabled(ITEM_READ_ONLY != spinnerItems[0] && item.spinnersEnabled);
                holderSpinners[i].setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapter, View view,
                            int position, long id) {
                        item.selectedSpinnerPositions.put(spinnerName, position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapter) {
                        ;
                    }
                });
            }
        }

        private class ViewHolder {
            //int contentFieldIndex;
            CheckBox checkBox;
            Spinner spnKey;
            EditText etxKey;
            Spinner spnValuePrefix_1;
            EditText etxValuePrefix_1;
            Spinner spnValuePrefix_2;
            EditText etxValuePrefix_2;
            Spinner spnValue;
            EditText etxValueShort;
            Spinner spnValueSuffix_1;
            EditText etxValueSuffix_1;
            Spinner spnValueSuffix_2;
            EditText etxValueSuffix_2;
            EditText etxValueLong;
        }

        private class SmarterTextWatcher implements TextWatcher {
            private int mPositionAtListView;
            private int mCursorPositionAfterChanged = 1;
            private final String mTargetEditTextName;

            public SmarterTextWatcher(String targetEditTextName) {
                mTargetEditTextName = targetEditTextName;
            }

            public void updatePositionAtListView(int position) {
                mPositionAtListView = position;
            }

            public int getPositionAtListView() {
                return mPositionAtListView;
            }

            public int getCursorPositionAfterChanged() {
                return mCursorPositionAfterChanged;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mEditable || !mTouchHappens)
                    return;

                DetailContentData dataItem = mItemList.get(mPositionAtListView);
                String[] fieldNames = DbHelper.getTableColumnsList(mOpType, mPositionAtFunctionalityList);

                dataItem.editTextContents.put(mTargetEditTextName, s.toString());
                Log.v(TAG, "afterTextChanged(): [" + fieldNames[dataItem.contentFieldIndex] + "]"
                    + "[" + mPositionAtListView + "][" + mTargetEditTextName + "]: " + s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                mCursorPositionAfterChanged = start + after;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                ;
            }
        }
    }
}
