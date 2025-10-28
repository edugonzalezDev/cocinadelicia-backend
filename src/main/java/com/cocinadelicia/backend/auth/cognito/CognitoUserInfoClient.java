package com.cocinadelicia.backend.auth.cognito;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CognitoUserInfoClient {

  @Value("${cognito.domain}")
  private String domain; // ej. cdd-auth.auth.us-east-1.amazoncognito.com

  private final RestTemplate restTemplate;

  /**
   * Llama a https://{domain}/oauth2/userInfo con el Access Token de Cognito. Devuelve atributos
   * como email, given_name, family_name, phone_number (si están disponibles).
   */
  public Map<String, Object> fetchUserInfo(String accessToken) {
    String url = "https://" + domain + "/oauth2/userInfo";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

    if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
      throw new IllegalStateException(
          "Cannot fetch userInfo from Cognito (status=" + resp.getStatusCode() + ")");
    }
    //noinspection unchecked
    return resp.getBody();
  }
}
