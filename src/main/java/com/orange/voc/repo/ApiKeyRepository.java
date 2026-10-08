package com.orange.voc.repo;

import com.orange.voc.domain.ApiKey; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository;
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> { Optional<ApiKey> findByKeyHashAndActiveTrue(String h); }
