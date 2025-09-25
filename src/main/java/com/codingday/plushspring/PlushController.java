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
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlushController {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final PlushRepository plushRepository;

    @PostMapping("/buy-heavy")
    @Transactional
    public String buyHeavy(@RequestBody List<BuyRequest> requests) {
        if (requests.isEmpty()) {
            return "No orders sent";
        }

        Long customerId = requests.get(0).getCustomerId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<Plush> allPlushes = plushRepository.findAll();

        for (BuyRequest req : requests) {
            List<Plush> plushesForOrder = allPlushes.stream()
                    .filter(p -> req.getPlushIds().contains(p.getId().toString()))
                    .toList();

            if (plushesForOrder.isEmpty()) continue;

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
        }

        return "Processed " + requests.size() + " orders";
    }
    @GetMapping("/test-heap")
    public void test() {
        List<byte[]> leak = new ArrayList<>();
        while (true) {
            leak.add(new byte[1024 * 1024]);
        }
    }

}
