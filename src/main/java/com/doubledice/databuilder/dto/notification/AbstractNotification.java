package com.doubledice.databuilder.dto.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author anton
 * @since 23.12.2022
 */
public abstract class AbstractNotification  implements Serializable {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Long id;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  LocalDateTime created;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  LocalDateTime updated;
}
