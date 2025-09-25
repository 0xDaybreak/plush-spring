package com.codingday.plushspring;

import com.codingday.plushspring.entity.Customer;
import com.codingday.plushspring.entity.Order;
import com.codingday.plushspring.entity.Plush;
import com.codingday.plushspring.model.BuyRequest;
import com.codingday.plushspring.repository.CustomerRepository;
import com.codingday.plushspring.repository.OrderRepository;
import com.codingday.plushspring.repository.PlushRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlushController {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final PlushRepository plushRepository;

    @PostMapping("/buy-heavy")
    public String buyHeavy(@RequestBody BuyRequest req) {


        Long customerId = req.getCustomerId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<Plush> allPlushes = plushRepository.findAll();

        List<Plush> plushesForOrder = allPlushes.stream()
                .filter(p -> req.getPlushIds().contains(p.getId().toString()))
                .toList();


        Order order = new Order();
        order.setCustomer(customer);
        order.setPlushies(plushesForOrder);
        order.setTotalAmount(plushesForOrder.stream()
                .map(p -> p.getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        order.setOrderDate(LocalDateTime.now());

        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        boolean hasRecentOrders = orderRepository.findByCustomerIdAndOrderDateAfter(customerId, sixMonthsAgo)
                .size() > 0;

        if (hasRecentOrders) {
            BigDecimal discount = order.getTotalAmount().multiply(BigDecimal.valueOf(0.1));
            order.setTotalAmount(order.getTotalAmount().subtract(discount));
        }

        orderRepository.save(order);
        String res = "Processed " + req + " order";
        System.out.println(res);

        return res;
    }

    @GetMapping("/test-heap")
    public void test() {
        List<byte[]> leak = new ArrayList<>();
        while (true) {
            leak.add(new byte[1024 * 1024]);
        }
    }

}
