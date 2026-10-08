package com.orange.voc.repo;

import com.orange.voc.domain.*; import java.util.List; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    @Query("select distinct s from Survey s join s.channels c where c.id = :cid and s.status = :st")
    List<Survey> findForChannel(@Param("cid") Long channelId, @Param("st") SurveyStatus status);
    List<Survey> findByDefaultForAllChannelsTrueAndStatus(SurveyStatus status);
    List<Survey> findByStatus(SurveyStatus status);
}
