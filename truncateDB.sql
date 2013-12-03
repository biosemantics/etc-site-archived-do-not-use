--
-- Normally don't want to truncate
-- filetypes
-- glossaries
-- users
-- tasktypes
-- taskstages
--

delimiter //
create procedure drop_charaparser_table(IN pattern VARCHAR(100)) 
begin
	SELECT CONCAT('DROP TABLE IF EXISTS ',GROUP_CONCAT(CONCAT(database(),'.',table_name)),';')
	INTO @droplike
	FROM information_schema.tables
	WHERE table_schema = database()
	AND table_name LIKE pattern;
    if @droplike is not null then 
		PREPARE stmt FROM @droplike;
		EXECUTE stmt;
		DEALLOCATE PREPARE stmt;
    end if;
end;
//
delimiter ;
call drop_charaparser_table('%_allwords');
call drop_charaparser_table('%_discounted');
call drop_charaparser_table('%_heuristicnouns');
call drop_charaparser_table('%_isa');
call drop_charaparser_table('%_modifiers');
call drop_charaparser_table('%_permanentglossary');
call drop_charaparser_table('%_sentence');
call drop_charaparser_table('%_sentinfile');
call drop_charaparser_table('%_singularplural');
call drop_charaparser_table('%_substructure');
call drop_charaparser_table('%_syns');
call drop_charaparser_table('%_term_category');
call drop_charaparser_table('%_unknownwords');
call drop_charaparser_table('%_wordpos');
call drop_charaparser_table('%_wordroles');

drop procedure drop_charaparser_table;

DROP TABLE IF EXISTS `datasetprefixes`;

SET foreign_key_checks = 0;

-- truncate shares
TRUNCATE TABLE  `shareinvitees`;

-- truncate files in use
TRUNCATE TABLE  `tasksfiles`;
TRUNCATE TABLE  `filesinuse`;

-- truncate task configurations
TRUNCATE TABLE  `semanticmarkupconfigurations`;
TRUNCATE TABLE  `matrixgenerationconfigurations`;
TRUNCATE TABLE  `treegenerationconfigurations`;
TRUNCATE TABLE  `taxonomycomparisonconfigurations`;
TRUNCATE TABLE  `visualizationconfigurations`;
TRUNCATE TABLE  `pipelineconfigurations`;
TRUNCATE TABLE  `pipelinestageconfigurations`;
TRUNCATE TABLE  `configurations`;

-- truncate tasks
TRUNCATE TABLE `tasksoutputfiles`;
TRUNCATE TABLE  `shares`;
TRUNCATE TABLE  `tasks`;

SET foreign_key_checks = 1;