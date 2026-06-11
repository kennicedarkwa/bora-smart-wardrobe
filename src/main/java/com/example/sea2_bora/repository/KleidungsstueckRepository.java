package com.example.sea2_bora.repository;

import com.example.sea2_bora.implementation.Kleidungsstueck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.sea2_bora.implementation.Tags;

import java.util.List;

public interface KleidungsstueckRepository extends JpaRepository<Kleidungsstueck, Long>
{
    @Query("SELECT k FROM Kleidungsstueck k JOIN k.tags t " +
            "WHERE t = :anlassTag " +
            "AND k.temperaturIndex = :tempIndex " +
            "AND k.istImWaeschekorb = false")
    List<Kleidungsstueck> findByAnlassAndWetter(@Param("anlassTag") Tags anlassTag,
                                                @Param("tempIndex") int tempIndex);
}
