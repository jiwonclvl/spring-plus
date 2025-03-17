package org.example.expert.domain.log.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    /**
     * 요청된 정보 저장
     *
     * @param userId (요청한 유저의 아이디)
     * @param request (요청 정보)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRequestLog(Long userId, HttpServletRequest request) {
        logRepository.save(new Log(userId, request.getMethod(), request.getRequestURI()));
    }
}
