package com.example.server.Controller

import com.example.server.TestEntity
import com.example.server.TestService
import org.springframework.http.MediaType
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
        method = arrayOf(RequestMethod.GET),
        produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun test(): TestResponse{
        return TestResponse(status = true, service.getAll())
    }

    @RequestMapping(
        path = arrayOf("/test2"),
        method = arrayOf(RequestMethod.GET),
        produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun testWithParams(@RequestParam(value = "id") id: Int): TestResponse{
        return TestResponse(status = true, listOf(service.get(id)));
    }
}