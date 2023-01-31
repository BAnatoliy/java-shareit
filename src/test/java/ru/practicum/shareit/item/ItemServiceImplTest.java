package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingSlimDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ItemCheckException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.impl.ItemServiceImpl;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pageableImpl.CustomPageRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserSlimDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemMapperImpl itemMapper;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;
    User user;
    User user2;
    UserSlimDto userSlimDto;
    UserSlimDto userSlimDto2;
    Item item;
    Item item2;
    ItemDto itemDto;
    ItemDto itemDto2;
    Booking booking;
    Booking booking2;
    BookingSlimDto bookingSlimDto;
    BookingSlimDto bookingSlimDto2;
    Comment comment;
    CommentDto commentDto;
    CommentRequestDto commentRequestDto;
    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User(1L, "userName", "user@ya.ru", null, null);
        userSlimDto = new UserSlimDto(1L, "userName", "user@ya.ru");
        user2 = new User(2L, "userName2", "user2@ya.ru", null, null);
        userSlimDto2 = new UserSlimDto(2L, "userName2", "user2@ya.ru");
        comment = new Comment(1L, "comment", LocalDateTime.now(), user2, null);
        commentDto = new CommentDto(1L, "comment", "user2", LocalDateTime.now());
        commentRequestDto = new CommentRequestDto(null, "text");
        item = new Item(1L, "item", "isem description", true, user,
                null, null, null, Set.of(comment), itemRequest);
        itemDto = new ItemDto(1L, "item", "isem description", true, userSlimDto,
                1L, null, null, null, Set.of(commentDto));
        comment.setItem(item);
        item2 = new Item(2L, "item2", "isem description2", true, user,
                null, null, null, null, null);
        itemDto2 = new ItemDto(2L, "item2", "isem description2", true, userSlimDto,
                null, null, null, null, null);
        booking = new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.WAITING, user2, item);
        bookingSlimDto = new BookingSlimDto(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.WAITING, 1L);
        booking2 = new Booking(2L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), BookingStatus.WAITING, user2, item);
        bookingSlimDto2 = new BookingSlimDto(2L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), BookingStatus.WAITING, 1L);
        item.setLastBooking(booking);
        item.setNextBooking(booking2);
        itemDto.setLastBooking(bookingSlimDto);
        itemDto.setNextBooking(bookingSlimDto2);
        itemRequest = new ItemRequest(1L, "description", LocalDateTime.now(), null, user2);
    }

    @Test
    void createItemTest() {
        when(itemMapper.mapToItem(itemDto)).thenReturn(item);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.mapToDto(item)).thenReturn(itemDto);

        ItemDto itemReturn = itemService.createItem(itemDto, 1L);
        assertEquals(itemDto, itemReturn);
    }

    @Test
    void createItemTest_whenUserNotFound_shouldThrowException() {
        when(itemMapper.mapToItem(itemDto)).thenReturn(item);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                itemService.createItem(itemDto, 1L));
    }

    @Test
    void updateItemTest() {
        Item newItem = new Item();
        ItemDto newItemDto = new ItemDto();
        newItem.setName("newName");
        newItem.setDescription("newDescription");
        newItem.setAvailable(false);
        newItemDto.setName("newName");
        newItemDto.setDescription("newDescription");
        newItemDto.setAvailable(false);
        when(itemMapper.mapToItem(newItemDto)).thenReturn(newItem);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.mapToDto(item)).thenReturn(itemDto);

        ItemDto itemReturn = itemService.updateItem(newItemDto, 1L, 1L);
        assertEquals(itemDto, itemReturn);
        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();
        assertEquals("newName", savedItem.getName());
        assertEquals("newDescription", savedItem.getDescription());
        assertFalse(savedItem.getAvailable());
    }

    @Test
    void updateItemTest_whenItemNotFound_shouldThrowException() {
        Item newItem = new Item();
        ItemDto newItemDto = new ItemDto();
        newItem.setName("newName");
        newItem.setDescription("newDescription");
        newItemDto.setName("newName");
        newItemDto.setDescription("newDescription");
        when(itemMapper.mapToItem(newItemDto)).thenReturn(newItem);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                itemService.updateItem(newItemDto, 1L, 1L));
    }

    @Test
    void updateItemTest_whenNameIsBlank_shouldThrowException() {
        Item newItem = new Item();
        ItemDto newItemDto = new ItemDto();
        newItem.setName("  ");
        newItem.setDescription("newDescription");
        newItemDto.setName("  ");
        newItemDto.setDescription("newDescription");
        when(itemMapper.mapToItem(newItemDto)).thenReturn(newItem);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> itemService.updateItem(newItemDto, 1L, 1L));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItemTest_whenDescriptionIsBlank_shouldThrowException() {
        Item newItem = new Item();
        ItemDto newItemDto = new ItemDto();
        newItem.setName("newName");
        newItem.setDescription("   ");
        newItemDto.setName("newName");
        newItemDto.setDescription("   ");
        when(itemMapper.mapToItem(newItemDto)).thenReturn(newItem);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> itemService.updateItem(newItemDto, 1L, 1L));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItemTest_whenUserIsNotOwner_shouldThrowException() {
        Item newItem = new Item();
        ItemDto newItemDto = new ItemDto();
        newItem.setName("newName");
        newItem.setDescription("newDescription");
        newItemDto.setName("newName");
        newItemDto.setDescription("newDescription");
        when(itemMapper.mapToItem(newItemDto)).thenReturn(newItem);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class, () ->
                itemService.updateItem(newItemDto, 1L, 111L));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getItemByIdTest_whenLastAndNextBookingsAreNull_shouldGetItem() {
        item.setLastBooking(null);
        item.setNextBooking(null);
        item.setComments(null);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItem_Id(1L)).thenReturn(List.of(booking, booking2));
        when(commentRepository.findAllByItem_Id(1L)).thenReturn(List.of(comment));
        when(itemMapper.mapToDto(item)).thenReturn(itemDto);

        ItemDto itemReturn = itemService.getItemById(1L, 1L);
        assertEquals(itemDto, itemReturn);
        verify(itemMapper).mapToDto(itemArgumentCaptor.capture());
        Item value = itemArgumentCaptor.getValue();
        assertNotNull(value.getLastBooking());
        assertNotNull(value.getNextBooking());
        assertNotNull(value.getComments());
        assertEquals(value.getLastBooking().getId(), itemReturn.getLastBooking().getId());
        assertEquals(value.getNextBooking().getId(), itemReturn.getNextBooking().getId());
        assertEquals(value.getComments().size(), itemReturn.getComments().size());
    }

    @Test
    void getItemByIdTest_whenLastAndNextBookingsAreNotNull_shouldGetItemWithNewBookings() {
        Booking oldBooking = new Booking();
        Booking oldBooking2 = new Booking();
        oldBooking.setStart(LocalDateTime.now().minusDays(5));
        oldBooking.setEnd(LocalDateTime.now().minusDays(4));
        oldBooking2.setStart(LocalDateTime.now().plusDays(15));
        oldBooking2.setEnd(LocalDateTime.now().plusDays(16));
        item.setLastBooking(oldBooking);
        item.setNextBooking(oldBooking2);
        item.setComments(null);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItem_Id(1L)).thenReturn(List.of(booking, booking2));
        when(commentRepository.findAllByItem_Id(1L)).thenReturn(List.of(comment));
        when(itemMapper.mapToDto(item)).thenReturn(itemDto);

        ItemDto itemReturn = itemService.getItemById(1L, 1L);
        assertEquals(itemDto, itemReturn);
        verify(itemMapper).mapToDto(itemArgumentCaptor.capture());
        Item value = itemArgumentCaptor.getValue();
        assertNotNull(value.getLastBooking());
        assertNotNull(value.getNextBooking());
        assertNotNull(value.getComments());
        assertEquals(value.getLastBooking().getId(), itemReturn.getLastBooking().getId());
        assertEquals(value.getNextBooking().getId(), itemReturn.getNextBooking().getId());
        assertEquals(value.getComments().size(), itemReturn.getComments().size());
    }

    @Test
    void getItemByIdTest_whenItemNotFound_shouldThrowException() {
        when(itemRepository.findById(111L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                itemService.getItemById(111L, 1L));
        verify(itemRepository).findById(111L);
    }

    @Test
    void getItemsByTheOwnerTest_whenUserNotFound_shouldThrowException() {
        when(userRepository.findById(111L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                itemService.getItemsByTheOwner(111L, 0, 2));
        verify(userRepository).findById(111L);
    }

    @Test
    void getItemsByTheOwnerTest_whenParametersIsNegative_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ItemCheckException.class, () ->
                itemService.getItemsByTheOwner(1L, -1, 2));
        assertThrows(ItemCheckException.class, () ->
                itemService.getItemsByTheOwner(1L, 1, -1));
        assertThrows(ItemCheckException.class, () ->
                itemService.getItemsByTheOwner(1L, 1, 0));

        verify(userRepository, times(3)).findById(1L);
    }

    @Test
    void getItemsByTheOwnerTest_whenParametersIsNull_shouldGetAllItems() {
        List<Item> itemList = List.of(item, item2);
        List<Booking> bookingList = List.of(booking, booking2);
        List<Comment> commentList = List.of(comment);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerIdOrderById(1L)).thenReturn(itemList);
        when(bookingRepository.findAllByItem_Owner_IdAndItem_AvailableOrderByStartDesc(1L, true))
                .thenReturn(bookingList);
        when(commentRepository.findAllByItem_IdIn(itemList.stream().map(Item::getId).collect(Collectors.toList())))
                .thenReturn(commentList);
        when(itemMapper.mapToListDto(itemList)).thenReturn(List.of(itemDto, itemDto2));

        List<ItemDto> itemsByTheOwner = itemService.getItemsByTheOwner(1L, null, null);
        assertEquals(2, itemsByTheOwner.size());
        verify(userRepository).findById(1L);
        verify(itemRepository).findByOwnerIdOrderById(1L);
        verify(bookingRepository).findAllByItem_Owner_IdAndItem_AvailableOrderByStartDesc(1L, true);
        verify(commentRepository).findAllByItem_IdIn(anyList());
        verify(itemMapper).mapToListDto(itemList);
    }

    @Test
    void getItemsByTheOwnerTest_whenFromIs1AndSizeIs1_shouldGetListOf1Item() {
        List<Item> itemList = List.of(item);
        List<Booking> bookingList = List.of(booking, booking2);
        List<Comment> commentList = List.of(comment);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerIdOrderById(anyLong(), any(CustomPageRequest.class))).thenReturn(itemList);
        when(bookingRepository.findAllByItem_Owner_IdAndItem_AvailableOrderByStartDesc(1L, true))
                .thenReturn(bookingList);
        when(commentRepository.findAllByItem_IdIn(itemList.stream().map(Item::getId).collect(Collectors.toList())))
                .thenReturn(commentList);
        when(itemMapper.mapToListDto(itemList)).thenReturn(List.of(itemDto));

        List<ItemDto> itemsByTheOwner = itemService.getItemsByTheOwner(1L, 0, 1);
        assertEquals(1, itemsByTheOwner.size());
        verify(userRepository).findById(1L);
        verify(itemRepository).findByOwnerIdOrderById(anyLong(), any(CustomPageRequest.class));
        verify(bookingRepository).findAllByItem_Owner_IdAndItem_AvailableOrderByStartDesc(1L, true);
        verify(commentRepository).findAllByItem_IdIn(anyList());
        verify(itemMapper).mapToListDto(itemList);
    }

    @Test
    void getAvailableItemTest_whenTextIsBlank_shouldGetEmptyList() {
        List<ItemDto> itemDtoList = itemService.getAvailableItem("  ", null, null);
        List<ItemDto> itemDtoList2 = itemService.getAvailableItem("", null, null);

        assertTrue(itemDtoList.isEmpty());
        assertTrue(itemDtoList2.isEmpty());
    }

    @Test
    void getAvailableItemTest_whenParametersAreNull_shouldGetListOfAllItems() {
        List<Item> itemList = List.of(item, item2);
        when(itemRepository.findAvailableItemByNameAndDescription("dEsC")).thenReturn(itemList);
        when(itemMapper.mapToListDto(itemList)).thenReturn(List.of(itemDto, itemDto2));

        List<ItemDto> itemDtoList = itemService.getAvailableItem("dEsC", null, null);
        assertEquals(2, itemDtoList.size());
        verify(itemRepository).findAvailableItemByNameAndDescription("dEsC");
    }

    @Test
    void getAvailableItemTest_whenFromIs0SizeIs1_shouldGetListOf1Item() {
        List<Item> itemList = List.of(item);
        when(itemRepository.findAvailableItemByNameAndDescription("dEsC", 0, 1))
                .thenReturn(itemList);
        when(itemMapper.mapToListDto(itemList)).thenReturn(List.of(itemDto));

        List<ItemDto> itemDtoList = itemService.getAvailableItem("dEsC", 0, 1);
        assertEquals(1, itemDtoList.size());
        verify(itemRepository).findAvailableItemByNameAndDescription("dEsC", 0, 1);
    }

    @Test
    void createCommentTest() {
        item.setBookings(Set.of(booking, booking2));
        when(itemMapper.mapToComment(commentRequestDto)).thenReturn(comment);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(itemMapper.mapToCommentDto(comment)).thenReturn(commentDto);

        CommentDto commentReturn = itemService.createComment(commentRequestDto, 1L, 2L);
        assertEquals(commentDto, commentReturn);
    }

    @Test
    void createCommentTest_whenItemNotFound_shouldThrowException() {
        item.setBookings(Set.of(booking, booking2));
        when(itemMapper.mapToComment(commentRequestDto)).thenReturn(comment);
        when(itemRepository.findById(111L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                itemService.createComment(commentRequestDto, 111L, 2L));
    }

    @Test
    void createCommentTest_whenUserNotFound_shouldThrowException() {
        item.setBookings(Set.of(booking, booking2));
        when(itemMapper.mapToComment(commentRequestDto)).thenReturn(comment);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(222L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                itemService.createComment(commentRequestDto, 1L, 222L));
    }

    @Test
    void createCommentTest_whenUserDoesNotBook_shouldThrowException() {
        item.setBookings(Set.of(booking, booking2));
        when(itemMapper.mapToComment(commentRequestDto)).thenReturn(comment);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ItemCheckException.class, () ->
                itemService.createComment(commentRequestDto, 1L, 1L));
    }
}