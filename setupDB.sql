--
-- Table structure for table `configurations`
--

CREATE TABLE IF NOT EXISTS `configurations` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `filesinuse`
--

CREATE TABLE IF NOT EXISTS `filesinuse` (
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

CREATE TABLE IF NOT EXISTS `filetypes` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Dumping data for table `filetypes`
--

INSERT INTO `filetypes` (`id`, `name`, `created`) VALUES
(1, 'TAXON_DESCRIPTION', '2013-10-22 02:02:26'),
(2, 'MARKED_UP_TAXON_DESCRIPTION', '2013-11-19 23:55:51'),
(3, 'MATRIX', '2013-11-19 22:25:17'), 
(4, 'PLAIN_TEXT', '2013-11-19 22:25:17');

-- --------------------------------------------------------

--
-- Table structure for table `glossaries`
--

CREATE TABLE IF NOT EXISTS `glossaries` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Dumping data for table `glossaries`
--

INSERT INTO `glossaries` (`id`, `name`, `created`) VALUES
(1, 'Hymenoptera', '2013-10-22 01:07:24'),
(2, 'Porifera', '2013-10-22 01:07:24'),
(3, 'Algea', '2013-10-22 01:07:24'),
(4, 'Fossil', '2013-10-22 01:07:24'),
(5, 'Plant', '2013-10-22 01:07:24');

-- --------------------------------------------------------

--
-- Table structure for table `matrixgenerationconfigurations`
--

CREATE TABLE IF NOT EXISTS `matrixgenerationconfigurations` (
  `configuration` bigint(20) unsigned DEFAULT NULL,
  `input` varchar(200) DEFAULT NULL,
  `output` varchar(200) DEFAULT NULL,
  KEY `configurations_matrixgenerationconfigurations_CON` (`configuration`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `pipelineconfigurations`
--

CREATE TABLE IF NOT EXISTS `pipelineconfigurations` (
  `configuration` bigint(20) unsigned DEFAULT NULL,
  KEY `configurations_pipelineconfigurations_CON` (`configuration`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `pipelinestageconfigurations`
--

CREATE TABLE IF NOT EXISTS `pipelinestageconfigurations` (
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

CREATE TABLE IF NOT EXISTS `semanticmarkupconfigurations` (
  `configuration` bigint(20) unsigned DEFAULT NULL,
  `input` varchar(200) DEFAULT NULL,
  `numberofinputfiles` int(11) DEFAULT NULL,
  `glossary` bigint(20) unsigned DEFAULT NULL,
  `oto_uploadid` int(11) DEFAULT NULL,
  `oto_secret` varchar(100) DEFAULT NULL,
  `output` varchar(200) DEFAULT NULL,
  KEY `configurations_semanticmarkupconfigurations_CON` (`configuration`),
  KEY `glossaries_semanticmarkupconfigurations_CON` (`glossary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `shareinvitees`
--

CREATE TABLE IF NOT EXISTS `shareinvitees` (
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

CREATE TABLE IF NOT EXISTS `shares` (
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

CREATE TABLE IF NOT EXISTS `tasks` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `tasktype` bigint(20) unsigned DEFAULT NULL,
  `taskstage` bigint(20) unsigned DEFAULT NULL,
  `configuration` bigint(20) unsigned DEFAULT NULL,
  `user` bigint(20) unsigned DEFAULT NULL,
  `resumable` tinyint(1) DEFAULT NULL,
  `complete` tinyint(1) DEFAULT NULL,
  `completed` timestamp NULL DEFAULT NULL,
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

CREATE TABLE IF NOT EXISTS `tasksfiles` (
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

CREATE TABLE IF NOT EXISTS `taskstages` (
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

INSERT INTO `taskstages` (`id`, `name`, `tasktype`, `created`) VALUES
(12, 'INPUT', 6, '2013-11-19 22:30:35'),
(13, 'PREPROCESS_TEXT', 6, '2013-11-19 22:30:35'),
(14, 'LEARN_TERMS', 6, '2013-11-19 22:30:35'),
(15, 'REVIEW_TERMS', 6, '2013-11-19 22:30:35'),
(16, 'PARSE_TEXT', 6, '2013-11-19 22:30:35'),
(17, 'OUTPUT', 6, '2013-11-19 22:30:35'),
(18, 'INPUT', 7, '2013-11-19 22:30:35'),
(19, 'PROCESS', 7, '2013-11-19 22:30:35'),
(20, 'OUTPUT', 7, '2013-11-19 22:30:35'),
(21, 'REVIEW', 7, '2013-11-19 22:30:35');

-- --------------------------------------------------------

--
-- Table structure for table `tasktypes`
--

CREATE TABLE IF NOT EXISTS `tasktypes` (
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

INSERT INTO `tasktypes` (`id`, `name`, `inputfiletype`, `inputtype`, `outputfiletype`, `outputtype`, `created`) VALUES
(6, 'SEMANTIC_MARKUP', 1, 'directory', 2, 'directory', '2013-11-19 22:25:43'),
(7, 'MATRIX_GENERATION', 2, 'directory', 3, 'file', '2013-11-19 22:26:26');

-- --------------------------------------------------------

--
-- Table structure for table `taxonomycomparisonconfigurations`
--

CREATE TABLE IF NOT EXISTS `taxonomycomparisonconfigurations` (
  `configuration` bigint(20) unsigned DEFAULT NULL,
  KEY `configurations_taxonomycomparisonconfigurations_CON` (`configuration`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `treegenerationconfigurations`
--

CREATE TABLE IF NOT EXISTS `treegenerationconfigurations` (
  `configuration` bigint(20) unsigned DEFAULT NULL,
  KEY `configurations_treegenerationconfigurations_CON` (`configuration`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `bioportaluserid` varchar(100) DEFAULT NULL,
  `bioportalapikey` varchar(100) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `password`, `bioportaluserid`, `bioportalapikey`, `created`) VALUES
(1, 'thomas', '', 'bpuid', 'bpak', '2013-10-22 01:13:18'),
(2, 'hong', '', 'b', 'c', '2013-10-31 04:06:14'),
(3, 'elvis', '', 'bpuid' 'bppw', '2013-11-09 00:59:11'), 
(4, 'james', '', 'b', 'c', '2013-10-31 04:06:14'),
(5, 'bertram', '', 'b', 'c', '2013-10-31 04:06:14'),
(6, 'bob', '', 'b', 'c', '2013-10-31 04:06:14'),
(7, 'shizhuo', '', 'b', 'c', '2013-10-31 04:06:14');

-- --------------------------------------------------------

--
-- Table structure for table `visualizationconfigurations`
--

CREATE TABLE IF NOT EXISTS `visualizationconfigurations` (
  `configuration` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`configuration`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `tasksoutputfiles`
--

CREATE TABLE IF NOT EXISTS `tasksoutputfiles` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `file` varchar(100) NOT NULL,
  `task` bigint(20) unsigned DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `tasks_tasksoutputfiles_CON` (`task`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `matrixgenerationconfigurations`
--
ALTER TABLE `tasksoutputfiles`
  ADD CONSTRAINT `tasks_tasksoutputfiles_CON` FOREIGN KEY (`task`) REFERENCES `tasks` (`id`);


--
-- Constraints for table `matrixgenerationconfigurations`
--
ALTER TABLE `matrixgenerationconfigurations`
  ADD CONSTRAINT `configurations_matrixgenerationconfigurations_CON` FOREIGN KEY (`configuration`) REFERENCES `configurations` (`id`);

--
-- Constraints for table `pipelineconfigurations`
--
ALTER TABLE `pipelineconfigurations`
  ADD CONSTRAINT `configurations_pipelineconfigurations_CON` FOREIGN KEY (`configuration`) REFERENCES `configurations` (`id`);

--
-- Constraints for table `pipelinestageconfigurations`
--
ALTER TABLE `pipelinestageconfigurations`
  ADD CONSTRAINT `configurations_pipelinestageconfigurations_CON` FOREIGN KEY (`configuration`) REFERENCES `configurations` (`id`),
  ADD CONSTRAINT `configurations_pipelinestageconfigurations_CON2` FOREIGN KEY (`pipeline`) REFERENCES `configurations` (`id`),
  ADD CONSTRAINT `tasktypes_pipelinestageconfigurations_CON` FOREIGN KEY (`tasktype`) REFERENCES `tasktypes` (`id`);

--
-- Constraints for table `semanticmarkupconfigurations`
--
ALTER TABLE `semanticmarkupconfigurations`
  ADD CONSTRAINT `configurations_semanticmarkupconfigurations_CON` FOREIGN KEY (`configuration`) REFERENCES `configurations` (`id`),
  ADD CONSTRAINT `glossaries_semanticmarkupconfigurations_CON` FOREIGN KEY (`glossary`) REFERENCES `glossaries` (`id`);

--
-- Constraints for table `shareinvitees`
--
ALTER TABLE `shareinvitees`
  ADD CONSTRAINT `shares_shareinvitees_CON` FOREIGN KEY (`share`) REFERENCES `shares` (`id`),
  ADD CONSTRAINT `users_shareinvitees_CON` FOREIGN KEY (`inviteeuser`) REFERENCES `users` (`id`);

--
-- Constraints for table `shares`
--
ALTER TABLE `shares`
  ADD CONSTRAINT `tasks_shares_CON` FOREIGN KEY (`task`) REFERENCES `tasks` (`id`);

--
-- Constraints for table `tasks`
--
ALTER TABLE `tasks`
  ADD CONSTRAINT `configurations_tasks_CON` FOREIGN KEY (`configuration`) REFERENCES `configurations` (`id`),
  ADD CONSTRAINT `taskstages_tasks_CON` FOREIGN KEY (`taskstage`) REFERENCES `taskstages` (`id`),
  ADD CONSTRAINT `tasktypes_tasks_CON` FOREIGN KEY (`tasktype`) REFERENCES `tasktypes` (`id`),
  ADD CONSTRAINT `users_tasks_CON` FOREIGN KEY (`user`) REFERENCES `users` (`id`);

--
-- Constraints for table `tasksfiles`
--
ALTER TABLE `tasksfiles`
  ADD CONSTRAINT `filesinuse_tasksfiles_CON` FOREIGN KEY (`task`) REFERENCES `tasks` (`id`);

--
-- Constraints for table `taskstages`
--
ALTER TABLE `taskstages`
  ADD CONSTRAINT `tasktypes_taskstages_CON` FOREIGN KEY (`tasktype`) REFERENCES `tasktypes` (`id`);

--
-- Constraints for table `tasktypes`
--
ALTER TABLE `tasktypes`
  ADD CONSTRAINT `inputfiletypes_tasktypes_CON` FOREIGN KEY (`inputfiletype`) REFERENCES `filetypes` (`id`),
  ADD CONSTRAINT `outputfiletypes_tasktypes_CON` FOREIGN KEY (`outputfiletype`) REFERENCES `filetypes` (`id`);

--
-- Constraints for table `taxonomycomparisonconfigurations`
--
ALTER TABLE `taxonomycomparisonconfigurations`
  ADD CONSTRAINT `configurations_taxonomycomparisonconfigurations_CON` FOREIGN KEY (`configuration`) REFERENCES `configurations` (`id`);

--
-- Constraints for table `treegenerationconfigurations`
--
ALTER TABLE `treegenerationconfigurations`
  ADD CONSTRAINT `configurations_treegenerationconfigurations_CON` FOREIGN KEY (`configuration`) REFERENCES `configurations` (`id`);

--
-- Constraints for table `visualizationconfigurations`
--
ALTER TABLE `visualizationconfigurations`
  ADD CONSTRAINT `configurations_visualizationconfigurations_CON` FOREIGN KEY (`configuration`) REFERENCES `configurations` (`id`);