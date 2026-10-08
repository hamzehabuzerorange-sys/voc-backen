package com.orange.voc.repo;

import com.orange.voc.domain.Invitation; import java.time.LocalDateTime; import java.util.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByToken(String token);
    boolean existsByMsisdnAndCreatedAtAfter(String msisdn, LocalDateTime since);
    boolean existsByMsisdnAndChannelIdAndCreatedAtAfter(String msisdn, Long channelId, LocalDateTime since);
    @Query("select i.status, count(i) from Invitation i where i.surveyId = :sid group by i.status")
    List<Object[]> countByStatus(@Param("sid") Long surveyId);
}
