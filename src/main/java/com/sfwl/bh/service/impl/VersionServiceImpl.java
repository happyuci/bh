package com.sfwl.bh.service.impl;

import com.sfwl.bh.entity.Version;
import com.sfwl.bh.mapper.VersionMapper;
import com.sfwl.bh.service.IVersionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author huhy
 * @since 2020-05-09
 */
@Service
public class VersionServiceImpl extends ServiceImpl<VersionMapper, Version> implements IVersionService {

}
