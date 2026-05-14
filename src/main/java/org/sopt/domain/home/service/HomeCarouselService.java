package org.sopt.domain.home.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.sopt.domain.home.domain.HomeCarousel;
import org.sopt.domain.home.dto.response.HomeCarouselListResponse;
import org.sopt.domain.home.dto.response.HomeCarouselResponse;
import org.sopt.domain.home.repository.HomeCarouselRepository;
import org.sopt.global.s3.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeCarouselService {

    private final HomeCarouselRepository homeCarouselRepository;
    private final S3Service s3Service;

    public HomeCarouselListResponse getCarousels() {
        List<HomeCarousel> carousels = homeCarouselRepository.findAllByOrderByDisplayOrderAscIdAsc();

        List<HomeCarouselResponse> responses = carousels.stream()
                .map(carousel -> new HomeCarouselResponse(
                        carousel.getId(),
                        s3Service.generatePresignedUrlOrNull(carousel.getImageUrl()),
                        carousel.getAltText()
                ))
                .toList();

        return new HomeCarouselListResponse(responses);
    }
}
