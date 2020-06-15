package com.sfwl.bh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sfwl.bh.entity.Script;
import com.sfwl.bh.entity.Test;
import com.sfwl.bh.mapper.ScriptMapper;
import com.sfwl.bh.mapper.TestMapper;
import com.sfwl.bh.service.IScriptService;
import com.sfwl.bh.service.ITestService;
import com.sfwl.bh.vo.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author huhy
 * @since 2020-05-13
 */
@Service

public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements ITestService {

    @Resource
    private TestMapper testMapper;


    public List<Test> selectAll(LambdaQueryWrapper lambdaQueryWrapper){
        List<Test> testlist = testMapper.selectAll(lambdaQueryWrapper);
        return testlist;
    };

    public List<Test> findResultByInfo(Select select){
        List<Test> testlist = testMapper.findResultByInfo(select);
        return testlist;
    };

    public int updateByInfo(Test test){
        int a = testMapper.updateByInfo(test);
        return a;
    };
}
