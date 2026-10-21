package com.thy.parca_rezervasyon.repository;

import com.thy.parca_rezervasyon.entity.Parca;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParcaRepository extends JpaRepository<Parca, Long>
{
  Optional<Parca> findByParcaNo(String parcaNo);

}
