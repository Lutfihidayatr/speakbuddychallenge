package com.lutfi.spchallenge.repository;

import com.lutfi.spchallenge.entity.UserPhrase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPhraseRepository extends JpaRepository<UserPhrase, Long> {

    // Find single phrase by user ID and phrase ID
    @Query("SELECT p FROM UserPhrase p WHERE p.user.id = :userId " +
            "AND p.id = :phraseId")
    Optional<UserPhrase> findByUserIdAndPhraseId(
            @Param("userId") Long userId,
            @Param("phraseId") Long phraseId
    );
}
