
update prescription_items
set alias = '',
	effects = '',
	actions_and_indications = '',
	contraindications = '',
	relative_prescriptions = '',
	remarks = ''
where (effects is null or length(effects) = 0)
	and (actions_and_indications is null or length(actions_and_indications) = 0)
	and (alias is null or length(alias) = 0)
	and name = '十全大补汤';

