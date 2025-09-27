package com.codingday.plushspring;

import com.codingday.plushspring.entity.Customer;
import com.codingday.plushspring.entity.Order;
import com.codingday.plushspring.entity.Plush;
import com.codingday.plushspring.model.BuyRequest;
import com.codingday.plushspring.repository.CustomerRepository;
import com.codingday.plushspring.repository.OrderRepository;
import com.codingday.plushspring.repository.PlushRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlushControllerTest {

    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;
    private PlushRepository plushRepository;
    private PlushController plushController;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        orderRepository = mock(OrderRepository.class);
        plushRepository = mock(PlushRepository.class);
        plushController = new PlushController(customerRepository, orderRepository, plushRepository);
    }

    @Test
    void testCustomerExists() {
        Customer customer = new Customer();
        customer.setId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        BuyRequest request = new BuyRequest();
        request.setCustomerId(1L);

        assertDoesNotThrow(() -> plushController.buyHeavy(List.of(request)));
    }
    @Test
    void testPlushFiltering() {
        Customer customer = new Customer();
        customer.setId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Plush plush1 = new Plush(); plush1.setId(100L); plush1.setPrice(BigDecimal.valueOf(50));
        Plush plush2 = new Plush(); plush2.setId(101L); plush2.setPrice(BigDecimal.valueOf(100));
        when(plushRepository.findAll()).thenReturn(List.of(plush1, plush2));

        BuyRequest request = new BuyRequest();
        request.setCustomerId(1L);
        request.setPlushIds(List.of("100"));

        plushController.buyHeavy(List.of(request));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());

        assertEquals(1, captor.getValue().getPlushies().size());
        assertEquals(BigDecimal.valueOf(50), captor.getValue().getTotalAmount());
    }
    @Test
    void testDiscountAppliedForRecentOrders() {
        Customer customer = new Customer();
        customer.setId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Plush plush = new Plush(); plush.setId(100L); plush.setPrice(BigDecimal.valueOf(200));
        when(plushRepository.findAll()).thenReturn(List.of(plush));
        when(orderRepository.findByCustomerIdAndOrderDateAfter(eq(1L), any()))
                .thenReturn(List.of(new Order()));

        BuyRequest request = new BuyRequest();
        request.setCustomerId(1L);
        request.setPlushIds(List.of("100"));

        plushController.buyHeavy(List.of(request));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());

        BigDecimal expected = BigDecimal.valueOf(200).multiply(BigDecimal.valueOf(0.9));
        assertEquals(expected.setScale(1), captor.getValue().getTotalAmount().setScale(1));
    }
    @Test
    void testMultipleBuyRequests() {
        Customer customer = new Customer();
        customer.setId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Plush plush1 = new Plush(); plush1.setId(100L); plush1.setPrice(BigDecimal.valueOf(50));
        Plush plush2 = new Plush(); plush2.setId(101L); plush2.setPrice(BigDecimal.valueOf(100));
        when(plushRepository.findAll()).thenReturn(List.of(plush1, plush2));
        when(orderRepository.findByCustomerIdAndOrderDateAfter(eq(1L), any()))
                .thenReturn(List.of());

        BuyRequest req1 = new BuyRequest();
        req1.setCustomerId(1L);
        req1.setPlushIds(List.of("100"));

        BuyRequest req2 = new BuyRequest();
        req2.setCustomerId(1L);
        req2.setPlushIds(List.of("101"));

        String res = plushController.buyHeavy(List.of(req1, req2));

        assertEquals("Processed 2 order", res);
        verify(orderRepository, times(2)).save(any());
    }

    @Test
    void testCustomerNotFound() {
        BuyRequest request = new BuyRequest();
        request.setCustomerId(999L);
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> plushController.buyHeavy(List.of(request)));

        assertEquals("Customer not found", ex.getMessage());
    }

    @Test
    void testCacheIsUsed() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Plush plush = new Plush();
        plush.setId(100L);
        plush.setPrice(BigDecimal.valueOf(50));
        when(plushRepository.findAll()).thenReturn(List.of(plush));
        when(orderRepository.findByCustomerIdAndOrderDateAfter(eq(1L), any()))
                .thenReturn(List.of());

        BuyRequest request = new BuyRequest();
        request.setCustomerId(1L);
        request.setPlushIds(List.of("100"));

        plushController.buyHeavy(List.of(request));

        Field cacheField = PlushController.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        Map<Long, BuyRequest> cache = (Map<Long, BuyRequest>) cacheField.get(null);

        assertFalse(cache.isEmpty(), "Cache should not be empty");
        assertTrue(cache.containsValue(request), "Cache should contain the request");
    }

}
