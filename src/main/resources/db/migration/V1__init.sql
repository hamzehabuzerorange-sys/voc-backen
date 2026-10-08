-- ===== Security / admin =====
CREATE TABLE app_user (id BIGINT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(80) NOT NULL UNIQUE, password_hash VARCHAR(100) NOT NULL, active TINYINT(1) NOT NULL DEFAULT 1, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE role (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50) NOT NULL UNIQUE);
CREATE TABLE user_role (user_id BIGINT NOT NULL, role_id BIGINT NOT NULL, PRIMARY KEY(user_id, role_id));
CREATE TABLE api_key (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, key_hash CHAR(64) NOT NULL UNIQUE, active TINYINT(1) NOT NULL DEFAULT 1, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);
INSERT INTO role(name) VALUES ('ADMIN'),('DESIGNER'),('APPROVER'),('VIEWER');

-- ===== Channels / segments =====
CREATE TABLE channel (id BIGINT AUTO_INCREMENT PRIMARY KEY, code VARCHAR(50) NOT NULL UNIQUE, name VARCHAR(100) NOT NULL, delivery_method VARCHAR(20) NOT NULL, active TINYINT(1) NOT NULL DEFAULT 1);
CREATE TABLE segment (id BIGINT AUTO_INCREMENT PRIMARY KEY, channel_id BIGINT NOT NULL, parent_id BIGINT NULL, name VARCHAR(100) NOT NULL);

-- ===== Presentation =====
CREATE TABLE theme (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, config_json TEXT, is_default TINYINT(1) NOT NULL DEFAULT 0);
CREATE TABLE welcome_message (id BIGINT AUTO_INCREMENT PRIMARY KEY, code VARCHAR(50) NOT NULL UNIQUE, text_en TEXT, text_ar TEXT);

-- ===== Surveys =====
CREATE TABLE survey (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, code VARCHAR(50) NOT NULL UNIQUE, name VARCHAR(150) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
  language_mode VARCHAR(20) NOT NULL DEFAULT 'SELECTABLE', fixed_language VARCHAR(5),
  welcome_message_id BIGINT NULL, theme_id BIGINT NULL, segment_id BIGINT NULL,
  start_at DATETIME NULL, end_at DATETIME NULL,
  validity_days INT NULL, expiry_behavior VARCHAR(20) NOT NULL DEFAULT 'BLOCK',
  require_mandatory TINYINT(1) NOT NULL DEFAULT 0, default_for_all_channels TINYINT(1) NOT NULL DEFAULT 0,
  version INT NOT NULL DEFAULT 1, created_by VARCHAR(80), approved_by VARCHAR(80),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE survey_channel (survey_id BIGINT NOT NULL, channel_id BIGINT NOT NULL, PRIMARY KEY(survey_id, channel_id));
CREATE TABLE question (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, survey_id BIGINT NOT NULL, position INT NOT NULL, type VARCHAR(30) NOT NULL,
  mandatory TINYINT(1) NOT NULL DEFAULT 0, text_en TEXT, text_ar TEXT, config_json TEXT,
  branching_json TEXT, -- [{"optionId":5,"next":12},{"min":1,"max":6,"next":13},{"min":7,"max":10,"next":null}]
  INDEX(survey_id));
CREATE TABLE answer_option (id BIGINT AUTO_INCREMENT PRIMARY KEY, question_id BIGINT NOT NULL, position INT NOT NULL, text_en VARCHAR(500), text_ar VARCHAR(500), value VARCHAR(100), emoji VARCHAR(20), INDEX(question_id));
-- immutable copy taken on approval so historical answers stay interpretable
CREATE TABLE survey_snapshot (survey_id BIGINT NOT NULL, version INT NOT NULL, snapshot_json LONGTEXT NOT NULL, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY(survey_id, version));

-- ===== Rules =====
CREATE TABLE sampling_rule (id BIGINT AUTO_INCREMENT PRIMARY KEY, channel_id BIGINT NOT NULL, segment_id BIGINT NULL, percentage DOUBLE NOT NULL, enabled TINYINT(1) NOT NULL DEFAULT 1);
CREATE TABLE frequency_rule (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, days INT NOT NULL, scope VARCHAR(20) NOT NULL DEFAULT 'ALL_CHANNELS', enabled TINYINT(1) NOT NULL DEFAULT 1);

-- ===== Interactions, invitations, responses =====
CREATE TABLE interaction (id BIGINT AUTO_INCREMENT PRIMARY KEY, source VARCHAR(10) NOT NULL, external_id VARCHAR(100) NOT NULL, msisdn VARCHAR(20) NOT NULL, channel_id BIGINT NULL, segment_id BIGINT NULL, request_type VARCHAR(50), occurred_at DATETIME, fetched_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, processed TINYINT(1) NOT NULL DEFAULT 0, UNIQUE KEY uq_src_ext(source, external_id));
CREATE TABLE invitation (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, token VARCHAR(16) NOT NULL UNIQUE, survey_id BIGINT NOT NULL, survey_version INT NOT NULL,
  channel_id BIGINT NOT NULL, interaction_id BIGINT NULL, msisdn VARCHAR(20) NOT NULL, delivery_method VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'CREATED', sent_at DATETIME NULL, delivered_at DATETIME NULL, expires_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, INDEX(msisdn, created_at), INDEX(survey_id, status));
CREATE TABLE static_link (id BIGINT AUTO_INCREMENT PRIMARY KEY, survey_id BIGINT NOT NULL, token VARCHAR(16) NOT NULL UNIQUE, require_identification TINYINT(1) NOT NULL DEFAULT 0, active TINYINT(1) NOT NULL DEFAULT 1);
CREATE TABLE survey_response (id BIGINT AUTO_INCREMENT PRIMARY KEY, invitation_id BIGINT NULL, static_link_id BIGINT NULL, survey_id BIGINT NOT NULL, survey_version INT NOT NULL, language VARCHAR(5), after_expiry TINYINT(1) NOT NULL DEFAULT 0, submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE response_answer (id BIGINT AUTO_INCREMENT PRIMARY KEY, response_id BIGINT NOT NULL, question_id BIGINT NOT NULL, option_id BIGINT NULL, value_text TEXT, INDEX(response_id));

-- ===== Ops =====
CREATE TABLE scheduled_job (name VARCHAR(80) PRIMARY KEY, enabled TINYINT(1) NOT NULL DEFAULT 1, last_run_at DATETIME NULL, last_status VARCHAR(20));
INSERT INTO scheduled_job(name) VALUES ('CTI_POLL'),('OST_POLL'),('SURVEY_EXPIRY');
CREATE TABLE audit_log (id BIGINT AUTO_INCREMENT PRIMARY KEY, actor VARCHAR(80), action VARCHAR(80) NOT NULL, entity VARCHAR(50), entity_id VARCHAR(50), detail TEXT, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, INDEX(created_at));
CREATE TABLE integration_log (id BIGINT AUTO_INCREMENT PRIMARY KEY, correlation_id VARCHAR(64), interface_name VARCHAR(60), request_status VARCHAR(20), response_status VARCHAR(20), error_code VARCHAR(40), error_desc TEXT, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);
