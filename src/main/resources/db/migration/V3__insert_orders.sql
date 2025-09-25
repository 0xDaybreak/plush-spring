DO $$
    DECLARE
        amt numeric;
    BEGIN
        FOR i IN 1..500000 LOOP
                amt := (random()*200 + 10)::numeric;  -- cast only, no rounding
                INSERT INTO orders (order_date, total_amount, customer_id)
                VALUES ('2025-09-24'::date + (i % 30), amt, 1);
            END LOOP;
    END
$$;
