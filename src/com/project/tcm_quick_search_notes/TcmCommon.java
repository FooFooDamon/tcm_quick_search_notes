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
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.android_assistant.Hint;

public class TcmCommon {

    public static final String OP_TYPE_KEY = "op_type";

    public static final int OP_TYPE_VALUE_MEDICINE = 0;
    public static final int OP_TYPE_VALUE_PRESCRIPTION = 1;
    public static final int OP_TYPE_VALUE_MISC_MANAGEMENT = 2;

    public static final String CONDITION_KEY = "query_condition";
    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";

    public static final int CONDITION_BY_ID = 0;
    public static final int CONDITION_BY_NAME = 1;

    public static final String FUNC_LIST_POS_KEY = "position_at_functionality_list";

    public static final String INNER_FIELD_DELIM = "\b";
    public static final String LINE_DELIM = "\f";
    
    public static void upgradeDatabase(Context context, DbHelper businessDbHelper, boolean isNewDb) {
    	UpgradeManager upgradeManager = new UpgradeManager(context, businessDbHelper, null);
    	
    	DialogInterface.OnClickListener exitOperation = new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	System.exit(1);
            }
        };

        try {
            if (isNewDb) {
                /*
                 * manual upgrade
                 */
                businessDbHelper.upgradeV10111();
                businessDbHelper.upgradeV10112();
            }
            else {
                // automatic upgrade: triggers operations such as creating or altering tables.
                upgradeManager.getWritableDatabase();
                upgradeManager.close();
            }

            if (upgradeManager.hasException())
                Hint.alert(context, R.string.db_error, upgradeManager.getExceptionMessage(), exitOperation);
        } catch (Exception e) {
            e.printStackTrace();

            String eMsg = ((null != e.getCause()) ? e.getCause().getMessage() : e.getMessage());

            Hint.alert(context, R.string.db_error, eMsg, exitOperation);
        }
    }
}
