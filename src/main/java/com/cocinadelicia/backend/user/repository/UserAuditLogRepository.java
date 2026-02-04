package com.cocinadelicia.backend.user.repository;

import com.cocinadelicia.backend.user.model.UserAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para acceso a logs de auditoría de usuarios.
 */
@Repository
public interface UserAuditLogRepository extends JpaRepository<UserAuditLog, Long> {

  /**
   * Obtiene logs de auditoría de un usuario ordenados por fecha descendente.
   *
   * @param userId ID del usuario
   * @param pageable configuración de paginación
   * @return página de logs de auditoría
   */
  Page<UserAuditLog> findByUserIdOrderByChangedAtDesc(Long userId, Pageable pageable);
}
