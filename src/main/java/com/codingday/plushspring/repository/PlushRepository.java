package com.codingday.plushspring.repository;

import com.codingday.plushspring.entity.Plush;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlushRepository extends JpaRepository<Plush, Long> {
}
