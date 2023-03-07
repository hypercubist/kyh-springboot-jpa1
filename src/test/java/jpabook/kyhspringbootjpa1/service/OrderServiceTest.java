package jpabook.kyhspringbootjpa1.service;

import jpabook.kyhspringbootjpa1.domain.Address;
import jpabook.kyhspringbootjpa1.domain.Member;
import jpabook.kyhspringbootjpa1.domain.Order;
import jpabook.kyhspringbootjpa1.domain.OrderStatus;
import jpabook.kyhspringbootjpa1.domain.item.Book;
import jpabook.kyhspringbootjpa1.exception.NotEnoughStockException;
import jpabook.kyhspringbootjpa1.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Test
    public void 상품주문() throws Exception {

        //given
        Member member = createMember();

        Book book = createBook("반지의 제왕", 30000, 10);

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(getOrder.getOrderItems().size()).isEqualTo(1);
        assertThat(getOrder.getTotalPrice()).isEqualTo(30000 * orderCount);
        assertThat(book.getStockQuantity()).isEqualTo(8);

    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook("반지의 제왕", 30000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(book.getStockQuantity()).isEqualTo(10);
    }

    @Test
    public void 상품주문_재고수량초과() {
        //given
        Member member = createMember();
        Book book = createBook("반지의 제왕", 30000, 10);

        int orderCount = 11;

        //when

        //then
        assertThatThrownBy(() -> orderService.order(member.getId(), book.getId(), orderCount)).isInstanceOf(NotEnoughStockException.class);
    }


    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "논현로", "24124"));
        em.persist(member);
        return member;
    }
}