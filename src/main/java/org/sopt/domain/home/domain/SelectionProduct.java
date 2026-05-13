package org.sopt.domain.home.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.domain.product.domain.Product;

@Entity
@Getter
@Table(
        name = "selection_products",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_selection_products_selection_product",
                        columnNames = {"home_selection_id", "product_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_selection_products_selection_order_id",
                        columnList = "home_selection_id, display_order, id"
                ),
                @Index(name = "idx_selection_products_product", columnList = "product_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectionProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_selection_id", nullable = false)
    private HomeSelection homeSelection;

    @Column(name = "home_selection_id", nullable = false, insertable = false, updatable = false)
    private Long homeSelectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_id", nullable = false, insertable = false, updatable = false)
    private Long productId;

    @Column(nullable = false)
    private int displayOrder;
}
