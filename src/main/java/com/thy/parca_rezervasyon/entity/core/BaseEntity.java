package com.thy.parca_rezervasyon.entity.core;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass          // Kendi tablosu yok; alanlarını kendisinden türeyen entity'lere verir
@Getter
@Setter
public abstract class BaseEntity {   // abstract: tek başına new'lenmesin, sadece miras alınsın

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)   // id'yi veritabanı otomatik artırır
  protected Long id;

  @Column(nullable = false, updatable = false)   // Oluşturma anı bir kere yazılır, asla değişmez
  protected LocalDateTime createdAt;

  protected LocalDateTime updatedAt;

  @Column(nullable = false)
  protected boolean deleted;   // primitive boolean: null olamaz, varsayılanı false

  @Version
  private Long version; // JPA'nın optimistic locking mekanizması için kullanılır. Her güncellemede bu alan otomatik olarak artırılır ve böylece aynı kaydı aynı anda güncelleyen işlemler arasında çakışmaların önüne geçilir.

  @PrePersist   // Kayıt İLK KEZ veritabanına eklenmeden hemen önce JPA bu metodu çağırır
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = this.createdAt;
  }

  @PreUpdate    // Kayıt her GÜNCELLENMEDEN hemen önce JPA bu metodu çağırır
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}