package com.codingday.plushspring;

import com.codingday.plushspring.entity.Plush;
import com.codingday.plushspring.model.BuyRequest;
import com.codingday.plushspring.repository.PlushRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PlushController {

    private final PlushRepository plushRepo;
    private final List<Plush> ourPlushies = new ArrayList<>();

    public PlushController(PlushRepository plushRepo) {
        this.plushRepo = plushRepo;
    }

    @PostMapping("/buy")
    public Map<String, Object> buyPlush(@RequestBody BuyRequest req) {

        Plush plush = plushRepo.findById(req.getId()).orElseThrow(() -> new RuntimeException("Plush not found"));

        if (plush.getQuantity().compareTo(req.getQuantity()) < 0) {
            throw new RuntimeException("Insufficient quantity to make purchase");
        }
        BigDecimal remainingStock = plush.getQuantity().subtract(req.getQuantity());
        plush.setQuantity(remainingStock);

        BigDecimal totalPrice = req.getQuantity().multiply(plush.getPrice());
        plushRepo.save(plush);

        return Map.of("id", req.getId(), "name", plush.getName(), "totalPrice", totalPrice, "remainingStock", remainingStock);

    }

    @PostMapping("/buy-heavy")
    public String buyHeavy(@RequestBody BuyRequest req) {
        Plush plush = plushRepo.findById(req.getId())
                .orElseThrow();
        ourPlushies.add(plush);
        System.out.println("OUR PLUSHIES: " + ourPlushies);
        return "Bought " + req.getQuantity() + " plushes " + plush.getName();
    }

}
