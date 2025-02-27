package ru.practicum.shareit.item.dto;

import lombok.Value;
import java.time.LocalDateTime;


@Value
public class BookerInfoDto {
    Long id;
    Long bookerId;
    LocalDateTime start;
    LocalDateTime end;
}
