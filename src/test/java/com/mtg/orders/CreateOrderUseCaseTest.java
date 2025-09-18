package com.mtg.orders;

import com.mtg.orders.adapters.out.persistence.OrderRepository;
import com.mtg.orders.domain.model.CardOrder;
import com.mtg.orders.domain.model.OrderStatus;
import com.mtg.orders.domain.usecase.CreateCardOrderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateCardOrderUseCaseTest {

    private OrderRepository repository;
    private CreateCardOrderUseCase useCase;

    @BeforeEach
    void setup() {
        repository = mock(OrderRepository.class);
        useCase = new CreateCardOrderUseCase(repository);
    }

    @Test
    void shouldThrowException_WhenQuantityIsZeroOrNegative() {
        CardOrder invalidOrder = new CardOrder("Alex", "Black Lotus", 0);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(invalidOrder));
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowException_WhenCardNameIsBlank() {
        CardOrder invalidOrder = new CardOrder("Alex", "   ", 2);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(invalidOrder));
        verify(repository, never()).save(any());
    }

    @Test
    void shouldSaveOrderWithPendingStatus_WhenValid() {
        CardOrder order = new CardOrder("Alex", "Mox Emerald", 3);

        CardOrder savedOrder = new CardOrder("Alex", "Mox Emerald", 3);
        savedOrder.setStatus(OrderStatus.PENDING);

        when(repository.save(order)).thenReturn(savedOrder);

        CardOrder result = useCase.execute(order);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals("Mox Emerald", result.getCardName());
        assertEquals(3, result.getQuantity());
        verify(repository).save(order);
    }
}
