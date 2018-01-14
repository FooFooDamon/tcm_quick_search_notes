package com.project.tcm_quick_search_notes;

public class TcmCommon {
	
	public static final String OP_TYPE_KEY = "op_type";
	public static final String CONDITION_KEY = "query_condition";
    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String MISC_ITEM_POS_KEY = "misc_item_position";
	
	public static final int OP_TYPE_VALUE_MEDICINE = 0;
    public static final int OP_TYPE_VALUE_PRESCRIPTION = 1;
    public static final int OP_TYPE_VALUE_MISC_MANAGEMENT = 2;
    
    public static final int CONDITION_BY_ID = 0;
    public static final int CONDITION_BY_NAME = 1;

    public static final String FIELD_DELIM = "\b";
    public static final String ITEM_DELIM = "\f";
}
