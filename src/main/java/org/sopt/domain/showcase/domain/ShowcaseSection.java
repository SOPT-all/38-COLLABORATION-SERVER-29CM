package org.sopt.domain.showcase.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "showcase_sections",
        indexes = {
                @Index(name = "idx_showcase_sections_display_order_id", columnList = "display_order, id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowcaseSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String theme;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private Integer displayOrder;
}
