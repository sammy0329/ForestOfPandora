package com.ssafy.forest.service;

import com.ssafy.forest.domain.dto.request.ArticleCommentReplyReqDto;
import com.ssafy.forest.domain.dto.response.ArticleCommentReplyResDto;
import com.ssafy.forest.domain.entity.ArticleComment;
import com.ssafy.forest.domain.entity.ArticleCommentReply;
import com.ssafy.forest.domain.entity.Member;
import com.ssafy.forest.exception.CustomException;
import com.ssafy.forest.exception.ErrorCode;
import com.ssafy.forest.repository.ArticleCommentReplyRepository;
import com.ssafy.forest.repository.ArticleCommentRepository;
import com.ssafy.forest.repository.MemberRepository;
import com.ssafy.forest.security.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ArticleCommentReplyServiceImpl implements ArticleCommentReplyService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final ArticleCommentReplyRepository articleCommentReplyRepository;
    private final ArticleCommentRepository articleCommentRepository;

    public Member getMemberFromAccessToken(HttpServletRequest request) {
        // accessToken으로부터 Member 객체 추출
        Member memberFromAccessToken = tokenProvider.getMemberFromAccessToken(request);

        // memberFromAccessToken의 id로 최신 상태의 Member 객체 조회
        return memberRepository.findById(memberFromAccessToken.getId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }

    @Override
    public ArticleCommentReplyResDto create(HttpServletRequest request, Long commentId,
        ArticleCommentReplyReqDto articleCommentReplyReqDto) {
        // CommentId를 통해 Comment 엔티티 찾기
        ArticleComment articleComment = articleCommentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMENT));

        Member member = getMemberFromAccessToken(request);

        return ArticleCommentReplyResDto.from(articleCommentReplyRepository.save(
            ArticleCommentReply.of(articleCommentReplyReqDto, articleComment, member)));
    }

}