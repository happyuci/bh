package com.sfwl.bh.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sfwl.bh.entity.OpLog;
import com.sfwl.bh.entity.Test;
import com.sfwl.bh.vo.Select;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author huhy
 * @since 2020-05-09
 */
public interface ITestService extends IService<Test> {
    List<Test> selectAll(LambdaQueryWrapper lambdaQueryWrapper);

    List<Test> findResultByInfo(Select select);

    int updateByInfo(Test test);

}
