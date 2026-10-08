package com.orange.voc.repo;

import com.orange.voc.domain.SamplingRule; import java.util.List; import org.springframework.data.jpa.repository.JpaRepository;
public interface SamplingRuleRepository extends JpaRepository<SamplingRule, Long> { List<SamplingRule> findByEnabledTrueAndChannelId(Long channelId); }
