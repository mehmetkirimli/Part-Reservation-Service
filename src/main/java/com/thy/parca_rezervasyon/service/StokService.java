package com.thy.parca_rezervasyon.service;

import com.thy.parca_rezervasyon.entity.Parca;
import com.thy.parca_rezervasyon.exception.ParcaBulunamadiException;
import com.thy.parca_rezervasyon.exception.YetersizStokException;
import com.thy.parca_rezervasyon.repository.ParcaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StokService
{
  private final ParcaRepository parcaRepository;

  @Transactional
  public void stokPart(String parcaNo, int adet)
  {
    Parca parca = parcaRepository.findByParcaNo(parcaNo).orElseThrow(() -> new ParcaBulunamadiException(parcaNo));

    if (parca.getStokAdet() >= adet)
    {
      parca.setStokAdet(parca.getStokAdet() - adet);
      parcaRepository.save(parca);
    }
    else
    {
      throw new YetersizStokException(parca.getParcaNo(), parca.getStokAdet(), adet);
    }
  }

}
