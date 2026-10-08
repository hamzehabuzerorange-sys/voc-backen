package com.orange.voc.repo;

import com.orange.voc.domain.AppUser; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<AppUser, Long> { Optional<AppUser> findByUsernameAndActiveTrue(String u);
    Optional<AppUser> findByUsername(String u); }
