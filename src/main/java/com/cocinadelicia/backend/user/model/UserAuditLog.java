package com.cocinadelicia.backend.user.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

/**
 * Registro de auditoría de acciones críticas sobre usuarios.
 *
 * <p>Esta entidad es inmutable (no soft-delete) y registra:
 *
 * <ul>
 *   <li>USER_INVITED - Usuario invitado y creado en Cognito
 *   <li>USER_IMPORTED - Usuario importado desde Cognito
 *   <li>ROLE_CHANGED - Cambio de roles del usuario
 *   <li>STATUS_CHANGED - Cambio de estado activo/inactivo
 *   <li>USER_SYNCED - Sincronización manual desde Cognito
 * </ul>
 */
@Entity
@Table(name = "user_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "action", nullable = false, length = 50)
  private String action;

  @Column(name = "changed_by", nullable = false, length = 255)
  private String changedBy;

  @Column(name = "changed_at", nullable = false)
  @Builder.Default
  private Instant changedAt = Instant.now();

  @Column(name = "details", columnDefinition = "TEXT")
  private String details;

  // Relación opcional para carga lazy si se necesita
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private AppUser user;

  /**
   * Constructor de conveniencia para crear logs de auditoría.
   */
  public UserAuditLog(Long userId, String action, String changedBy, String details) {
    this.userId = userId;
    this.action = action;
    this.changedBy = changedBy;
    this.details = details;
    this.changedAt = Instant.now();
  }
}
