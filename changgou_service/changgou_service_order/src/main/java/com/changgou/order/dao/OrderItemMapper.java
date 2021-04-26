package com.changgou.order.dao;

import com.changgou.order.pojo.OrderItem;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface OrderItemMapper extends Mapper<OrderItem> {

}
