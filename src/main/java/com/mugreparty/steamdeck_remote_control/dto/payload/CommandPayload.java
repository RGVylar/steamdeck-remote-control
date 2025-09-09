package com.mugreparty.steamdeck_remote_control.dto.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// usa el campo externo "type" de CommandDto para resolver el subtipo
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
  property = "type",
  visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = MousePayload.class,     name = "MOUSE"),
  @JsonSubTypes.Type(value = KeyboardPayload.class,  name = "KEYBOARD"),
  @JsonSubTypes.Type(value = TextPayload.class,      name = "TEXT_INPUT"),
  @JsonSubTypes.Type(value = SystemPayload.class,   name = "SYSTEM")
})
public sealed interface CommandPayload permits MousePayload, KeyboardPayload, TextPayload, SystemPayload {}
