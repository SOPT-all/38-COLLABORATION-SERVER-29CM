package org.sopt.global.s3.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.global.code.GlobalSuccessCode;
import org.sopt.global.response.CommonApiResponse;
import org.sopt.global.s3.service.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

@Profile("!prod")
@Tag(name = "S3 Test", description = "S3 연결 테스트 API")
@RestController
@RequestMapping("/api/v1/test/s3")
@RequiredArgsConstructor
public class S3TestController {

    private final S3Client s3Client;
    private final S3Service s3Service;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Operation(
            summary = "S3 버킷 연결 확인",
            description = "설정된 S3 버킷에 headBucket 요청을 보내 연결 및 접근 권한을 확인합니다."
    )
    @GetMapping("/connection")
    public ResponseEntity<CommonApiResponse<Map<String, Object>>> testConnection() {
        s3Client.headBucket(builder -> builder.bucket(bucketName));

        Map<String, Object> result = new HashMap<>();
        result.put("status", "SUCCESS");
        result.put("message", "S3 버킷 연결 성공");
        result.put("bucketName", bucketName);
        result.put("region", region);

        return CommonApiResponse.successResponse(GlobalSuccessCode.OK, result);
    }

    @Operation(
            summary = "S3 객체 목록 조회",
            description = "설정된 S3 버킷에서 prefix 조건에 해당하는 객체 목록을 최대 10개까지 조회합니다."
    )
    @GetMapping("/list")
    public ResponseEntity<CommonApiResponse<Map<String, Object>>> listObjects(
            @Parameter(description = "조회할 S3 객체 key prefix입니다. 비워두면 전체 객체를 조회합니다.", example = "test/")
            @RequestParam(defaultValue = "") String prefix
    ) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .maxKeys(10)
                .build();

        List<Map<String, Object>> objects = s3Client.listObjectsV2(request)
                .contents()
                .stream()
                .map(s3Object -> {
                    Map<String, Object> object = new HashMap<>();
                    object.put("key", s3Object.key());
                    object.put("size", s3Object.size());
                    object.put("lastModified", s3Object.lastModified().toString());
                    return object;
                })
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("bucketName", bucketName);
        result.put("prefix", prefix.isBlank() ? "전체" : prefix);
        result.put("objectCount", objects.size());
        result.put("objects", objects);

        return CommonApiResponse.successResponse(GlobalSuccessCode.OK, result);
    }

    @Operation(
            summary = "S3 Presigned URL 생성",
            description = "요청한 objectKey에 대해 일정 시간 동안 접근 가능한 S3 Presigned URL을 생성합니다."
    )
    @GetMapping("/presigned-url")
    public ResponseEntity<CommonApiResponse<Map<String, Object>>> generatePresignedUrl(
            @Parameter(description = "Presigned URL을 생성할 S3 객체 key입니다.", example = "test/sample.png")
            @RequestParam String objectKey
    ) {
        String presignedUrl = s3Service.generatePresignedUrl(objectKey);

        Map<String, Object> result = new HashMap<>();
        result.put("objectKey", objectKey);
        result.put("presignedUrl", presignedUrl);
        result.put("expiresIn", "3600 seconds");

        return CommonApiResponse.successResponse(GlobalSuccessCode.OK, result);
    }
}
