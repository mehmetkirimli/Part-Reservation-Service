package com.thy.parca_rezervasyon.service;

import com.thy.parca_rezervasyon.entity.Parca;
import com.thy.parca_rezervasyon.entity.StokIslemKaydi;
import com.thy.parca_rezervasyon.exception.ParcaBulunamadiException;
import com.thy.parca_rezervasyon.exception.YetersizStokException;
import com.thy.parca_rezervasyon.repository.ParcaRepository;
import com.thy.parca_rezervasyon.repository.StokIslemKaydiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StokService
{
  private final ParcaRepository parcaRepository;
  private final StokIslemKaydiRepository stokIslemKaydiRepository;

  @Transactional
  public void stokPart(String parcaNo, int adet, String idempotencyKey)
  {
    if(stokIslemKaydiRepository.existsByIdempotencyKey(idempotencyKey))
    {
      return; // bu zaten önceden işlenmiş bir işlem, tekrar işleme almayın
    }

    Parca parca = parcaRepository.findByParcaNo(parcaNo).orElseThrow(() -> new ParcaBulunamadiException(parcaNo));

    if (parca.getStokAdet() < adet)
    {
      throw new YetersizStokException(parca.getParcaNo(), parca.getStokAdet(), adet);
    }

    parca.setStokAdet(parca.getStokAdet() - adet);

    StokIslemKaydi kayit = new StokIslemKaydi();   // deftere yaz 📒
    kayit.setIdempotencyKey(idempotencyKey);
    kayit.setParcaNo(parcaNo);
    kayit.setAdet(adet);
    stokIslemKaydiRepository.save(kayit);

  }

}
