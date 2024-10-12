package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemFromItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestAnswerDto {

    long id;
    String description;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime created;
    List<ItemFromItemRequestDto> items;
}
