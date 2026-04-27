package com.example.sea2_bora.repository;

import com.example.sea2_bora.implementation.Kleidungsstueck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface KleidungsstueckRepository extends JpaRepository<Kleidungsstueck, Long>
{

}
