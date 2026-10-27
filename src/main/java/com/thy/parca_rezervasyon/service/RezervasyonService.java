package com.thy.parca_rezervasyon.service;

import com.thy.parca_rezervasyon.validations.Validations;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RezervasyonService
{
  private final StokService stokService;
  private static final int MAX_RETRY = 3;

  //public synchronized void reservePart(String parcaNo, int adet)
  public synchronized void reservePart(String parcaNo, int adet)
  {
    Validations.requirePositive(adet, "Adet");
    Validations.requireNotBlank(parcaNo, "Parça Numarası");

    for(int retryCount = 1; retryCount <= MAX_RETRY; retryCount++)
    {
      try
      {
        System.out.println("Bana gelen stokService'in gerçek sınıfı: " + stokService.getClass().getName());
        stokService.stokPart(parcaNo, adet);
        return;
      }
      catch (ObjectOptimisticLockingFailureException optimisticLockingFailureException)
      {
        if(retryCount== MAX_RETRY)
        {
          throw optimisticLockingFailureException;
        }
        else
        {
          bekle(retryCount);
        }
      }
    }
  }
  private void bekle(int retryCount)
  {
    try
    {
      //long sleepTime = (long) ((Math.random() * 91) + 10) * retryCount; // backoff süreyi retryCount ile çarpıyoruz
      long sleepTime = ThreadLocalRandom.current().nextLong(10, 101) * retryCount; // backoff süreyi retryCount ile çarpıyoruz
      System.out.println("Tekrar deneme sayısı: " + retryCount + ", bekleme süresi: " + sleepTime + " ms");
      Thread.sleep(sleepTime);
    }
    catch (InterruptedException e)
    {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted during sleep", e);
    }
  }
}
