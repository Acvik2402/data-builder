package com.doubledice.databuilder.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.doubledice.databuilder.model.Group;
import com.doubledice.databuilder.service.AnalyticService;
import com.doubledice.databuilder.service.GroupService;
import com.doubledice.databuilder.service.VkService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@ContextConfiguration(classes = {GroupController.class})
@ExtendWith(SpringExtension.class)
class GroupControllerTest {
  @MockBean
  private AnalyticService analyticService;

  @Autowired
  private GroupController groupController;

  @MockBean
  private GroupService groupService;

  @MockBean
  private ObjectMapper objectMapper;

  @MockBean
  private VkService vkService;

  /**
   * Method under test: {@link GroupController#addGroup(Group, BindingResult, Model)}
   */
  @Test
  void testAddGroup() throws Exception {
    Group group = new Group();
    group.setAdditionalInformation("Additional Information");
    group.setGroupName("Group Name");
    group.setId(123L);
    group.setVkLink("Vk Link");
    group.setVkUsers(new HashSet<>());
    when(groupService.addGroup((Group) any())).thenReturn(group);
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/group/group-create")
            .param("vkLink", "Vk Link");
    MockMvcBuilders.standaloneSetup(groupController)
            .build()
            .perform(requestBuilder)
            .andExpect(MockMvcResultMatchers.status().isFound())
            .andExpect(MockMvcResultMatchers.model().size(1))
            .andExpect(MockMvcResultMatchers.model().attributeExists("group"))
            .andExpect(MockMvcResultMatchers.view().name("redirect:/group/groups"))
            .andExpect(MockMvcResultMatchers.redirectedUrl("/group/groups"));
  }
}

