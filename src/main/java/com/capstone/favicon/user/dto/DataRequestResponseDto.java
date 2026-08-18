package com.capstone.favicon.user.dto;

import com.capstone.favicon.user.domain.DataRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/** DataRequest 는 User 를 EAGER 로 물고 있어, 엔티티를 그대로 내보내면 비밀번호 해시까지 직렬화된다. */
@Getter
@AllArgsConstructor
public class DataRequestResponseDto {

    private Long dataRequestId;
    private Long userId;
    private String username;
    private String purpose;
    private String title;
    private String content;
    private LocalDate uploadDate;
    private String fileUrl;
    private String reviewStatus;
    private Long reviewedById;
    private LocalDate reviewDate;
    private String organization;

    public static DataRequestResponseDto from(DataRequest request) {
        return new DataRequestResponseDto(
                request.getDataRequestId(),
                request.getUser() == null ? null : request.getUser().getUserId(),
                // getUsername() 은 email 을 돌려준다. 그대로 쓰면 응답에 이메일이 실린다
                request.getUser() == null ? null : request.getUser().getDisplayName(),
                request.getPurpose(),
                request.getTitle(),
                request.getContent(),
                request.getUploadDate(),
                request.getFileUrl(),
                request.getReviewStatus() == null ? null : request.getReviewStatus().name(),
                request.getReviewedBy() == null ? null : request.getReviewedBy().getUserId(),
                request.getReviewDate(),
                request.getOrganization()
        );
    }
}
