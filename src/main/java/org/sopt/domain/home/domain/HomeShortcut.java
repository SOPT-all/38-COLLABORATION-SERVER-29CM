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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.domain.category.domain.Category;

@Entity
@Getter
@Table(
        name = "home_shortcuts",
        indexes = {
                @Index(name = "idx_home_shortcuts_order_id", columnList = "display_order, id"),
                @Index(name = "idx_home_shortcuts_category", columnList = "category_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HomeShortcut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "category_id", insertable = false, updatable = false)
    private Long categoryId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private int displayOrder;
}
