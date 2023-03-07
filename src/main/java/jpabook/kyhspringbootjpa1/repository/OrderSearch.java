package jpabook.kyhspringbootjpa1.repository;

import jpabook.kyhspringbootjpa1.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {

    private String memberName; //회원 이름
    private OrderStatus orderStatus;
}
