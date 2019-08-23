package com.example.testdemo.controller.normal;

import com.example.testdemo.controller.BillController;
import com.example.testdemo.entity.BillEntity;
import com.example.testdemo.response.AppResponse;
import com.example.testdemo.services.BillService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {BillController.class})
@MockBeans(value = {
        @MockBean(classes = {BillService.class})
})
public class BillControllerTests {

    //Mock对象
    @Autowired
    private BillService billService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test() throws Exception {
        BillEntity billEntity = new BillEntity();
        when(billService.getBill(anyString())).thenReturn(billEntity);

        String body = objectMapper.writeValueAsString(new AppResponse<>(billEntity));

        mockMvc.perform(
                get("/getBill")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(body));
    }
}
