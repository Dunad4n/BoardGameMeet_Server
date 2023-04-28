package com.example.bgm

import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ServerApplicationTests {

//    private val persons1 = listOf(
//        Person("name", "nickname", "password", "secret word", Gender.Male, 20, "City", 5, listOf(), listOf()),
//        Person("name2", "nickname2", "password2", "secret word2", Gender.Male, 20, "City2", 10, listOf(), listOf())
//    )
//
//    private val persons2 = listOf(
//        Person("name1", "nickname1", "password1", "secret word1", Gender.Male, 20, "City1", 5, listOf(), listOf()),
//        Person("name2", "nickname2", "password2", "secret word2", Gender.Male, 20, "City2", 10, listOf(), listOf()),
//        Person("name3", "nickname3", "password3", "secret word3", Gender.Male, 20, "City3", 10, listOf(), listOf()),
//        Person("name4", "nickname4", "password4", "secret word4", Gender.Male, 20, "City4", 10, listOf(), listOf()),
//        Person("name5", "nickname5", "password5", "secret word5", Gender.Male, 20, "City5", 10, listOf(), listOf())
//    )
//
//    @Test
//    fun testFilterEvents() {
//        var events = mutableListOf<Event>()
//        events.add(Event("name1", "game1", "city1", "address1", LocalDateTime.now().minusHours(5), 12, 18, 25, persons1[0], persons1, persons1, "description", "items", listOf()))
//        events.add(Event("name2", "game2", "city2", "address2", LocalDateTime.now().minusDays(1), 12, 18, 25, persons1[0], listOf(), persons1, "description", "items", listOf()))
//        events.add(Event("name3", "game3", "city3", "address3", LocalDateTime.now().plusDays(10), 12, 18, 25, persons1[0], persons2, persons1, "description", "items", listOf()))
//        events.add(Event("name4", "game4", "city4", "address4", LocalDateTime.now().plusDays(10).plusHours(10), 10, 18, 25, persons1[0], persons1, persons1, "description", "items", listOf()))
//        events = eventService.filterEvents(events).toMutableList()
//        assert(events[0].name == "name3")
//        assert(events[1].name == "name4")
//    }
//
//    @Test
//    fun testSortEventsForMainPage() {
//        var events = mutableListOf<Event>()
//        events.add(Event("name1", "game1", "city1", "address1", LocalDateTime.now().plusHours(10), 12, 18, 25, persons1[0], persons1, persons1, "description", "items", listOf()))
//        events.add(Event("name2", "game2", "city2", "address2", LocalDateTime.now().plusHours(15), 12, 18, 25, persons1[0], listOf(), persons1, "description", "items", listOf()))
//        events.add(Event("name3", "game3", "city3", "address3", LocalDateTime.now().plusDays(10), 12, 18, 25, persons1[0], persons2, persons1, "description", "items", listOf()))
//        events.add(Event("name4", "game4", "city4", "address4", LocalDateTime.now().plusDays(10).plusHours(10), 10, 18, 25, persons1[0], persons1, persons1, "description", "items", listOf()))
//        events = eventService.getSortedEventsForMainPage(events).toMutableList()
//        assert(events[0].name == "name3")
//        assert(events[1].name == "name4")
//        assert(events[2].name == "name1")
//        assert(events[3].name == "name2")
//    }
//
//    @Test
//    fun testSortEventsForUserEventPage() {
//        var events = mutableListOf<Event>()
//        events.add(Event("name1", "game1", "city1", "address1", LocalDateTime.now().plusHours(10), 12, 18, 25, persons1[0], persons1, persons1, "description", "items", listOf()))
//        events.add(Event("name2", "game2", "city2", "address2", LocalDateTime.now().plusHours(15), 12, 18, 25, persons1[0], listOf(), persons1, "description", "items", listOf()))
//        events.add(Event("name3", "game3", "city3", "address3", LocalDateTime.now().minusDays(10), 12, 18, 25, persons1[0], persons2, persons1, "description", "items", listOf()))
//        events.add(Event("name4", "game4", "city4", "address4", LocalDateTime.now().minusDays(10).plusHours(10), 10, 18, 25, persons1[0], persons1, persons1, "description", "items", listOf()))
//        events = eventService.sortEventsForUserEventPage(events).toMutableList()
//        assert(events[0].date.toEpochSecond(ZoneOffset.UTC) <= events[1].date.toEpochSecond(ZoneOffset.UTC))
//        assert(events[2].date.toEpochSecond(ZoneOffset.UTC) >= events[3].date.toEpochSecond(ZoneOffset.UTC))
//    }

}
