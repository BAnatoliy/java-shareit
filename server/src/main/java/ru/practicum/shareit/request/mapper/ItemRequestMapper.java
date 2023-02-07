package ru.practicum.shareit.request.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.item.dto.ItemSlimDtoForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemRequestMapper {
    ItemRequestDto mapToDto(ItemRequest itemRequest);

    ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto);

    List<ItemRequestDto> mapToListDto(List<ItemRequest> itemRequestList);

    @Mapping(target = "requestId", source = "request", qualifiedByName = "idFromRequest")
    ItemSlimDtoForRequest mapToItemDtoForRequest(Item item);

    @Named("idFromRequest")
    default Long idFromRequest(ItemRequest itemRequest) {
        return itemRequest.getId();
    }
}
