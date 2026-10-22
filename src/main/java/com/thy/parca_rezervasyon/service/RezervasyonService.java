package com.thy.parca_rezervasyon.service;

import com.thy.parca_rezervasyon.entity.Parca;
import com.thy.parca_rezervasyon.exception.ParcaBulunamadiException;
import com.thy.parca_rezervasyon.exception.YetersizStokException;
import com.thy.parca_rezervasyon.repository.ParcaRepository;
import com.thy.parca_rezervasyon.validations.Validations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RezervasyonService
{
  private final ParcaRepository parcaRepository;

  public synchronized void reservePart(String parcaNo, int adet)
  {

      Validations.requirePositive(adet, "Adet");
      Validations.requireNotBlank(parcaNo, "Parça Numarası");

      Parca parca = parcaRepository.findByParcaNo(parcaNo).orElseThrow(() -> new ParcaBulunamadiException(parcaNo));

      if (parca.getStokAdet() >= adet)
      {
        parca.setStokAdet(parca.getStokAdet() - adet);
        parcaRepository.save(parca);
      }else
      {
       throw new YetersizStokException( parca.getParcaNo(),parca.getStokAdet() ,adet );
      }
  }

}
