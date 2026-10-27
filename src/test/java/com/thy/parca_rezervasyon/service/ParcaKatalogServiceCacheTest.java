package com.thy.parca_rezervasyon.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.thy.parca_rezervasyon.dto.ParcaKatalogDto;
import com.thy.parca_rezervasyon.entity.Parca;
import com.thy.parca_rezervasyon.repository.ParcaRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@SpringBootTest
public class ParcaKatalogServiceCacheTest
{
  @Autowired
  private ParcaKatalogService parcaKatalogService;
  @Autowired
  private ParcaRepository parcaRepository;
  @Autowired
  private CacheManager cacheManager;

  @Test
  void ayniParcaIkinciKezIsteninceCachedenGelmeli()
  {
    String parcaNo = "PGS-1000-" + UUID.randomUUID();
    Parca parca = new Parca();
    parca.setParcaNo(parcaNo);
    parca.setParcaAdi("Motor Enjeksiyon Bobini");
    parca.setBirimFiyat(new BigDecimal("2500.00"));
    parca.setStokAdet(5);
    parcaRepository.save(parca);

    // İlk çağrı: veritabanından alınır ve cache'e eklenir
    ParcaKatalogDto ilkCagri = parcaKatalogService.katalogBilgisiGetir(parcaNo);

    // İkinci çağrı: cache'ten gelir, veritabanına gidilmez
    ParcaKatalogDto ikinciCagri = parcaKatalogService.katalogBilgisiGetir(parcaNo);

    assertThat(ikinciCagri).as("İki çağrı aynı sonucu dönmeli").isEqualTo(ilkCagri);

    Cache.ValueWrapper cachedekiKayit = cacheManager.getCache("parcaKatalog").get(parcaNo);
    assertThat(cachedekiKayit).as("Parça cache'e girmiş olmalı").isNotNull();
    assertThat(cachedekiKayit.get()).as("Cache'teki değer ilk çağrıyla aynı olmalı").isEqualTo(ilkCagri);
  }
}