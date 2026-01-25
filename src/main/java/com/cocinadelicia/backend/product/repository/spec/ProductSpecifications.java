// src/main/java/com/cocinadelicia/backend/product/repository/spec/ProductSpecifications.java
package com.cocinadelicia.backend.product.repository.spec;

import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.model.Tag;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecifications {

  /**
   * Filtra productos activos (isActive = true)
   */
  public static Specification<Product> isActive() {
    return (root, query, cb) -> cb.isTrue(root.get("isActive"));
  }

  /**
   * Filtra por slug de categoría (case-insensitive)
   */
  public static Specification<Product> hasCategory(String categorySlug) {
    if (categorySlug == null || categorySlug.isBlank()) return null;
    return (root, query, cb) ->
        cb.equal(cb.lower(root.get("category").get("slug")), categorySlug.toLowerCase().trim());
  }

  /**
   * Búsqueda de texto en nombre, descripción y tags (case-insensitive)
   */
  public static Specification<Product> searchText(String searchQuery) {
    if (searchQuery == null || searchQuery.isBlank()) return null;

    String pattern = "%" + searchQuery.toLowerCase().trim() + "%";

    return (root, query, cb) -> {
      Predicate namePredicate = cb.like(cb.lower(root.get("name")), pattern);
      Predicate descriptionPredicate = cb.like(cb.lower(root.get("description")), pattern);

      // Búsqueda en tags
      Join<Product, Tag> tagsJoin = root.join("tags", JoinType.LEFT);
      Predicate tagPredicate = cb.like(cb.lower(tagsJoin.get("name")), pattern);

      // Distinct para evitar duplicados cuando un producto tiene varios tags que coinciden
      query.distinct(true);

      return cb.or(namePredicate, descriptionPredicate, tagPredicate);
    };
  }

  /**
   * Filtra productos que tienen al menos una variante destacada (featured = true)
   */
  public static Specification<Product> hasFeaturedVariant() {
    return (root, query, cb) -> {
      Subquery<Long> subquery = query.subquery(Long.class);
      Root<ProductVariant> variantRoot = subquery.from(ProductVariant.class);
      subquery.select(variantRoot.get("product").get("id"));
      subquery.where(
          cb.and(
              cb.equal(variantRoot.get("product").get("id"), root.get("id")),
              cb.isTrue(variantRoot.get("featured")),
              cb.isTrue(variantRoot.get("isActive"))));

      return cb.exists(subquery);
    };
  }

  /**
   * Filtra productos que tienen al menos una variante de menú del día (dailyMenu = true)
   */
  public static Specification<Product> hasDailyMenuVariant() {
    return (root, query, cb) -> {
      Subquery<Long> subquery = query.subquery(Long.class);
      Root<ProductVariant> variantRoot = subquery.from(ProductVariant.class);
      subquery.select(variantRoot.get("product").get("id"));
      subquery.where(
          cb.and(
              cb.equal(variantRoot.get("product").get("id"), root.get("id")),
              cb.isTrue(variantRoot.get("dailyMenu")),
              cb.isTrue(variantRoot.get("isActive"))));

      return cb.exists(subquery);
    };
  }

  /**
   * Filtra productos que tienen al menos una variante nueva (isNew = true)
   */
  public static Specification<Product> hasNewVariant() {
    return (root, query, cb) -> {
      Subquery<Long> subquery = query.subquery(Long.class);
      Root<ProductVariant> variantRoot = subquery.from(ProductVariant.class);
      subquery.select(variantRoot.get("product").get("id"));
      subquery.where(
          cb.and(
              cb.equal(variantRoot.get("product").get("id"), root.get("id")),
              cb.isTrue(variantRoot.get("isNew")),
              cb.isTrue(variantRoot.get("isActive"))));

      return cb.exists(subquery);
    };
  }

  /**
   * Filtra productos que tienen al menos una variante disponible
   * (activa y con stock si managesStock=true)
   */
  public static Specification<Product> hasAvailableVariant() {
    return (root, query, cb) -> {
      Subquery<Long> subquery = query.subquery(Long.class);
      Root<ProductVariant> variantRoot = subquery.from(ProductVariant.class);
      subquery.select(variantRoot.get("product").get("id"));

      // Variante disponible: activa Y (no maneja stock O tiene stock > 0)
      Predicate isActiveVariant = cb.isTrue(variantRoot.get("isActive"));
      Predicate doesNotManageStock = cb.isFalse(variantRoot.get("managesStock"));
      Predicate hasStock = cb.greaterThan(variantRoot.get("stockQuantity"), 0);
      Predicate availablePredicate = cb.or(doesNotManageStock, hasStock);

      subquery.where(
          cb.and(
              cb.equal(variantRoot.get("product").get("id"), root.get("id")),
              isActiveVariant,
              availablePredicate));

      return cb.exists(subquery);
    };
  }
}
