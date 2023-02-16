package ru.practicum.shareit.booking.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.constant.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.impl.BookingServiceImpl;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BookingApprovedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ItemCheckException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemSlimDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pageableImpl.CustomPageRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserSlimDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingMapperImpl bookingMapper;
    @InjectMocks
    private BookingServiceImpl bookingService;
    User booker;
    UserSlimDto bookerSlimDto;
    User booker2;
    UserSlimDto bookerSlimDto2;
    User user;
    Item item;
    ItemSlimDto itemSlimDto;
    Item item2;
    ItemSlimDto itemSlimDto2;
    Booking booking;
    Booking booking1Approved;
    Booking booking1Rejected;
    Booking booking2;
    Booking booking3;
    Booking booking4;
    BookingDto bookingDto;
    BookingDto bookingDto2;
    BookingDto bookingDto3;
    BookingDto bookingDto4;
    BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "bookerName", "booker@ya.ru", null, null);
        bookerSlimDto = new UserSlimDto(1L, "bookerName", "booker@ya.ru");
        booker2 = new User(2L, "bookerName2", "booker2@ya.ru", null, null);
        bookerSlimDto2 = new UserSlimDto(2L, "bookerName2", "booker2@ya.ru");
        user = new User(4L, "userName", "user@ya.ru", null, null);
        item = new Item(1L, "item", "isem description", true, user,
                null, null, null, null, null);
        itemSlimDto = new ItemSlimDto(1L, "item", "isem description", true);
        item2 = new Item(2L, "item2", "isem2 description", true, user,
                null, null, null, null, null);
        itemSlimDto2 = new ItemSlimDto(1L, "item", "isem description", true);
        booking = new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.WAITING, booker, item);
        booking1Approved = new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.APPROVED, booker, item);
        booking1Rejected = new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.REJECTED, booker, item);
        booking2 = new Booking(2L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), BookingStatus.REJECTED, booker, item);
        booking3 = new Booking(3L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), BookingStatus.APPROVED, booker, item);
        booking4 = new Booking(4L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.WAITING, booker2, item2);
        bookingDto = new BookingDto(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.WAITING, bookerSlimDto, itemSlimDto);
        bookingDto2 = new BookingDto(2L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), BookingStatus.REJECTED, bookerSlimDto, itemSlimDto);
        bookingDto3 = new BookingDto(3L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), BookingStatus.APPROVED, bookerSlimDto, itemSlimDto);
        bookingDto4 = new BookingDto(4L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.WAITING, bookerSlimDto2, itemSlimDto2);
        bookingRequestDto = new BookingRequestDto(1L, 1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.WAITING);
    }

    @Test
    void createBookingTest() {
        when(bookingMapper.mapToBooking(bookingRequestDto)).thenReturn(booking);
        when(bookingMapper.mapToDto(booking)).thenReturn(bookingDto);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto bookingReturn = bookingService.createBooking(bookingRequestDto, 1L);

        assertEquals(bookingDto, bookingReturn);
        verify(bookingMapper).mapToBooking(bookingRequestDto);
        verify(bookingMapper).mapToDto(booking);
        verify(itemRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(bookingRepository).save(booking);
    }

    @Test
    void createBookingTest_whenItemNotFound_shouldThrowException() {
        when(bookingMapper.mapToBooking(bookingRequestDto)).thenReturn(booking);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                bookingService.createBooking(bookingRequestDto, 1L));

        verify(bookingMapper).mapToBooking(bookingRequestDto);
        verify(itemRepository).findById(1L);
    }

    @Test
    void createBookingTest_whenItemIsUnavailable_shouldThrowException() {
        item.setAvailable(false);
        when(bookingMapper.mapToBooking(bookingRequestDto)).thenReturn(booking);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ItemCheckException.class, () ->
                bookingService.createBooking(bookingRequestDto, 2L));

        verify(bookingMapper).mapToBooking(bookingRequestDto);
        verify(itemRepository).findById(1L);
    }

    @Test
    void createBookingTest_whenItemIsUnavailableUserNotFound_shouldThrowException() {
        when(bookingMapper.mapToBooking(bookingRequestDto)).thenReturn(booking);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(111L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                bookingService.createBooking(bookingRequestDto, 111L));

        verify(bookingMapper).mapToBooking(bookingRequestDto);
        verify(itemRepository).findById(1L);
        verify(userRepository).findById(111L);
    }

    @Test
    void createBookingTest_whenUserIdEqualsOwnerId_shouldThrowException() {
        when(bookingMapper.mapToBooking(bookingRequestDto)).thenReturn(booking);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(4L)).thenReturn(Optional.of(user));

        assertThrows(EntityNotFoundException.class, () ->
                bookingService.createBooking(bookingRequestDto, 4L));

        verify(bookingMapper).mapToBooking(bookingRequestDto);
        verify(itemRepository).findById(1L);
        verify(userRepository).findById(4L);
    }

    @Test
    void confirmBookingTest_whenApprovedTrue_shouldSetStatusApproved() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking1Approved)).thenReturn(booking1Approved);
        bookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingMapper.mapToDto(booking1Approved)).thenReturn(bookingDto);
        BookingDto bookingDtoReturn = bookingService.confirmBooking(1L, true, 4L);
        assertEquals(bookingDto, bookingDtoReturn);
    }

    @Test
    void confirmBookingTest_whenApprovedFalse_shouldSetStatusRejected() {
        bookingDto.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking1Rejected)).thenReturn(booking1Rejected);
        when(bookingMapper.mapToDto(booking1Rejected)).thenReturn(bookingDto);
        BookingDto bookingDtoReturn = bookingService.confirmBooking(1L, false, 4L);
        assertEquals(bookingDto, bookingDtoReturn);
    }

    @Test
    void confirmBookingTest_whenBookingNotFound_shouldThrowException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                bookingService.confirmBooking(1L, true, 4L));
    }

    @Test
    void confirmBookingTest_whenUserIsNotOwner_shouldThrowException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        assertThrows(EntityNotFoundException.class, () ->
                bookingService.confirmBooking(1L, true, 1L));
    }

    @Test
    void confirmBookingTest_whenStatusHasAlreadyApproved_shouldThrowException() {
        when(bookingRepository.findById(3L)).thenReturn(Optional.of(booking3));
        assertThrows(BookingApprovedException.class, () ->
                bookingService.confirmBooking(3L, true, 4L));
    }

    @Test
    void getBookingByIdTest() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.mapToDto(booking)).thenReturn(bookingDto);
        BookingDto bookingDtoReturn = bookingService.getBookingById(1L, 4L);
        assertEquals(bookingDto, bookingDtoReturn);

        when(bookingRepository.findById(4L)).thenReturn(Optional.of(booking4));
        when(bookingMapper.mapToDto(booking4)).thenReturn(bookingDto4);
        BookingDto bookingDtoReturn2 = bookingService.getBookingById(4L, 2L);
        assertEquals(bookingDto4, bookingDtoReturn2);
    }

    @Test
    void getBookingByIdTest_whenBookingNotFound_shouldThrowException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                bookingService.getBookingById(1L, 2L));
    }

    @Test
    void getBookingByIdTest_whenUserIsNotOwnerOrBooker_shouldThrowException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class, () ->
                bookingService.getBookingById(1L, 2L));
    }

    @Test
    void checkParametersFromAndSizeTest() {
        assertThrows(ItemCheckException.class, () ->
                bookingService.getBookingByBooker(State.ALL, -1, 2, 4L));
        assertThrows(ItemCheckException.class, () ->
                bookingService.getBookingByBooker(State.ALL, 1, 0, 4L));
        assertThrows(ItemCheckException.class, () ->
                bookingService.getBookingByBooker(State.ALL, 1, -1, 4L));
    }

    @Test
    void getBookingByBookerTest_whenUserNotFound_shouldThrowException() {
        when(userRepository.findById(111L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                bookingService.getBookingByBooker(State.ALL, 0, 10, 111L));
    }

    @Test
    void getBookingByBookerTest_whenStateIsAll_shouldGetListBooking() {
        List<Booking> bookingList = List.of(booking, booking2);
        List<BookingDto> bookingDtoList = List.of(bookingDto, bookingDto2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(1L))
                .thenReturn(bookingList);
        when(bookingMapper.mapToListDto(bookingList)).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByBooker(State.ALL, null, null, 1L);
        verify(bookingRepository).findAllByBooker_IdOrderByStartDesc(1L);
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByBookerTest_whenStateIsAllFromIs0SizeIs2_shouldGetListBooking() {
        List<Booking> bookingList = List.of(booking, booking2);
        List<BookingDto> bookingDtoList = List.of(bookingDto, bookingDto2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker_IdOrderByStartDesc(anyLong(), any(CustomPageRequest.class)))
                .thenReturn(bookingList);
        when(bookingMapper.mapToListDto(bookingList)).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByBooker(State.ALL, 0, 2, 1L);
        verify(bookingRepository).findAllByBooker_IdOrderByStartDesc(anyLong(), any(CustomPageRequest.class));
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByBookerTest_whenStateIsPast_shouldGetListBooking() {
        List<Booking> bookingList = List.of(booking);
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class)))
                .thenReturn(bookingList);
        when(bookingMapper.mapToListDto(bookingList)).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByBooker(
                State.PAST, null, null, 1L);
        verify(bookingRepository).findAllByBooker_IdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class));
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByBookerTest_whenStateIsPastFromIs0SizeIs1_shouldGetListBooking() {
        List<Booking> bookingList = List.of(booking);
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(CustomPageRequest.class)))
                .thenReturn(bookingList);
        when(bookingMapper.mapToListDto(bookingList)).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByBooker(
                State.PAST, 0, 1, 1L);
        verify(bookingRepository).findAllByBooker_IdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(CustomPageRequest.class));
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByBookerTest_whenStateIsFuture_shouldGetListBooking() {
        List<Booking> bookingList = List.of(booking3);
        List<BookingDto> bookingDtoList = List.of(bookingDto3);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class))).thenReturn(bookingList);
        when(bookingMapper.mapToListDto(bookingList)).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByBooker(
                State.FUTURE, null, null, 1L);
        verify(bookingRepository).findAllByBooker_IdAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class));
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByBookerTest_whenStateIsFutureFromIs0SizeIs1_shouldGetListBooking() {
        List<BookingDto> bookingDtoList = List.of(bookingDto3);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(CustomPageRequest.class))).thenReturn(List.of(booking3));
        when(bookingMapper.mapToListDto(List.of(booking3))).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByBooker(
                State.FUTURE, 0, 1, 1L);
        verify(bookingRepository).findAllByBooker_IdAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class),  any(CustomPageRequest.class));
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByBookerTest_whenStateIsCurrent_shouldGetListBooking() {
        List<Booking> bookingList = List.of(booking2);
        List<BookingDto> bookingDtoList = List.of(bookingDto2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker_IdAndEndAfterAndStartBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(bookingList);
        when(bookingMapper.mapToListDto(bookingList)).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByBooker(
                State.CURRENT, null, null, 1L);
        verify(bookingRepository).findAllByBooker_IdAndEndAfterAndStartBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByBookerTest_whenStateIsCurrentFromIs0SizeIs1_shouldGetListBooking() {
        List<BookingDto> bookingDtoList = List.of(bookingDto2);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker_IdAndEndAfterAndStartBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(CustomPageRequest.class)))
                .thenReturn(List.of(booking2));
        when(bookingMapper.mapToListDto(List.of(booking2))).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByBooker(
                State.CURRENT, 0, 1, 1L);
        verify(bookingRepository).findAllByBooker_IdAndEndAfterAndStartBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(CustomPageRequest.class));
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByBookerTest_whenStateIsWaiting_shouldGetListBooking() {
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(
                1L, BookingStatus.WAITING)).thenReturn(List.of(booking));
        when(bookingMapper.mapToListDto(List.of(booking))).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByBooker(
                State.WAITING, null, null, 1L);
        verify(bookingRepository).findAllByBooker_IdAndStatusIsOrderByStartDesc(
                1L, BookingStatus.WAITING);
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByBookerTest_whenFromIs0FromIs1StateIsWaiting_shouldGetListBooking() {
        List<Booking> bookingList = List.of(booking);
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(
                anyLong(), any(BookingStatus.class), any(CustomPageRequest.class))).thenReturn(bookingList);
        when(bookingMapper.mapToListDto(bookingList)).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByBooker(
                State.WAITING, 0, 1, 1L);
        verify(bookingRepository).findAllByBooker_IdAndStatusIsOrderByStartDesc(
                anyLong(), any(BookingStatus.class), any(CustomPageRequest.class));
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByBookerTest_whenStateIsRejected_shouldGetListBooking() {
        List<BookingDto> bookingDtoList = List.of(bookingDto3);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(
                1L, BookingStatus.REJECTED)).thenReturn(List.of(booking3));
        when(bookingMapper.mapToListDto(List.of(booking3))).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByBooker(
                State.REJECTED, null, null, 1L);
        verify(bookingRepository).findAllByBooker_IdAndStatusIsOrderByStartDesc(
                1L, BookingStatus.REJECTED);
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByBookerTest_whenFromIs0SizeIs1StateIsRejected_shouldGetListBooking() {
        List<BookingDto> bookingDtoList = List.of(bookingDto3);
        List<Booking> bookingList = List.of(booking3);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(
                anyLong(), any(BookingStatus.class), any(CustomPageRequest.class))).thenReturn(bookingList);
        when(bookingMapper.mapToListDto(bookingList)).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByBooker(
                State.REJECTED, 0, 1, 1L);
        verify(bookingRepository).findAllByBooker_IdAndStatusIsOrderByStartDesc(
                anyLong(), any(BookingStatus.class), any(CustomPageRequest.class));
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByOwnerTest_whenUserNotFound_shouldThrowException() {
        when(userRepository.findById(4L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                bookingService.getBookingByOwner(State.ALL, null, null, 4L));
    }

    @Test
    void getBookingByOwnerTest_whenStateIsAll_shouldGetListBookings() {
        List<BookingDto> bookingDtoList = List.of(bookingDto, bookingDto2, bookingDto3);
        List<Booking> bookingList = List.of(booking, booking2, booking3);
        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItem_Owner_IdAndItem_AvailableOrderByStartDesc(
                4L, true)).thenReturn(bookingList);
        when(bookingMapper.mapToListDto(bookingList)).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByOwner(
                State.ALL, null, null, 4L);
        verify(bookingRepository).findAllByItem_Owner_IdAndItem_AvailableOrderByStartDesc(
                4L, true);
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByOwnerTest_whenStateIsPast_shouldGetListBookings() {
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        List<Booking> bookingList = List.of(booking);
        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItem_Owner_IdAndItem_AvailableAndEndBeforeOrderByStartDesc(
                anyLong(), anyBoolean(), any(LocalDateTime.class)))
                .thenReturn(bookingList);
        when(bookingMapper.mapToListDto(bookingList)).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByOwner(
                State.PAST, null, null, 4L);
        verify(bookingRepository).findAllByItem_Owner_IdAndItem_AvailableAndEndBeforeOrderByStartDesc(
                anyLong(), anyBoolean(), any(LocalDateTime.class));
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByOwnerTest_whenStateIsFuture_shouldGetListBookings() {
        List<BookingDto> bookingDtoList = List.of(bookingDto3);
        List<Booking> bookingList = List.of(booking3);
        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItem_Owner_IdAndItem_AvailableAndStartAfterOrderByStartDesc(
                anyLong(), anyBoolean(), any(LocalDateTime.class)))
                .thenReturn(bookingList);
        when(bookingMapper.mapToListDto(bookingList)).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByOwner(
                State.FUTURE, null, null, 4L);
        verify(bookingRepository).findAllByItem_Owner_IdAndItem_AvailableAndStartAfterOrderByStartDesc(
                anyLong(), anyBoolean(), any(LocalDateTime.class));
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByOwnerTest_whenStateIsCurrent_shouldGetListBookings() {
        List<BookingDto> bookingDtoList = List.of(bookingDto2);
        List<Booking> bookingList = List.of(booking2);
        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItem_Owner_IdAndItem_AvailableAndEndAfterAndStartBeforeOrderByStartDesc(
                anyLong(), anyBoolean(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(bookingList);
        when(bookingMapper.mapToListDto(bookingList)).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByOwner(
                State.CURRENT, null, null, 4L);
        verify(bookingRepository).findAllByItem_Owner_IdAndItem_AvailableAndEndAfterAndStartBeforeOrderByStartDesc(
                anyLong(), anyBoolean(), any(LocalDateTime.class), any(LocalDateTime.class));
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByOwnerTest_whenStateIsWaiting_shouldGetListBookings() {
        List<BookingDto> bookingDtoList = List.of(bookingDto2);
        List<Booking> bookingList = List.of(booking2);
        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItem_Owner_IdAndStatusIsOrderByStartDesc(
                4L, BookingStatus.WAITING)).thenReturn(bookingList);
        when(bookingMapper.mapToListDto(bookingList)).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByOwner(
                State.WAITING, null, null, 4L);
        verify(bookingRepository).findAllByItem_Owner_IdAndStatusIsOrderByStartDesc(
                4L, BookingStatus.WAITING);
        assertEquals(bookingDtoList, bookingByBooker);
    }

    @Test
    void getBookingByOwnerTest_whenStateIsRejected_shouldGetListBookings() {
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        List<Booking> bookingList = List.of(booking);
        when(userRepository.findById(4L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItem_Owner_IdAndStatusIsOrderByStartDesc(
                4L, BookingStatus.REJECTED)).thenReturn(bookingList);
        when(bookingMapper.mapToListDto(bookingList)).thenReturn(bookingDtoList);

        List<BookingDto> bookingByBooker = bookingService.getBookingByOwner(
                State.REJECTED, null, null, 4L);
        verify(bookingRepository).findAllByItem_Owner_IdAndStatusIsOrderByStartDesc(
                4L, BookingStatus.REJECTED);
        assertEquals(bookingDtoList, bookingByBooker);
    }
}