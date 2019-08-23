package com.example.testdemo.controller.system;

import com.example.testdemo.controller.BillController;
import com.example.testdemo.entity.BillEntity;
import com.example.testdemo.response.AppResponse;
import com.example.testdemo.services.BillService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@WebMvcTest(controllers = {BillController.class})
@MockBeans(value = {
        @MockBean(classes = {BillService.class})
})
@PrepareForTest({BillController.class})
public class BillControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test() throws Exception {
        long time = 1L;
        Integer id = 1;
        BillEntity billEntity = BillEntity.builder()
                .id("System:" + time + ":" + id)
                .cost(BigDecimal.TEN).build();

        mockStatic(System.class);
        when(System.currentTimeMillis()).thenReturn(time);

        String body = objectMapper.writeValueAsString(new AppResponse<>(billEntity));

        mockMvc.perform(
                get("/getBillSystem")
                        .param("id", id.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(body));
    }
}
