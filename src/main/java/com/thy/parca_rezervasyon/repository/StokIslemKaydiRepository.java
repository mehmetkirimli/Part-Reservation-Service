package com.thy.parca_rezervasyon.repository;

import com.thy.parca_rezervasyon.entity.StokIslemKaydi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StokIslemKaydiRepository extends JpaRepository<StokIslemKaydi,Long>
{
  boolean existsByIdempotencyKey(String idempotencyKey);
}
