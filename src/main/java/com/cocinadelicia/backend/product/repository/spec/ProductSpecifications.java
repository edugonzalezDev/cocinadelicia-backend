// src/main/java/com/cocinadelicia/backend/product/repository/spec/ProductSpecifications.java
package com.cocinadelicia.backend.product.repository.spec;

import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.model.Tag;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecifications {

  public static Specification<Product> isActive() {
    return (root, query, cb) -> cb.isTrue(root.get("isActive"));
  }

  public static Specification<Product> hasCategory(String categorySlug) {
    if (categorySlug == null || categorySlug.isBlank()) return null;
    return (root, query, cb) ->
        cb.equal(cb.lower(root.get("category").get("slug")), categorySlug.toLowerCase().trim());
  }

  public static Specification<Product> searchText(String searchQuery) {
    if (searchQuery == null || searchQuery.isBlank()) return null;

    String pattern = "%" + searchQuery.toLowerCase().trim() + "%";

    return (root, query, cb) -> {
      Predicate namePredicate = cb.like(cb.lower(root.get("name")), pattern);
      Predicate descriptionPredicate = cb.like(cb.lower(root.get("description")), pattern);

      Join<Product, Tag> tagsJoin = root.join("tags", JoinType.LEFT);
      Predicate tagPredicate = cb.like(cb.lower(tagsJoin.get("name")), pattern);

      query.distinct(true);

      return cb.or(namePredicate, descriptionPredicate, tagPredicate);
    };
  }

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

  public static Specification<Product> hasAvailableVariant() {
    return (root, query, cb) -> {
      Subquery<Long> subquery = query.subquery(Long.class);
      Root<ProductVariant> variantRoot = subquery.from(ProductVariant.class);
      subquery.select(variantRoot.get("product").get("id"));

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

  /**
   * Filtra productos que tengan TODOS los tags especificados (slug).
   * Si la lista está vacía, devuelve null (sin filtro).
   */
  public static Specification<Product> hasTags(java.util.List<String> tagSlugs) {
    if (tagSlugs == null || tagSlugs.isEmpty()) return null;

    return (root, query, cb) -> {
      // Normalizar slugs a lowercase
      java.util.List<String> normalizedSlugs = tagSlugs.stream()
          .filter(s -> s != null && !s.isBlank())
          .map(String::trim)
          .map(String::toLowerCase)
          .distinct()
          .toList();

      if (normalizedSlugs.isEmpty()) return null;

      // Para cada tag requerido, verificamos que el producto lo tenga
      Predicate[] tagPredicates = new Predicate[normalizedSlugs.size()];

      for (int i = 0; i < normalizedSlugs.size(); i++) {
        String tagSlug = normalizedSlugs.get(i);

        // Subquery: EXISTS (SELECT 1 FROM product_tag pt JOIN tag t WHERE pt.product_id = product.id AND t.slug = ?)
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Product> subProduct = subquery.from(Product.class);
        Join<Product, Tag> subTagJoin = subProduct.join("tags", JoinType.INNER);

        subquery.select(cb.literal(1L));
        subquery.where(
            cb.and(
                cb.equal(subProduct.get("id"), root.get("id")),
                cb.equal(cb.lower(subTagJoin.get("slug")), tagSlug)
            )
        );

        tagPredicates[i] = cb.exists(subquery);
      }

      // El producto debe tener TODOS los tags (AND)
      return cb.and(tagPredicates);
    };
  }
}
