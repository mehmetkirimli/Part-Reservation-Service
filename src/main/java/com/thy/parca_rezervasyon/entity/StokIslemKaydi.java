package com.thy.parca_rezervasyon.entity;

import com.thy.parca_rezervasyon.entity.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "STOK_ISLEM_KAYDI")
@Getter
@Setter
public class StokIslemKaydi extends BaseEntity
{
  @Column(name = "IDEMPOTENCY_KEY", nullable = false, unique = true)
  private String idempotencyKey;

  @Column(name = "PARCA_NO", nullable = false)
  private String parcaNo;

  @Column(name = "ADET", nullable = false)
  private Integer adet;
}