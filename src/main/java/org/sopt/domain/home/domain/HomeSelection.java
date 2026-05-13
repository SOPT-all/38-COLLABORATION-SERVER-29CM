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

@Entity
@Getter
@Table(
        name = "home_selections",
        indexes = {
                @Index(name = "idx_home_selections_section_order_id", columnList = "home_section_id, display_order, id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HomeSelection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_section_id", nullable = false)
    private HomeSection homeSection;

    @Column(name = "home_section_id", nullable = false, insertable = false, updatable = false)
    private Long homeSectionId;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private int displayOrder;
}
