package ru.practicum.shareit.request.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemSlimDtoForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemRequestMapper {
    ItemRequestDto mapToDto(ItemRequest itemRequest);

    ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto);

    @Mapping(target = "requestId", source = "request", qualifiedByName = "idFromRequest")
    ItemSlimDtoForRequest mapToItemDtoForRequest (Item item);

    @Named("idFromRequest")
    default Long idFromRequest(ItemRequest itemRequest) {
        return itemRequest.getId();
    }
}
