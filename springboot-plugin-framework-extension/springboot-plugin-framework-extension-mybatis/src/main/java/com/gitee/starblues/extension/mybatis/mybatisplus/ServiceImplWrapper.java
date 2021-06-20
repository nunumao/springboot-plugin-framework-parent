package com.gitee.starblues.extension.mybatis.mybatisplus;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Objects;

/**
 * mybatis plus ServiceImpl 的包装。解决原生mybatis plus 中ServiceImpl Mapper无法注入的问题
 * 升级mybatis-plus到3.4.3.1
 * @author starBlues
 * @version 2.4.0
 */
public class ServiceImplWrapper<M extends BaseMapper<T>, T> extends ServiceImpl<M,T> {

    public ServiceImplWrapper(M baseMapper) {
        this.baseMapper = Objects.requireNonNull(baseMapper);
    }
}
