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
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import com.android_assistant.Hint;

public class MiscManagementActivity extends Activity
    implements OnItemClickListener {

    public static final int LIST_ITEM_POS_MEDICINE_CATEGORY = 0;
    public static final int LIST_ITEM_POS_PRESCRIPTION_CATEGORY = 1;
    public static final int LIST_ITEM_POS_LEVEL_WORD = 2;
    public static final int LIST_ITEM_POS_PROCESSING_METHOD = 3;
    public static final int LIST_ITEM_POS_UNIT = 4;
    public static final int LIST_ITEM_POS_DOSAGE_FORM = 5;
    public static final int LIST_ITEM_POS_METHOD_OF_TAKING_MEDICINE = 6;
    public static final int LIST_ITEM_POS_REFERENCE_MATERIAL = 7;
    public static final int LIST_ITEM_POS_MEDICINE_NATURE = 8;
    public static final int LIST_ITME_POS_MEDICINE_TASTE = 9;
    public static final int LIST_ITEM_POS_CHANNEL_TROPISIM = 10;
    public static final int LIST_ITEM_POS_MEDICINE_ROLE = 11;
    public static final int LIST_ITEM_POS_LIFE_FUNDAMENTAL = 12;

    private Intent mIntentQueryEntry = null;

    public static String getItemNameByPosition(int positionAtMiscList) {
        if (positionAtMiscList < LIST_ITEM_POS_MEDICINE_CATEGORY || positionAtMiscList > LIST_ITEM_POS_LIFE_FUNDAMENTAL) {
            return "Unknown Misc Item, pos: " + String.valueOf(positionAtMiscList);
        }

        final String[] NAMES = {
            "药物种类",
            "方剂种类",
            "程度（修饰词）",
            "炮制方法",
            "单位",
            "剂型",
            "服法",
            "参考资料",
            "药性",
            "药味",
            "归经",
            "药物角色",
            "基础生命物质"
        };

        return NAMES[positionAtMiscList];
    }

    public static String getDbPrimaryIdNameByPosition(int positionAtMiscList) {
        if (positionAtMiscList < LIST_ITEM_POS_MEDICINE_CATEGORY || positionAtMiscList > LIST_ITEM_POS_LIFE_FUNDAMENTAL) {
            return "Unknown Misc Item, pos: " + String.valueOf(positionAtMiscList);
        }

        final String[] ID_NAMES = {
            "cid",
            "cid",
            "aid",
            "aid",
            "aid",
            "aid",
            "aid",
            "rid",
            "aid",
            "aid",
            "aid",
            "aid",
            "aid"
        };

        return ID_NAMES[positionAtMiscList];
    }

    public static String getTableNameByPosition(int positionAtMiscList) {
        if (positionAtMiscList < LIST_ITEM_POS_MEDICINE_CATEGORY || positionAtMiscList > LIST_ITEM_POS_LIFE_FUNDAMENTAL) {
            return "Unknown Misc Item, pos: " + String.valueOf(positionAtMiscList);
        }

        final String[] names = {
            "medicine_categories",
            "prescription_categories",
            "level_word_definitions",
            "processing_method_definitions",
            "medicine_unit_definitions",
            "dosage_form_definitions",
            "method_of_taking_medicine_definitions",
            "reference_material",
            "medicine_nature_definitions",
            "medicine_taste_definitions",
            "channel_tropism_definitions",
            "medicine_role_definitions",
            "life_fundamental_definitions"
        };

        return names[positionAtMiscList];
    }

    public static boolean isModificationRestricted(int positionAtMiscList) {
        if (LIST_ITEM_POS_MEDICINE_NATURE == positionAtMiscList
            || LIST_ITME_POS_MEDICINE_TASTE == positionAtMiscList
            || LIST_ITEM_POS_CHANNEL_TROPISIM == positionAtMiscList
            || LIST_ITEM_POS_MEDICINE_ROLE == positionAtMiscList
            || LIST_ITEM_POS_LIFE_FUNDAMENTAL == positionAtMiscList)
            return true;

        return false;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misc_management);
        getActionBar().setBackgroundDrawable(
            getResources().getDrawable(R.drawable.default_action_bar_style));

        initResources();

        LinearLayout pageLayout = (LinearLayout) findViewById(R.id.llayout_misc_page);

        pageLayout.setBackgroundColor(android.graphics.Color.parseColor("#88b27e50"));

        final Drawable ICON = getResources().getDrawable(R.drawable.ic_blue_arrow);
        final PageItem[] MISC_ITEMS = {
            new PageItem(ICON, getString(R.string.medicine_category)),
            new PageItem(ICON, getString(R.string.prescription_category)),
            new PageItem(ICON, getString(R.string.level_word)),
            new PageItem(ICON, getString(R.string.processing_method)),
            new PageItem(ICON, getString(R.string.medicine_unit)),
            new PageItem(ICON, getString(R.string.dosage_form)),
            new PageItem(ICON, getString(R.string.method_of_taking_medicine)),
            new PageItem(ICON, getString(R.string.reference_material)),
            new PageItem(ICON, getString(R.string.medicine_nature)),
            new PageItem(ICON, getString(R.string.medicine_taste)),
            new PageItem(ICON, getString(R.string.channel_tropism)),
            new PageItem(ICON, getString(R.string.medicine_role)),
            new PageItem(ICON, getString(R.string.life_fundamental))
        };
        List<PageItem> itemList = new ArrayList<PageItem>();

        for (PageItem item : MISC_ITEMS)
        {
            itemList.add(item);
        }

        PageItemAdapter adapter = new PageItemAdapter(this, itemList);
        ListView listView = (ListView) findViewById(R.id.lsv_all_in_one_group);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setBackground(getResources().getDrawable(R.drawable.background));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.misc_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (R.id.menu_misc_help == id)
            Hint.alert(this, R.string.help, R.string.help_info_for_misc_page);
        else
            ; // more things in future ...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        mIntentQueryEntry.putExtra(TcmCommon.OP_TYPE_KEY, TcmCommon.OP_TYPE_VALUE_MISC_MANAGEMENT);
        mIntentQueryEntry.putExtra(TcmCommon.FUNC_LIST_POS_KEY, pos);
        startActivity(mIntentQueryEntry);
    }

    private void initResources() {
        if (null == mIntentQueryEntry)
            mIntentQueryEntry = new Intent(this, QueryEntryActivity.class);
    }

    private class PageItem {
        public Drawable icon;
        public String name;

        public PageItem() {
        }

        public PageItem(Drawable icon, String name) {
            this.icon = icon;
            this.name = name;
        }
    }

    private class PageItemAdapter extends BaseAdapter {

        private final Context mContext;
        private final List<PageItem> mItemList;
        private final LayoutInflater mInflater;

        public PageItemAdapter(Context context, List<PageItem> itemList) {
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

            PageItem item = mItemList.get(position);

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
