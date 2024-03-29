package com.example.demoinflearnrestapi.events;

import com.example.demoinflearnrestapi.events.common.BaseControllerTest;
import com.example.demoinflearnrestapi.events.common.RestDocsConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class EventControllerTest extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Test
    @DisplayName("정상적으로 이벤트를 생성하는 test")
    public void createEvent() throws Exception {

        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API DEVELOpment with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2010, 11, 7, 11, 11, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2010, 11, 8, 13, 11, 20))
                .beginEventDateTime(LocalDateTime.of(2010, 11, 25, 20, 30, 11))
                .endEventDateTime(LocalDateTime.of(2010, 11, 25, 20, 30, 12))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();
//        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.toString()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to query events"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime")
                                        .description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime")
                                        .description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime")
                                        .description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime")
                                        .description("date time of end of new event"),
                                fieldWithPath("location")
                                        .description("location of new event"),
                                fieldWithPath("basePrice")
                                        .description("base price of new event"),
                                fieldWithPath("maxPrice")
                                        .description("max price of new event"),
                                fieldWithPath("limitOfEnrollment")
                                        .description("limit of new event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        // 일부분의 필드만 명시하겠다. 
                        relaxedResponseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime")
                                        .description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime")
                                        .description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime")
                                        .description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime")
                                        .description("date time of end of new event"),
                                fieldWithPath("location")
                                        .description("location of new event"),
                                fieldWithPath("basePrice")
                                        .description("base price of new event"),
                                fieldWithPath("maxPrice")
                                        .description("max price of new event"),
                                fieldWithPath("limitOfEnrollment")
                                        .description("limit of new event"),
                                fieldWithPath("free")
                                        .description("it tells if this event is free or not"),
                                fieldWithPath("offline")
                                        .description("it tells if this event is offline or online"),
                                fieldWithPath("eventStatus")
                                        .description("event status")
                        )
                ));

    }

    @Test
    @DisplayName("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEventBad() throws Exception {

        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API DEVELOpment with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2010, 11, 7, 11, 11, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2010, 11, 8, 13, 11, 20))
                .beginEventDateTime(LocalDateTime.of(2010, 11, 25, 20, 30, 11))
                .endEventDateTime(LocalDateTime.of(2010, 11, 25, 20, 30, 12))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();
//        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력 값이 비어있는 경우 에러가 발생하는 테스트")
    public void CreateEvent_Bad_request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API DEVELOpment with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2010, 11, 7, 11, 11, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2010, 11, 8, 13, 11, 20))
                .beginEventDateTime(LocalDateTime.of(2010, 11, 25, 20, 30, 11))
                .endEventDateTime(LocalDateTime.of(2010, 11, 25, 20, 30, 12))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());

    }

    @ParameterizedTest
    @MethodSource("paramsForTestOffline")
    public void testFree(int basePrice, int maxPrice, boolean isFree) {
        // Given
        Event event1 = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        // When
        event1.update();

        // Then
        assertThat(event1.isFree()).isEqualTo(isFree);
    }

    @ParameterizedTest
    @MethodSource("paramsForTestOffline")
    public void testOffline(int basePrice, int maxPrice, boolean isFree) {

        // Given
        Event event = Event.builder()
                .location("강남역 네이버 D2 스타텁 팩토리")
                .build();
        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isTrue();

        // Given
        event = Event.builder()
                .build();
        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isFalse();
    }

    @Test
    public void queryEvents() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(
                this::generateEvent
        );

        // When
        this.mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"));
    }

    @Test
    @DisplayName("기존의 이벤트를 하나 조작하기")
    public void getEvent() throws Exception {

        // Given
        Event event = this.generateEvent(100);

        // When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"));
    }

    @Test
    @DisplayName("없는 이벤트를 조회했을 때 404 응답받기")
    public void getEvent404() throws Exception {
        // Given
        Event event = this.generateEvent(100);

        // When & Then
        this.mockMvc.perform(get("/api/events/112323"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
        // Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists());
    }

    @Test
    @DisplayName("입력값이 비어있는 경우에 이벤트 수정 실패")
    public void updateEvent400() throws Exception {
        // Given
        Event event = this.generateEvent(200);

        EventDto eventDto = new EventDto();

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEvent400wrong() throws Exception {
        // Given
        Event event = this.generateEvent(200);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("존재하지 않는 이벤트 수정 실패")
    public void updateEvent404wrong() throws Exception {
        // Given
        Event event = this.generateEvent(200);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        // When & Then
        this.mockMvc.perform(put("/api/events/11232323")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private Event generateEvent(int i) {
        Event event = Event.builder()
                .name("event" + i)
                .description("test event")
                .description("REST API DEVELOpment with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2010, 11, 7, 11, 11, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2010, 11, 8, 13, 11, 20))
                .beginEventDateTime(LocalDateTime.of(2010, 11, 25, 20, 30, 11))
                .endEventDateTime(LocalDateTime.of(2010, 11, 25, 20, 30, 12))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return this.eventRepository.save(event);

    }

    private static Stream<Arguments> paramsForTestOffline() {

        return Stream.of(
                Arguments.of(0, 0, true),
                Arguments.of(100, 0, false),
                Arguments.of(0, 100, false),
                Arguments.of(100, 200, false)
        );
    }
}
