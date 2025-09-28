package uk.gov.hmcts.reform.pcs.ccd.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT;
import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;
import static com.fasterxml.jackson.databind.MapperFeature.INFER_BUILDER_TYPE_BINDINGS;
import static org.junit.Assert.assertEquals;

class PartyListTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
//        objectMapper = new ObjectMapper();
//        objectMapper.setSerializationInclusion(Include.NON_NULL);
//        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);


        objectMapper = JsonMapper.builder()
            .configure(ACCEPT_CASE_INSENSITIVE_ENUMS, true)
            .enable(INFER_BUILDER_TYPE_BINDINGS)
            .disable(AUTO_CLOSE_JSON_CONTENT)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();

        JavaTimeModule datetime = new JavaTimeModule();
        objectMapper.registerModule(datetime);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new StdDateFormat());

        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        objectMapper.registerModules(new Jdk8Module(), new JavaTimeModule(), new ParameterNamesModule());
    }

//    @Test
//    void testSerialisation() throws JsonProcessingException {
//        PartyList partyList = PartyList.builder()
//            .partyList(List.of("C", "D"))
//            .build();
//
//        String json = objectMapper.writeValueAsString(partyList);
//
//        System.out.println(json);
//        System.out.println("------");
//
//        PartyList deserialised = objectMapper.readValue(json, PartyList.class);
//
//        System.out.println(deserialised.getProperties());
//    }

    @Test
    void testSerialisation2() throws JsonProcessingException {
        List<Party> rawPartyList = List.of(createParty("party3b"), createParty("party4"));
        PartyList partyList = PartyList.builder()
            .partyList(rawPartyList)
            .build();

        PCSCase pcsCase = PCSCase.builder()
//            .party1(createParty("party1"))
//            .party2(createParty("party2"))
            .myPartyList(rawPartyList)
            .build();

        String json = objectMapper.writeValueAsString(pcsCase);

        System.out.println(json);
        System.out.println("------");

        PCSCase deserialised = objectMapper.readValue(json, PCSCase.class);

        System.out.println(deserialised.getMyPartyList());
    }

    @Test
    public void whenDeserializingUsingJsonAnySetter_thenCorrect()
        throws IOException {
        String json
            = "{\"name\":\"My bean\",\"attr2\":\"val2\",\"attr1\":\"val1\"}";

        ExtendableBean bean = new ObjectMapper()
            .readerFor(ExtendableBean.class)
            .readValue(json);

        Assertions.assertEquals("My bean", bean.name);
        assertEquals("val2", bean.getProperties().get("attr2"));
    }


    private static Party createParty(String party1) {
        return Party.builder().forename(party1).build();
    }

}
