package com.sfwl.bh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.sfwl.bh.entity.Test;
import com.sfwl.bh.entity.response.BaseModel;
import com.sfwl.bh.enums.ResultStatus;
import com.sfwl.bh.service.ITestService;
import com.sfwl.bh.service.IUserService;
import com.sfwl.bh.vo.Select;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.jpush.api.push.model.PushModel.gson;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/8 16:19
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ITestService testService;
    @Autowired
    private IUserService userService;
    @Value("${upload.uploadPath}")
    private static String uploadPath;
    @GetMapping(value = "/hello")
    public Mono<String> hello(@RequestParam String name) {
        return Mono.just(name);
    }


    @GetMapping(value = "/datetime", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Date> datetime() {
        return Flux.interval(Duration.ofMillis(500)).map(l -> new Date());
    }


    @PostMapping(value = "/addTest")
    public Mono<BaseModel<String>>  add(@RequestBody Test test){
        return  Mono.just(testService.save(test) ? new BaseModel<>(ResultStatus.SUCCESS) : new BaseModel<>(ResultStatus.FAIL));
    }

    @DeleteMapping(value = "/deleteTest")
    public Mono<BaseModel<String>>  delete(@RequestBody Test test){
        return  Mono.just(testService.removeById(test.getId()) ? new BaseModel<>(ResultStatus.SUCCESS) : new BaseModel<>(ResultStatus.FAIL));
    }

    @GetMapping("/list")
    public Mono<BaseModel<List<Test>>> list() {
        LambdaQueryWrapper<Test> lambdaQueryWrapper = new LambdaQueryWrapper<Test>()
                .select(Test::getId, Test::getName);
        return Mono.just(new BaseModel<>(ResultStatus.SUCCESS, testService.list(lambdaQueryWrapper)));
    }

    @PostMapping("/updateTest")
    public  Mono<BaseModel<String>> update(@RequestBody Test test) {
        //boolean flag = testService.updateById(test);
        LambdaQueryWrapper lambdaQueryWrapper=new LambdaQueryWrapper<Test>().eq(Test::getId, test.getId());
        boolean flag = testService.update(test,lambdaQueryWrapper);
        return Mono.just(flag==true?new BaseModel<>(ResultStatus.SUCCESS, "修改成功"):new BaseModel<>(ResultStatus.FAIL,"修改失敗"));
    }

    @GetMapping("Test")
    public Flux test(){
        return (Flux) Flux.range(1, 1000).take(10).subscribe(System.out::println);
    }

    @GetMapping("Test2")
    public Mono<BaseModel<List<Test>>> getMyTest(){
        Select select=new Select();
        select.setId(1);
        select.setName("组");
        List<Test> userInfoList = testService.findResultByInfo(select);
        return Mono.just(new BaseModel<>(ResultStatus.SUCCESS, userInfoList));
    }

    @GetMapping("Test3")
    public Mono<BaseModel<List<Test>>> selectAll(){
        LambdaQueryWrapper<Test> lambda = new LambdaQueryWrapper<Test>();
        lambda.likeLeft(Test::getName,"娟").in(Test::getId,Arrays.asList(1,2,3,4));
        List<Test> list = testService.selectAll(lambda);
        return Mono.just(new BaseModel<>(ResultStatus.SUCCESS, list));
    }

    @GetMapping("Test4")
    public Mono<BaseModel<Integer>>updateMyTest(){
       Test test=new Test();
       test.setId(3);
       test.setOrderid(11);
        int count = testService.updateByInfo(test);
        return Mono.just(new BaseModel<>(ResultStatus.SUCCESS, count));
    }

    @GetMapping("Test5")
    public void testMap() throws InterruptedException {
       /* Flux.range(1,10)
                .subscribe(System.out::println);*/
//        Flux.interval(Duration.ofSeconds(2)).doOnNext(System.out::println).blockFirst();
       /* Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                sink.next(i);
            }
            sink.complete();
        }).subscribe(System.out::println);*/
      // Flux.range(0,100).buffer(40).subscribe(System.out::println);
      /* Flux.range(0,100).bufferTimeout(20,Duration.ofSeconds(5)).subscribe(System.out::println);*/
      /*  Flux.range(1, 100).buffer(20).subscribe(System.out::println);
        Flux.range(1, 10).bufferUntil(i -> i % 2 == 0).subscribe(System.out::println);
        Flux.range(1, 10).bufferWhile(i -> i % 2 == 0).subscribe(System.out::println);*/
//        Flux.range(1, 100).window(20).subscribe(System.out::println);
        //Flux.interval(Duration.ofMillis(1000)).window(1001).take(2).toStream().forEach(System.out::println);
    /*    Flux.range(1, 1000).take(10).subscribe(System.out::println);
        Flux.range(1, 1000).takeLast(10).subscribe(System.out::println);
        Flux.range(1, 1000).takeWhile(i -> i < 10).subscribe(System.out::println);
        Flux.range(1, 1000).takeUntil(i -> i == 10).subscribe(System.out::println);*/
       /* Mono.empty().subscribe(System.out::println);
        Flux.just("hello")
                .subscribe(System.out::println);*/
        Mono.error(new RuntimeException("error")).subscribe(System.out::println, System.err::println);
        Mono.never().subscribe(System.out::println);

    }
    @GetMapping("Test6")
    public void testMono() throws InterruptedException {
//        Mono.just(6000).subscribe(System.out::println);
        //      Mono.delay(Duration.ofSeconds(3)).doOnNext(System.out::println).block();
        //Mono.just(3000).doOnNext(System.out::println).block();
        Mono.create(monoSink -> monoSink.success("hello")).subscribe(System.out::println);
    }

    @GetMapping("Test7")
    public String testResp() throws InterruptedException {
//        Mono.just(6000).subscribe(System.out::println);
        //      Mono.delay(Duration.ofSeconds(3)).doOnNext(System.out::println).block();
        //Mono.just(3000).doOnNext(System.out::println).block();
        return "hello";
    }

    public static void main(String[] args) {
       String test="hello_%s_man";
       System.out.println( String.format(test,"xiaoM"));
         String a="{\"id\":\"3\",\"name\":\"hhhhhhhhhhhhh\"}";
        Gson gson = new Gson();
        System.out.println(gson.fromJson(a,Test.class));;

    }
}
