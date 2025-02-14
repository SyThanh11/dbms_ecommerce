package com.group12.ecommerce.service.schedule;

import com.group12.ecommerce.repository.token.IInvalidatedTokenRepository;
import com.group12.ecommerce.service.interfaceService.schedule.IScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.verify;

@SpringBootTest
class ScheduleServiceTest {
    @Autowired
    IScheduleService scheduleService;

    @MockitoBean
    IInvalidatedTokenRepository invalidatedTokenRepository;

    @Test
    void deleteAllInvalidTokens_success(){
        // When
        scheduleService.deleteAllInvalidTokens();

        // Then
        verify(invalidatedTokenRepository).deleteAll();
    }
}
