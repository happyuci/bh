package com.sfwl.bh.controller;


import com.sfwl.bh.entity.Suggest;
import com.sfwl.bh.entity.response.BaseModel;
import com.sfwl.bh.enums.ResultStatus;
import com.sfwl.bh.service.ISuggestService;
import com.sfwl.bh.service.IUserService;
import com.sfwl.bh.service.impl.MultiPartFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author huhy
 * @since 2020-05-09
 */
@RestController
@RequestMapping("/suggest")
public class SuggestController {

    @Autowired
    private MultiPartFileService multiPartFileService;

    @Autowired
    private ISuggestService suggestService;
    @Autowired
    private IUserService userService;

    @PostMapping("/add")
    public Mono<BaseModel<Suggest>> add(@RequestBody Suggest suggest, Authentication authentication) {
        suggest.setUserId(userService.getUserIdByAuthentication(authentication));
        if (StringUtils.isNotBlank(suggest.getImages())) {
            suggest.setImages(multiPartFileService.removeUrlPrefix(suggest.getImages()));
        }
        return Mono.just(suggestService.save(suggest) ? new BaseModel<>(ResultStatus.SUCCESS) : new BaseModel<>(ResultStatus.FAIL));
    }

}
