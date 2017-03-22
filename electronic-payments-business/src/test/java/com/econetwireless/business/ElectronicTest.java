package com.econetwireless.business;

import com.econetwireless.epay.business.config.EpayBusinessConfig;
import com.econetwireless.epay.business.config.IntegrationsConfig;
import com.econetwireless.epay.business.config.RootConfig;
import com.econetwireless.utils.enums.ResponseCode;
import com.econetwireless.utils.messages.AirtimeTopupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Shelton Chiuswa on 22/3/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({
        @ContextConfiguration(classes = {RootConfig.class}),
        @ContextConfiguration(classes = {IntegrationsConfig.class}),
        @ContextConfiguration(classes = {EpayBusinessConfig.class})
})
public class ElectronicTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    public String partnerCode;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        partnerCode = "hot-recharge";
    }

    @Test
    public  void  airtimeTopupResponseShouldReturnCreditSuccess()throws Exception{

        final AirtimeTopupRequest airtimeTopupRequest = new AirtimeTopupRequest();
        airtimeTopupRequest.setAmount(2.73);
        airtimeTopupRequest.setMsisdn("773303584");
        airtimeTopupRequest.setPartnerCode(partnerCode);
        airtimeTopupRequest.setReferenceNumber("TOPUP-REF-0123");
        this.mockMvc.perform(post("/resources/services/credits").content(asJsonString(airtimeTopupRequest)).
                contentType(MediaType.parseMediaType("application/json;charset=UTF-8")).accept(MediaType.parseMediaType("application/json;charset=UTF-8"))).
                andExpect(status().isOk()).
                andExpect(content().contentType("application/json;charset=UTF-8")).
                andExpect(jsonPath("$.responseCode").value(ResponseCode.SUCCESS.getCode())).
                andExpect(jsonPath("$.balance").value(is(greaterThan(airtimeTopupRequest.getAmount()))));
    }






    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
