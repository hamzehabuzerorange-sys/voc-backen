package com.orange.voc.repo;

import com.orange.voc.domain.FrequencyRule; import java.util.List; import org.springframework.data.jpa.repository.JpaRepository;
public interface FrequencyRuleRepository extends JpaRepository<FrequencyRule, Long> { List<FrequencyRule> findByEnabledTrue(); }
