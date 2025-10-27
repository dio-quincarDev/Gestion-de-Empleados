package com.employed.bar.infrastructure.adapter.mapper.in;

import com.employed.bar.domain.model.payment.AchPaymentMethod;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.infrastructure.adapter.in.mapper.EmployeeApiMapper;
import com.employed.bar.infrastructure.dto.domain.EmployeeDto;
import com.employed.bar.infrastructure.dto.payment.AchPaymentMethodDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeApiMapperTest {

    private EmployeeApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(EmployeeApiMapper.class);
    }

    @Test
    void toDomain_withAchDto_shouldCreateDomainWithAchPaymentMethod() {
        // Arrange
        AchPaymentMethodDto achDto = new AchPaymentMethodDto();
        achDto.setBankName("Test Bank");
        achDto.setAccountNumber("12345");

        EmployeeDto dto = new EmployeeDto();
        dto.setPaymentMethod(achDto);
        dto.setName("Test Employee");

        // Act
        EmployeeClass domain = mapper.toDomain(dto);

        // Assert
        assertNotNull(domain);
        assertNotNull(domain.getPaymentMethod(), "PaymentMethod in domain object should not be null");
        assertTrue(domain.getPaymentMethod() instanceof AchPaymentMethod, "PaymentMethod should be an instance of AchPaymentMethod");

        AchPaymentMethod ach = (AchPaymentMethod) domain.getPaymentMethod();
        assertEquals("Test Bank", ach.getBankName());
        assertEquals("12345", ach.getAccountNumber());
    }

    @Test
    void toDto_withAchPaymentMethod_shouldCreateDtoWithAchDto() {
        // Arrange
        AchPaymentMethod ach = new AchPaymentMethod("Test Bank", "12345", null);
        EmployeeClass domain = new EmployeeClass();
        domain.setPaymentMethod(ach);

        // Act
        EmployeeDto dto = mapper.toDto(domain);

        // Assert
        assertNotNull(dto);
        assertNotNull(dto.getPaymentMethod(), "PaymentMethodDto in DTO should not be null");
        assertTrue(dto.getPaymentMethod() instanceof AchPaymentMethodDto, "PaymentMethodDto should be an instance of AchPaymentMethodDto");

        AchPaymentMethodDto achDto = (AchPaymentMethodDto) dto.getPaymentMethod();
        assertEquals("Test Bank", achDto.getBankName());
        assertEquals("12345", achDto.getAccountNumber());
    }
}