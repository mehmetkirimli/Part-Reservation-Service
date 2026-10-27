package com.thy.parca_rezervasyon.service;

import com.thy.parca_rezervasyon.dto.ParcaKatalogDto;
import com.thy.parca_rezervasyon.entity.Parca;
import com.thy.parca_rezervasyon.exception.ParcaBulunamadiException;
import com.thy.parca_rezervasyon.repository.ParcaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParcaKatalogService
{

  private final ParcaRepository parcaRepository;

  @Cacheable(value = "parcaKatalog", key = "#parcaNo")
  public ParcaKatalogDto katalogBilgisiGetir(String parcaNo)
  {
    return parcaRepository.findByParcaNo(parcaNo).
        map(parca -> new ParcaKatalogDto(parca.getParcaNo(), parca.getParcaAdi(), parca.getBirimFiyat()))
        .orElseThrow(() -> new ParcaBulunamadiException(parcaNo));

  }
}
