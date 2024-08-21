package com.busanit501.logintest.service;

import com.busanit501.logintest.Util.JwtUtil;
import com.busanit501.logintest.domain.Member;
import com.busanit501.logintest.domain.MemberRole;
import com.busanit501.logintest.dto.MemberJoinDTO;
import com.busanit501.logintest.dto.upload.UploadResultDTO;
import com.busanit501.logintest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    @Value("${com.busanit501.upload.path}")
    private String uploadPath;

    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // JwtUtil 주입

    @Override
    public boolean checkMid(String mid) {
        Optional<Member> result = memberRepository.findByMid(mid);
        return result.isPresent();
    }

    @Override
    public void join(MemberJoinDTO memberJoinDTO) throws MidExistException {
        String mid = memberJoinDTO.getMid();
        Optional<Member> result = memberRepository.findByMid(mid);

        if (result.isPresent()) {
            throw new MidExistException();
        }

        Member member = dtoToEntity(memberJoinDTO);
        member.changePassword(passwordEncoder.encode(member.getMpw()));
        member.addRole(MemberRole.USER);

        // JWT 생성
        String accessToken = jwtUtil.generateAccessToken(mid);
        String refreshToken = jwtUtil.generateRefreshToken(mid);
        member.setRefreshToken(refreshToken); // 리프레시 토큰 저장

        log.info("joinMember: " + member);
        log.info("joinMember Roles: " + member.getRoleSet());

        memberRepository.save(member);
    }

    @Override
    public void update(MemberJoinDTO memberJoinDTO) throws MidExistException {
        // 기존 로직 유지
    }

    @Override
    public void updateSocial(String mid, String mpw) throws MidExistException {
        Optional<Member> result = memberRepository.findByEmail(mid);
        Member member = result.orElseThrow();

        member.changePassword(passwordEncoder.encode(mpw));
        member.addRole(MemberRole.USER);

        log.info("updateMember: " + member);
        log.info("updateMember Roles: " + member.getRoleSet());

        memberRepository.save(member);
    }

    @Override
    public UploadResultDTO uploadProfileImage(MultipartFile fileImageName) {
        log.info("MemberServiceImpl uploadFileDTO : " + fileImageName);
        if (fileImageName != null) {
            String originName = fileImageName.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            Path savePath = Paths.get(uploadPath, uuid + "_" + originName);

            boolean imgCheck = false;

            try {
                fileImageName.transferTo(savePath);

                if (Files.probeContentType(savePath).startsWith("image")) {
                    imgCheck = true;
                    File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originName);
                    Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            UploadResultDTO uploadResultDTO = UploadResultDTO.builder()
                    .uuid(uuid)
                    .fileName(originName)
                    .imgCheck(imgCheck)
                    .build();

            return uploadResultDTO;
        }
        return null;
    }

    @Override
    public String generateAccessToken(String username) {
        return jwtUtil.generateAccessToken(username);
    }

    @Override
    public String generateRefreshToken(String username) {
        return jwtUtil.generateRefreshToken(username);
    }


}
