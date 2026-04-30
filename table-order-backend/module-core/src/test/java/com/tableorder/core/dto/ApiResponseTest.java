package com.tableorder.core.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    @DisplayName("ok(data) - 데이터와 함께 성공 응답을 생성한다")
    void ok_withData() {
        ApiResponse<String> response = ApiResponse.ok("hello");

        assertThat(response.success()).isTrue();
        assertThat(response.data()).isEqualTo("hello");
        assertThat(response.message()).isNull();
    }

    @Test
    @DisplayName("ok(data, message) - 데이터와 메시지를 포함한 성공 응답을 생성한다")
    void ok_withDataAndMessage() {
        ApiResponse<Integer> response = ApiResponse.ok(42, "생성 완료");

        assertThat(response.success()).isTrue();
        assertThat(response.data()).isEqualTo(42);
        assertThat(response.message()).isEqualTo("생성 완료");
    }

    @Test
    @DisplayName("ok() - 데이터 없이 성공 응답을 생성한다")
    void ok_noData() {
        ApiResponse<Void> response = ApiResponse.ok();

        assertThat(response.success()).isTrue();
        assertThat(response.data()).isNull();
        assertThat(response.message()).isNull();
    }
}
