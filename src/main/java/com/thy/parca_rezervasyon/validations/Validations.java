package com.thy.parca_rezervasyon.validations;
/*
 final sınıf, başka sınıfların bu sınıftan türetilmesini engeller

 */
public final class Validations
{
  private Validations()
  {
    /*
    Private constructor nesne üretmeyi engeller.
    ** Ufak bir bonus: reflection ile private constructor’a bile zorla erişilebilir.
    Bunu da engellemek isteyenler constructor’ın içine throw new AssertionError(); yazar.
     */

  }

  public static void requirePositive(int value, String fieldName)
  {
    if (value <= 0)
    {
      throw new IllegalArgumentException(fieldName + " sıfırdan büyük olmalıdır. Gelen değer: " + value);
    }
  }

  public static void requireNotBlank(String value, String fieldName)
  {
    if (value == null || value.isBlank())
    {
      throw new IllegalArgumentException(fieldName + " boş olamaz.");
    }
  }
}
