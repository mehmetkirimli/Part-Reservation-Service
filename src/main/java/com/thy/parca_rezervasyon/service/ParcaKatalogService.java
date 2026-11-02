package com.thy.parca_rezervasyon.service;

import com.thy.parca_rezervasyon.dto.ParcaKatalogDto;
import com.thy.parca_rezervasyon.entity.Parca;
import com.thy.parca_rezervasyon.exception.ParcaBulunamadiException;
import com.thy.parca_rezervasyon.repository.ParcaRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


  @Transactional
  @CacheEvict(value = "parcaKatalog", key = "#parcaNo")
  public void fiyatGuncelle(String parcaNo, BigDecimal yeniFiyat)
  {
    Parca parca = parcaRepository.findByParcaNo(parcaNo).orElseThrow(() -> new ParcaBulunamadiException(parcaNo));
    parca.setBirimFiyat(yeniFiyat);
    parcaRepository.save(parca);
  }
}
