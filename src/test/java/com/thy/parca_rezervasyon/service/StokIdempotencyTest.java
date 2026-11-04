package com.thy.parca_rezervasyon.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.thy.parca_rezervasyon.entity.Parca;
import com.thy.parca_rezervasyon.repository.ParcaRepository;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StokIdempotencyTest
{
  @Autowired
  private RezervasyonService rezervasyonService;
  @Autowired
  private ParcaRepository parcaRepository;

  @Test
  void ayniAnahtarlaGelenIsteklerStoguSadeceBirKezDusurmeli() throws InterruptedException
  {
    String parcaNo = "TK-" + UUID.randomUUID();
    Parca parca = new Parca();
    parca.setParcaNo(parcaNo);
    parca.setParcaAdi("Fren Balatası");
    parca.setStokAdet(5);
    parca.setBirimFiyat(new BigDecimal("1120.00"));
    parcaRepository.save(parca);

    String ayniAnahtar = UUID.randomUUID().toString();   // TEK fiş, 10 kere gönderilecek
    int threadSayisi = 10;

    ExecutorService executor = Executors.newFixedThreadPool(threadSayisi);
    CountDownLatch baslangicKapisi = new CountDownLatch(1);
    CountDownLatch bitisSayaci = new CountDownLatch(threadSayisi);
    AtomicInteger basarili = new AtomicInteger();
    AtomicInteger hata = new AtomicInteger();
    Set<String> hataTurleri = ConcurrentHashMap.newKeySet();

    for (int i = 0; i < threadSayisi; i++)
    {
      executor.submit(() -> {
        try
        {
          baslangicKapisi.await();
          rezervasyonService.reservePart(parcaNo, 1, ayniAnahtar);
          basarili.incrementAndGet();
        }
        catch (Exception e)
        {
          hata.incrementAndGet();
          hataTurleri.add(e.getClass().getSimpleName());
        }
        finally
        {
          bitisSayaci.countDown();
        }
      });
    }

    baslangicKapisi.countDown();
    boolean hepsiBitti = bitisSayaci.await(10, TimeUnit.SECONDS);
    executor.shutdown();

    int kalanStok = parcaRepository.findByParcaNo(parcaNo).orElseThrow().getStokAdet();
    System.out.println("Başarılı: " + basarili.get() + " | Hata: " + hata.get()
        + " | Hata türleri: " + hataTurleri + " | Kalan stok: " + kalanStok);

    assertThat(hepsiBitti).as("Tüm thread'ler bitmeli").isTrue();
    assertThat(kalanStok).as("Aynı fiş ile stok SADECE BİR KEZ düşmeli").isEqualTo(4);
  }
}