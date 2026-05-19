package com.example.sea2_bora.repository;

import com.example.sea2_bora.implementation.Empfehlung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpfehlungRepository extends JpaRepository<Empfehlung, Long>
{

}
