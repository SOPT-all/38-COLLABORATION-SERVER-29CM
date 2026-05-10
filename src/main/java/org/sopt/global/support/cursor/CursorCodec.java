package org.sopt.global.support.cursor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import lombok.RequiredArgsConstructor;
import org.sopt.global.code.GlobalErrorCode;
import org.sopt.global.exception.BaseException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CursorCodec {

    private final ObjectMapper objectMapper;

    public String encode(Object payload) {
        if (payload == null) {
            throw new BaseException(GlobalErrorCode.INVALID_REQUEST);
        }

        try {
            String json = objectMapper.writeValueAsString(payload);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException exception) {
            throw new BaseException(GlobalErrorCode.INVALID_REQUEST);
        }
    }

    public <T> T decode(String cursor, Class<T> payloadType) {
        if (cursor == null || cursor.isBlank()) {
            throw new BaseException(GlobalErrorCode.INVALID_REQUEST);
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(cursor);
            T payload = objectMapper.readValue(decoded, payloadType);
            validate(payload);
            return payload;
        } catch (IllegalArgumentException | IOException exception) {
            throw new BaseException(GlobalErrorCode.INVALID_REQUEST);
        }
    }

    private void validate(Object payload) {
        if (payload == null) {
            throw new BaseException(GlobalErrorCode.INVALID_REQUEST);
        }

        if (payload instanceof CursorPayload cursorPayload) {
            cursorPayload.validate();
        }
    }
}
