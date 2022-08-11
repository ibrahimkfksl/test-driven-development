package com.ibrahim.tdd.repositories;

import com.ibrahim.tdd.models.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //embedded database kullanmaya çalışmasını engelledik
class OrderRepositoryTest {

    //integration test

    //1 saat 54. dakikada kaldım
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Container
    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql"); //docker image ını kendisi pull ediyor

    @Test
    void it_should_find_orders(){
        //given
        Order order1 = Order.builder()
                .totalPrice(BigDecimal.valueOf(3)).build();
        Order order2 = Order.builder()
                .totalPrice(BigDecimal.valueOf(2)).build();

        Object id1 = this.testEntityManager.persistAndGetId(order1);
        Object id2 = this.testEntityManager.persistAndGetId(order2); //şuan ekleme işleme yaptık
        //this.testEntityManager.flush();

        //when
        List<Order> orders = this.orderRepository.findAll();

        //then
        then(orders).isNotEmpty();
        Order o1 = orders.get(0);
        Order o2 = orders.get(1);
        then(o1.getId()).isEqualTo(id1);
        then(o2.getId()).isEqualTo(id2);
    }

    //mysql static bir field olduğu için properties atamalarını bu şekilde yapıyoruz. Static olmasaydı properties dosyası üzerinden de yapılabilirdi.
    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry){
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.hibernate.database-platform", () -> "org.hibernate.dialect.MySQL5InnoDBDialect");
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", mySQLContainer::getDriverClassName);
    }
}