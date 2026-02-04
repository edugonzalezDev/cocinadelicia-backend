package com.cocinadelicia.backend.user.service;

import com.cocinadelicia.backend.common.web.PageResponse;
import com.cocinadelicia.backend.user.dto.AdminUserFilter;
import com.cocinadelicia.backend.user.dto.AdminUserListItemDTO;
import org.springframework.data.domain.Pageable;

/** Servicio de gestión de usuarios para Admin. */
public interface AdminUserService {

  /**
   * Lista usuarios con búsqueda, filtros y paginación.
   *
   * @param filter filtros opcionales (búsqueda, roles, estado, pedidos pendientes)
   * @param pageable configuración de paginación y ordenamiento
   * @return página de usuarios con metadata de paginación
   */
  PageResponse<AdminUserListItemDTO> listUsers(AdminUserFilter filter, Pageable pageable);
}
