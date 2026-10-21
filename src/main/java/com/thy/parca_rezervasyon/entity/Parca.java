package com.thy.parca_rezervasyon.entity;

import com.thy.parca_rezervasyon.entity.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "PARCA")
@SQLRestriction("deleted = false")  // deleted = false olan kayıtları getir
@SQLDelete(sql = "UPDATE PARCA SET deleted = true WHERE id = ?")  // silme işlemi yerine deleted = true yap
@Getter
@Setter
public class Parca extends BaseEntity implements Serializable
{
  private static final long serialVersionUID = 1L;

  @Column(name = "PARCA_NO", nullable = false, unique = true)
  private String parcaNo;

  @Column(name = "PARCA_ADI", nullable = false)
  private String parcaAdi;

  @Column(name = "STOK_ADET", nullable = false)
  private Integer stokAdet;

  @Column(name = "BIRIM_FIYAT", nullable = false , precision = 12 , scale = 2)
  private BigDecimal birimFiyat;
}
