package com.capstone.favicon.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 수정 시 실제로 반영되는 필드만 받는다. 엔티티를 그대로 받으면 심사 상태나 작성자까지 요청 바디로 들어온다.
 * fileUrl 은 업로드 시 서버가 정한다. 클라이언트가 바꿀 수 있으면 심사(updateReviewStatus)가 그 값으로
 * S3 키를 만들어 삭제/이동하므로, 버킷의 임의 객체를 지울 수 있게 된다.
 */
@Getter
@Setter
@NoArgsConstructor
public class DataRequestUpdateDto {

    private String purpose;
    private String title;
    private String content;
    private String organization;
}
