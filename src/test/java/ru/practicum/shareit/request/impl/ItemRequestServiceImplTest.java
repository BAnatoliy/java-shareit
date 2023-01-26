package ru.practicum.shareit.request.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ItemCheckException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestMapperImpl itemRequestMapper;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void create() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("description");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setDescription("description");

        User user = new User();
        user.setId(1L);
        user.setName("UserName");
        user.setEmail("u@ya.ru");

        ItemRequest itemRequestWithUser = new ItemRequest();
        itemRequestWithUser.setId(1L);
        itemRequestWithUser.setCreated(LocalDateTime.now());
        itemRequestWithUser.setDescription("description");
        itemRequestWithUser.setUser(user);

        when(itemRequestMapper.mapToItemRequest(itemRequestDto)).thenReturn(itemRequest);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequestWithUser);
        when(itemRequestMapper.mapToDto(itemRequestWithUser)).thenReturn(itemRequestDto);

        ItemRequestDto itemRequestDtoReturn = itemRequestService.create(itemRequestDto, 1L);
        assertEquals(itemRequestDto, itemRequestDtoReturn);
        verify(itemRequestMapper).mapToItemRequest(itemRequestDto);
        verify(userRepository).findById(1L);
        verify(itemRequestRepository).save(itemRequest);
        verify(itemRequestMapper).mapToDto(itemRequestWithUser);

        when(userRepository.findById(222L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () ->
                itemRequestService.create(itemRequestDto, 222L));
    }

    @Test
    void getUsersRequests() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("description");

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("description2");

        User user = new User();
        user.setId(1L);
        user.setName("UserName");
        user.setEmail("u@ya.ru");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setDescription("description");

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(2L);
        itemRequestDto2.setCreated(LocalDateTime.now());
        itemRequestDto2.setDescription("description2");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByUserIdOrderByCreatedDesc(1L)).thenReturn(
                List.of(itemRequest, itemRequest2));
        when(itemRequestMapper.mapToDto(itemRequest)).thenReturn(itemRequestDto);
        when(itemRequestMapper.mapToDto(itemRequest2)).thenReturn(itemRequestDto2);

        List<ItemRequestDto> usersRequests = itemRequestService.getUsersRequests(1L);
        assertEquals(List.of(itemRequestDto, itemRequestDto2), usersRequests);
        verify(userRepository).findById(1L);
        verify(itemRequestRepository).findAllByUserIdOrderByCreatedDesc(1L);
        verify(itemRequestMapper).mapToDto(itemRequest);
        verify(itemRequestMapper).mapToDto(itemRequest2);
    }

    @Test
    void getAllRequests_whenParametersIsNull_shouldGetListAllRequests() {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("UserName");
        user2.setEmail("u@ya.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("description");
        itemRequest.setUser(user2);
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("description2");
        itemRequest2.setUser(user2);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setDescription("description");
        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(2L);
        itemRequestDto2.setCreated(LocalDateTime.now());
        itemRequestDto2.setDescription("description2");

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findAllByUserIdNotOrderByCreatedDesc(1L)).thenReturn(
                List.of(itemRequest, itemRequest2));
        when(itemRequestMapper.mapToDto(itemRequest)).thenReturn(itemRequestDto);
        when(itemRequestMapper.mapToDto(itemRequest2)).thenReturn(itemRequestDto2);

        List<ItemRequestDto> usersRequests = itemRequestService.getAllRequests(1L, null, null);
        assertEquals(List.of(itemRequestDto, itemRequestDto2), usersRequests);
        verify(userRepository).findById(1L);
        verify(itemRequestRepository).findAllByUserIdNotOrderByCreatedDesc(1L);
        verify(itemRequestMapper).mapToDto(itemRequest);
        verify(itemRequestMapper).mapToDto(itemRequest2);
    }

    @Test
    void getAllRequests_whenParametersIsCorrect_shouldGetListAllRequests() {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("UserName");
        user2.setEmail("u@ya.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("description");
        itemRequest.setUser(user2);
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setDescription("description2");
        itemRequest2.setUser(user2);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setDescription("description");
        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(2L);
        itemRequestDto2.setCreated(LocalDateTime.now());
        itemRequestDto2.setDescription("description2");

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findAllByUserIdNotOrderByCreatedDesc(anyLong(), any(Pageable.class))).thenReturn(
                new PageImpl<>(new ArrayList<>(List.of(itemRequest, itemRequest2))));
        when(itemRequestMapper.mapToDto(itemRequest)).thenReturn(itemRequestDto);
        when(itemRequestMapper.mapToDto(itemRequest2)).thenReturn(itemRequestDto2);

        List<ItemRequestDto> usersRequests = itemRequestService.getAllRequests(1L, 0, 2);
        assertEquals(List.of(itemRequestDto, itemRequestDto2), usersRequests);
        verify(userRepository).findById(1L);
        verify(itemRequestRepository).findAllByUserIdNotOrderByCreatedDesc(anyLong(), any(Pageable.class));
        verify(itemRequestMapper).mapToDto(itemRequest);
        verify(itemRequestMapper).mapToDto(itemRequest2);
    }

    @Test
    void getAllRequests_whenParametersIsNegative_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        assertThrows(ItemCheckException.class, () ->
                itemRequestService.getAllRequests(1L, -1, 2));
        assertThrows(ItemCheckException.class, () ->
                itemRequestService.getAllRequests(1L, 1, -2));
        assertThrows(ItemCheckException.class, () ->
                itemRequestService.getAllRequests(1L, 1, 0));
        verify(userRepository, times(3)).findById(1L);
        verify(itemRequestRepository, never()).findAllByUserIdNotOrderByCreatedDesc(anyLong(), any(Pageable.class));
        verify(itemRequestMapper, never()).mapToDto(any(ItemRequest.class));
    }


    @Test
    void getItemRequestById() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("description");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setDescription("description");

        when(userRepository.findById(5L)).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.mapToDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto itemRequestById = itemRequestService.getItemRequestById(5L, 1L);
        assertEquals(itemRequestDto, itemRequestById);
        verify(userRepository).findById(5L);
        verify(itemRequestRepository).findById(1L);
        verify(itemRequestMapper).mapToDto(itemRequest);
    }

    @Test
    void getItemRequestById_whenItemNotFound_shouldThrowException() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("description");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setDescription("description");

        when(userRepository.findById(5L)).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                itemRequestService.getItemRequestById(5L, 1L));
        verify(userRepository).findById(5L);
        verify(itemRequestRepository).findById(1L);
        verify(itemRequestMapper, never()).mapToDto(any(ItemRequest.class));
    }
}