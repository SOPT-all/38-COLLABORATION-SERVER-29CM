package org.sopt.domain.home.domain;

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
        name = "home_sections",
        indexes = {
                @Index(name = "idx_home_sections_order_id", columnList = "display_order, id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HomeSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, length = 500)
    private String heroImageUrl;

    @Column(nullable = false)
    private int displayOrder;
}
