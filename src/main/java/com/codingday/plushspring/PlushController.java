package com.codingday.plushspring;

import com.codingday.plushspring.entity.Customer;
import com.codingday.plushspring.entity.Order;
import com.codingday.plushspring.entity.Plush;
import com.codingday.plushspring.model.BuyRequest;
import com.codingday.plushspring.repository.CustomerRepository;
import com.codingday.plushspring.repository.OrderRepository;
import com.codingday.plushspring.repository.PlushRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlushController {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final PlushRepository plushRepository;

    private static final Map<Long, BuyRequest> cache = new HashMap<>();

    @PostMapping("/buy-heavy")
    public String buyHeavy(@RequestBody List<BuyRequest> reqs) {


    }

}
