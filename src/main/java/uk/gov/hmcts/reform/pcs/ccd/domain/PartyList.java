package uk.gov.hmcts.reform.pcs.ccd.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartyList {

    @Builder.Default
    @JsonIgnore
    private List<Party> partyList = new ArrayList<>();

//    private Map<String, String> properties = new HashMap<>();

    @JsonAnySetter
    public void add(String key, Party party) {
        System.out.printf("Adding %s -> %s%n", key, party);
//        properties.put(key, value);
    }

//    @JsonAnyGetter
//    public Map<String, Party> anyGetter() {
//        LinkedHashMap<String, Party> map = new LinkedHashMap<>();
//
//        if (partyList != null) {
//            for (int i = 0; i < partyList.size(); i++) {
//                Party party = partyList.get(i);
//                map.put("myPartyList_" + i, party);
//            }
//        }
//
//        return map;
//    }

//    @JsonAnySetter
//    public void setNames(Map<String, Object> map) {
//        System.out.println("Setting with " + map);
//        this.names.add("E");
//        this.names.add("F");
//    }

//    @JsonIgnore
//    public List<String> getPartyList() {
//        return partyList;
//    }
//
//    public Map<String, String> getProperties() {
//        return properties;
//    }
}
