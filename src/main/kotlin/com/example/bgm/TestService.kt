package com.example.bgm

class TestService {
    private val data = listOf(TestEntity("Test1", 1),
        TestEntity("Test2", 2),
        TestEntity("Test3", 3),
        TestEntity("Test4", 4),
        TestEntity("Test5", 5),)

    fun getAll() = data

    fun get(i: Int) = data[i]
}