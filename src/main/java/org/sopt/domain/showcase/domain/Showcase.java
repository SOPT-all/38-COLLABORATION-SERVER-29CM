package org.sopt.domain.showcase.domain;

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

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Table(
        name = "showcases",
        indexes = {
                @Index(name = "idx_showcases_section_display_order_id", columnList = "showcase_section_id, display_order, id"),
                @Index(name = "idx_showcases_featured_display_order_id", columnList = "is_featured, display_order, id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Showcase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showcase_section_id", nullable = false)
    private ShowcaseSection section;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ColumnDefault("false")
    @Column(name = "is_featured", nullable = false)
    private boolean isFeatured;

    @Column(nullable = false)
    private Integer displayOrder;
}
