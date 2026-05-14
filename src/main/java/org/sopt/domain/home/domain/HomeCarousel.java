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
        name = "home_carousels",
        indexes = {
                @Index(name = "idx_home_carousels_order_id", columnList = "display_order, id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HomeCarousel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false, length = 255)
    private String altText;

    @Column(nullable = false)
    private int displayOrder;
}
