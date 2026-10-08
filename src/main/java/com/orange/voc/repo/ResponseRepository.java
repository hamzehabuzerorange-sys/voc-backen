package com.orange.voc.repo;

import com.orange.voc.domain.SurveyResponse; import org.springframework.data.jpa.repository.JpaRepository;
public interface ResponseRepository extends JpaRepository<SurveyResponse, Long> { long countBySurveyId(Long surveyId); }
