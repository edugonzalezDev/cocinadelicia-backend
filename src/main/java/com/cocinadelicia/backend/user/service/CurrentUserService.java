// src/main/java/com/cocinadelicia/backend/user/service/CurrentUserService.java
package com.cocinadelicia.backend.user.service;

public interface CurrentUserService {
  Long getOrCreateCurrentUserId(); // id en app_user
}
