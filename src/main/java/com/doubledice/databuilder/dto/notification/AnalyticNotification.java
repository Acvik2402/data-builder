package com.doubledice.databuilder.dto.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author anton
 * @since 23.12.2022
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AnalyticNotification extends AbstractNotification{
  private String message;
  private String groupName;
  private String analyticData;

  public AnalyticNotification(String message, String groupName, String analyticData) {
    this.message = message;
    this.groupName = groupName;
    this.analyticData = analyticData;
  }
}
