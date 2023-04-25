package com.example.bgm.Controller

import com.example.bgm.TestEntity
import com.example.bgm.TestService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class TestResponse(val status: Boolean, val data: List<TestEntity>)

@RestController
class ServiceController {
    val service: TestService = TestService();

    @RequestMapping(
        path = arrayOf("/test"),
        method = arrayOf(RequestMethod.GET),)
    fun test(): TestResponse{
        return TestResponse(status = true, service.getAll())
    }

    @RequestMapping(
        path = arrayOf("/test2"),
        method = arrayOf(RequestMethod.GET),)
    fun testWithParams(@RequestParam(value = "id") id: Int): TestResponse{
        return TestResponse(status = true, listOf(service.get(id)));
    }
}