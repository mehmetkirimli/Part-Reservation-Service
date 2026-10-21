package com.thy.parca_rezervasyon.exception;

public class YetersizStokException extends RuntimeException
{
  public YetersizStokException(String parcaNo, int mevcutStok, int talepEdilenAdet)
  {
    super("Yetersiz stok: Parça No: " + parcaNo + ", Mevcut Stok: " + mevcutStok + ", Talep Edilen Adet: " + talepEdilenAdet);
  }

}
