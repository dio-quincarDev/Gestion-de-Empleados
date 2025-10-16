package com.employed.bar.infrastructure.adapter.mapper.out;

import com.employed.bar.domain.enums.BankAccount;
import com.employed.bar.domain.enums.PaymentMethodType;
import com.employed.bar.domain.model.payment.AchPaymentMethod;
import com.employed.bar.domain.model.payment.CashPaymentMethod;
import com.employed.bar.domain.model.payment.YappyPaymentMethod;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.mapper.EmployeeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeMapperTest {

    private EmployeeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(EmployeeMapper.class);
    }
    @Test
    void toEntity_withAchPaymentMethod_shouldMapFlattenedFields() {
        // Arrange
        EmployeeClass domain = new EmployeeClass();
        AchPaymentMethod paymentMethod = new AchPaymentMethod("Test Bank", "12345", BankAccount.SAVINGS);
        domain.setPaymentMethod(paymentMethod);

        // Act - Ahora el mapeo del payment method está incluido en toEntity()
        EmployeeEntity entity = mapper.toEntity(domain);

        // Assert - Todo funciona sin llamadas manuales
        assertNotNull(entity);
        assertEquals(PaymentMethodType.ACH, entity.getPaymentMethodType());
        assertEquals("Test Bank", entity.getBankName());
        assertEquals("12345", entity.getAccountNumber());
        assertEquals(BankAccount.SAVINGS, entity.getBankAccountType());
        assertNull(entity.getPhoneNumber());
    }

    @Test
    void toEntity_withYappyPaymentMethod_shouldMapFlattenedFields() {
        // Arrange
        EmployeeClass domain = new EmployeeClass();
        YappyPaymentMethod paymentMethod = new YappyPaymentMethod("66778899");
        domain.setPaymentMethod(paymentMethod);

        // Act
        EmployeeEntity entity = mapper.toEntity(domain);

        // Assert
        assertNotNull(entity);
        assertEquals(PaymentMethodType.YAPPY, entity.getPaymentMethodType());
        assertEquals("66778899", entity.getPhoneNumber());
        assertNull(entity.getBankName());
        assertNull(entity.getAccountNumber());
    }

    @Test
    void toDomain_fromAchEntity_shouldReconstructObject() {
        // Arrange
        EmployeeEntity entity = new EmployeeEntity();
        entity.setPaymentMethodType(PaymentMethodType.ACH);
        entity.setBankName("Test Bank");
        entity.setAccountNumber("12345");
        entity.setBankAccountType(BankAccount.SAVINGS);

        // Act
        EmployeeClass domain = mapper.toDomain(entity);

        // Assert
        assertNotNull(domain);
        assertNotNull(domain.getPaymentMethod());
        assertTrue(domain.getPaymentMethod() instanceof AchPaymentMethod);
        AchPaymentMethod ach = (AchPaymentMethod) domain.getPaymentMethod();
        assertEquals("Test Bank", ach.getBankName());
        assertEquals("12345", ach.getAccountNumber());
        assertEquals(BankAccount.SAVINGS, ach.getBankAccountType());
    }

    @Test
    void toDomain_fromYappyEntity_shouldReconstructObject() {
        // Arrange
        EmployeeEntity entity = new EmployeeEntity();
        entity.setPaymentMethodType(PaymentMethodType.YAPPY);
        entity.setPhoneNumber("66778899");

        // Act
        EmployeeClass domain = mapper.toDomain(entity);

        // Assert
        assertNotNull(domain);
        assertNotNull(domain.getPaymentMethod());
        assertTrue(domain.getPaymentMethod() instanceof YappyPaymentMethod);
        YappyPaymentMethod yappy = (YappyPaymentMethod) domain.getPaymentMethod();
        assertEquals("66778899", yappy.getPhoneNumber());
    }

    @Test
    void toDomain_fromCashEntity_shouldReconstructObject() {
        // Arrange
        EmployeeEntity entity = new EmployeeEntity();
        entity.setPaymentMethodType(PaymentMethodType.CASH);

        // Act
        EmployeeClass domain = mapper.toDomain(entity);

        // Assert
        assertNotNull(domain);
        assertNotNull(domain.getPaymentMethod());
        assertTrue(domain.getPaymentMethod() instanceof CashPaymentMethod);
    }
}