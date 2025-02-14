package com.group12.ecommerce.service.implementService.schedule;

import com.group12.ecommerce.repository.token.IInvalidatedTokenRepository;
import com.group12.ecommerce.service.interfaceService.schedule.IScheduleService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduleService implements IScheduleService {
    @Autowired
    IInvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void deleteAllInvalidTokens() {
        invalidatedTokenRepository.deleteAll();
        log.info("Expired tokens deleted successfully.");
    }
}
