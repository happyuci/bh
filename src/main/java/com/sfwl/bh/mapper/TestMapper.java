package com.sfwl.bh.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sfwl.bh.entity.Script;
import com.sfwl.bh.entity.Test;
import com.sfwl.bh.vo.Select;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author huhy
 * @since 2020-05-13
 */
public interface TestMapper extends BaseMapper<Test> {

    List<Test> selectAll(@Param(Constants.WRAPPER) Wrapper<Test> testWrapper);

    List<Test> findResultByInfo(Select select);

    int updateByInfo(Test test);
}
