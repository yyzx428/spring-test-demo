package com.example.testdemo.controller.parameter;

import com.example.testdemo.controller.BillController;
import com.example.testdemo.controller.parameter.assembly.CompatibleParameterized;
import com.example.testdemo.controller.parameter.assembly.SpringRunnerWithParametersFactory;
import com.example.testdemo.entity.BillEntity;
import com.example.testdemo.response.AppResponse;
import com.example.testdemo.services.BillService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(CompatibleParameterized.class)
@Parameterized.UseParametersRunnerFactory(SpringRunnerWithParametersFactory.class)
@WebMvcTest(controllers = {BillController.class})
@MockBeans(value = {
        @MockBean(classes = {BillService.class})
})
@PrepareForTest({BillController.class})
public class BillControllerTests {

    @Parameterized.Parameter
    public Integer id;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Parameterized.Parameters
    public static List<Integer> ids() {
        return Lists.newArrayList(1, 2, 3, 4);
    }

    @Test
    public void test() throws Exception {
        long time = 1L;
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
