--
-- Table structure for table `configurations`
--

CREATE TABLE IF NOT EXISTS `etcsite_configurations` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `filesinuse`
--

CREATE TABLE IF NOT EXISTS `etcsite_filesinuse` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `file` varchar(100) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `file` (`file`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `filetypes`
--

CREATE TABLE IF NOT EXISTS `etcsite_filetypes` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Dumping data for table `filetypes`
--

INSERT INTO `etcsite_filetypes` (`id`, `name`, `created`) VALUES
(1, 'TAXON_DESCRIPTION', '2013-10-22 02:02:26'),
(2, 'MARKED_UP_TAXON_DESCRIPTION', '2013-11-19 23:55:51'),
(3, 'MATRIX', '2013-11-19 22:25:17'), 
(4, 'PLAIN_TEXT', '2013-11-19 22:25:17');

-- --------------------------------------------------------

--
-- Table structure for table `taxon_group`
--

CREATE TABLE IF NOT EXISTS `etcsite_taxon_group` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Dumping data for table `taxon_group`
--

INSERT INTO `etcsite_taxon_group` (`id`, `name`, `created`) VALUES
(1, 'Hymenoptera', '2013-10-22 01:07:24'),
(2, 'Porifera', '2013-10-22 01:07:24'),
(3, 'Algae', '2013-10-22 01:07:24'),
(4, 'Fossil', '2013-10-22 01:07:24'),
(5, 'Plant', '2013-10-22 01:07:24'),
(6, 'Gastropods', '2013-10-22 01:07:24'),
(7, 'Cnidaria', '2013-10-22 01:07:24'),
(8, 'Empty', '2013-10-22 01:07:24');

-- --------------------------------------------------------

--
-- Table structure for table `matrixgenerationconfigurations`
--

CREATE TABLE IF NOT EXISTS `etcsite_matrixgenerationconfigurations` (
  `configuration` bigint(20) unsigned DEFAULT NULL,
  `input` varchar(200) DEFAULT NULL,
  `output` varchar(200) DEFAULT NULL,
  `inheritvalues` tinyint(1) DEFAULT NULL,
  `generateabsentpresent` tinyint(1) DEFAULT NULL,
  KEY `configurations_matrixgenerationconfigurations_CON` (`configuration`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `pipelineconfigurations`
--

CREATE TABLE IF NOT EXISTS `etcsite_pipelineconfigurations` (
  `configuration` bigint(20) unsigned DEFAULT NULL,
  KEY `configurations_pipelineconfigurations_CON` (`configuration`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `pipelinestageconfigurations`
--

CREATE TABLE IF NOT EXISTS `etcsite_pipelinestageconfigurations` (
  `pipeline` bigint(20) unsigned DEFAULT NULL,
  `tasktype` bigint(20) unsigned DEFAULT NULL,
  `configuration` bigint(20) unsigned DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `configurations_pipelinestageconfigurations_CON` (`configuration`),
  KEY `tasktypes_pipelinestageconfigurations_CON` (`tasktype`),
  KEY `configurations_pipelinestageconfigurations_CON2` (`pipeline`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `semanticmarkupconfigurations`
--

CREATE TABLE IF NOT EXISTS `etcsite_semanticmarkupconfigurations` (
  `configuration` bigint(20) unsigned DEFAULT NULL,
  `input` varchar(200) DEFAULT NULL,
  `numberofinputfiles` int(11) DEFAULT NULL,
  `taxon_group` bigint(20) unsigned DEFAULT NULL,
  `use_empty_glossary` tinyint(1) DEFAULT NULL,
  `oto_uploadid` int(11) DEFAULT NULL,
  `oto_secret` varchar(100) DEFAULT NULL,
  `output` varchar(200) DEFAULT NULL,
  KEY `configurations_semanticmarkupconfigurations_CON` (`configuration`),
  KEY `taxon_group_semanticmarkupconfigurations_CON` (`taxon_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `shareinvitees`
--

CREATE TABLE IF NOT EXISTS `etcsite_shareinvitees` (
  `share` bigint(20) unsigned DEFAULT NULL,
  `inviteeuser` bigint(20) unsigned DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `share` (`share`,`inviteeuser`),
  KEY `shares_shareinvitees_CON` (`share`),
  KEY `users_shareinvitees_CON` (`inviteeuser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `shares`
--

CREATE TABLE IF NOT EXISTS `etcsite_shares` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `task` bigint(20) unsigned DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `tasks_shares_CON` (`task`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `tasks`
--

CREATE TABLE IF NOT EXISTS `etcsite_tasks` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `tasktype` bigint(20) unsigned DEFAULT NULL,
  `taskstage` bigint(20) unsigned DEFAULT NULL,
  `configuration` bigint(20) unsigned DEFAULT NULL,
  `user` bigint(20) unsigned DEFAULT NULL,
  `resumable` tinyint(1) DEFAULT NULL,
  `complete` tinyint(1) DEFAULT NULL,
  `completed` timestamp NULL DEFAULT NULL,
  `failed` tinyint(1) DEFAULT NULL,
  `failedtime` timestamp NULL DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `taskstages_tasks_CON` (`taskstage`),
  KEY `users_tasks_CON` (`user`),
  KEY `configurations_tasks_CON` (`configuration`),
  KEY `tasktypes_tasks_CON` (`tasktype`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `tasksfiles`
--

CREATE TABLE IF NOT EXISTS `etcsite_tasksfiles` (
  `fileinuse` bigint(20) unsigned NOT NULL,
  `task` bigint(20) unsigned NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `fileinuse` (`fileinuse`,`task`),
  KEY `filesinuse_tasksfiles_CON` (`task`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `taskstages`
--

CREATE TABLE IF NOT EXISTS `etcsite_taskstages` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `tasktype` bigint(20) unsigned DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `tasktypes_taskstages_CON` (`tasktype`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Dumping data for table `taskstages`
--

INSERT INTO `etcsite_taskstages` (`id`, `name`, `tasktype`, `created`) VALUES
(12, 'INPUT', 6, '2013-11-19 22:30:35'),
(13, 'PREPROCESS_TEXT', 6, '2013-11-19 22:30:35'),
(14, 'LEARN_TERMS', 6, '2013-11-19 22:30:35'),
(15, 'REVIEW_TERMS', 6, '2013-11-19 22:30:35'),
(16, 'PARSE_TEXT', 6, '2013-11-19 22:30:35'),
(17, 'OUTPUT', 6, '2013-11-19 22:30:35'),
(18, 'INPUT', 7, '2013-11-19 22:30:35'),
(19, 'PROCESS', 7, '2013-11-19 22:30:35'),
(20, 'OUTPUT', 7, '2013-11-19 22:30:35'),
(21, 'REVIEW', 7, '2013-11-19 22:30:35'),
(22, 'TO_ONTOLOGIES', 6, '2014-01-15 22:30:35'),
(23, 'HIERARCHY', 6, '2014-01-15 22:30:35'),
(24, 'ORDERS', 6, '2014-01-15 22:30:35'),
(25, 'INPUT', 8, '2014-01-15 22:30:35'),
(26, 'VIEW', 8, '2014-01-15 22:30:35'), 
(27, 'INPUT', 9, '2014-01-15 22:30:35'),
(28, 'ALIGN', 9, '2014-01-15 22:30:35'),
(29, 'ANALYZE', 9, '2014-01-15 22:30:35'),
(30, 'ANALYZE_COMPLETE', 9, '2014-01-15 22:30:35');

-- --------------------------------------------------------

--
-- Table structure for table `tasktypes`
--

CREATE TABLE IF NOT EXISTS `etcsite_tasktypes` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `inputfiletype` bigint(20) unsigned DEFAULT NULL,
  `inputtype` enum('file','directory') NOT NULL,
  `outputfiletype` bigint(20) unsigned DEFAULT NULL,
  `outputtype` enum('file','directory') NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `id` (`id`),
  KEY `inputfiletypes_tasktypes_CON` (`inputfiletype`),
  KEY `outputfiletypes_tasktypes_CON` (`outputfiletype`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Dumping data for table `tasktypes`
--

INSERT INTO `etcsite_tasktypes` (`id`, `name`, `inputfiletype`, `inputtype`, `outputfiletype`, `outputtype`, `created`) VALUES
(6, 'SEMANTIC_MARKUP', 1, 'directory', 2, 'directory', '2013-11-19 22:25:43'),
(7, 'MATRIX_GENERATION', 2, 'directory', 3, 'file', '2013-11-19 22:26:26'),
(8, 'TREE_GENERATION', 3, 'directory', 4, 'file', '2013-11-19 22:26:26'),
(9, 'TAXONOMY_COMPARISON', 4, 'file', 4, 'directory', '2013-11-19 22:26:26');

-- --------------------------------------------------------

--
-- Table structure for table `taxonomycomparisonconfigurations`
--

CREATE TABLE IF NOT EXISTS `etcsite_taxonomycomparisonconfigurations` (
  `configuration` bigint(20) unsigned DEFAULT NULL,
  `input` varchar(200) DEFAULT NULL,
  `output` varchar(200) DEFAULT NULL,
  KEY `configurations_taxonomycomparisonconfigurations_CON` (`configuration`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `treegenerationconfigurations`
--

CREATE TABLE IF NOT EXISTS `etcsite_treegenerationconfigurations` (
  `configuration` bigint(20) unsigned DEFAULT NULL,
  `input` varchar(200) DEFAULT NULL,
  KEY `configurations_treegenerationconfigurations_CON` (`configuration`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `visualizationconfigurations`
--

CREATE TABLE IF NOT EXISTS `etcsite_visualizationconfigurations` (
  `configuration` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`configuration`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `tasksoutputfiles`
--

CREATE TABLE IF NOT EXISTS `etcsite_tasksoutputfiles` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `file` varchar(100) NOT NULL,
  `task` bigint(20) unsigned DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `tasks_tasksoutputfiles_CON` (`task`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `etcsite_users` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `openidproviderid` varchar(50) DEFAULT NULL,
  `openidprovider` varchar(20) NOT NULL DEFAULT 'none',
  `password` varchar(100) DEFAULT NULL,
  `firstname` varchar(20) NOT NULL,
  `lastname` varchar(20) NOT NULL,
  `email` varchar(50) NOT NULL,
  `affiliation` varchar(50) NOT NULL,
  `bioportaluserid` varchar(50) NOT NULL,
  `bioportalapikey` varchar(50) NOT NULL,
  `otoaccountemail` varchar(50) NOT NULL,
  `otoauthenticationtoken` varchar(50) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `id_2` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1068 ;

--
-- Table structure for table `passwordresetrequests`
--

CREATE TABLE IF NOT EXISTS `etcsite_passwordresetrequests` (
  `user` bigint(20) unsigned DEFAULT NULL,
  `authenticationcode` varchar(20) NOT NULL,
  `requesttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `user` (`user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `etcsite_captchas` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `solution` varchar(20) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `matrixgenerationconfigurations`
--
ALTER TABLE `etcsite_tasksoutputfiles`
  ADD CONSTRAINT `tasks_tasksoutputfiles_CON` FOREIGN KEY (`task`) REFERENCES `etcsite_tasks` (`id`);


--
-- Constraints for table `matrixgenerationconfigurations`
--
ALTER TABLE `etcsite_matrixgenerationconfigurations`
  ADD CONSTRAINT `configurations_matrixgenerationconfigurations_CON` FOREIGN KEY (`configuration`) REFERENCES `etcsite_configurations` (`id`);

--
-- Constraints for table `pipelineconfigurations`
--
ALTER TABLE `etcsite_pipelineconfigurations`
  ADD CONSTRAINT `configurations_pipelineconfigurations_CON` FOREIGN KEY (`configuration`) REFERENCES `etcsite_configurations` (`id`);

--
-- Constraints for table `pipelinestageconfigurations`
--
ALTER TABLE `etcsite_pipelinestageconfigurations`
  ADD CONSTRAINT `configurations_pipelinestageconfigurations_CON` FOREIGN KEY (`configuration`) REFERENCES `etcsite_configurations` (`id`),
  ADD CONSTRAINT `configurations_pipelinestageconfigurations_CON2` FOREIGN KEY (`pipeline`) REFERENCES `etcsite_configurations` (`id`),
  ADD CONSTRAINT `tasktypes_pipelinestageconfigurations_CON` FOREIGN KEY (`tasktype`) REFERENCES `etcsite_tasktypes` (`id`);

--
-- Constraints for table `semanticmarkupconfigurations`
--
ALTER TABLE `etcsite_semanticmarkupconfigurations`
  ADD CONSTRAINT `configurations_semanticmarkupconfigurations_CON` FOREIGN KEY (`configuration`) REFERENCES `etcsite_configurations` (`id`),
  ADD CONSTRAINT `taxon_group_semanticmarkupconfigurations_CON` FOREIGN KEY (`taxon_group`) REFERENCES `etcsite_taxon_group` (`id`);

--
-- Constraints for table `shareinvitees`
--
ALTER TABLE `etcsite_shareinvitees`
  ADD CONSTRAINT `shares_shareinvitees_CON` FOREIGN KEY (`share`) REFERENCES `etcsite_shares` (`id`),
  ADD CONSTRAINT `users_shareinvitees_CON` FOREIGN KEY (`inviteeuser`) REFERENCES `etcsite_users` (`id`);

--
-- Constraints for table `shares`
--
ALTER TABLE `etcsite_shares`
  ADD CONSTRAINT `tasks_shares_CON` FOREIGN KEY (`task`) REFERENCES `etcsite_tasks` (`id`);

--
-- Constraints for table `tasks`
--
ALTER TABLE `etcsite_tasks`
  ADD CONSTRAINT `configurations_tasks_CON` FOREIGN KEY (`configuration`) REFERENCES `etcsite_configurations` (`id`),
  ADD CONSTRAINT `taskstages_tasks_CON` FOREIGN KEY (`taskstage`) REFERENCES `etcsite_taskstages` (`id`),
  ADD CONSTRAINT `tasktypes_tasks_CON` FOREIGN KEY (`tasktype`) REFERENCES `etcsite_tasktypes` (`id`),
  ADD CONSTRAINT `users_tasks_CON` FOREIGN KEY (`user`) REFERENCES `etcsite_users` (`id`);

--
-- Constraints for table `tasksfiles`
--
ALTER TABLE `etcsite_tasksfiles`
  ADD CONSTRAINT `filesinuse_tasksfiles_CON` FOREIGN KEY (`task`) REFERENCES `etcsite_tasks` (`id`);

--
-- Constraints for table `taskstages`
--
ALTER TABLE `etcsite_taskstages`
  ADD CONSTRAINT `tasktypes_taskstages_CON` FOREIGN KEY (`tasktype`) REFERENCES `etcsite_tasktypes` (`id`);

--
-- Constraints for table `tasktypes`
--
ALTER TABLE `etcsite_tasktypes`
  ADD CONSTRAINT `inputfiletypes_tasktypes_CON` FOREIGN KEY (`inputfiletype`) REFERENCES `etcsite_filetypes` (`id`),
  ADD CONSTRAINT `outputfiletypes_tasktypes_CON` FOREIGN KEY (`outputfiletype`) REFERENCES `etcsite_filetypes` (`id`);

--
-- Constraints for table `taxonomycomparisonconfigurations`
--
ALTER TABLE `etcsite_taxonomycomparisonconfigurations`
  ADD CONSTRAINT `configurations_taxonomycomparisonconfigurations_CON` FOREIGN KEY (`configuration`) REFERENCES `etcsite_configurations` (`id`);

--
-- Constraints for table `treegenerationconfigurations`
--
ALTER TABLE `etcsite_treegenerationconfigurations`
  ADD CONSTRAINT `configurations_treegenerationconfigurations_CON` FOREIGN KEY (`configuration`) REFERENCES `etcsite_configurations` (`id`);

--
-- Constraints for table `visualizationconfigurations`
--
ALTER TABLE `etcsite_visualizationconfigurations`
  ADD CONSTRAINT `configurations_visualizationconfigurations_CON` FOREIGN KEY (`configuration`) REFERENCES `etcsite_configurations` (`id`);