package com.thy.parca_rezervasyon.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.thy.parca_rezervasyon.entity.Parca;
import com.thy.parca_rezervasyon.exception.YetersizStokException;
import com.thy.parca_rezervasyon.repository.ParcaRepository;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@SpringBootTest
public class RezervasyonServiceRaceConditionTest
{

  @Autowired
  private RezervasyonService rezervasyonService;

  @Autowired
  private ParcaRepository parcaRepository;

  @Test
  void ayniAndaGelenRezervasyonlarStoktanFazlaParcaVermemeli() throws InterruptedException
  {
    // 1) HAZIRLIK: stokta 5 parça olan bir kayıt

    String parcaNo = "TK-" + UUID.randomUUID();
    Parca parca = new Parca();
    parca.setParcaNo(parcaNo);
    parca.setParcaAdi("Fren Balatası");
    parca.setStokAdet(5);
    parca.setBirimFiyat(new BigDecimal("1500.00"));
    parcaRepository.save(parca);

    int threadSayisi = 10;

    ExecutorService executor = Executors.newFixedThreadPool(threadSayisi);
    CountDownLatch baslangicKapisi = new CountDownLatch(1); // threadların aynı anda başlaması için kapı
    CountDownLatch bitisSayaci = new CountDownLatch(threadSayisi);

    AtomicInteger basarili = new AtomicInteger();
    AtomicInteger yetersizStok = new AtomicInteger();
    AtomicInteger optimisticHata = new AtomicInteger();


    // 2) SALDIRI: 10 thread hazırlanır, kapıda bekler

    for (int i = 0; i < threadSayisi; i++)
    {
      executor.submit(() -> {
        try
        {
          baslangicKapisi.await();
          rezervasyonService.reservePart(parcaNo, 1);
          basarili.incrementAndGet();
        }
        catch (YetersizStokException e)
        {
          yetersizStok.incrementAndGet();
        }
        catch (ObjectOptimisticLockingFailureException e)
        {
          optimisticHata.incrementAndGet();
        }
        catch (InterruptedException e)
        {
          Thread.currentThread().interrupt();
        }
        finally
        {
          bitisSayaci.countDown();
        }
      });
    }

    baslangicKapisi.countDown();                              // Hepsi aynı anda koşmaya başlar!

    boolean hepsiBitti = bitisSayaci.await(10, TimeUnit.SECONDS);         // Herkesin bitirmesini bekle
    executor.shutdown();

    // 3) SONUÇ
    int kalanStok = parcaRepository.findByParcaNo(parcaNo).orElseThrow().getStokAdet();

    System.out.println("Başarılı: " + basarili.get()
        + " | Yetersiz stok: " + yetersizStok.get()
        + " | Optimistic: " + optimisticHata.get()
        + " | Kalan stok: " + kalanStok);


    // 4) ASSERT: Başarılı rezervasyon sayısı 5'i geçmemeli ve kalan stok 0 olmalı
    assertThat(hepsiBitti).as("Tüm thread'ler süresinde bitirdi mi?").isTrue();
    assertThat(basarili.get() + yetersizStok.get() + optimisticHata.get())
        .as("Her thread hesaba katıldı mı?")
        .isEqualTo(threadSayisi);

    assertThat(basarili.get()).as("Başarılı rezervasyon sayısı").isEqualTo(5);
    assertThat(kalanStok).as("Kalan stok").isEqualTo(0);

  }
}
