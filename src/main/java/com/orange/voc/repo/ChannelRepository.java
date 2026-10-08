package com.orange.voc.repo;

import com.orange.voc.domain.Channel; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository;
public interface ChannelRepository extends JpaRepository<Channel, Long> { Optional<Channel> findByCodeAndActiveTrue(String code); }
