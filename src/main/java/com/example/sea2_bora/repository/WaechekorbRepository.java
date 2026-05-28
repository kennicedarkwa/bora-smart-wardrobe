package com.example.sea2_bora.repository;

import com.example.sea2_bora.implementation.Waechekorb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WaechekorbRepository extends JpaRepository<Waechekorb, Long> {
    Optional<Waechekorb> findFirstByOrderByIdAsc();
}