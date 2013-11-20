--
-- Normally don't want to truncate
-- filetypes
-- glossaries
-- users
-- tasktypes
-- taskstages
--

-- truncate shares
TRUNCATE TABLE  `shareinvitees`;


-- truncate files in use
TRUNCATE TABLE  `taskfiles`;
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
TRUNCATE TABLE  `shares`;
TRUNCATE TABLE  `tasks`;
