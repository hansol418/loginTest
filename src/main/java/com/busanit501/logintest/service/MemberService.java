package com.busanit501.logintest.service;

import com.busanit501.logintest.domain.Member;
import com.busanit501.logintest.dto.MemberJoinDTO;
import com.busanit501.logintest.dto.upload.UploadResultDTO;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    // 중복 아이디 예외처리
    static class MidExistException extends Exception {

    }

    // 중복 아이디 검사
    boolean checkMid(String mid);

    void join(MemberJoinDTO memberJoinDTO) throws MidExistException;

    // 회원 수정 재사용. join
    void update(MemberJoinDTO memberJoinDTO) throws MidExistException;

    // 소셜 로그인 시 수정하는 서비스
    void updateSocial(String mid, String mpw) throws MidExistException;

    // 프로필 이미지 업로드
    UploadResultDTO uploadProfileImage(MultipartFile fileImageName);

    // 새로 추가: 회원가입 시 JWT 생성 및 저장
    String generateAccessToken(String username);
    String generateRefreshToken(String username);

    // DTO를 엔티티로 변환
    default Member dtoToEntity(MemberJoinDTO memberJoinDTO) {
        Member member = Member.builder()
                .mid(memberJoinDTO.getMid())
                .mpw(memberJoinDTO.getMpw())
                .email(memberJoinDTO.getEmail())
                .memberName(memberJoinDTO.getMemberName())
                .address(memberJoinDTO.getAddress())
                .uuid(memberJoinDTO.getUuid())
                .fileName(memberJoinDTO.getFileName())
                .build();

        return member;
    }

    // 엔티티를 DTO로 변환
    default MemberJoinDTO entityToDTO(Member member) {
        MemberJoinDTO memberJoinDTO = MemberJoinDTO.builder()
                .mid(member.getMid())
                .mpw(member.getMpw())
                .email(member.getEmail())
                .uuid(member.getUuid())
                .fileName(member.getFileName())
                .build();

        return memberJoinDTO;
    }
}
