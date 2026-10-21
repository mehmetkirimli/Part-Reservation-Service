package com.thy.parca_rezervasyon.exception;

public class ParcaBulunamadiException extends RuntimeException
{
  public ParcaBulunamadiException(String parcaNo)
  {
    super("Parça bulunamadı: " + parcaNo);
  }

}
