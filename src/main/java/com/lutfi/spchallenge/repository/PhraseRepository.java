package com.lutfi.spchallenge.repository;

import com.lutfi.spchallenge.entity.Phrase;
import com.lutfi.spchallenge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhraseRepository extends JpaRepository<Phrase, Long> {
    // Find all non-deleted phrases by user ID
    List<Phrase> findByUserIdAndDeletedAtIsNull(Long userId);

    // Find specific phrase ensuring it's not deleted
    Optional<Phrase> findByIdAndDeletedAtIsNull(Long id);

    // Find single phrase by user ID and phrase ID
    @Query("SELECT p FROM Phrase p WHERE p.user.id = :userId " +
            "AND p.id = :phraseId")
    Optional<Phrase> findByUserIdAndPhraseId(
            @Param("userId") Long userId,
            @Param("phraseId") Long phraseId
    );
}
